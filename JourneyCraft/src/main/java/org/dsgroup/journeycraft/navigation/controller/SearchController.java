package org.dsgroup.journeycraft.navigation.controller;

import lombok.extern.slf4j.Slf4j;
import org.dsgroup.journeycraft.common.result.Response;
import org.dsgroup.journeycraft.navigation.service.SearchService;
import org.dsgroup.journeycraft.navigation.vo.rspvo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public Response<SearchResultVO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "scenic,node") String types,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "1") Integer page) {

        if (page < 1) page = 1;
        int offset = (page - 1) * limit;

        SearchResultVO result = searchService.search(keyword, types, limit, offset);
        return Response.ok(result);
    }
}
