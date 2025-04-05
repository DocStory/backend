package com.ky.docstory.dto.file;

import com.ky.docstory.entity.File;
import java.util.UUID;

public record FileResponse (

        UUID id,

        String name,

        File.FileType fileType
) {
    public static FileResponse from(File file) {
        return new FileResponse(
                file.getId(),
                file.getOriginFilename(),
                file.getFileType()
        );
    }
}
