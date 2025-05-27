package com.ky.docstory.dto.history;

import com.ky.docstory.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "히스토리 검색용 파일 리스트 응답 DTO")
public record HistoryFileResponse (

        @Schema(description = "파일 ID", example = "3c1a0ab4-0b90-4db4-b8e0-1ef2b6db1944")
        UUID Id,

        @Schema(description = "파일명", example = "docStory.pdf")
        String fileName
)
{
    public static HistoryFileResponse from(File file) {
        return new HistoryFileResponse(
                file.getId(),
                file.getOriginFilename()
        );
    }
}
