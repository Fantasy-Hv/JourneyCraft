package org.dsgroup.journeycraft.diary.repository;

import org.dsgroup.journeycraft.diary.entity.Diary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Diary MongoDB Repository 接口
 * 继承 MongoRepository 获得基础 CRUD 能力
 */
@Repository
public interface DiaryRepository extends MongoRepository<Diary, String> {

    /**
     * 根据用户 ID 查询日记列表
     * @param userId 用户 ID
     * @return 日记列表
     */
    List<Diary> findByUserId(Long userId);

    /**
     * 根据状态查询日记列表
     * @param status 状态
     * @return 日记列表
     */
    List<Diary> findByStatus(Integer status);

    /**
     * 根据用户 ID 和状态查询日记列表
     * @param userId 用户 ID
     * @param status 状态
     * @return 日记列表
     */
    List<Diary> findByUserIdAndStatus(Long userId, Integer status);
}
