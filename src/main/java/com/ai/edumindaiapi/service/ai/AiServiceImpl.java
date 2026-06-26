package com.ai.edumindaiapi.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiServiceImpl implements AiService {

    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final RestClient restClient;

    public AiServiceImpl(ObjectMapper objectMapper,
                         @Value("${ai.api.key:}") String apiKey,
                         @Value("${ai.api.url:https://api.openai.com/v1/chat/completions}") String apiUrl,
                         @Value("${ai.api.model:gpt-4}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    private boolean isRealMode() {
        return !apiKey.isEmpty();
    }

    @Override
    public String generateQuiz(String topic, String difficulty, int count) {
        if (isRealMode()) {
            return callOpenAI(buildQuizPrompt(topic, difficulty, count));
        }
        return mockQuizJson();
    }

    @Override
    public String tutorReply(String context, List<Map<String, String>> conversationHistory, String userMessage) {
        if (isRealMode()) {
            return callOpenAIWithHistory(context, conversationHistory, userMessage);
        }
        return mockTutorReply(userMessage);
    }

    @Override
    public Map<String, Object> gradeAssignment(String content) {
        if (isRealMode()) {
            String response = callOpenAI(buildGradingPrompt(content));
            try {
                return objectMapper.readValue(response, Map.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse AI grading response", e);
            }
        }
        return mockGradingResult();
    }

    @Override
    public Map<String, Object> predictGrade(Map<String, Object> userPerformanceData) {
        if (isRealMode()) {
            try {
                String data = objectMapper.writeValueAsString(userPerformanceData);
                String response = callOpenAI(buildPredictionPrompt(data));
                return objectMapper.readValue(response, Map.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse AI prediction response", e);
            }
        }
        return mockPredictionResult();
    }

    @Override
    public String recommendPath(Map<String, Object> userData) {
        if (isRealMode()) {
            try {
                String data = objectMapper.writeValueAsString(userData);
                return callOpenAI(buildRecommendationPrompt(data));
            } catch (JsonProcessingException e) {
                log.error("Failed to parse recommendation data", e);
            }
        }
        return mockRecommendation();
    }

    @Override
    public List<Map<String, String>> generateInsights(Map<String, Object> data) {
        if (isRealMode()) {
            try {
                String dataStr = objectMapper.writeValueAsString(data);
                String response = callOpenAI(buildInsightsPrompt(dataStr));
                return objectMapper.readValue(response, List.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse AI insights response", e);
            }
        }
        return mockInsights();
    }

    private String callOpenAI(String prompt) {
        return callOpenAI(List.of(Map.of("role", "user", "content", prompt)));
    }

    private String callOpenAI(List<Map<String, String>> messages) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            Map response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("OpenAI API call failed", e);
        }
        return "";
    }

    private String callOpenAIWithHistory(String context, List<Map<String, String>> conversationHistory, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
            "You are EduMind AI, a helpful programming tutor. Context: " + context));

        if (conversationHistory != null) {
            for (Map<String, String> entry : conversationHistory) {
                String role = "user".equals(entry.get("sender")) ? "user" : "assistant";
                messages.add(Map.of("role", role, "content", entry.get("text")));
            }
        }

        messages.add(Map.of("role", "user", "content", userMessage));
        return callOpenAI(messages);
    }

    private String buildQuizPrompt(String topic, String difficulty, int count) {
        return String.format(
            "Generate a JSON quiz with %d multiple-choice questions about '%s' at '%s' difficulty. " +
            "Return ONLY valid JSON array. Each question must have: id (int), question (string), " +
            "options (array of 4 strings), answer (int index 0-3), explanation (string). " +
            "No markdown, no code fences, just the JSON array.",
            count, topic, difficulty
        );
    }

    private String buildGradingPrompt(String content) {
        return String.format(
            "Grade this student assignment submission and return ONLY a JSON object with: " +
            "grammar (0-100), logic (0-100), completeness (0-100), text (string feedback). " +
            "No markdown, no code fences.\n\nSubmission:\n%s", content
        );
    }

    private String buildPredictionPrompt(String data) {
        return String.format(
            "Based on this student performance data, predict their final grade. " +
            "Return ONLY a JSON object with: grade (string like 'A', 'B+'), " +
            "confidence (0-100), insights (array of strings). " +
            "No markdown, no code fences.\n\nData:\n%s", data
        );
    }

    private String buildRecommendationPrompt(String data) {
        return String.format(
            "Based on this student's learning data, recommend what they should focus on next. " +
            "Return a concise recommendation string (2-3 sentences).\n\nData:\n%s", data
        );
    }

    private String buildInsightsPrompt(String data) {
        return String.format(
            "Based on this classroom performance data, generate 3 AI insights for the teacher. " +
            "Return ONLY a JSON array of objects, each with: id (int), text (string), severity (string: 'high'/'medium'/'low'). " +
            "No markdown, no code fences.\n\nData:\n%s", data
        );
    }

    // ---- Mock implementations ----

    private String mockQuizJson() {
        return "[{\"id\":1,\"question\":\"Which OOP concept allows a sub-class to provide a specific implementation of a method that is already defined in its super-class?\",\"options\":[\"Method Overriding (Polymorphism)\",\"Encapsulation\",\"Data Abstraction\",\"Multiple Inheritance\"],\"answer\":0,\"explanation\":\"Method overriding happens when a subclass defines a method with the same signature as a method in its superclass.\"},{\"id\":2,\"question\":\"Which of the following is true about abstract classes in Java?\",\"options\":[\"They cannot contain concrete methods.\",\"They can be instantiated using the 'new' keyword.\",\"They can contain both abstract and concrete methods.\",\"They do not support inheritance.\"],\"answer\":2,\"explanation\":\"Abstract classes can have abstract and concrete methods but cannot be instantiated directly.\"},{\"id\":3,\"question\":\"What is the default access modifier of interface variables in Java?\",\"options\":[\"private final\",\"public static final\",\"protected static\",\"package-private\"],\"answer\":1,\"explanation\":\"Interface variables are implicitly public, static, and final.\"},{\"id\":4,\"question\":\"Which keyword is used to refer to a direct parent class member?\",\"options\":[\"this\",\"parent\",\"super\",\"base\"],\"answer\":2,\"explanation\":\"The 'super' keyword refers to parent class members.\"},{\"id\":5,\"question\":\"Which of the following prevents a class from being inherited in Java?\",\"options\":[\"Declaring class as static\",\"Declaring class as abstract\",\"Declaring class as final\",\"Declaring class as sealed with no permissions\"],\"answer\":2,\"explanation\":\"A final class cannot be subclassed.\"}]";
    }

    private String mockTutorReply(String userMessage) {
        String msg = userMessage.toLowerCase();
        if (msg.contains("polymorphism")) {
            return "Polymorphism allows objects of different subclasses to be treated as instances of a common parent class. In Java, this is commonly implemented via method overriding. Would you like me to generate a practice quiz on this?";
        } else if (msg.contains("interface")) {
            return "An Interface in Java is a reference type containing only constants, method signatures, default methods, static methods, and nested types. It achieves complete abstraction and multiple inheritance.";
        } else if (msg.contains("abstract class")) {
            return "An Abstract Class in Java that cannot be instantiated. It can contain concrete methods (with implementation) and instance variables, which interfaces cannot hold (prior to Java 8).";
        } else if (msg.contains("recursion")) {
            return "Recursion is a technique where a method calls itself. A recursive method requires:\n1. **Base Case**: Halting condition to prevent StackOverflowError.\n2. **Recursive Step**: Method calls itself with smaller/reduced parameters.";
        } else if (msg.contains("summarize") || msg.contains("summary")) {
            return "Here is a summary of Java Interfaces vs Abstract Classes:\n\n1. **Interface**: Defines a behavior contract. Methods are implicitly abstract/public. Cannot hold state variables.\n2. **Abstract Class**: Base class that cannot be instantiated. Can contain both abstract and concrete methods. Can hold state variables.";
        }
        return "I have analyzed your query. Focus on code efficiency and object models. Let me know if you would like me to compile a code snippet or generate a practice quiz for you.";
    }

    private Map<String, Object> mockGradingResult() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("grammar", 94);
        result.put("logic", 88);
        result.put("completeness", 92);
        result.put("text", "AI check successful. The codebase structured polymorphic abstract definitions correctly. Recommend renaming helper classes to comply with camelCase guidelines.");
        return result;
    }

    private Map<String, Object> mockPredictionResult() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("grade", "A");
        result.put("confidence", 89);
        result.put("insights", List.of(
            "Focus on recursion and memory allocation. You scored 45% in past practices for these modules.",
            "Strong performance in OOP principles. Maintain your study streak to secure an A-grade."
        ));
        return result;
    }

    private String mockRecommendation() {
        return "Your average score in past Collections checks is 45%. The engine prioritizes solidifying generic types and mapping structures before unlocking advanced Spring RESTful framework features.";
    }

    private List<Map<String, String>> mockInsights() {
        return List.of(
            Map.of("id", "1", "text", "32 students are struggling with Java OOP Polymorphism.", "severity", "high"),
            Map.of("id", "2", "text", "Quiz scores for Relational Algebra dropped by 12% in Database course.", "severity", "medium"),
            Map.of("id", "3", "text", "Study times drop mid-week (Wednesday). Recommendation: Schedule reminder pushes.", "severity", "low")
        );
    }
}
