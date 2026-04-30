package org.dsgroup.journeycraft.navigation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.navigation.entity.PhotoSpot;
import org.dsgroup.journeycraft.navigation.service.PhotoSpotService;
import org.dsgroup.journeycraft.common.result.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 拍照点控制器
 * <p>
 * 简化版：仅提供基础CRUD接口
 * 
 * @author 后端智能体
 * @since 2026-04-14
 */
@Slf4j
@RestController
@RequestMapping("/api/navigation/photo-spots")
@Tag(name = "导航模块-拍照点", description = "拍照点推荐管理接口")
public class PhotoSpotController {

    @Autowired
    private PhotoSpotService photoSpotService;

    @GetMapping("/list")
    @Operation(summary = "获取拍照点列表（分页）")
    public Response<IPage<PhotoSpot>> listPhotoSpots(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<PhotoSpot> page = new Page<>(pageNum, pageSize);
            return Response.ok(photoSpotService.page(page));
        } catch (Exception e) {
            log.error("查询拍照点列表失败", e);
            return Response.error("查询拍照点列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取拍照点详情")
    public Response<PhotoSpot> getPhotoSpotDetail(@PathVariable Long id) {
        try {
            PhotoSpot spot = photoSpotService.getById(id);
            if (spot == null) {
                return Response.error("拍照点不存在");
            }
            return Response.ok(spot);
        } catch (Exception e) {
            log.error("查询拍照点详情失败，ID: {}", id, e);
            return Response.error("查询拍照点详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/scenic/{scenicAreaId}")
    @Operation(summary = "获取景区的拍照点")
    public Response<List<PhotoSpot>> getPhotoSpotsByScenicArea(@PathVariable Long scenicAreaId) {
        try {
            List<PhotoSpot> spots = photoSpotService.lambdaQuery()
                    .eq(PhotoSpot::getScenicAreaId, scenicAreaId)
                    .orderByDesc(PhotoSpot::getRating)
                    .list();
            return Response.ok(spots);
        } catch (Exception e) {
            log.error("查询景区拍照点失败，景区ID: {}", scenicAreaId, e);
            return Response.error("查询景区拍照点失败: " + e.getMessage());
        }
    }

    @GetMapping("/high-rated")
    @Operation(summary = "获取高评分拍照点")
    public Response<List<PhotoSpot>> getHighRatedPhotoSpots() {
        try {
            List<PhotoSpot> spots = photoSpotService.lambdaQuery()
                    .gt(PhotoSpot::getRating, 0)
                    .orderByDesc(PhotoSpot::getRating)
                    .last("LIMIT 20")
                    .list();
            return Response.ok(spots);
        } catch (Exception e) {
            log.error("查询高评分拍照点失败", e);
            return Response.error("查询高评分拍照点失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建拍照点")
    public Response<PhotoSpot> createPhotoSpot(@RequestBody PhotoSpot photoSpot) {
        try {
            photoSpotService.save(photoSpot);
            return Response.ok(photoSpot);
        } catch (Exception e) {
            log.error("创建拍照点失败", e);
            return Response.error("创建拍照点失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新拍照点")
    public Response<PhotoSpot> updatePhotoSpot(@PathVariable Long id, @RequestBody PhotoSpot photoSpot) {
        try {
            photoSpot.setId(id);
            photoSpotService.updateById(photoSpot);
            return Response.ok(photoSpot);
        } catch (Exception e) {
            log.error("更新拍照点失败，ID: {}", id, e);
            return Response.error("更新拍照点失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除拍照点")
    public Response<Void> deletePhotoSpot(@PathVariable Long id) {
        try {
            photoSpotService.removeById(id);
            return Response.ok();
        } catch (Exception e) {
            log.error("删除拍照点失败，ID: {}", id, e);
            return Response.error("删除拍照点失败: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Response<String> healthCheck() {
        try {
            return Response.ok("拍照点服务正常，当前总数: " + photoSpotService.count());
        } catch (Exception e) {
            log.error("拍照点服务异常", e);
            return Response.error("拍照点服务异常: " + e.getMessage());
        }
    }
}
