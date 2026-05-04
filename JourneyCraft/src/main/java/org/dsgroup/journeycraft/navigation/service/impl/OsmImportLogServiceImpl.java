package org.dsgroup.journeycraft.navigation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.OsmImportLog;
import org.dsgroup.journeycraft.navigation.mapper.OsmImportLogMapper;
import org.dsgroup.journeycraft.navigation.service.OsmImportLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OSM导入日志 Service 实现类
 * <p>
 * 简化版：仅保留基础CRUD，复杂统计逻辑暂不实现
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@Service
public class OsmImportLogServiceImpl extends ServiceImpl<OsmImportLogMapper, OsmImportLog> implements OsmImportLogService {

    @Override
    public List<OsmImportLog> getRecentImportTasks(Integer limit) {
        log.debug("查询最近的导入任务，限制 {} 条", limit);
        return lambdaQuery()
                .orderByDesc(OsmImportLog::getStartedAt)
                .last("LIMIT " + (limit != null ? limit : 10))
                .list();
    }

    @Override
    public OsmImportLog getImportTaskDetail(Long logId) {
        log.debug("查询导入任务详情: logId={}", logId);
        return getById(logId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteImportLog(Long logId) {
        log.debug("删除导入日志: logId={}", logId);
        return removeById(logId);
    }
}
