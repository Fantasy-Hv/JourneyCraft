package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

@Data
public class ScenicAccessRspVO {

    private Long scenicAreaId;
    private String scenicAreaName;
    private CoordinateVO scenicCenter;
    private AccessNodesVO accessNodes;

    @Data
    public static class CoordinateVO {
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class AccessNodesVO {
        private List<AccessNodeVO> walk;
        private List<AccessNodeVO> bike;
        private List<AccessNodeVO> shuttle;
    }
}
