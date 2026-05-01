package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.SupplierMessage;


/**
 * 供应商信息Service接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface ISupplierMessageService extends IService<SupplierMessage> {

    /**
     * 查询供应商信息列表
     *
     * @param supplierMessage 供应商信息
     * @return 供应商信息集合
     */
    public List<SupplierMessage> selectSupplierMessageList(SupplierMessage supplierMessage);

}
