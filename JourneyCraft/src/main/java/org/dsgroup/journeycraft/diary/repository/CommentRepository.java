package org.dsgroup.journeycraft.diary.repository;

import org.dsgroup.journeycraft.diary.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Comment MongoDB Repository 接口
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    /**
     * 根据日记 ID 查询评论列表
     */
    List<Comment> findByDiaryId(String diaryId, Pageable pageable);

    /**
     * 根据日记 ID 和状态查询评论列表
     */
    List<Comment> findByDiaryIdAndStatus(String diaryId, Integer status, Pageable pageable);

    /**
     * 根据父评论 ID 查询回复列表
     */
    List<Comment> findByParentId(String parentId);

    /**
     * 统计日记的评论数
     */
    long countByDiaryId(String diaryId);

    /**
     * 统计日记的评论数 (按状态)
     */
    long countByDiaryIdAndStatus(String diaryId, Integer status);

    /**
     * 删除日记的所有评论
     */
    void deleteByDiaryId(String diaryId);
}
