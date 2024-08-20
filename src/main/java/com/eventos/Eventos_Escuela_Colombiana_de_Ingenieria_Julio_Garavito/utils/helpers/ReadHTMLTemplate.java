package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ReadHTMLTemplate {
    private final Environment environment;

    public String readHtmlTemplate(String body, String htmlFile) {
        Path TEMPLATE_PATH = Paths.get(Objects.requireNonNull(
                environment.getProperty("route.email.html")).concat("\\") + htmlFile);

        try (var lines = Files.lines(TEMPLATE_PATH)) {
            String html = lines.collect(Collectors.joining());
            return html.replace("{body}", body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
