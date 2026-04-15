package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.IndoorFloor;

import java.util.List;

/**
 * 室内楼层 Service 接口
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface IndoorFloorService extends IService<IndoorFloor> {

    /**
     * 根据建筑ID查询楼层列表
     *
     * @param buildingId 建筑ID
     * @return 楼层列表
     */
    List<IndoorFloor> getFloorsByBuilding(Long buildingId);

    /**
     * 根据楼层号查询楼层信息
     *
     * @param buildingId 建筑ID
     * @param floorNumber 楼层号
     * @return 楼层信息
     */
    IndoorFloor getFloorByNumber(Long buildingId, Integer floorNumber);
}
