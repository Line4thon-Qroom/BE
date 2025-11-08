package com.likelion.server.domain.quiz.entity;

import com.likelion.server.domain.quiz.entity.enums.Type;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;


}
