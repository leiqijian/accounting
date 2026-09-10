package com.liquido.core.common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.liquido.core.common.exception.CommonExceptionCode;
import com.liquido.core.common.logger.LogConstant;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.MDC;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileUploadUtil
 */
@Slf4j
public class FileUploadUtil {

    // 文件名仅支持：字母, 数字, 中划线, 下划线, 中文, . 空格;
    private static final Pattern VALID_FILENAME_PATTERN =
            Pattern.compile("^[\\w\\-\\s\\u4e00-\\u9fa5_. ]+$");

    private static final List<String> DEFAULT_ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "csv", "xls", "xlsx", "pptx",
                    "pdf", "txt", "zip", "gzip", "tar", "rar", "7z");

    private static final List<String> DEFAULT_ALLOWED_FILE_TYPES =
            Arrays.asList(
                    "image/jpg",
                    "image/png",
                    "image/jpeg",
                    "text/csv",
                    "text/plain",
                    "application/pdf",
                    "application/msword",
                    "application/x-tika-ooxml",
                    "application/vnd.ms-excel",
                    "application/x-tika-msoffice",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/zip",
                    "application/gzip",
                    "application/x-tar",
                    "application/vnd.rar",
                    "application/x-7z-compressed");

    /**
     * Default temporary file upload local directory
     */
    private static final String DEFAULT_UPLOAD_TEMP_PATH = "/app/upload/temp";

    private static final String IMAGE_TYPE = "JPEG";

    private static final List<String> SUPPORT_IMAGE_TYPES =
            Lists.newArrayList("jpeg", "jpg", "png");

    private static String buildFileName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Upload Base64 string image (without image header information)
     *
     * @param url
     * @param base64Image
     * @param paramsMap
     * @param headers
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T> T uploadImage(
            final String url, final String base64Image,
            final Map<String, Object> paramsMap,
            final Map<String, String> headers,
            final Class<T> clazz) {

        log.info(">>>> FileUploadUtil.uploadImage params: url={}, paramsMap={}, "
                        + "headerMap={}, clazz={}", url, JsonUtil.toJson(paramsMap),
                JsonUtil.toJson(headers),
                clazz);
        if (StringUtils.isBlank(base64Image)) {
            log.error("Upload image content cannot be empty");
            return null;
        }

        File localTempFile = null;
        final byte[] bytes = java.util.Base64.getDecoder().decode(base64Image.trim());
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {

            String imgType = getImageType(bytes);
            String fileName = buildFileName();
            if (StringUtils.isNotBlank(imgType)) {
                if (IMAGE_TYPE.equalsIgnoreCase(imgType.trim())) {
                    imgType = "jpg";
                }
                fileName = fileName + "." + imgType.toLowerCase();
            }

            if (!SUPPORT_IMAGE_TYPES.contains(fileName.trim())) {
                throw CommonExceptionCode.PARAMETER_ILLEGAL.exception(
                        "Image type is not supported, "
                                + "please upload image in jpg or png format");
            }

            final File fileDic = new File(DEFAULT_UPLOAD_TEMP_PATH);
            if (!fileDic.exists()) {
                fileDic.mkdirs();
            }

            final String filePath = fileDic.getPath() + File.separator + fileName;
            localTempFile = new File(filePath);
            FileUtils.copyInputStreamToFile(inputStream, localTempFile);

            return uploadFile(url, localTempFile, paramsMap, headers, clazz);
        } catch (Exception e) {
            log.error("File upload failed", e);
        } finally {
            if (localTempFile != null) {
                localTempFile.delete();
            }
        }

        return null;
    }

    /**
     * Analyze image formats
     *
     * @param bytes
     *
     * @return
     */
    private static String getImageType(final byte[] bytes) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             final ImageInputStream iis = ImageIO.createImageInputStream(inputStream)) {
            final Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            while (imageReaders.hasNext()) {
                ImageReader reader = imageReaders.next();
                return reader.getFormatName();
            }
        } catch (Exception e) {
            log.error("getImageType error", e);
        }

        return "";
    }


    // url to file
    public static File getFile(
            final String url,
            final String fileName) throws Exception {

        if (StringUtils.isBlank(url)) {
            throw CommonExceptionCode.PARAMETER_MISSING.exception("url");
        }

        final File file = File.createTempFile("file", fileName);
        final URL urlFile = new URL(url);
        try (final InputStream inStream = urlFile.openStream();
             final OutputStream os = new FileOutputStream(file)) {
            final byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            return file;
        } catch (Exception e) {
            log.error("get file error", e);
        }

        return null;
    }


    /**
     * upload File
     *
     * @param url
     * @param file
     * @param paramsMap
     * @param headers
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T> T uploadFile(
            final String url,
            final File file,
            final Map<String, Object> paramsMap,
            final Map<String, String> headers,
            final Class<T> clazz) {

        if (file == null) {
            log.error("Upload file cannot be empty");
            return null;
        }

        return uploadFile(url, Lists.newArrayList(file), paramsMap, headers, clazz);
    }

    /**
     * batch Upload file
     *
     * @param url
     * @param files
     * @param paramsMap
     * @param headers
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T> T uploadFile(
            final String url,
            final List<File> files,
            final Map<String, Object> paramsMap,
            Map<String, String> headers,
            final Class<T> clazz) {

        log.info(">>>> FileUploadUtil.uploadFile params: url={}, paramsMap={}, "
                        + "headerMap={}, clazz={}", url, JsonUtil.toJson(paramsMap),
                JsonUtil.toJson(headers), clazz);

        if (CollectionUtils.isEmpty(files)) {
            log.error("Upload file cannot be empty");
            return null;
        }

        final long beginTime = System.currentTimeMillis();
        final MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        for (int i = 0, size = files.size(); i < size; i++) {
            final File file = files.get(i);
            multipartBody.addFormDataPart("multipart", file.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream;"), file));
        }

        if (MapUtils.isNotEmpty(paramsMap)) {
            for (final Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey())) {
                    multipartBody.addFormDataPart(entry.getKey().trim(),
                            Objects.nonNull(entry.getValue()) ? entry.getValue().toString() : null);
                }
            }
        }

        if (MapUtils.isEmpty(headers)) {
            headers = Maps.newHashMap();
        }

        headers.put(HttpHeaders.CACHE_CONTROL, "no-cache");
        headers.put(HttpHeaders.CONTENT_TYPE, "application/octet-stream;");
        final String traceId = MDC.get(LogConstant.TRACE_ID);
        headers.put(LogConstant.TRACE_ID, StringUtils.isNotBlank(traceId) ? traceId :
                UUID.randomUUID().toString().replaceAll("-", ""));

        final Request request = new Request.Builder().headers(Headers.of(headers)).url(url)
                .post(multipartBody.build()).build();

        final T result = JsonUtil.toBean(OkHttpClientUtil.execute(request), clazz);
        log.info(">>>> FileUploadUtil.uploadFile result: url={}, paramsMap={}, "
                        + "headerMap={}, clazz={}, time={}ms", url, JsonUtil.toJson(paramsMap),
                JsonUtil.toJson(headers), clazz, (System.currentTimeMillis() - beginTime));
        return result;
    }

    /**
     * upload file (MultipartFile file)
     *
     * @param url
     * @param file
     * @param paramsMap
     * @param headers
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T> T uploadMultipartFile(
            final String url,
            final MultipartFile file,
            final Map<String, Object> paramsMap,
            final Map<String, String> headers,
            final Class<T> clazz)
            throws IOException {

        if (file == null) {
            log.error("Upload file cannot be empty");
            return null;
        }

        return uploadMultipartFile(url, Lists.newArrayList(file), paramsMap, headers, clazz);
    }

    /**
     * batch upload file(MultipartFile file)
     *
     * @param url
     * @param files
     * @param paramsMap
     * @param headers
     * @param clazz
     * @param <T>
     *
     * @return
     */
    public static <T> T uploadMultipartFile(
            final String url,
            final List<MultipartFile> files,
            final Map<String, Object> paramsMap,
            Map<String, String> headers,
            final Class<T> clazz) throws IOException {

        log.info(">>>> FileUploadUtil.uploadMultipartFile params: url={}, paramsMap={}, "
                        + "headerMap={}, clazz={}", url, JsonUtil.toJson(paramsMap),
                JsonUtil.toJson(headers), clazz);
        final List<File> localTempFileList = Lists.newArrayList();
        try {
            if (CollectionUtils.isEmpty(files)) {
                log.error("Upload file cannot be empty");
            }

            final long beginTime = System.currentTimeMillis();
            final MultipartBody.Builder multipartBody = new MultipartBody.Builder();
            multipartBody.setType(MultipartBody.FORM);
            for (int i = 0, size = files.size(); i < size; i++) {
                final MultipartFile mf = files.get(i);

                final String fileName = mf.getOriginalFilename();
                final File fileDic = new File(DEFAULT_UPLOAD_TEMP_PATH);
                if (!fileDic.exists()) {
                    fileDic.mkdirs();
                }

                final String filePath =
                        fileDic.getPath() + File.separator + buildFileName() + "-" + fileName;
                final File localTempFile = new File(filePath);
                mf.transferTo(localTempFile);
                localTempFileList.add(localTempFile);

                multipartBody.addFormDataPart("multipart", fileName,
                        RequestBody.create(MediaType.parse("application/octet-stream;"),
                                localTempFile));
            }

            if (MapUtils.isNotEmpty(paramsMap)) {
                final Iterator iterator = paramsMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, String> entry =
                            (Map.Entry<String, String>) iterator.next();
                    multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }

            if (MapUtils.isEmpty(headers)) {
                headers = Maps.newHashMap();
            }
            headers.put(HttpHeaders.CACHE_CONTROL, "no-cache");
            headers.put(HttpHeaders.CONTENT_TYPE, "application/octet-stream;");
            final String traceId = MDC.get(LogConstant.TRACE_ID);
            headers.put(LogConstant.TRACE_ID, StringUtils.isNotBlank(traceId) ? traceId :
                    UUID.randomUUID().toString().replaceAll("-", ""));

            final Request request = new Request.Builder()
                    .headers(Headers.of(headers))
                    .url(url)
                    .post(multipartBody.build())
                    .build();

            final T result = JsonUtil.toBean(OkHttpClientUtil.execute(request), clazz);
            log.info(">>>> FileUploadUtil.uploadMultipartFile result: url={}, "
                            + "paramsMap={}, headerMap={}, clazz={}, time={}ms", url,
                    JsonUtil.toJson(paramsMap), JsonUtil.toJson(headers), clazz,
                    (System.currentTimeMillis() - beginTime));
            return result;
        } finally {
            if (CollectionUtils.isNotEmpty(localTempFileList)) {
                for (final File file : localTempFileList) {
                    if (file != null) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 文件名仅支持：字母, 数字, 中划线, 下划线, 中文, . 空格;
     *
     * @param fileName
     *
     * @return
     */
    public static boolean checkFileNameAllowed(final String fileName) {
        return checkFileNameAllowed(fileName, VALID_FILENAME_PATTERN);
    }

    /**
     * 文件名仅支持：字母, 数字, 中划线, 下划线, 中文, . 空格;
     *
     * @param fileName
     *
     * @return
     */
    public static boolean checkFileNameAllowed(
            final String fileName,
            final Pattern pattern) {

        if (StringUtils.isBlank(fileName)) {
            return false;
        }

        return Optional.ofNullable(pattern).orElse(VALID_FILENAME_PATTERN)
                .matcher(fileName.trim()).matches();
    }


    @SneakyThrows
    public static boolean checkFileTypeAllowed(final MultipartFile file) {
        return checkFileTypeAllowed(file, DEFAULT_ALLOWED_EXTENSIONS, DEFAULT_ALLOWED_FILE_TYPES);
    }

    @SneakyThrows
    public static boolean checkFileTypeAllowed(
            final MultipartFile file,
            List<String> allowedExtensions,
            List<String> allowedFileTypes) {

        if (CollectionUtils.isEmpty(allowedExtensions)) {
            allowedExtensions = DEFAULT_ALLOWED_EXTENSIONS;
        }

        if (CollectionUtils.isEmpty(allowedFileTypes)) {
            allowedFileTypes = DEFAULT_ALLOWED_FILE_TYPES;
        }

        allowedExtensions.forEach(x -> x.toLowerCase());
        allowedFileTypes.forEach(x -> x.toLowerCase());

        final String filename = file.getOriginalFilename();
        final String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            log.error("FileType not allowed filename={}", filename);
            return false;
        }

        // Detecting file types using Apache Tika
        final String detectedType = detectMimeType(file);
        final String contentType = file.getContentType();

        if (allowedFileTypes.contains(contentType.toLowerCase())
                && allowedFileTypes.contains(detectedType.toLowerCase())) {
            return true;
        }

        log.error("FileType not allowed filename={}", filename);
        return false;
    }

    public static String detectMimeType(final MultipartFile file) {
        final Tika tika = new Tika();

        try (final InputStream inputStream = file.getInputStream()) {
            final AutoDetectParser parser = new AutoDetectParser();
            final Metadata metadata = new Metadata();
            final BodyContentHandler handler = new BodyContentHandler();

            parser.parse(inputStream, handler, metadata, new ParseContext());

            final String detailedMimeType = metadata.get(Metadata.CONTENT_TYPE);
            if (detailedMimeType != null) {
                return detailedMimeType;
            } else {
                return tika.detect(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
