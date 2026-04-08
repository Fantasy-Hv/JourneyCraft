package org.dsgroup.journeycraft.file.api;

import io.swagger.v3.oas.models.security.SecurityScheme;

import java.io.InputStream;

/**
 * 这里定义暴露给其他模块调用的方法
    然后在service包的impl包下写实现
 */
public interface FileService {
    /**
     * 这个是示例，仅供参考
     * 上传文件到Oss存储，在本项目中是MinIo，
     * @return 返回是否上传成功
     */
    boolean uploadFile(InputStream fileData);
}
