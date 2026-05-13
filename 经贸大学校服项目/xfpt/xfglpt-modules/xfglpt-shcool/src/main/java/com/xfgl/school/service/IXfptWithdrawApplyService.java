package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptWithdrawApply;

/**
 * 供应商提现申请Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptWithdrawApplyService extends IService<XfptWithdrawApply> {

    /**
     * 查询供应商提现申请列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请集合
     */
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply);

    /**
     * 查询结算管理列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请集合
     */
    public List<XfptWithdrawApply> selectSettlementManageList(XfptWithdrawApply xfptWithdrawApply);

    /**
     * 执行结算
     *
     * @param id 申请结算记录ID
     * @param payChannel 支付渠道
     * @param tradeNo 外部交易单号
     * @param remark 结算备注
     * @return 更新后的申请结算记录
     */
    public XfptWithdrawApply settleSettlementManage(Long id, String payChannel, String tradeNo, String remark);

}
