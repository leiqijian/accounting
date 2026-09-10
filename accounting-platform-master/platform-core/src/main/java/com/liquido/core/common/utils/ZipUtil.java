package com.liquido.core.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class ZipUtil {

    @SneakyThrows
    public static int compress(final List<String> filePaths,
                               final String zipFilePath,
                               final Boolean keepDirStructure) {
        final byte[] buf = new byte[1024];
        final File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            zipFile.createNewFile();
        }
        int fileCount = 0;
        @Cleanup final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        for (final String relativePath : filePaths) {
            if (StringUtils.isEmpty(relativePath)) {
                continue;
            }
            final File sourceFile = new File(relativePath);
            if (sourceFile == null || !sourceFile.exists()) {
                continue;
            }
            @Cleanup final FileInputStream fis = new FileInputStream(sourceFile);
            if (keepDirStructure != null && keepDirStructure) {
                zos.putNextEntry(new ZipEntry(relativePath));
            } else {
                zos.putNextEntry(new ZipEntry(sourceFile.getName()));
            }
            int len;
            while ((len = fis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            fileCount++;
        }
        return fileCount;
    }
}
