package org.dsgroup.journeycraft.diary.repository;

import org.dsgroup.journeycraft.diary.entity.DiaryLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 日记点赞 Repository（MongoDB）。
 */
@Repository
public interface DiaryLikeRepository extends MongoRepository<DiaryLike, String> {

    /** 查询某条日记的所有点赞记录 */
    List<DiaryLike> findByDiaryId(String diaryId);

    /** 查询某个用户对某条日记的点赞记录 */
    Optional<DiaryLike> findByDiaryIdAndUserId(String diaryId, Long userId);

    /** 统计某条日记的点赞数 */
    long countByDiaryId(String diaryId);

    /** 查询用户是否点过赞 */
    boolean existsByDiaryIdAndUserId(String diaryId, Long userId);
}
