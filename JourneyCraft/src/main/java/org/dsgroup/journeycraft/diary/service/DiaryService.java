package org.dsgroup.journeycraft.diary.service;

import org.dsgroup.journeycraft.diary.dto.DiaryCreateDTO;

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
}
