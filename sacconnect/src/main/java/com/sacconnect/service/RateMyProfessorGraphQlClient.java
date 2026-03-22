package com.sacconnect.service;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RateMyProfessorGraphQlClient implements RateMyProfessorClient {

    private static final URI GRAPHQL_URI = URI.create("https://www.ratemyprofessors.com/graphql");
    private static final String AUTHORIZATION_HEADER = "Basic dGVzdDp0ZXN0";
    private static final String SCHOOL_QUERY = """
            query NewSearchSchoolsQuery($query: SchoolSearchQuery) {
              newSearch {
                schools(query: $query) {
                  edges {
                    node {
                      id
                      legacyId
                      name
                      city
                      state
                    }
                  }
                }
              }
            }
            """;
    private static final String SEARCH_TEACHER_QUERY = """
            query NewSearchTeachersQuery($query: TeacherSearchQuery!, $count: Int) {
              newSearch {
                teachers(query: $query, first: $count) {
                  didFallback
                  edges {
                    node {
                      id
                      legacyId
                      firstName
                      lastName
                      department
                      avgRating
                      numRatings
                      school {
                        id
                        legacyId
                        name
                      }
                    }
                  }
                }
              }
            }
            """;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RateMyProfessorGraphQlClient(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<String> findSchoolId(String schoolName) throws Exception {
        JsonNode variables = objectMapper.createObjectNode()
                .set("query", objectMapper.createObjectNode().put("text", schoolName));
        JsonNode root = executeQuery(SCHOOL_QUERY, variables);
        JsonNode edges = root.path("data").path("newSearch").path("schools").path("edges");

        if (!edges.isArray()) {
            return Optional.empty();
        }

        String normalizedTarget = normalize(schoolName);
        String fallbackId = null;

        for (JsonNode edge : edges) {
            JsonNode node = edge.path("node");
            String candidateName = node.path("name").asText("");
            String candidateId = node.path("id").asText("");
            String normalizedCandidate = normalize(candidateName);

            if (normalizedCandidate.equals(normalizedTarget)) {
                return Optional.of(candidateId);
            }

            if (fallbackId == null && normalizedCandidate.contains("sacramento")) {
                fallbackId = candidateId;
            }
        }

        return Optional.ofNullable(fallbackId);
    }

    @Override
    public List<RateMyProfessorTeacher> searchTeachers(String teacherName, String schoolId) throws Exception {
        JsonNode variables = objectMapper.createObjectNode()
                .put("count", 5)
                .set("query", objectMapper.createObjectNode()
                        .put("text", teacherName)
                        .put("schoolID", schoolId)
                        .put("fallback", true));
        JsonNode root = executeQuery(SEARCH_TEACHER_QUERY, variables);
        JsonNode edges = root.path("data").path("newSearch").path("teachers").path("edges");
        List<RateMyProfessorTeacher> teachers = new ArrayList<>();

        if (!edges.isArray()) {
            return teachers;
        }

        for (JsonNode edge : edges) {
            JsonNode node = edge.path("node");
            teachers.add(new RateMyProfessorTeacher(
                    node.path("id").asText(""),
                    node.path("firstName").asText(""),
                    node.path("lastName").asText(""),
                    node.path("school").path("name").asText(""),
                    node.path("avgRating").isNumber() ? node.path("avgRating").asDouble() : null,
                    node.path("numRatings").isInt() ? node.path("numRatings").asInt() : 0
            ));
        }

        return teachers;
    }

    private JsonNode executeQuery(String query, JsonNode variables) throws Exception {
        JsonNode payload = objectMapper.createObjectNode()
                .put("query", query)
                .set("variables", variables);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(GRAPHQL_URI)
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", AUTHORIZATION_HEADER)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Rate My Professors request failed with status " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        if (root.has("errors")) {
            throw new IllegalStateException("Rate My Professors returned GraphQL errors: " + root.get("errors"));
        }

        return root;
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.toLowerCase()
                        .replaceAll("[^a-z0-9 ]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
    }
}
