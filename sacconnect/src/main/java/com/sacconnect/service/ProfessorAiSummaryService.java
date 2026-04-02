package com.sacconnect.service;

import com.sacconnect.dto.response.ProfessorAiSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfessorAiSummaryService {

    private static final Logger log = LoggerFactory.getLogger(ProfessorAiSummaryService.class);
    private static final URI OPENAI_URI = URI.create("https://api.openai.com/v1/responses");

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String openAiApiKey;
    private final String openAiModel;

    public ProfessorAiSummaryService(
            JdbcTemplate jdbcTemplate,
            ObjectMapper objectMapper,
            @Value("${openai.api.key:${OPENAI_API_KEY:}}") String openAiApiKey,
            @Value("${openai.model:gpt-4o}") String openAiModel
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public ProfessorAiSummaryResponse summarizeProfessor(Long professorId) throws Exception {
        if (professorId == null || professorId <= 0) {
            throw new IllegalArgumentException("Invalid professor id.");
        }
        String professorName = fetchProfessorName(professorId);
        return summarizeProfessorByNameInternal(professorId, professorName);
    }

    public ProfessorAiSummaryResponse summarizeProfessorByName(String professorName) throws Exception {
        if (professorName == null || professorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Professor name is required.");
        }
        return summarizeProfessorByNameInternal(null, professorName.trim());
    }

    private ProfessorAiSummaryResponse summarizeProfessorByNameInternal(Long professorId, String professorName) throws Exception {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            throw new IllegalStateException("OPENAI key not configured. Set openai.api.key or OPENAI_API_KEY.");
        }

        String userPrompt = buildPrompt(professorName);
        JsonNode aiRoot = callOpenAi(userPrompt);
        JsonNode parsed = parseSummaryJson(aiRoot);

        String summary = parsed.path("threeSentenceSummary").asText("No summary generated.");
        List<String> pros = toStringList(parsed.path("pros"));
        List<String> cons = toStringList(parsed.path("cons"));
        int reviewsAnalyzed = parsed.path("reviewsAnalyzed").asInt(5);

        return new ProfessorAiSummaryResponse(
                professorId,
                professorName,
                summary,
                pros,
                cons,
                reviewsAnalyzed,
                "openai-web-rmp"
        );
    }

    private String fetchProfessorName(Long professorId) {
        String sql = "SELECT name FROM professors WHERE id = ?";
        List<String> names = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("name"), professorId);
        if (names.isEmpty()) {
            throw new IllegalArgumentException("Professor not found for id " + professorId);
        }
        return names.get(0);
    }

    private String buildPrompt(String professorName) {
        return """
                Use web search to find this professor on RateMyProfessors and summarize the first 5 written reviews.
                Professor: %s

                Constraints:
                - Prioritize RateMyProfessors pages.
                - If exact name has multiple matches, choose the one for California State University Sacramento when available.
                - Use up to the first 5 written reviews you can find.

                Output STRICT JSON only with this exact shape:
                {
                  "threeSentenceSummary": "exactly 3 sentences, in-depth but concise",
                  "pros": ["short bullet", "short bullet", "short bullet"],
                  "cons": ["short bullet", "short bullet", "short bullet"],
                  "reviewsAnalyzed": 5
                }

                If fewer than 5 written reviews are discoverable, set reviewsAnalyzed to the actual number used and summarize based on available evidence.
                """.formatted(professorName);
    }

    private JsonNode callOpenAi(String prompt) throws Exception {
        JsonNode payload = objectMapper.createObjectNode()
                .put("model", openAiModel)
                .put("input", prompt)
                .set("tools", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode().put("type", "web_search_preview")));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(OPENAI_URI)
                .timeout(Duration.ofSeconds(45))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("OpenAI request failed with status " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readTree(response.body());
    }

    private JsonNode parseSummaryJson(JsonNode openAiRoot) throws Exception {
        String content = openAiRoot.path("output_text").asText("");
        if (content.isBlank()) {
            JsonNode output = openAiRoot.path("output");
            if (output.isArray()) {
                for (JsonNode item : output) {
                    JsonNode contentArray = item.path("content");
                    if (!contentArray.isArray()) continue;
                    for (JsonNode contentItem : contentArray) {
                        String maybeText = contentItem.path("text").asText("");
                        if (!maybeText.isBlank()) {
                            content = maybeText;
                            break;
                        }
                    }
                    if (!content.isBlank()) break;
                }
            }
        }
        if (content.isBlank()) {
            throw new IllegalStateException("OpenAI returned empty content.");
        }
        String stripped = stripCodeFences(content);
        return objectMapper.readTree(stripped);
    }

    private String stripCodeFences(String raw) {
        String content = raw.trim();
        if (content.startsWith("```")) {
            int firstNewline = content.indexOf('\n');
            if (firstNewline > 0) {
                content = content.substring(firstNewline + 1);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
        }
        return content.trim();
    }

    private List<String> toStringList(JsonNode node) {
        if (!node.isArray()) {
            return List.of();
        }
        List<String> values = new ArrayList<>();
        node.forEach(item -> {
            String value = item.asText("").trim();
            if (!value.isBlank()) {
                values.add(value);
            }
        });
        return values;
    }
}
