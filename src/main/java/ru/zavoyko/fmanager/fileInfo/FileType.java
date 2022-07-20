package ru.zavoyko.fmanager.fileInfo;

import lombok.Getter;

@Getter
public enum FileType {
    FILE("F"), DIR("D");

    private String desc;

    FileType(String str) {
        this.desc = str;
    }

}