package com.changqing.gov.file.properties;


import com.changqing.gov.file.enumeration.FileStorageType;
import com.changqing.gov.utils.StrPool;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.io.File;


/**
 * @author changqing
 */
@Setter
@Getter
@ConfigurationProperties(prefix = FileServerProperties.PREFIX)
@RefreshScope
public class FileServerProperties {
    public static final String PREFIX = "changqing.file";
    /**
     * 为以下3个值，指定不同的自动化配置
     * qiniu：七牛oss
     * aliyun：阿里云oss
     * fastdfs：本地部署的fastDFS
     */
    private FileStorageType type = FileStorageType.LOCAL;
    /**
     * 文件访问前缀
     */
    private String uriPrefix = "http://127.0.0.1:10000/";
    /**
     * 文件存储路径
     */
    private String storagePath = "/data/projects/uploadfile/file/";
    /**
     * 内网通道前缀 主要用于解决某些服务器的无法访问外网ip的问题
     */
    private String innerUriPrefix = "";
    private String downByUrl = "";
    private String downByBizId = "";
    private String downById = "";

    private Ali ali;

    public String getDownByUrl(Object... param) {
        return String.format(downByUrl, param);
    }

    public String getDownByBizId(Object... param) {
        return String.format(downByBizId, param);
    }

    public String getDownById(Object... param) {
        return String.format(downById, param);
    }

    public String getInnerUriPrefix() {
        return innerUriPrefix;
    }

    public String getUriPrefix() {
        if (!uriPrefix.endsWith(StrPool.SLASH)) {
            uriPrefix += StrPool.SLASH;
        }
        return uriPrefix;
    }

    public String getStoragePath() {
        if (!storagePath.endsWith(File.separator)) {
            storagePath += File.separator;
        }
        return storagePath;
    }

    @Data
    public static class Ali {
        private String uriPrefix;
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
    }
}
