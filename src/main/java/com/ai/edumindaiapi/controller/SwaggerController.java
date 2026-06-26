package com.ai.edumindaiapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerController {

    @GetMapping(value = "/swagger-ui", produces = MediaType.TEXT_HTML_VALUE)
    public String swaggerUI(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getHeader("Host");
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>EduMind AI API - Swagger UI</title>
                <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css" />
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js"></script>
                <script>
                    SwaggerUIBundle({
                        url: '%s/v3/api-docs',
                        dom_id: '#swagger-ui',
                        deepLinking: true,
                        displayRequestDuration: true,
                        presets: [
                            SwaggerUIBundle.presets.apis
                        ],
                        layout: "BaseLayout"
                    });
                </script>
            </body>
            </html>
            """.formatted(baseUrl);
    }
}
