package org.dsgroup.journeycraft.file.controller;

import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.file.api.FileService;
import org.dsgroup.journeycraft.file.vo.rspvo.FileUploadRspVO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件模块 HTTP 接口。
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传图片接口。
     */
    @PostMapping("/upload/image")
    public Response<FileUploadRspVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return Response.ok(fileService.uploadImage(file));
    }

    /**
     * 上传视频接口。
     */
    @PostMapping("/upload/video")
    public Response<FileUploadRspVO> uploadVideo(@RequestParam("file") MultipartFile file) {
        return Response.ok(fileService.uploadVideo(file));
    }

    /**
     * 获取文件访问地址并重定向。
     */
    @GetMapping("/{*key}")
    public ResponseEntity<Void> getFile(@PathVariable("key") String key) {
        String normalizedKey = normalizeKey(key);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, fileService.getFileUrl(normalizedKey));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * 删除文件接口。
     */
    @DeleteMapping("/{*key}")
    public Response<Void> deleteFile(@PathVariable("key") String key) {
        fileService.deleteFile(normalizeKey(key));
        return Response.ok();
    }

    /**
     * 归一化路径 key。
     */
    private String normalizeKey(String key) {
        if (key == null || key.isBlank()) {
            return key;
        }
        return key.startsWith("/") ? key.substring(1) : key;
    }
}
