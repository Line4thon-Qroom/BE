package com.likelion.server.domain.qa.entity;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.quiz.entity.Quiz;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QaBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Column(nullable = false)
    private String boardName;


    // === Setter ===
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
