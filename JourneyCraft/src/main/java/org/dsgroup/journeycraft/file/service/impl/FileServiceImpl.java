package org.dsgroup.journeycraft.file.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.dsgroup.journeycraft.file.api.FileService;
import org.dsgroup.journeycraft.file.vo.rspvo.FileUploadRspVO;
import org.dsgroup.journeycraft.common.enums.ResponseCodeEnum;
import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件服务实现，负责 MinIO 上传与删除。
 */
@Service
public class FileServiceImpl implements FileService {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.bucket-name:journeycraft}")
    private String bucketName;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    /**
     * 上传图片文件。
     */
    @Override
    public FileUploadRspVO uploadImage(MultipartFile file) {
        return buildUploadResponse(file, "images");
    }

    /**
     * 上传视频文件。
     */
    @Override
    public FileUploadRspVO uploadVideo(MultipartFile file) {
        return buildUploadResponse(file, "videos");
    }

    /**
     * 生成文件访问 URL。
     */
    @Override
    public String getFileUrl(String key) {
        String base = endpoint.endsWith("/") ? endpoint.substring(0, endpoint.length() - 1) : endpoint;
        return base + "/" + bucketName + "/" + key;
    }

    /**
     * 删除对象存储中的文件。
     */
    @Override
    public void deleteFile(String key) {
        try {
            MinioClient client = buildClient();
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "删除文件失败");
        }
    }

    /**
     * 统一上传流程并组装响应。
     */
    private FileUploadRspVO buildUploadResponse(MultipartFile file, String dir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST, "文件不能为空");
        }
        String safeName = file == null ? "unknown" : file.getOriginalFilename();
        String key = dir + "/" + UUID.randomUUID() + "-" + safeName;
        uploadToMinio(file, key);

        FileUploadRspVO rspVO = new FileUploadRspVO();
        rspVO.setKey(key);
        rspVO.setUrl(getFileUrl(key));
        rspVO.setSize(toIntSize(file));
        return rspVO;
    }

    /**
     * 文件大小转换为 Integer。
     */
    private Integer toIntSize(MultipartFile file) {
        if (file == null) {
            return 0;
        }
        long size = file.getSize();
        return size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size;
    }

    /**
     * 上传文件到 MinIO。
     */
    private void uploadToMinio(MultipartFile file, String key) {
        try (InputStream inputStream = file.getInputStream()) {
            MinioClient client = buildClient();
            ensureBucketExists(client);
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(key)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new BusinessException(ResponseCodeEnum.SYSTEM_ERROR, "上传文件失败");
        }
    }

    /**
     * 创建 MinIO 客户端。
     */
    private MinioClient buildClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 确保 bucket 存在，不存在则创建。
     */
    private void ensureBucketExists(MinioClient client) throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}
