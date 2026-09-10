package com.liquido.transaction.service;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.liquido.transaction.pojo.dto.AppendixDto;
import com.liquido.transaction.pojo.entity.Appendix;
import com.liquido.transaction.pojo.vo.DownloadAppendixVo;
import com.liquido.transaction.pojo.vo.QueryAppendixVo;

import org.springframework.web.multipart.MultipartFile;

public interface AppendixService {

    Appendix uploadAppendixAndSave(final MultipartFile file, final String storePath,
                                   final Long merchantId);

    void downloadFile(final Long appendixId, final String filePath);

    Appendix uploadAppendixAndSave(final MultipartFile file, final String storePath,
                                   final String fileName, String referenceCode,
                                   final String s3FileName,
                                   final Long merchantId);

    Appendix uploadAppendixAndSave(final File file, final String storePath, final Long merchantId);

    Appendix uploadAppendixAndSave(final File file, final String storePath, final String fileName,
                                   final String referenceCode, final String s3FileName,
                                   final Long merchantId);

    Appendix uploadAppendixAndSave(final File file, final String storePath, final String fileName,
                                   final String referenceCode, final String s3FileName,
                                   final String merchantCode);

    List<AppendixDto> findAllByIds(final List<Long> appendixIds);

    void downloadAppendix(final DownloadAppendixVo vo, final HttpServletResponse response);

    List<AppendixDto> generatorAppendixDownloadUrl(final QueryAppendixVo vo);

    void appendixMerchantIdAdd();
}



