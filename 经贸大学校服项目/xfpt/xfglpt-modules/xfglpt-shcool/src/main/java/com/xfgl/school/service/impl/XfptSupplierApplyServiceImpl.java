package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSupplierApplyMapper;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.service.IXfptSupplierApplyService;

/**
 * 供应商报名Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptSupplierApplyServiceImpl extends ServiceImpl<XfptSupplierApplyMapper, XfptSupplierApply> implements IXfptSupplierApplyService {

    @Autowired
    private XfptSupplierApplyMapper xfptSupplierApplyMapper;

    /**
     * 查询供应商报名列表
     *
     * @param xfptSupplierApply 供应商报名
     * @return 供应商报名
     */
    @Override
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply)
    {
        return xfptSupplierApplyMapper.selectXfptSupplierApplyList(xfptSupplierApply);
    }


}
