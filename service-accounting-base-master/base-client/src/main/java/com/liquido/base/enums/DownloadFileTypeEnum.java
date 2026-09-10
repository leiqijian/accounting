package com.liquido.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public enum DownloadFileTypeEnum {

    BIN("bin", "application/octet-stream", "attachment"),
    BMP("bmp", "image/bmp", "image"),
    CSS("css", "text/css", "attachment"),
    CSV("csv", "text/csv", "attachment"),
    DOC("doc", "application/msword", "attachment"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "attachment"),
    GIF("gif", "image/gif", "image"),
    HTM("htm", "text/html", "attachment"),
    HTML("html", "text/html", "attachment"),
    ICO("ico", "image/vnd.microsoft.icon", "image"),
    JPEG("jpeg", "image/jpeg", "image"),
    JPG("jpg", "image/jpeg", "image"),
    JS("js", "text/javascript", "attachment"),
    JSON("json", "application/json", "attachment"),
    MP3("mp3", "audio/mpeg", "attachment"),
    MPEG("mpeg", "video/mpeg", "attachment"),
    PNG("png", "image/png", "image"),
    PDF("pdf", "application/pdf", "attachment"),
    PPT("ppt", "application/vnd.ms-powerpoint", "attachment"),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "attachment"),
    RAR("rar", "application/x-rar-compressed", "attachment"),
    SH("sh", "application/x-sh", "attachment"),
    TAR("tar", "application/x-tar", "attachment"),
    TXT("txt", "text/plain", "attachment"),
    WEBP("webp", "image/webp", "image"),
    XLS("xls", "application/vnd.ms-excel", "attachment"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "attachment"),
    XML("xml", "text/xml", "attachment"),
    ZIP("zip", "application/zip", "attachment"),
    ONE("one", "application/one", "attachment"),

    ;

    private final String extension;

    private final String fileContentType;

    private final String larkFileType;

}
