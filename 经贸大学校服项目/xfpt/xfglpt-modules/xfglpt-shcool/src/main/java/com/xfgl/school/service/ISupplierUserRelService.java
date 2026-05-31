package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.SupplierUserRel;
import com.xfgl.system.api.domain.SysUser;

import java.util.List;

/**
 * 供应商用户关联Service接口
 *
 * @date 2026-05-31
 */
public interface ISupplierUserRelService extends IService<SupplierUserRel> {

    /**
     * 创建供应商与用户的关联
     */
    void createRel(Long supplierId, Long userId);

    /**
     * 根据用户ID查找关联的厂商ID列表
     */
    List<Long> findSupplierIdsByUserId(Long userId);

    /**
     * 根据用户ID查找第一个关联的厂商ID
     */
    Long findFirstSupplierIdByUserId(Long userId);

    /**
     * 根据厂商ID查找关联的用户ID列表
     */
    List<Long> findUserIdsBySupplierId(Long supplierId);

    /**
     * 根据厂商ID获取关联的用户详情列表
     */
    List<SysUser> getUserListBySupplierId(Long supplierId);

    /**
     * 解除厂商与用户的绑定
     */
    void unbindUser(Long supplierId, Long userId);
}