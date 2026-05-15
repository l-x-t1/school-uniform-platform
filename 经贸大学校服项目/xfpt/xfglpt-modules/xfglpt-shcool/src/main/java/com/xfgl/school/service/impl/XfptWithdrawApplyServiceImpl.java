package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptWithdrawApplyMapper;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.service.IXfptWithdrawApplyService;

/**
 * 供应商提现申请Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptWithdrawApplyServiceImpl extends ServiceImpl<XfptWithdrawApplyMapper, XfptWithdrawApply> implements IXfptWithdrawApplyService {

    @Autowired
    private XfptWithdrawApplyMapper xfptWithdrawApplyMapper;

    /**
     * 查询供应商提现申请列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请
     */
    @Override
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply)
    {
        return xfptWithdrawApplyMapper.selectXfptWithdrawApplyList(xfptWithdrawApply);
    }


}
