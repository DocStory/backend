package com.ky.docstory.dto.repository;

import com.ky.docstory.entity.Team;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "내가 속한 레포지토리 응답 DTO")
public record MyRepositoryResponse(

        @Schema(description = "레포지토리 ID", example = "3c1a0ab4-0b90-4db4-b8e0-1ef2b6db1944")
        UUID id,

        @Schema(description = "레포지토리 이름", example = "docstory-core")
        String name,

        @Schema(description = "레포지토리 설명", example = "협업용 문서 저장소")
        String description,

        @Schema(description = "소유자 닉네임", example = "kanguk")
        String ownerNickname,

        @Schema(description = "내 역할", example = "CONTRIBUTOR")
        Team.Role myRole,

        @Schema(description = "즐겨 찾기 여부", example = "ture")
        boolean isFavorite
) {
    public static MyRepositoryResponse from(Team team, boolean isFavorite) {
        return new MyRepositoryResponse(
                team.getRepository().getId(),
                team.getRepository().getName(),
                team.getRepository().getDescription(),
                team.getRepository().getOwner().getNickname(),
                team.getRole(),
                isFavorite
        );
    }
}
