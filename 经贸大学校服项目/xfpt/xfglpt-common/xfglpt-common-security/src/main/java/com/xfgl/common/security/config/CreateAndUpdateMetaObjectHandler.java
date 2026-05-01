package com.xfgl.common.security.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;

import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.domain.BaseEntity;
import com.xfgl.common.security.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MP注入处理器
 *
 * @author Lion Li
 * @date 2021/4/25
 */
@Slf4j
@Component
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler, InnerInterceptor {

    public CreateAndUpdateMetaObjectHandler() {
    }
    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                Date current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                        ? baseEntity.getCreateTime() : new Date();
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);
                if (SecurityUtils.getLoginUser() != null) {
                    String username = StringUtils.isNotBlank(baseEntity.getCreateBy())
                            ? baseEntity.getCreateBy() : SecurityUtils.getLoginUser().getSysUser().getNickName();
                    // 当前已登录 且 创建人为空 则填充
                    baseEntity.setCreateBy(username);
                    // 当前已登录 且 更新人为空 则填充
                    baseEntity.setUpdateBy(username);
                    baseEntity.setDelFlag("0");
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) metaObject.getOriginalObject();
                Date current = new Date();
                // 更新时间填充(不管为不为空)
                baseEntity.setUpdateTime(current);
                if (StringUtils.isEmpty(baseEntity.getUpdateBy())) {
                    String username = SecurityUtils.getLoginUser().getSysUser().getNickName();
                    // 当前已登录 更新人填充(不管为不为空)
                    if (StringUtils.isNotBlank(username)) {
                        baseEntity.setUpdateBy(username);
                    }
                }
            }
        } catch (Exception e) {
            //throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

}
