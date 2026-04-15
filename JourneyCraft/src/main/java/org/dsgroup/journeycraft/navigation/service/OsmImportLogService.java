package org.dsgroup.journeycraft.navigation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dsgroup.journeycraft.navigation.entity.OsmImportLog;

/**
 * OSM导入日志 Service 接口
 * <p>
 * 简化版：仅继承基础CRUD方法
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
public interface OsmImportLogService extends IService<OsmImportLog> {

    /**
     * 查询最近的导入任务
     *
     * @param limit 返回数量限制
     * @return 导入任务列表
     */
    java.util.List<OsmImportLog> getRecentImportTasks(Integer limit);

    /**
     * 查询导入任务详情
     *
     * @param logId 任务ID
     * @return 导入任务详情
     */
    OsmImportLog getImportTaskDetail(Long logId);

    /**
     * 删除导入日志
     *
     * @param logId 任务ID
     * @return 是否删除成功
     */
    boolean deleteImportLog(Long logId);
}
