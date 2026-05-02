package org.dsgroup.journeycraft.scenic.api;

import org.dsgroup.journeycraft.scenic.vo.reqvo.BuildingListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.FacilityListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ReportCrowdReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicSearchReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.BuildingRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.FacilityRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicItemRspVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.dsgroup.journeycraft.scenic.entity.CrowdLevel;

import java.util.List;

/**
 * 景点模块对外服务接口。
 */
public interface ScenicService {

    /**
     * 获取景点列表。
     */
    ScenicListRspVO listScenic(ScenicListReqVO reqVO);

    /**
     * 按关键词搜索景点。
     */
    ScenicListRspVO searchScenic(ScenicSearchReqVO reqVO);

    /**
     * 获取景点详情。
     */
    ScenicItemRspVO getScenicDetail(Long scenicId);

    /**
     * 获取景点下建筑物列表。
     */
    List<BuildingRspVO> listBuildings(Long scenicId, BuildingListReqVO reqVO);

    /**
     * 获取景点下设施列表。
     */
    List<FacilityRspVO> listFacilities(Long scenicId, FacilityListReqVO reqVO);

    /**
     * 上报节点拥挤度。
     */
    void reportCrowd(Long scenicId, ReportCrowdReqVO reqVO);

    /**
     * 获取景区各节点最新拥挤度记录，供 navigation 模块聚合查询。
     */
    List<CrowdLevel> getCrowdLevelsByScenicArea(Long scenicId);

    /**
     * 获取校园列表。
     */
    ScenicListRspVO listCampus(ScenicListReqVO reqVO);
}
