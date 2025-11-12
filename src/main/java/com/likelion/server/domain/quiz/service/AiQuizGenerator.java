package com.likelion.server.domain.quiz.service;

import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.quiz.entity.Quiz;
import com.likelion.server.domain.quiz.entity.QuizQuestion;
import com.likelion.server.domain.quiz.web.dto.CreateQuizRequest;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * PDF 내용을 기반으로 GPT를 호출해 퀴즈(정답 + 해설)를 생성하는 orchestrator 클래스
 */
@Component
@RequiredArgsConstructor
public class AiQuizGenerator {

    private final PdfTextExtractor pdfTextExtractor;   // PDF → 텍스트 변환 담당
    private final QuizPromptBuilder quizPromptBuilder; // GPT 프롬프트 생성 담당
    private final QuizResponseParser quizResponseParser; // GPT 응답 파싱 담당

    @Value("${openai.key}")
    private String openAiKey;

    @Value("${openai.timeout}")
    private Long timeout;

    /**
     * 전체 퀴즈 생성 절차
     * 1. S3에서 PDF 텍스트 추출
     * 2. GPT 프롬프트 구성
     * 3. GPT 호출
     * 4. 응답 파싱 → QuizQuestion 리스트 반환
     */
    public List<QuizQuestion> generateQuestions(Pdf pdf, Quiz quiz, CreateQuizRequest req) {
        try {
            // PDF 텍스트 추출
            String pdfText = pdfTextExtractor.extract(pdf);

            // PDF 내용 길이 제한 (6,000자 초과 시 잘라내기)
            if (pdfText.length() > 6000) {
                System.out.println("[DEBUG] ✂️ PDF 내용이 너무 깁니다. 6000자로 잘라냅니다.");
                pdfText = pdfText.substring(0, 6000);
            }

            // 프롬프트 구성
            String prompt = quizPromptBuilder.build(pdfText, req, pdf.getFileName());

            System.out.println("[DEBUG] 🧠 프롬프트 길이: " + prompt.length());
            System.out.println("[DEBUG] 🧠 요청 난이도: " + req.difficulty());
            System.out.println("[DEBUG] 🧠 문제 유형: " + req.question_types());
            System.out.println("[DEBUG] 🧠 문제 수: " + req.total_questions());
            System.out.println("[DEBUG] 🧠 Key 존재 여부: " + (openAiKey != null));


            //GPT API 호출
            OpenAiService service = new OpenAiService(openAiKey, Duration.ofSeconds(180));
            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                    .model("gpt-4o")
                    .messages(List.of(
                            new ChatMessage("system", "너는 대학생을 위한 학습용 퀴즈를 생성하는 AI 교사야."),
                            new ChatMessage("user", prompt)
                    ))
                    .temperature(0.7)
                    .maxTokens(2500)
                    .build();

            String aiResponse = service.createChatCompletion(chatRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            // GPT 응답 파싱 → QuizQuestion 엔티티 리스트 변환
            return quizResponseParser.parse(aiResponse, quiz);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI 퀴즈 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
