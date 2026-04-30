package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.PhotoSpot;

import java.util.List;

/**
 * 拍照点 Service 接口
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface PhotoSpotService extends IService<PhotoSpot> {

    /**
     * 查询景区下的拍照点
     *
     * @param scenicAreaId 景区ID
     * @return 拍照点列表
     */
    List<PhotoSpot> getPhotoSpotsByScenicArea(Long scenicAreaId);

    /**
     * 获取高评分拍照点
     *
     * @return 拍照点列表
     */
    List<PhotoSpot> getHighRatedPhotoSpots();
}
