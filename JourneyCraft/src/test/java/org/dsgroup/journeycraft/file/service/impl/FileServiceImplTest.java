package org.dsgroup.journeycraft.file.service.impl;

import org.dsgroup.journeycraft.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 文件服务单元测试。
 */
class FileServiceImplTest {

    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl();
        ReflectionTestUtils.setField(fileService, "endpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(fileService, "bucketName", "journeycraft");
        ReflectionTestUtils.setField(fileService, "accessKey", "minioadmin");
        ReflectionTestUtils.setField(fileService, "secretKey", "minioadmin");
    }

    @Test
    void getFileUrlShouldJoinEndpointBucketAndKey() {
        assertEquals("http://localhost:9000/journeycraft/images/test.png", fileService.getFileUrl("images/test.png"));
    }

    @Test
    void uploadImageShouldRejectEmptyFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(BusinessException.class, () -> fileService.uploadImage(file));
    }
}
