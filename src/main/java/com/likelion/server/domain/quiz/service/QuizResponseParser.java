package com.likelion.server.domain.quiz.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizOption;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.entity.enums.Type;
import com.likelion.server.domain.quiz.exception.QuizAiParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuizResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<QuizQuestion> parse(String aiResponse, Quiz quiz) {
        List<QuizQuestion> questions = new ArrayList<>();

        try {
            System.out.println("\n==============================");
            System.out.println("[DEBUG] 🧠 GPT 응답 원문 ↓↓↓");
            System.out.println(aiResponse);
            System.out.println("==============================");

            // 백틱 및 마크다운 제거 (모든 케이스 커버)
            String cleaned = aiResponse
                    .replaceAll("(?i)```json", "")  // ```json, ```JSON
                    .replaceAll("(?i)```", "")      // ``` or ```anything
                    .replaceAll("`", "")            // 단일 백틱
                    .replaceAll("[\\uFEFF]", "")    // BOM 제거
                    .replaceAll("^[\\s\\p{Z}]+", "") // 선행 공백 제거
                    .replaceAll("[\\s\\p{Z}]+$", "") // 후행 공백 제거
                    .trim();

            System.out.println("[DEBUG] 🧹 백틱 제거 후 첫 문자: '" +
                    cleaned.substring(0, Math.min(5, cleaned.length())) + "'");

            JsonNode root;
            try {
                root = objectMapper.readTree(cleaned);
            } catch (Exception e1) {
                System.err.println("⚠️ 1차 파싱 실패 → 재시도 중...");
                try {
                    // 남은 제어문자 제거 후 재시도
                    String retry = aiResponse
                            .replaceAll("(?i)```json", "")
                            .replaceAll("(?i)```", "")
                            .replaceAll("`", "")
                            .replaceAll("[^\\x20-\\x7E]", "")
                            .trim();
                    root = objectMapper.readTree(retry);
                } catch (Exception e2) {
                    System.err.println("❌ JSON 파싱 완전 실패: " + e2.getMessage());
                    throw new QuizAiParseException(); // ❗ JSON 파싱 실패 예외
                }
            }

            System.out.println("[DEBUG] ✂️ 정제된 응답 시작 문자열: " +
                    cleaned.substring(0, Math.min(100, cleaned.length())));

            if (!root.isArray()) {
                System.err.println("❌ GPT 응답이 JSON 배열이 아닙니다.");
                throw new QuizAiParseException();
            }

            // 각 문항 파싱
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

                // 객관식 보기 처리
                if (type == Type.MULTIPLE_CHOICE) {
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
                    } else {
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

                // OX 문제 보기 자동 추가
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
                System.err.println("⚠️ 문제 리스트가 비어 있습니다. Fallback 처리 대신 예외 발생.");
                throw new QuizAiParseException();
            }

        } catch (QuizAiParseException e) {
            throw e; // ❗ 우리가 만든 예외는 그대로 던짐
        } catch (Exception e) {
            System.err.println("❌ GPT 응답 파싱 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            throw new QuizAiParseException(); // ❗ 그 외 모든 예외도 파싱 실패로 처리
        }

        return questions;
    }

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
