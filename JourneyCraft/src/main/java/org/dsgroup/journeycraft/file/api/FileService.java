package org.dsgroup.journeycraft.file.api;

import org.dsgroup.journeycraft.file.vo.rspvo.FileUploadRspVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件模块对外服务接口。
 */
public interface FileService {

    /**
     * 上传图片文件。
     */
    FileUploadRspVO uploadImage(MultipartFile file);

    /**
     * 上传视频文件。
     */
    FileUploadRspVO uploadVideo(MultipartFile file);

    /**
     * 获取文件访问地址。
     */
    String getFileUrl(String key);

    /**
     * 删除指定文件。
     */
    void deleteFile(String key);
}
