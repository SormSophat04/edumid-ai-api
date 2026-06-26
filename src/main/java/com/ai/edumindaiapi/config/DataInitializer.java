package com.ai.edumindaiapi.config;

import com.ai.edumindaiapi.common.enums.*;
import com.ai.edumindaiapi.domain.*;
import com.ai.edumindaiapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final LearningPathMilestoneRepository milestoneRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping DataInitializer");
            return;
        }
        log.info("Seeding database with initial data...");

        User admin = createUser("Admin", "admin@edumind.edu", "admin123", Role.ADMIN);
        User teacher = createUser("Dr. Jane Foster", "teacher@edumind.edu", "teacher123", Role.TEACHER);
        User student = createUser("Sarah Jenkins", "student@edumind.edu", "password", Role.STUDENT);

        Course javaCourse = createCourse("Java Programming",
                "Learn object-oriented programming in Java, including inheritance, interfaces, polymorphism, and memory management.",
                "Computer Science", "Medium",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=400&q=80",
                teacher.getId());

        createModule(javaCourse.getId(), "Module 1: Java Basics", 0, List.of(
                createLessonData("Introduction to Java Virtual Machine (JVM)", LessonType.VIDEO, "12 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("Variables, Data Types, and Operators", LessonType.VIDEO, "18 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 1),
                createLessonData("Control Flow: Switch & Loops", LessonType.VIDEO, "15 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 2)
        ));
        createModule(javaCourse.getId(), "Module 2: Object-Oriented Design", 1, List.of(
                createLessonData("Classes, Objects, and Constructors", LessonType.VIDEO, "20 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("Understanding Inheritance in Java", LessonType.VIDEO, "22 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 1),
                createLessonData("Polymorphism: Overloading & Overriding", LessonType.VIDEO, "25 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 2)
        ));
        createModule(javaCourse.getId(), "Module 3: Advanced Concepts", 2, List.of(
                createLessonData("Interfaces and Abstract Classes", LessonType.VIDEO, "24 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("Exceptions: Try, Catch, and Finally", LessonType.READING, "18 mins", null, "Detailed explanation of Java exception handling...", 1),
                createLessonData("Java Collections Framework Overview", LessonType.VIDEO, "30 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 2)
        ));
        enrollUser(student.getId(), javaCourse.getId(), 45);

        Course dbCourse = createCourse("Database Management Systems",
                "Relational database concepts, SQL queries, normalization, indexing, and transactional processing mechanisms.",
                "Information Technology", "Medium",
                "https://images.unsplash.com/photo-1544383835-bda2bc66a55d?auto=format&fit=crop&w=400&q=80",
                teacher.getId());
        createModule(dbCourse.getId(), "Module 1: Relational Model & SQL", 0, List.of(
                createLessonData("Introduction to Relational Databases", LessonType.VIDEO, "15 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("SQL Basics: SELECT, WHERE, JOINs", LessonType.VIDEO, "30 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 1)
        ));
        createModule(dbCourse.getId(), "Module 2: Database Design", 1, List.of(
                createLessonData("Entity-Relationship Diagrams (ERDs)", LessonType.VIDEO, "22 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("Functional Dependencies & Normalization", LessonType.VIDEO, "28 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 1)
        ));
        enrollUser(student.getId(), dbCourse.getId(), 20);

        Course aiCourse = createCourse("Artificial Intelligence & ML",
                "Fundamentals of machine learning algorithms, deep learning models, neural networks, and decision tree engines.",
                "Advanced Science", "Hard",
                "https://images.unsplash.com/photo-1677442136019-21780efad99a?auto=format&fit=crop&w=400&q=80",
                teacher.getId());
        createModule(aiCourse.getId(), "Module 1: AI Fundamentals", 0, List.of(
                createLessonData("What is Artificial Intelligence?", LessonType.VIDEO, "14 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 0),
                createLessonData("Linear Regression and Cost Functions", LessonType.VIDEO, "25 mins", "https://www.w3schools.com/html/mov_bbb.mp4", null, 1)
        ));
        enrollUser(student.getId(), aiCourse.getId(), 5);

        createAssignment(javaCourse.getId(), student.getId(), "Data Structures Report",
                "Write a report on common data structures in Java.", LocalDate.of(2026, 6, 8));
        createAssignment(dbCourse.getId(), student.getId(), "SQL Normalization Assignment",
                "Design a normalized database schema for a library management system.", LocalDate.of(2026, 5, 28));
        Assignment gradedAsg = createAssignment(aiCourse.getId(), student.getId(), "Neural Network Implementation",
                "Implement a simple neural network using backpropagation.", LocalDate.of(2026, 5, 15));
        gradedAsg.setStatus(AssignmentStatus.GRADED);
        gradedAsg.setScore(95);
        gradedAsg.setFeedbackJson("{\"grammar\":98,\"logic\":95,\"completeness\":93,\"text\":\"Outstanding backpropagation implementation. Matrix multiplications are optimized and convergence graphs are well presented.\"}");
        assignmentRepository.save(gradedAsg);

        createMilestone(student.getId(), "Java Basics", MilestoneStatus.COMPLETED, "Easy", "Variables, arrays & simple flow controls.", 100, 0);
        createMilestone(student.getId(), "OOP Principles", MilestoneStatus.COMPLETED, "Medium", "Classes, Inheritance & Dynamic Polymorphism.", 100, 1);
        createMilestone(student.getId(), "Collections & Generics", MilestoneStatus.CURRENT, "Medium", "Lists, Maps, Sets & Safe Type structures.", 45, 2);
        createMilestone(student.getId(), "Spring Boot Framework", MilestoneStatus.LOCKED, "Hard", "REST APIs, dependency injection & MVC patterns.", 0, 3);
        createMilestone(student.getId(), "Microservices with Spring Cloud", MilestoneStatus.LOCKED, "Hard", "Discovery service, API Gateway & Eureka server structures.", 0, 4);

        createActivityLog(student.getId(), "quiz", "Scored 85% in Java OOP Quiz", "Award");
        createActivityLog(student.getId(), "assignment", "Submitted Data Structures Report", "FileText");
        createActivityLog(student.getId(), "course", "Completed lesson \"Interface & Abstract Classes\"", "BookOpen");
        createActivityLog(student.getId(), "ai", "Generated study plan with AI Tutor", "Cpu");

        log.info("Database seeding completed");
    }

    private User createUser(String name, String email, String password, Role role) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private Course createCourse(String title, String description, String category, String difficulty, String imageUrl, Long teacherId) {
        Course course = Course.builder()
                .title(title)
                .description(description)
                .category(category)
                .difficulty(difficulty)
                .imageUrl(imageUrl)
                .teacherId(teacherId)
                .build();
        return courseRepository.save(course);
    }

    private Lesson.LessonBuilder createLessonData(String title, LessonType type, String duration, String videoUrl, String content, int order) {
        return Lesson.builder()
                .title(title)
                .type(type)
                .duration(duration)
                .videoUrl(videoUrl)
                .content(content)
                .orderIndex(order);
    }

    private void createModule(Long courseId, String title, int orderIndex, List<Lesson.LessonBuilder> lessonBuilders) {
        com.ai.edumindaiapi.domain.Module mod = com.ai.edumindaiapi.domain.Module.builder()
                .courseId(courseId)
                .title(title)
                .orderIndex(orderIndex)
                .build();
        mod = moduleRepository.save(mod);
        for (Lesson.LessonBuilder lb : lessonBuilders) {
            lessonRepository.save(lb.moduleId(mod.getId()).build());
        }
    }

    private void enrollUser(Long userId, Long courseId, int progress) {
        Enrollment enrollment = Enrollment.builder()
                .userId(userId)
                .courseId(courseId)
                .progress(progress)
                .build();
        enrollmentRepository.save(enrollment);
    }

    private Assignment createAssignment(Long courseId, Long userId, String title, String description, LocalDate dueDate) {
        Assignment assignment = Assignment.builder()
                .courseId(courseId)
                .userId(userId)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .status(AssignmentStatus.PENDING)
                .build();
        return assignmentRepository.save(assignment);
    }

    private void createMilestone(Long userId, String title, MilestoneStatus status, String difficulty, String focus, int completion, int order) {
        LearningPathMilestone milestone = LearningPathMilestone.builder()
                .userId(userId)
                .title(title)
                .status(status)
                .difficulty(difficulty)
                .focus(focus)
                .completion(completion)
                .orderIndex(order)
                .build();
        milestoneRepository.save(milestone);
    }

    private void createActivityLog(Long userId, String type, String text, String icon) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .type(type)
                .text(text)
                .icon(icon)
                .build();
        activityLogRepository.save(log);
    }
}
