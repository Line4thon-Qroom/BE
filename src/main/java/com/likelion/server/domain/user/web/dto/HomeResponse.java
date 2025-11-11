package com.likelion.server.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likelion.server.domain.group.entity.Group;
import com.likelion.server.domain.group.entity.enums.Role;
import com.likelion.server.domain.qa.entity.QaBoard;

import java.util.List;

public record HomeResponse(
        List<GroupDto> groups,
        @JsonProperty("qa_board") List<QaBoardDto> qaBoard,
        @JsonProperty("exam_schedule") List<ExamScheduleDto> examSchedule
) {

    public record GroupDto(
            Long id,
            String name,
            @JsonProperty("exam_date") String examDate,
            @JsonProperty("member_count") Integer memberCount,
            @JsonProperty("role") String role
    ) {
        public GroupDto(Group group, Integer memberCount, Role role) {
            this(
                    group.getId(),
                    group.getName(),
                    group.getExamDate(),
                    memberCount,
                    role.toString()
            );
        }
    }

    public record QaBoardDto(
            Long id,
            String title,
            String progress
    ) {
        public QaBoardDto(QaBoard qaBoard, String progress) {
            this(
                    qaBoard.getId(),
                    qaBoard.getBoardName(),
                    progress
            );
        }
    }

    public record ExamScheduleDto(
            @JsonProperty("course_name") String courseName,
            @JsonProperty("exam_date") String examDate
    ) {
        public ExamScheduleDto(Group group) {
            this(
                    group.getName(),
                    group.getExamDate()
            );
        }
    }
}
