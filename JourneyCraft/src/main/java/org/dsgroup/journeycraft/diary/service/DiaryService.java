package org.dsgroup.journeycraft.diary.service;

import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;
import org.dsgroup.journeycraft.diary.entity.Diary;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryDetailRspVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryListRspVO;

import java.util.List;

/**
 * 日记服务接口
 */
public interface DiaryService {

    /**
     * 创建日记
     * @param dto 创建日记数据传输对象
     * @return 日记 ID
     */
    String createDiary(DiaryCreateDTO dto);

    /**
     * 查询日记列表
     * @param userId 作者 ID（可选）
     * @param tags 标签过滤（可选）
     * @param sortBy 排序方式（可选）
     * @param page 页码
     * @param size 每页数量
     * @return 日记列表
     */
    List<DiaryListRspVO> getDiaryList(Long userId, String tags, String sortBy, Integer page, Integer size);

    /**
     * 查询日记详情
     * @param id 日记 ID
     * @return 日记详情
     */
    DiaryDetailRspVO getDiaryDetail(String id);
}
