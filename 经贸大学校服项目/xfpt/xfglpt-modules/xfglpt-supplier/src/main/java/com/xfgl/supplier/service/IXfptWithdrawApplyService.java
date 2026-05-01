package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptWithdrawApply;

/**
 * 提现申请Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptWithdrawApplyService extends IService<XfptWithdrawApply> {

    /**
     * 查询提现申请列表
     *
     * @param xfptWithdrawApply 提现申请
     * @return 提现申请集合
     */
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply);

}
