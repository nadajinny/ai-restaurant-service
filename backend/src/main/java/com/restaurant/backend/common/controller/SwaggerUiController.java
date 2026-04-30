package com.restaurant.backend.common.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webjars.WebJarVersionLocator;

@Controller
public class SwaggerUiController {

    private static final String SWAGGER_UI_VERSION = new WebJarVersionLocator().version("swagger-ui");

    @GetMapping(
            value = {"/swagger-ui.html", "/swagger-ui/index.html"},
            produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String swaggerUi() {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>AI Restaurant Service API Docs</title>
                    <link rel="stylesheet" href="/webjars/swagger-ui/%s/swagger-ui.css">
                    <style>
                        body { margin: 0; background: #f7f7f7; }
                    </style>
                </head>
                <body>
                    <div id="swagger-ui"></div>
                    <script src="/webjars/swagger-ui/%s/swagger-ui-bundle.js"></script>
                    <script src="/webjars/swagger-ui/%s/swagger-ui-standalone-preset.js"></script>
                    <script>
                        window.ui = SwaggerUIBundle({
                            url: '/v3/api-docs',
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            layout: 'BaseLayout'
                        });
                    </script>
                </body>
                </html>
                """.formatted(SWAGGER_UI_VERSION, SWAGGER_UI_VERSION, SWAGGER_UI_VERSION);
    }
}
