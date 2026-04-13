package org.dsgroup.journeycraft.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建日记数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCreateDTO {

    private String title;
    private String content;
    private List<Long> scenicAreaId;
    private Long tripId;
    private String coverImage;
    private List<String> images;
    private List<String> videos;
    private List<PathNodeDTO> path;
    private List<String> tags;
    private Double rating;
    private String mood;
    private String weather;
    private List<String> companions;
    private Integer status;

    /**
     * 游览路径节点 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathNodeDTO {

        private String nodeName;
        private Long nodeId;
        private String timestamp;
        private Double lat;
        private Double lng;
        private Integer photoCount;
        private List<String> images;
        private List<String> videos;
    }
}
