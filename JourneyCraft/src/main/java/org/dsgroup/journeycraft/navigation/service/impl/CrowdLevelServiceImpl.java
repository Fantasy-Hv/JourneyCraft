package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.CrowdLevel;
import org.dsgroup.journeycraft.navigation.mapper.NavigationCrowdLevelMapper;
import org.dsgroup.journeycraft.navigation.service.CrowdLevelService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拥挤度记录 Service 实现类
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class CrowdLevelServiceImpl extends ServiceImpl<NavigationCrowdLevelMapper, CrowdLevel> implements CrowdLevelService {

    @Override
    public List<CrowdLevel> getCrowdLevelsByNode(Long nodeId) {
        log.debug("查询节点 {} 的拥挤度记录", nodeId);
        return lambdaQuery()
                .eq(CrowdLevel::getNodeId, nodeId)
                .orderByDesc(CrowdLevel::getRecordedAt)
                .list();
    }

    @Override
    public List<CrowdLevel> getLatestCrowdLevelsByScenicArea(Long scenicAreaId) {
        log.debug("查询景区 {} 的最新拥挤度", scenicAreaId);
        return lambdaQuery()
                .eq(CrowdLevel::getScenicAreaId, scenicAreaId)
                .orderByDesc(CrowdLevel::getRecordedAt)
                .list();
    }
}