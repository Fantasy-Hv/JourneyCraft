package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.CrowdLevel;
import org.dsgroup.journeycraft.navigation.service.CrowdLevelService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 拥挤度记录控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/crowd-levels")
@Tag(name = "导航模块-拥挤度", description = "拥挤度监控和查询接口")
public class CrowdLevelController {

    @Autowired
    private CrowdLevelService crowdLevelService;

    /**
     * 获取拥挤度记录列表（分页）
     */
    @GetMapping("/list")
    @Operation(summary = "获取拥挤度记录列表（分页）", description = "分页查询拥挤度记录列表")
    public Response<IPage<CrowdLevel>> listCrowdLevels(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.info("查询拥挤度记录列表，页码: {}, 每页数量: {}", pageNum, pageSize);
            Page<CrowdLevel> page = new Page<>(pageNum, pageSize);
            IPage<CrowdLevel> result = crowdLevelService.page(page);
            return Response.ok(result);
        } catch (Exception e) {
            log.error("查询拥挤度记录列表失败", e);
            return Response.error("查询拥挤度记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取拥挤度记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取拥挤度记录详情", description = "根据ID获取拥挤度记录详细信息")
    public Response<CrowdLevel> getCrowdLevelDetail(
            @Parameter(description = "拥挤度记录ID", required = true) @PathVariable Long id) {
        try {
            log.info("查询拥挤度记录详情，ID: {}", id);
            CrowdLevel crowdLevel = crowdLevelService.getById(id);
            if (crowdLevel == null) {
                return Response.error("拥挤度记录不存在");
            }
            return Response.ok(crowdLevel);
        } catch (Exception e) {
            log.error("查询拥挤度记录详情失败，ID: {}", id, e);
            return Response.error("查询拥挤度记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据节点ID查询拥挤度记录
     */
    @GetMapping("/node/{nodeId}")
    @Operation(summary = "根据节点ID查询拥挤度记录", description = "查询指定节点的所有拥挤度记录")
    public Response<List<CrowdLevel>> getCrowdLevelsByNode(
            @Parameter(description = "节点ID", required = true) @PathVariable Long nodeId) {
        try {
            log.info("查询节点 {} 的拥挤度记录", nodeId);
            List<CrowdLevel> crowdLevels = crowdLevelService.lambdaQuery()
                    .eq(CrowdLevel::getNodeId, nodeId)
                    .orderByDesc(CrowdLevel::getRecordedAt)
                    .list();
            return Response.ok(crowdLevels);
        } catch (Exception e) {
            log.error("查询节点拥挤度记录失败，节点ID: {}", nodeId, e);
            return Response.error("查询节点拥挤度记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询景区的拥挤度记录
     */
    @GetMapping("/scenic/{scenicAreaId}")
    @Operation(summary = "查询景区的拥挤度记录", description = "查询指定景区的拥挤度记录")
    public Response<List<CrowdLevel>> getCrowdLevelsByScenicArea(
            @Parameter(description = "景区ID", required = true) @PathVariable Long scenicAreaId) {
        try {
            log.info("查询景区 {} 的拥挤度记录", scenicAreaId);
            List<CrowdLevel> crowdLevels = crowdLevelService.lambdaQuery()
                    .eq(CrowdLevel::getScenicAreaId, scenicAreaId)
                    .orderByDesc(CrowdLevel::getRecordedAt)
                    .list();
            return Response.ok(crowdLevels);
        } catch (Exception e) {
            log.error("查询景区拥挤度记录失败，景区ID: {}", scenicAreaId, e);
            return Response.error("查询景区拥挤度记录失败: " + e.getMessage());
        }
    }

    /**
     * 记录拥挤度数据
     */
    @PostMapping
    @Operation(summary = "记录拥挤度数据", description = "记录新的拥挤度数据")
    public Response<CrowdLevel> recordCrowdLevel(
            @Parameter(description = "拥挤度记录", required = true) @RequestBody CrowdLevel crowdLevel) {
        try {
            log.info("记录拥挤度数据: {}", crowdLevel);
            crowdLevelService.save(crowdLevel);
            return Response.ok(crowdLevel);
        } catch (Exception e) {
            log.error("记录拥挤度数据失败", e);
            return Response.error("记录拥挤度数据失败: " + e.getMessage());
        }
    }

    /**
     * 删除拥挤度记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除拥挤度记录", description = "删除指定的拥挤度记录")
    public Response<Void> deleteCrowdLevel(
            @Parameter(description = "拥挤度记录ID", required = true) @PathVariable Long id) {
        try {
            log.info("删除拥挤度记录，ID: {}", id);
            crowdLevelService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除拥挤度记录失败，ID: {}", id, e);
            return Response.error("删除拥挤度记录失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查拥挤度服务是否正常")
    public Response<String> healthCheck() {
        try {
            long count = crowdLevelService.count();
            return Response.ok("拥挤度服务正常，当前记录总数: " + count);
        } catch (Exception e) {
            log.error("拥挤度服务异常", e);
            return Response.error("拥挤度服务异常: " + e.getMessage());
        }
    }
}
