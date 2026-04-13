package org.dsgroup.journeycraft.file.vo.rspvo;

import lombok.Data;

/**
 * 文件上传结果数据。
 */
@Data
public class FileUploadRspVO {

    /** 文件访问地址。 */
    private String url;

    /** 文件存储 key。 */
    private String key;

    /** 文件大小，单位字节。 */
    private Integer size;
}
