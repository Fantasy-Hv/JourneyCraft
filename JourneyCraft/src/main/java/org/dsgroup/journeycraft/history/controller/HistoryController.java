package org.dsgroup.journeycraft.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.history.service.HistoryService;
import org.dsgroup.journeycraft.history.vo.reqvo.ClearHistoryReqVO;
import org.dsgroup.journeycraft.history.vo.reqvo.NavigationHistoryReqVO;
import org.dsgroup.journeycraft.history.vo.reqvo.RecordViewReqVO;
import org.dsgroup.journeycraft.history.vo.reqvo.SearchHistoryReqVO;
import org.dsgroup.journeycraft.history.vo.reqvo.ViewHistoryReqVO;
import org.dsgroup.journeycraft.history.vo.rspvo.NavigationHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.SearchHistoryRspVO;
import org.dsgroup.journeycraft.history.vo.rspvo.ViewHistoryRspVO;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史记录控制器
 */
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "历史记录管理", description = "浏览历史、搜索历史、导航历史相关接口")
public class HistoryController {
    @Resource
    private final HistoryService historyService;

    /**
     * 获取浏览历史
     */
    @GetMapping("/view")
    @Operation(summary = "获取浏览历史", description = "查询当前用户的浏览历史列表，支持按类型过滤和分页")
    public Response<Map<String, Object>> getViewHistory(
            ViewHistoryReqVO reqVO,
            @RequestAttribute("currentUserId") Long currentUserId) {
        List<ViewHistoryRspVO> list = historyService.getViewHistory(
                currentUserId,
                reqVO.getType(),
                reqVO.getPage(),
                reqVO.getSize()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());

        return Response.ok(result);
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/search")
    @Operation(summary = "获取搜索历史", description = "查询当前用户的搜索历史列表，支持按类型过滤")
    public Response<Map<String, Object>> getSearchHistory(
            SearchHistoryReqVO reqVO,
            @RequestAttribute("currentUserId") Long currentUserId) {
        List<SearchHistoryRspVO> list = historyService.getSearchHistory(
                currentUserId,
                reqVO.getType(),
                reqVO.getLimit()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());

        return Response.ok(result);
    }

    /**
     * 获取导航历史
     */
    @GetMapping("/navigation")
    @Operation(summary = "获取导航历史", description = "查询当前用户的导航历史列表，支持按景区过滤和分页")
    public Response<Map<String, Object>> getNavigationHistory(
            NavigationHistoryReqVO reqVO,
            @RequestAttribute("currentUserId") Long currentUserId) {
        List<NavigationHistoryRspVO> list = historyService.getNavigationHistory(
                currentUserId,
                reqVO.getScenicAreaId(),
                reqVO.getPage(),
                reqVO.getSize()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());

        return Response.ok(result);
    }

    /**
     * 记录浏览历史
     */
    @PostMapping("/view")
    @Operation(summary = "记录浏览历史", description = "记录用户的浏览行为")
    public Response recordViewHistory(
            @RequestBody @Valid RecordViewReqVO reqVO,
            @RequestAttribute("currentUserId") Long currentUserId) {
        historyService.recordViewHistory(currentUserId, reqVO);
        return Response.ok();
    }

    /**
     * 清空历史记录
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空历史记录", description = "清空用户的历史记录，支持按类型清空")
    public Response clearHistory(
            ClearHistoryReqVO reqVO,
            @RequestAttribute("currentUserId") Long currentUserId) {
        historyService.clearHistory(currentUserId, reqVO.getType());
        return Response.ok();
    }
}
