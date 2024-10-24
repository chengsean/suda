package org.suda.tika;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.suda.exception.DangerousFileTypeException;
import org.suda.exception.IllegalFileTypeException;
import org.suda.handler.ArgumentHandlerProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * tika包装类，主要用于检测文件类型（MediaType）
 * @author chengshaozhuang
 * @dateTime 2024-09-12 16:53
 */
public class TikaWrapper {
    private final Metadata metadata;
    private final ArgumentHandlerProperties properties;
    private final TikaConfig tikaConfig;

    public TikaWrapper(@NonNull TikaConfig tikaConfig, @NonNull Metadata metadata, @NonNull ArgumentHandlerProperties properties) {
        this.metadata = metadata;
        this.properties = properties;
        this.tikaConfig = tikaConfig;
    }

    public void checkFileType(@Nullable String filename, InputStream inputStream) throws IOException {
        if (filename == null || !filename.contains(".") || inputStream == null) {
            return;
        }
        String extensionName = filename.substring(filename.lastIndexOf("."));
        if (extensionName.equals(".")) {
            return;
        }
        // 检查文件类型（扩展名）黑名单
        String[] blacklist = properties.getFiles().getExtensionBlacklist();
        List<String> blackList = Arrays.asList(Optional.ofNullable(blacklist).orElse(new String[0]));
        if (blackList.contains(extensionName)) {
            String fileExtensions = String.join(",", blackList);
            throw new DangerousFileTypeException("Oops!please note that '"+filename+"' is a security risk to the system, " +
                    "so do not import file types such as '"+fileExtensions+"'");
        }
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, extensionName);
        MimeTypes mimeRepository = tikaConfig.getMimeRepository();
        MediaType mediaType;
        // 转换为支持mark和reset方法的输入流：BufferedInputStream
        try (TikaInputStream bis = TikaInputStream.get(inputStream)) {
            // 根据文件Magic获取的MimeType
            mediaType = mimeRepository.detect(bis, metadata);
        }
        if (mediaType.equals(MediaType.OCTET_STREAM)) {
            return;
        }
        // 根据文件扩展名获取的MimeType
        MediaType mediaTypeByExtension = mimeRepository.detect(null, metadata);
        // 检查文件扩展名是否被篡改
        if (!mediaType.equals(mediaTypeByExtension)) {
            throw new IllegalFileTypeException("Oops! please note that '"+filename+"' extension has been tampered with");
        }
    }

}
