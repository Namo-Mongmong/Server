package com.example.namo2.domain.moim.ui.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MoimResponse {
    private MoimResponse() {
        throw new IllegalStateException("Util Class");
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static class MoimIdDto {
        private Long moimId;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MoimDto {
        private Long groupId;
        private String groupName;
        private String groupImgUrl;
        private String groupCode;
        private List<MoimUserDto> moimUsers;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MoimUserDto {
        private Long userId;
        private String userName;
        private Integer color;
    }
}