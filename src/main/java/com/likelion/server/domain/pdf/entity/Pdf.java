package com.likelion.server.domain.pdf.entity;

import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.user.entity.User;
import com.likelion.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pdf extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String s3Url;
}
