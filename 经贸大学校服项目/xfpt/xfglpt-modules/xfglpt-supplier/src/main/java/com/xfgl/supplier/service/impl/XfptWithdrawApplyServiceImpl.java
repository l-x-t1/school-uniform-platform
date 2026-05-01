package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptWithdrawApplyMapper;
import com.xfgl.supplier.domain.XfptWithdrawApply;
import com.xfgl.supplier.service.IXfptWithdrawApplyService;

/**
 * 提现申请Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptWithdrawApplyServiceImpl extends ServiceImpl<XfptWithdrawApplyMapper, XfptWithdrawApply> implements IXfptWithdrawApplyService {

    @Autowired
    private XfptWithdrawApplyMapper xfptWithdrawApplyMapper;

    /**
     * 查询提现申请列表
     *
     * @param xfptWithdrawApply 提现申请
     * @return 提现申请
     */
    @Override
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply)
    {
        return xfptWithdrawApplyMapper.selectXfptWithdrawApplyList(xfptWithdrawApply);
    }


}
