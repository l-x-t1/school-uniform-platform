package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptSupplierApplyMapper;
import com.xfgl.supplier.domain.XfptSupplierApply;
import com.xfgl.supplier.service.IXfptSupplierApplyService;

/**
 * 企业报名及资格审查Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptSupplierApplyServiceImpl extends ServiceImpl<XfptSupplierApplyMapper, XfptSupplierApply> implements IXfptSupplierApplyService {

    @Autowired
    private XfptSupplierApplyMapper xfptSupplierApplyMapper;

    /**
     * 查询企业报名及资格审查列表
     *
     * @param xfptSupplierApply 企业报名及资格审查
     * @return 企业报名及资格审查
     */
    @Override
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply)
    {
        return xfptSupplierApplyMapper.selectXfptSupplierApplyList(xfptSupplierApply);
    }


}
