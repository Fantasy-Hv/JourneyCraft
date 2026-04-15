package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.IndoorFloor;
import org.dsgroup.journeycraft.navigation.mapper.IndoorFloorMapper;
import org.dsgroup.journeycraft.navigation.service.IndoorFloorService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 室内楼层 Service 实现类
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class IndoorFloorServiceImpl extends ServiceImpl<IndoorFloorMapper, IndoorFloor> implements IndoorFloorService {

    @Override
    public List<IndoorFloor> getFloorsByBuilding(Long buildingId) {
        log.debug("查询建筑 {} 的楼层列表", buildingId);
        return lambdaQuery()
                .eq(IndoorFloor::getBuildingId, buildingId)
                .orderByAsc(IndoorFloor::getFloorNumber)
                .list();
    }

    @Override
    public IndoorFloor getFloorByNumber(Long buildingId, Integer floorNumber) {
        log.debug("查询建筑 {} 楼层号 {} 的楼层", buildingId, floorNumber);
        return lambdaQuery()
                .eq(IndoorFloor::getBuildingId, buildingId)
                .eq(IndoorFloor::getFloorNumber, floorNumber)
                .one();
    }
}
