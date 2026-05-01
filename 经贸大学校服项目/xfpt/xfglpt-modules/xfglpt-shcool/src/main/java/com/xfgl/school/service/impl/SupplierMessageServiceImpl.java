package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.mapper.SupplierMessageMapper;
import com.xfgl.school.service.ISupplierMessageService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 供应商信息Service业务层处理
 *
 * @author yinet
 * @date 2025-04-22
 */
@Service
public class SupplierMessageServiceImpl extends ServiceImpl<SupplierMessageMapper, SupplierMessage> implements ISupplierMessageService {

    @Autowired
    private SupplierMessageMapper supplierMessageMapper;

    /**
     * 查询供应商信息列表
     *
     * @param supplierMessage 供应商信息
     * @return 供应商信息
     */
    @Override
    public List<SupplierMessage> selectSupplierMessageList(SupplierMessage supplierMessage)
    {
        return supplierMessageMapper.selectSupplierMessageList(supplierMessage);
    }


}
