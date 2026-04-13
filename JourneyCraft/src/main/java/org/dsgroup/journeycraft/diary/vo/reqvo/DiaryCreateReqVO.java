package org.dsgroup.journeycraft.diary.vo.reqvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建日记请求 VO
 */
@Data
@Schema(description = "创建日记请求")
public class DiaryCreateReqVO {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "故宫一日游")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "内容 (Markdown 格式)", requiredMode = Schema.RequiredMode.REQUIRED, example = "# 故宫游记\n\n今天天气很好...")
    private String content;

    @Schema(description = "关联景区 ID 列表", example = "[1, 2]")
    private List<Long> scenicAreaId;

    @Schema(description = "关联行程 ID", example = "1")
    private Long tripId;

    @Schema(description = "封面图 URL", example = "http://localhost:9000/journeycraft/diary/cover_001.jpg")
    private String coverImage;

    @Schema(description = "图片 URL 列表", example = "[\"http://localhost:9000/journeycraft/diary/img_001.jpg\"]")
    private List<String> images;

    @Schema(description = "视频 URL 列表")
    private List<String> videos;

    @Schema(description = "游览路径")
    private List<PathNodeReqVO> path;

    @Schema(description = "标签", example = "[\"历史\", \"建筑\", \"拍照\"]")
    private List<String> tags;

    @Schema(description = "评分 0-5", example = "4.9")
    private Double rating;

    @Schema(description = "心情", example = "happy")
    private String mood;

    @Schema(description = "天气", example = "sunny")
    private String weather;

    @Schema(description = "同行人", example = "[\"张三\", \"李四\"]")
    private List<String> companions;

    @Schema(description = "可见性：0-私密 1-公开", example = "1", defaultValue = "1")
    private Integer status;

    /**
     * 游览路径节点请求 VO
     */
    @Data
    @Schema(description = "游览路径节点")
    public static class PathNodeReqVO {

        @Schema(description = "节点名称", example = "太和殿")
        private String nodeName;

        @Schema(description = "节点 ID", example = "20")
        private Long nodeId;

        @Schema(description = "时间戳", example = "2024-04-01T09:30:00")
        private String timestamp;

        @Schema(description = "纬度", example = "39.915")
        private Double lat;

        @Schema(description = "经度", example = "116.397")
        private Double lng;

        @Schema(description = "照片数量", example = "20")
        private Integer photoCount;

        @Schema(description = "节点图片列表")
        private List<String> images;

        @Schema(description = "节点视频列表")
        private List<String> videos;
    }
}
