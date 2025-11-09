package com.likelion.server.domain.quiz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.enums.Type;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuizResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<QuizQuestion> parse(String aiResponse, Quiz quiz) {
        List<QuizQuestion> questions = new ArrayList<>();
        String cleaned = aiResponse; // 바깥에서 선언해서 어디서든 접근 가능

        try {
            System.out.println("\n==============================");
            System.out.println("[DEBUG] 🧠 GPT 응답 원문 ↓↓↓");
            System.out.println(aiResponse);
            System.out.println("==============================");

            JsonNode root = null;
            try {
                // ✅ 백틱(```) 및 제어문자 제거
                cleaned = aiResponse
                        .replaceAll("(?s)```json", "")
                        .replaceAll("(?s)```", "")
                        .replaceAll("(?s)`", "")
                        .replaceAll("[\\uFEFF]", "")
                        .replaceAll("(?s)^\\s+", "")
                        .replaceAll("(?s)\\s+$", "")
                        .trim();

                System.out.println("[DEBUG] 🧹 백틱 제거 후 첫 문자: '" +
                        cleaned.substring(0, Math.min(5, cleaned.length())) + "'");

                root = objectMapper.readTree(cleaned);

            } catch (Exception e1) {
                System.err.println("⚠️ 1차 파싱 실패 → 재시도 중...");
                try {
                    // 혹시 남은 제어문자 제거 후 재시도
                    String retry = aiResponse.replaceAll("[^\\x20-\\x7E]", "").trim();
                    root = objectMapper.readTree(retry);
                } catch (Exception e2) {
                    System.err.println("❌ JSON 파싱 완전 실패: " + e2.getMessage());
                    return createFallback(quiz);
                }
            }

            System.out.println("[DEBUG] ✂️ 정제된 응답 시작 문자열: " +
                    cleaned.substring(0, Math.min(100, cleaned.length())));

            if (!root.isArray()) {
                System.err.println("❌ GPT 응답이 JSON 배열이 아닙니다.");
                return createFallback(quiz);
            }

            // ✅ 각 문항 파싱
            for (JsonNode node : root) {
                String questionText = node.path("question").asText("");
                String correctAnswer = node.path("answer").asText("");
                String typeStr = node.path("type").asText("");
                String explanation = node.path("explanation").asText("");

                if (questionText.isBlank() || correctAnswer.isBlank()) continue;

                Type type = convertType(typeStr);

                QuizQuestion question = QuizQuestion.builder()
                        .quiz(quiz)
                        .type(type)
                        .questionText(questionText)
                        .correctAnswer(correctAnswer)
                        .explanation(explanation)
                        .build();

                // 객관식이면 보기 자동 생성
                if (type == Type.MULTIPLE_CHOICE) {
                    // AI 응답이 보기 리스트를 제공하는 경우
                    if (node.has("options")) {
                        for (JsonNode optionNode : node.get("options")) {
                            String optionText = optionNode.asText();
                            QuizOption option = QuizOption.builder()
                                    .question(question)
                                    .optionText(optionText)
                                    .isAnswer(optionText.equals(correctAnswer))
                                    .build();
                            question.getOptions().add(option);
                        }
                    }
                    // AI 응답이 보기 배열을 제공하지 않은 경우 (기본 4개 보기 예시)
                    else {
                        String[] defaultOptions = {"A", "B", "C", "D"};
                        for (String opt : defaultOptions) {
                            QuizOption option = QuizOption.builder()
                                    .question(question)
                                    .optionText(opt)
                                    .isAnswer(false)
                                    .build();
                            question.getOptions().add(option);
                        }
                    }
                }

                // OX 문제라면 O, X 보기 자동 추가
                else if (type == Type.OX) {
                    QuizOption o = QuizOption.builder()
                            .question(question)
                            .optionText("O")
                            .isAnswer(correctAnswer.equalsIgnoreCase("O"))
                            .build();

                    QuizOption x = QuizOption.builder()
                            .question(question)
                            .optionText("X")
                            .isAnswer(correctAnswer.equalsIgnoreCase("X"))
                            .build();

                    question.getOptions().addAll(List.of(o, x));
                }


                questions.add(question);
                System.out.println("[DEBUG] ✅ 문제 추가됨 → " + questionText);
            }

            System.out.println("[DEBUG] 🎯 최종 파싱된 문제 개수: " + questions.size());
            System.out.println("==============================\n");

            if (questions.isEmpty()) {
                return createFallback(quiz);
            }

        } catch (Exception e) {
            System.err.println("❌ GPT 응답 파싱 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            return createFallback(quiz);
        }

        return questions;
    }

    private List<QuizQuestion> createFallback(Quiz quiz) {
        List<QuizQuestion> fallback = new ArrayList<>();
        fallback.add(QuizQuestion.builder()
                .quiz(quiz)
                .type(Type.OX)
                .questionText("AI 퀴즈 생성 결과를 파싱하지 못했습니다.")
                .correctAnswer("N/A")
                .explanation("GPT 응답 형식을 확인하세요.")
                .build());
        return fallback;
    }

    // ✅ Type 문자열 → Enum 변환
    private Type convertType(String typeStr) {
        if (typeStr == null || typeStr.isBlank()) return Type.OX;
        typeStr = typeStr.trim().toUpperCase();

        if (typeStr.contains("OX")) return Type.OX;
        if (typeStr.contains("객관")) return Type.MULTIPLE_CHOICE;
        if (typeStr.contains("단답")) return Type.SHORT_ANSWER;

        try {
            return Type.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("[WARN] 알 수 없는 Type: " + typeStr + " → 기본값 OX 사용");
            return Type.OX;
        }
    }
}
