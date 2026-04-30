package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.CrowdLevel;

import java.util.List;

/**
 * 拥挤度记录 Service 接口
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface CrowdLevelService extends IService<CrowdLevel> {

    /**
     * 根据节点ID查询拥挤度记录
     *
     * @param nodeId 节点ID
     * @return 拥挤度记录列表
     */
    List<CrowdLevel> getCrowdLevelsByNode(Long nodeId);

    /**
     * 查询景区的最新拥挤度
     *
     * @param scenicAreaId 景区ID
     * @return 拥挤度记录列表
     */
    List<CrowdLevel> getLatestCrowdLevelsByScenicArea(Long scenicAreaId);
}
