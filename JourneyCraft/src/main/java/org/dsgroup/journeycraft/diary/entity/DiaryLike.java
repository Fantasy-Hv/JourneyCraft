package org.dsgroup.journeycraft.diary.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 日记点赞记录（MongoDB）。
 * <p>
 * 替代原有的 ConcurrentHashMap 内存存储，支持持久化。
 * 联合唯一索引 (diaryId, userId) 保证同一用户对同一日记只能点一次赞。
 */
@Data
@Document(collection = "diary_like")
@CompoundIndexes({
    @CompoundIndex(name = "uk_diary_user", def = "{'diaryId': 1, 'userId': 1}", unique = true)
})
public class DiaryLike {

    @Id
    private String id;

    /** 日记 ID（MongoDB ObjectId） */
    private String diaryId;

    /** 用户 ID */
    private Long userId;

    /** 点赞时间 */
    private LocalDateTime createdAt = LocalDateTime.now();
}
