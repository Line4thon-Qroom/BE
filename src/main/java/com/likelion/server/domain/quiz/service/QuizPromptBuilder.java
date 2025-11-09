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

    public String build(String pdfContent, CreateQuizRequest req, String pdfTitle) {
        return String.format("""
        당신은 대학생을 위한 학습용 퀴즈를 만들어주는 AI 교사입니다.
        오직 아래의 PDF 내용 안에서만 문제를 출제해야 합니다.
        외부 지식은 절대 사용하지 마세요.

        🎯 목표:
        PDF 내용을 기반으로 %d개의 퀴즈 문제를 생성하세요.

        🧩 문제 조건:
        - 난이도는 "%s"입니다.
        - 문제 유형은 다음 중에서 골라 다양하게 섞어주세요: %s.
        - 각 문제는 반드시 다음 4개의 속성을 가져야 합니다:
          • question : 문제 본문 (학생이 답변해야 할 질문)
          • answer : 정답
          • type : 문제 유형 (OX / 객관식 / 단답형)
          • explanation : 간단한 해설 (정답 이유나 추가 설명)
        - 모든 문제는 아래 JSON 형식으로만 출력하세요.
        - 설명, 인사말, 추가 텍스트 없이 JSON 배열만 출력해야 합니다.

        📘 출력 예시 (정확히 이 형식으로만 출력하세요):
        [
          {
            "question": "4호선톤 프로젝트의 주요 기술 스택은 무엇인가?",
            "answer": "Django",
            "type": "객관식",
            "explanation": "Django는 Python 기반 백엔드 프레임워크입니다."
          },
          {
            "question": "4호선톤은 협업 중심의 프로젝트이다. (O/X)",
            "answer": "O",
            "type": "OX",
            "explanation": "팀 단위 협업이 핵심인 프로젝트입니다."
          }
        ]

        ⚖️ 난이도 기준:
        - 상: 개념 응용, 문장 해석, 세부적 내용 이해가 필요한 문제
        - 중: 핵심 개념, 원리, 주요 내용을 직접적으로 묻는 문제
        - 하: 정의, 용어, 단순 사실을 확인하는 문제

        📄 PDF 제목: %s
        📑 PDF 내용:
        %s
    """, req.total_questions(), req.difficulty(),
                String.join(", ", req.question_types()), pdfTitle, pdfContent);
    }
}

