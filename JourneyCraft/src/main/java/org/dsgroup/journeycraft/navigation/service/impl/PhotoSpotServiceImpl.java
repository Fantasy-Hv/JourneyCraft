package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.PhotoSpot;
import org.dsgroup.journeycraft.navigation.mapper.PhotoSpotMapper;
import org.dsgroup.journeycraft.navigation.service.PhotoSpotService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拍照点 Service 实现类
 * <p>
 * 简化版：仅保留基础CRUD
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class PhotoSpotServiceImpl extends ServiceImpl<PhotoSpotMapper, PhotoSpot> implements PhotoSpotService {

    @Override
    public List<PhotoSpot> getPhotoSpotsByScenicArea(Long scenicAreaId) {
        log.debug("查询景区 {} 的拍照点", scenicAreaId);
        return lambdaQuery()
                .eq(PhotoSpot::getScenicAreaId, scenicAreaId)
                .orderByDesc(PhotoSpot::getRating)
                .list();
    }

    @Override
    public List<PhotoSpot> getHighRatedPhotoSpots() {
        log.debug("获取高评分拍照点");
        return lambdaQuery()
                .gt(PhotoSpot::getRating, 0)
                .orderByDesc(PhotoSpot::getRating)
                .last("LIMIT 20")
                .list();
    }
}
