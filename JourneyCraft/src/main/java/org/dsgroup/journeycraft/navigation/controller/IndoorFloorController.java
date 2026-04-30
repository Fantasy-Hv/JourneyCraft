package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.IndoorFloor;
import org.dsgroup.journeycraft.navigation.service.IndoorFloorService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 室内楼层控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/indoor-floors")
@Tag(name = "导航模块-室内楼层", description = "室内楼层管理和查询接口")
public class IndoorFloorController {

    @Autowired
    private IndoorFloorService indoorFloorService;

    @GetMapping("/list")
    @Operation(summary = "获取楼层列表（分页）")
    public Response<IPage<IndoorFloor>> listFloors(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<IndoorFloor> page = new Page<>(pageNum, pageSize);
            return Response.ok(indoorFloorService.page(page));
        } catch (Exception e) {
            log.error("查询楼层列表失败", e);
            return Response.error("查询楼层列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取楼层详情")
    public Response<IndoorFloor> getFloorDetail(@PathVariable Long id) {
        try {
            IndoorFloor floor = indoorFloorService.getById(id);
            if (floor == null) {
                return Response.error("楼层不存在");
            }
            return Response.ok(floor);
        } catch (Exception e) {
            log.error("查询楼层详情失败，ID: {}", id, e);
            return Response.error("查询楼层详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/building/{buildingId}")
    @Operation(summary = "根据建筑ID查询楼层")
    public Response<List<IndoorFloor>> getFloorsByBuilding(@PathVariable Long buildingId) {
        try {
            List<IndoorFloor> floors = indoorFloorService.lambdaQuery()
                    .eq(IndoorFloor::getBuildingId, buildingId)
                    .orderByAsc(IndoorFloor::getFloorNumber)
                    .list();
            return Response.ok(floors);
        } catch (Exception e) {
            log.error("查询建筑楼层失败，建筑ID: {}", buildingId, e);
            return Response.error("查询建筑楼层失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建楼层")
    public Response<IndoorFloor> createFloor(@RequestBody IndoorFloor floor) {
        try {
            indoorFloorService.save(floor);
            return Response.ok(floor);
        } catch (Exception e) {
            log.error("创建楼层失败", e);
            return Response.error("创建楼层失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新楼层")
    public Response<IndoorFloor> updateFloor(@PathVariable Long id, @RequestBody IndoorFloor floor) {
        try {
            floor.setId(id);
            indoorFloorService.updateById(floor);
            return Response.ok(floor);
        } catch (Exception e) {
            log.error("更新楼层失败，ID: {}", id, e);
            return Response.error("更新楼层失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除楼层")
    public Response<Void> deleteFloor(@PathVariable Long id) {
        try {
            indoorFloorService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除楼层失败，ID: {}", id, e);
            return Response.error("删除楼层失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("楼层服务正常，当前总数: " + indoorFloorService.count());
        } catch (Exception e) {
            log.error("楼层服务异常", e);
            return Response.error("楼层服务异常: " + e.getMessage());
        }
    }
}
