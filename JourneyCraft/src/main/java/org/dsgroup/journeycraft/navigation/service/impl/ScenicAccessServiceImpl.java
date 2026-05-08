package org.dsgroup.journeycraft.navigation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.entity.ScenicAccess;
import org.dsgroup.journeycraft.navigation.mapper.RoadNodeMapper;
import org.dsgroup.journeycraft.navigation.mapper.ScenicAccessMapper;
import org.dsgroup.journeycraft.navigation.service.ScenicAccessService;
import org.dsgroup.journeycraft.navigation.utils.CoordinateTransformUtil;
import org.dsgroup.journeycraft.navigation.vo.rspvo.AccessNodeVO;
import org.dsgroup.journeycraft.navigation.vo.rspvo.ScenicAccessRspVO;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScenicAccessServiceImpl implements ScenicAccessService {

    @Autowired
    private ScenicAccessMapper scenicAccessMapper;

    @Autowired
    private ScenicService scenicService;

    @Autowired
    private RoadNodeMapper roadNodeMapper;

    @Override
    public ScenicAccessRspVO getAccessNodes(Long scenicAreaId, Integer maxDistance) {
        List<ScenicAccess> accesses = scenicAccessMapper.selectByScenicArea(scenicAreaId);
        List<AccessNodeVO> walkNodes = new ArrayList<>();
        List<AccessNodeVO> bikeNodes = new ArrayList<>();
        List<AccessNodeVO> shuttleNodes = new ArrayList<>();

        for (ScenicAccess access : accesses) {
            RoadNode node = roadNodeMapper.selectById(access.getRoadNodeId());
            if (node == null || !node.isAvailableForNavigation()) {
                continue;
            }

            // Filter by maxDistance
            if (maxDistance != null && access.getDistanceToScenic() != null
                    && access.getDistanceToScenic().doubleValue() > maxDistance) {
                continue;
            }

            CoordinateTransformUtil.Gcj02Coord gcj = CoordinateTransformUtil.wgs84ToGcj02(
                    node.getLatitude().doubleValue(), node.getLongitude().doubleValue());

            AccessNodeVO vo = new AccessNodeVO();
            vo.setNodeId(node.getId());
            vo.setOsmId(node.getOsmId());
            vo.setName(node.getName());
            vo.setNodeType(node.getNodeType());
            vo.setLatitude(gcj.lat());
            vo.setLongitude(gcj.lng());
            vo.setDistance(access.getDistanceToScenic() != null
                    ? access.getDistanceToScenic().doubleValue() : null);
            vo.setIsPrimary(access.getIsPrimary() != null && access.getIsPrimary() == 1);
            vo.setViaWalk(false);

            Integer accessType = access.getAccessType();
            if (accessType == null) {
                continue;
            }
            switch (accessType) {
                case 1:
                    walkNodes.add(vo);
                    break;
                case 2:
                    bikeNodes.add(vo);
                    break;
                case 3:
                    shuttleNodes.add(vo);
                    break;
                default:
                    break;
            }
        }

        ScenicAccessRspVO.AccessNodesVO accessNodes = new ScenicAccessRspVO.AccessNodesVO();
        accessNodes.setWalk(walkNodes);
        accessNodes.setBike(bikeNodes);
        accessNodes.setShuttle(shuttleNodes);

        ScenicAccessRspVO rspVO = new ScenicAccessRspVO();
        rspVO.setScenicAreaId(scenicAreaId);
        rspVO.setAccessNodes(accessNodes);
        return rspVO;
    }
}
