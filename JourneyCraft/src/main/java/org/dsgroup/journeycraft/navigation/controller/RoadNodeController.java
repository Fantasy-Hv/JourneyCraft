package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.RoadNode;
import org.dsgroup.journeycraft.navigation.service.RoadNodeService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路网节点控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/nodes")
@Tag(name = "导航模块-路网节点", description = "路网节点管理接口")
public class RoadNodeController {

    @Autowired
    private RoadNodeService roadNodeService;

    @GetMapping("/list")
    @Operation(summary = "获取节点列表（分页）")
    public Response<IPage<RoadNode>> listNodes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<RoadNode> page = new Page<>(pageNum, pageSize);
            return Response.ok(roadNodeService.page(page));
        } catch (Exception e) {
            log.error("查询节点列表失败", e);
            return Response.error("查询节点列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取节点详情")
    public Response<RoadNode> getNodeDetail(@PathVariable Long id) {
        try {
            RoadNode node = roadNodeService.getById(id);
            if (node == null) {
                return Response.error("节点不存在");
            }
            return Response.ok(node);
        } catch (Exception e) {
            log.error("查询节点详情失败，ID: {}", id, e);
            return Response.error("查询节点详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/osm/{osmId}")
    @Operation(summary = "根据OSM ID查询节点")
    public Response<RoadNode> getNodeByOsmId(@PathVariable Long osmId) {
        try {
            RoadNode node = roadNodeService.lambdaQuery()
                    .eq(RoadNode::getOsmId, osmId)
                    .one();
            if (node == null) {
                return Response.error("节点不存在");
            }
            return Response.ok(node);
        } catch (Exception e) {
            log.error("根据OSM ID查询节点失败，osmId: {}", osmId, e);
            return Response.error("根据OSM ID查询节点失败: " + e.getMessage());
        }
    }

    @GetMapping("/scenic/{scenicAreaId}")
    @Operation(summary = "查询景区的节点")
    public Response<List<RoadNode>> getNodesByScenicArea(@PathVariable Long scenicAreaId) {
        try {
            List<RoadNode> nodes = roadNodeService.lambdaQuery()
                    .eq(RoadNode::getScenicAreaId, scenicAreaId)
                    .list();
            return Response.ok(nodes);
        } catch (Exception e) {
            log.error("查询景区节点失败，景区ID: {}", scenicAreaId, e);
            return Response.error("查询景区节点失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建节点")
    public Response<RoadNode> createNode(@RequestBody RoadNode node) {
        try {
            roadNodeService.save(node);
            return Response.ok(node);
        } catch (Exception e) {
            log.error("创建节点失败", e);
            return Response.error("创建节点失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新节点")
    public Response<RoadNode> updateNode(@PathVariable Long id, @RequestBody RoadNode node) {
        try {
            node.setId(id);
            roadNodeService.updateById(node);
            return Response.ok(node);
        } catch (Exception e) {
            log.error("更新节点失败，ID: {}", id, e);
            return Response.error("更新节点失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除节点")
    public Response<Void> deleteNode(@PathVariable Long id) {
        try {
            roadNodeService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除节点失败，ID: {}", id, e);
            return Response.error("删除节点失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("节点服务正常，当前总数: " + roadNodeService.count());
        } catch (Exception e) {
            log.error("节点服务异常", e);
            return Response.error("节点服务异常: " + e.getMessage());
        }
    }
}
