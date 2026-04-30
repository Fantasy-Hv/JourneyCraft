package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RouteCache;
import org.dsgroup.journeycraft.navigation.service.RouteCacheService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径规划缓存控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/route-cache")
@Tag(name = "导航模块-路径缓存", description = "路径规划缓存管理接口")
public class RouteCacheController {

    @Autowired
    private RouteCacheService routeCacheService;

    @GetMapping("/list")
    @Operation(summary = "获取缓存列表（分页）")
    public Response<IPage<RouteCache>> listCaches(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<RouteCache> page = new Page<>(pageNum, pageSize);
            return Response.ok(routeCacheService.page(page));
        } catch (Exception e) {
            log.error("查询缓存列表失败", e);
            return Response.error("查询缓存列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取缓存详情")
    public Response<RouteCache> getCacheDetail(@PathVariable Long id) {
        try {
            RouteCache cache = routeCacheService.getById(id);
            if (cache == null) {
                return Response.error("缓存不存在");
            }
            return Response.ok(cache);
        } catch (Exception e) {
            log.error("查询缓存详情失败，ID: {}", id, e);
            return Response.error("查询缓存详情失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建缓存")
    public Response<RouteCache> createCache(@RequestBody RouteCache cache) {
        try {
            routeCacheService.save(cache);
            return Response.ok(cache);
        } catch (Exception e) {
            log.error("创建缓存失败", e);
            return Response.error("创建缓存失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除缓存")
    public Response<Void> deleteCache(@PathVariable Long id) {
        try {
            routeCacheService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除缓存失败，ID: {}", id, e);
            return Response.error("删除缓存失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("缓存服务正常，当前总数: " + routeCacheService.count());
        } catch (Exception e) {
            log.error("缓存服务异常", e);
            return Response.error("缓存服务异常: " + e.getMessage());
        }
    }
}
