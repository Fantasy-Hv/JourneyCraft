package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.mapper.RoadNodeMapper;
import org.dsgroup.journeycraft.navigation.service.RoadNodeService;
import org.springframework.stereotype.Service;

/**
 * 路网节点 Service 实现类
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class RoadNodeServiceImpl extends ServiceImpl<RoadNodeMapper, RoadNode> implements RoadNodeService {
}
