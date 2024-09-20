package org.suda.util;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.suda.config.SudaProperties;
import org.suda.exception.DangerousFileTypeException;
import org.suda.exception.IllegalFileTypeException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * tika包装类，主要用于检测文件类型（MediaType）
 * @author chengshaozhuang
 * @dateTime 2024-09-12 16:53
 */
public class TikaWrapper {
    private final Metadata metadata;
    private final SudaProperties properties;
    private final Map<MediaType, Set<String>> fileTypeBlacklist;
    private final TikaConfig tikaConfig;

    public TikaWrapper(@NonNull TikaConfig tikaConfig, @NonNull Metadata metadata, @NonNull SudaProperties properties) {
        this.metadata = metadata;
        this.properties = properties;
        this.tikaConfig = tikaConfig;
        this.fileTypeBlacklist = initFileTypeBlacklist();
    }

    private Map<MediaType, Set<String>> initFileTypeBlacklist() {
        Map<MediaType, Set<String>> fileTypeBlacklist = new HashMap<>();
        // 添加文件扩展名黑名单
        String[] extensionNameBlacklist = properties.getFiles().getExtensionBlacklist();
        for (String extensionName : extensionNameBlacklist) {
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, extensionName);
            try {
                MediaType mediaType = tikaConfig.getMimeRepository().detect(null, metadata);
                // 忽略未知文件类型
                if (mediaType.equals(MediaType.OCTET_STREAM)) {
                    continue;
                }
                Set<String> list = fileTypeBlacklist.get(mediaType);
                // 处理一个媒体类型对应多个文件类型的情况
                if (list == null) {
                    fileTypeBlacklist.put(mediaType, new HashSet<>(Collections.singletonList(extensionName)));
                } else {
                    list.add(extensionName);
                    fileTypeBlacklist.put(mediaType, list);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileTypeBlacklist;
    }

    public void checkFileType(@Nullable String filename, InputStream inputStream) throws IOException {
        if (filename == null || !filename.contains(".") || inputStream == null) {
            return;
        }
        String extensionName = filename.substring(filename.lastIndexOf("."));
        if (extensionName.equals(".")) {
            return;
        }
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, extensionName);
        MimeTypes mimeRepository = tikaConfig.getMimeRepository();
        // 1.输入流拷贝
        MediaType mediaType;
        // 转换为支持mark和reset方法的输入流：BufferedInputStream
        try (TikaInputStream bis = TikaInputStream.get(inputStream)) {
            // 2.根据Magic获取的MimeType
            mediaType = mimeRepository.detect(bis, metadata);
        }
        if (mediaType.equals(MediaType.OCTET_STREAM)) {
            return;
        }
        // 3.根据文件扩展名获取的MimeType
        MediaType mediaTypeByExtension = mimeRepository.detect(null, metadata);
        // 检查文件扩展名是否被篡改
        if (!mediaType.equals(mediaTypeByExtension)) {
            throw new IllegalFileTypeException("Oops! please note that '"+filename+"' extension has been tampered with");
        }
        // 检查文件类型黑名单
        Set<String> blackList = Optional.ofNullable(fileTypeBlacklist.get(mediaType)).orElse(new HashSet<>());
        if (blackList.contains(extensionName)) {
            String fileExtensions = String.join(",", toStringList(fileTypeBlacklist.values()));
            throw new DangerousFileTypeException("Oops!please note that '"+filename+"' is a security risk to the system, " +
                    "so do not import file types such as '"+fileExtensions+"'");
        }
    }

    private Set<String> toStringList(Collection<Set<String>> values) {
        Set<String> list = new HashSet<>();
        values.forEach(list::addAll);
        return list;
    }
}
