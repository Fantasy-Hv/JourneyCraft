package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.mapper.RoadEdgeMapper;
import org.dsgroup.journeycraft.navigation.service.RoadEdgeService;
import org.springframework.stereotype.Service;

/**
 * 路径段 Service 实现类
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class RoadEdgeServiceImpl extends ServiceImpl<RoadEdgeMapper, RoadEdge> implements RoadEdgeService {
}
