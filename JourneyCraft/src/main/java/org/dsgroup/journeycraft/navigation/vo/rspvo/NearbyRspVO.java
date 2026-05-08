package org.dsgroup.journeycraft.navigation.vo.rspvo;

import lombok.Data;

import java.util.List;

@Data
public class NearbyRspVO {
    private Long sourceNodeId;
    private List<NearbyFacilityItemVO> facilities;

    @Data
    public static class NearbyFacilityItemVO {
        private Long facilityId;
        private String name;
        private Integer facilityType;
        private String subtype;
        private Double latitude;
        private Double longitude;
        private Double roadDistance;
        private Double straightDistance;
        private Integer walkTime;
        private Double rating;
    }
}
