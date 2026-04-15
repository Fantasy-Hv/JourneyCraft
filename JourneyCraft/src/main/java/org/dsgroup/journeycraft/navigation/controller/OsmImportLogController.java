package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.OsmImportLog;
import org.dsgroup.journeycraft.navigation.service.OsmImportLogService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OSM导入日志控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/osm-import")
@Tag(name = "导航模块-OSM导入日志", description = "OSM数据导入日志管理接口")
public class OsmImportLogController {

    @Autowired
    private OsmImportLogService osmImportLogService;

    @GetMapping("/list")
    @Operation(summary = "获取导入日志列表（分页）")
    public Response<IPage<OsmImportLog>> listLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<OsmImportLog> page = new Page<>(pageNum, pageSize);
            return Response.ok(osmImportLogService.page(page));
        } catch (Exception e) {
            log.error("查询导入日志列表失败", e);
            return Response.error("查询导入日志列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取导入日志详情")
    public Response<OsmImportLog> getLogDetail(@PathVariable Long id) {
        try {
            OsmImportLog log = osmImportLogService.getById(id);
            if (log == null) {
                return Response.error("导入日志不存在");
            }
            return Response.ok(log);
        } catch (Exception e) {
            log.error("查询导入日志详情失败，ID: {}", id, e);
            return Response.error("查询导入日志详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最近的导入任务")
    public Response<List<OsmImportLog>> getRecentLogs(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<OsmImportLog> logs = osmImportLogService.lambdaQuery()
                    .orderByDesc(OsmImportLog::getStartedAt)
                    .last("LIMIT " + limit)
                    .list();
            return Response.ok(logs);
        } catch (Exception e) {
            log.error("查询最近导入任务失败", e);
            return Response.error("查询最近导入任务失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除导入日志")
    public Response<Void> deleteLog(@PathVariable Long id) {
        try {
            osmImportLogService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除导入日志失败，ID: {}", id, e);
            return Response.error("删除导入日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("OSM导入服务正常，当前总数: " + osmImportLogService.count());
        } catch (Exception e) {
            log.error("OSM导入服务异常", e);
            return Response.error("OSM导入服务异常: " + e.getMessage());
        }
    }
}
