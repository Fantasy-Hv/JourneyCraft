package org.dsgroup.journeycraft.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dsgroup.journeycraft.user.entity.User;

/**
 * 用户表数据访问接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
