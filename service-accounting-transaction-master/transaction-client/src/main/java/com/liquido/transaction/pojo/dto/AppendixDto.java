package com.liquido.transaction.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppendixDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long merchantId;

    private String referenceCode;

    private String fileName;

    private String s3Url;

    private String s3FileName;

    private String downloadUrl;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
