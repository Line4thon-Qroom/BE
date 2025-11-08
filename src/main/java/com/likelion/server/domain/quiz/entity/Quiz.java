package com.likelion.server.domain.quiz.entity;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.pdf.entity.Pdf;
import com.likelion.server.domain.quiz.entity.enums.Difficulty;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_id")
    private Pdf pdf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private String title;

    private Integer round;
    private Integer totalQuestions;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
}
