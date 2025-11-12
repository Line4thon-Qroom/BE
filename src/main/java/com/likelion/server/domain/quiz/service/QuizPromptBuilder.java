package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import org.springframework.stereotype.Component;

@Component
public class QuizPromptBuilder {

    String build(String pdfText, CreateQuizRequest req, String pdfTitle) {
        String allowedTypes = String.join(", ", req.question_types());

        return String.format("""
        당신은 대학생을 위한 학습용 퀴즈를 만드는 AI 교사입니다.
        오직 아래의 PDF 내용 안에서만 문제를 출제하세요. 외부 지식이나 추론은 절대 사용하지 마세요.

        🎯 목표:
        - PDF 내용을 기반으로 총 %d개의 문제를 생성합니다.
        - 난이도: "%s"
        - 생성해야 할 문제 유형: [%s]

        ⚠️ 매우 중요:
        아래 문제 유형 중 **요청된 유형만 생성해야 합니다.**
        요청되지 않은 유형은 **절대 포함하지 마세요.**
        - ```json, 코드 블록, 마크다운, 설명 문장, 주석 등을 절대 포함하지 마세요.

        예시:
        - ["OX"] → 오직 OX 문제만 생성 (객관식·단답형 출제 금지)
        - ["OX", "객관식"] → 이 두 유형만 섞어서 생성 (단답형 출제 금지)

        🧩 문제 작성 규칙:
        - 모든 문제 유형은 반드시 "explanation" 필드를 포함해야 합니다.
        1️⃣ **객관식 문제**
            - 반드시 4개의 보기("options")를 포함해야 합니다.
            - 정답("answer")은 보기 번호나 보기의 텍스트 중 하나로 표시합니다.
        2️⃣ **OX 문제**
            - 반드시 "(O/X)" 형식으로 문장을 작성합니다.
            - 정답("answer")은 "O" 또는 "X" 중 하나여야 합니다.
        3️⃣ **단답형 문제**
            - 정답("answer")은 **한 단어만** 작성해야 합니다.
            - 한 단어란, 띄어쓰기 없이 하나의 개념을 나타내는 단어를 의미합니다.

        💬 출력 형식 (JSON 배열만 출력하세요):
        [
          {
            "question": "주문과 상품의 관계는?",
            "type": "객관식",
            "options": ["A. 일대다", "B. 다대다", "C. 일대일", "D. 다대일"],
            "answer": "B",
            "explanation": "주문과 상품은 다대다 관계이며, 이를 주문상품 엔티티로 풀었다."
          },
          {
            "question": "회원은 여러 상품을 주문할 수 있다. (O/X)",
            "type": "OX",
            "answer": "O",
            "explanation": "회원과 주문은 일대다 관계다."
          },
          {
            "question": "엔티티의 기본 키를 의미하는 약어는?",
            "type": "단답형",
            "answer": "PK",
            "explanation": "PK는 Primary Key의 약어로, 각 행을 고유하게 식별한다."
          }
        ]
        
        ⚖️ 난이도 기준:
        - 상: 개념 응용, 세부 내용 이해 중심
        - 중: 핵심 개념 및 정의 중심
        - 하: 단순 사실 또는 용어 확인 중심

        📄 PDF 제목: %s
        📑 PDF 내용 (발췌):
        %s
        """,
                req.total_questions(),
                req.difficulty(),
                allowedTypes,
                pdfTitle,
                pdfText.substring(0, Math.min(pdfText.length(), 6000)),
                allowedTypes
        );
    }
}
