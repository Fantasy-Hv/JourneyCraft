package org.dsgroup.journeycraft.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * MyBatis-Plus 元数据处理器
 * <p>
 * 自动填充创建时间、更新时间等字段
 * 
 * @author 后端智能体
 * @since 2026-04-13
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        
        // 填充创建时间（如果字段为空）
        if (metaObject.hasSetter("createdAt")) {
            Object createdAt = this.getFieldValByName("createdAt", metaObject);
            if (createdAt == null) {
                this.strictInsertFill(metaObject, "createdAt", Date.class, currentDate());
                log.debug("填充 createdAt: {}", currentDate());
            }
        }
        
        // 填充更新时间（如果字段为空）
        if (metaObject.hasSetter("updatedAt")) {
            Object updatedAt = this.getFieldValByName("updatedAt", metaObject);
            if (updatedAt == null) {
                this.strictInsertFill(metaObject, "updatedAt", Date.class, currentDate());
                log.debug("填充 updatedAt: {}", currentDate());
            }
        }
        
        // 填充逻辑删除字段默认值（如果字段为空）
        if (metaObject.hasSetter("deleted")) {
            Object deleted = this.getFieldValByName("deleted", metaObject);
            if (deleted == null) {
                this.strictInsertFill(metaObject, "deleted", Boolean.class, false);
                log.debug("填充 deleted: false");
            }
        }
        
        // 填充启用状态默认值（如果字段为空）
        if (metaObject.hasSetter("enabled")) {
            Object enabled = this.getFieldValByName("enabled", metaObject);
            if (enabled == null) {
                this.strictInsertFill(metaObject, "enabled", Boolean.class, true);
                log.debug("填充 enabled: true");
            }
        }
    }

    /**
     * 更新时自动填充字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        
        // 填充更新时间
        if (metaObject.hasSetter("updatedAt")) {
            this.strictUpdateFill(metaObject, "updatedAt", Date.class, currentDate());
            log.debug("填充 updatedAt: {}", currentDate());
        }
    }

    /**
     * 获取当前时间（Asia/Shanghai时区）
     */
    private Date currentDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).toInstant());
    }
}