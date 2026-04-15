package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadEdge;
import org.dsgroup.journeycraft.navigation.service.RoadEdgeService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径段控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/edges")
@Tag(name = "导航模块-路径段", description = "路径段管理接口")
public class RoadEdgeController {

    @Autowired
    private RoadEdgeService roadEdgeService;

    @GetMapping("/list")
    @Operation(summary = "获取路径段列表（分页）")
    public Response<IPage<RoadEdge>> listEdges(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<RoadEdge> page = new Page<>(pageNum, pageSize);
            return Response.ok(roadEdgeService.page(page));
        } catch (Exception e) {
            log.error("查询路径段列表失败", e);
            return Response.error("查询路径段列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取路径段详情")
    public Response<RoadEdge> getEdgeDetail(@PathVariable Long id) {
        try {
            RoadEdge edge = roadEdgeService.getById(id);
            if (edge == null) {
                return Response.error("路径段不存在");
            }
            return Response.ok(edge);
        } catch (Exception e) {
            log.error("查询路径段详情失败，ID: {}", id, e);
            return Response.error("查询路径段详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/node/{fromNodeId}")
    @Operation(summary = "查询起始节点的路径段")
    public Response<List<RoadEdge>> getEdgesByFromNode(@PathVariable Long fromNodeId) {
        try {
            List<RoadEdge> edges = roadEdgeService.lambdaQuery()
                    .eq(RoadEdge::getFromNodeId, fromNodeId)
                    .list();
            return Response.ok(edges);
        } catch (Exception e) {
            log.error("查询路径段失败，起点ID: {}", fromNodeId, e);
            return Response.error("查询路径段失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建路径段")
    public Response<RoadEdge> createEdge(@RequestBody RoadEdge edge) {
        try {
            roadEdgeService.save(edge);
            return Response.ok(edge);
        } catch (Exception e) {
            log.error("创建路径段失败", e);
            return Response.error("创建路径段失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新路径段")
    public Response<RoadEdge> updateEdge(@PathVariable Long id, @RequestBody RoadEdge edge) {
        try {
            edge.setId(id);
            roadEdgeService.updateById(edge);
            return Response.ok(edge);
        } catch (Exception e) {
            log.error("更新路径段失败，ID: {}", id, e);
            return Response.error("更新路径段失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除路径段")
    public Response<Void> deleteEdge(@PathVariable Long id) {
        try {
            roadEdgeService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除路径段失败，ID: {}", id, e);
            return Response.error("删除路径段失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("路径段服务正常，当前总数: " + roadEdgeService.count());
        } catch (Exception e) {
            log.error("路径段服务异常", e);
            return Response.error("路径段服务异常: " + e.getMessage());
        }
    }
}
