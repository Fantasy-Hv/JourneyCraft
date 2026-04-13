package org.dsgroup.journeycraft.file.vo.rspvo;

import lombok.Data;

/**
 * 文件上传响应数据。
 */
@Data
public class FileUploadRspVO {

    private String url;

    private String key;

    private Integer size;
}
