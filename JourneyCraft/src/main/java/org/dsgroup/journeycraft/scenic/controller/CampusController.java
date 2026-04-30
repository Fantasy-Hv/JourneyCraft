package org.dsgroup.journeycraft.scenic.controller;

import lombok.RequiredArgsConstructor;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.scenic.api.ScenicService;
import org.dsgroup.journeycraft.scenic.vo.reqvo.ScenicListReqVO;
import org.dsgroup.journeycraft.scenic.vo.rspvo.ScenicListRspVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 校园列表 HTTP 接口。
 */
@RestController
@RequestMapping("/api/campus")
@RequiredArgsConstructor
public class CampusController {

    private final ScenicService scenicService;

    /**
     * 校园列表接口（type 固定为 1）。
     */
    @GetMapping("/list")
    public Response<ScenicListRspVO> list(ScenicListReqVO reqVO) {
        reqVO.setType(1);
        return Response.ok(scenicService.listCampus(reqVO));
    }
}
