package org.dsgroup.journeycraft.navigation.service;

import org.dsgroup.journeycraft.navigation.vo.rspvo.ScenicAccessRspVO;

public interface ScenicAccessService {

    ScenicAccessRspVO getAccessNodes(Long scenicAreaId, Integer maxDistance);
}
