package org.dsgroup.journeycraft.diary.service;

import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;
import org.dsgroup.journeycraft.diary.entity.Diary;
import org.dsgroup.journeycraft.diary.vo.reqvo.CommentCreateReqVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.CommentRspVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryDetailRspVO;
import org.dsgroup.journeycraft.diary.vo.rspvo.DiaryLikeRspVO;
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

    /**
     * 更新日记
     * @param id 日记 ID
     * @param dto 更新数据传输对象
     */
    void updateDiary(String id, DiaryCreateDTO dto);

    /**
     * 删除日记
     * @param id 日记 ID
     */
    void deleteDiary(String id);

    /**
     * 点赞日记
     * @param id 日记 ID
     * @return 点赞结果
     */
    DiaryLikeRspVO toggleLike(String id);

    /**
     * 添加评论
     * @param diaryId 日记 ID
     * @param reqVO 评论请求 VO
     * @return 评论 ID
     */
    String addComment(String diaryId, CommentCreateReqVO reqVO);

    /**
     * 查询评论列表
     * @param diaryId 日记 ID
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    List<CommentRspVO> getCommentList(String diaryId, Integer page, Integer size);
}
