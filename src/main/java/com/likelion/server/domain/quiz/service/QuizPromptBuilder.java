package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import org.springframework.stereotype.Component;

/**
 * GPT 퀴즈 생성용 프롬프트를 구성하는 클래스
 * - PDF 내용 기반 문제 생성
 * - 난이도(상/중/하) 기준 포함
 * - 정답(answer) + 해설(explanation) 필드 포함
 */

@Component
public class QuizPromptBuilder {

    String build(String pdfText, CreateQuizRequest req, String pdfTitle) {
        return String.format("""
        당신은 대학생을 위한 학습용 퀴즈를 만드는 AI 교사입니다.
        오직 아래의 PDF 내용 안에서만 문제를 출제하세요. 외부 지식은 절대 사용하지 마세요.

        🎯 목표:
        PDF 내용을 기반으로 %d개의 문제를 생성하세요.
        난이도: "%s"
        문제 유형: %s

        🧩 문제 조건:
        - 객관식 문제는 반드시 4개의 보기를 포함해야 합니다.
        - 객관식 문제는 JSON 내에 "options" 배열을 포함해야 합니다.
        - 단답형 문제는 짧은 문장으로 정답을 제시합니다.
        - OX 문제는 정답이 반드시 "O" 또는 "X" 중 하나여야 합니다.
        - 모든 문제는 PDF의 내용을 정확히 반영해야 합니다.

        ⚙️ 출력 형식 (JSON 배열만 출력하세요):
        [
          {
            "question": "주문과 상품의 관계는?",
            "type": "객관식",
            "options": ["1. 일대다", "2. 다대다", "3. 일대일", "4. 다대일"],
            "answer": "2",
            "explanation": "주문과 상품은 다대다 관계이며, 이를 주문상품 엔티티로 풀었다."
          },
          {
            "question": "회원은 여러 상품을 주문할 수 있다. (O/X)",
            "type": "OX",
            "answer": "O",
            "explanation": "회원과 주문은 일대다 관계다."
          },
          {
            "question": "상품의 종류는?",
            "type": "단답형",
            "answer": "도서, 음반, 영화",
            "explanation": "상품은 세 가지 종류로 구분된다."
          }
        ]

        ⚖️ 난이도 기준:
        - 상: 개념 응용, 세부 내용 이해
        - 중: 핵심 개념 및 정의
        - 하: 단순 사실 또는 용어 확인

        📄 PDF 제목: %s
        📑 PDF 내용 (발췌):
        %s
        """,
                req.total_questions(),
                req.difficulty(),
                String.join(", ", req.question_types()),
                pdfTitle,
                pdfText.substring(0, Math.min(pdfText.length(), 6500))
        );
    }
}

