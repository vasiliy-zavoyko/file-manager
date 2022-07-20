package ru.zavoyko.fmanager.fileInfo;


import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static java.nio.file.Files.*;

@Data
public class FileInfo {

    private String name;
    private FileType type;
    private long size;
    private LocalDateTime lastModified;

    @SneakyThrows
    public FileInfo(Path path) {
        this.name = path.getFileName().getFileName().toString();
        this.type = isDirectory(path) ? FileType.DIR : FileType.FILE;
        if (FileType.DIR == this.type) {
            this.size = -1l;
        } else {
            this.size = size(path);
        }
        this.lastModified = LocalDateTime.ofInstant(
                getLastModifiedTime(path).toInstant(),
                ZoneOffset.systemDefault()
        );
    }

}
