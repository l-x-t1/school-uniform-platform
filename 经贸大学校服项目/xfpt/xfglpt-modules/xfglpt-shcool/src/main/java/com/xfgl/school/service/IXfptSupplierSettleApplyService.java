package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.school.domain.vo.SupplierSettleEntryContextVo;
import java.util.List;

/**
 * 供应商入驻申请Service接口
 */
public interface IXfptSupplierSettleApplyService extends IService<XfptSupplierSettleApply> {
    /**
     * 提交供应商入驻申请，只生成待审记录。
     *
     * @param apply 入驻申请
     * @return 结果
     */
    boolean submitApply(XfptSupplierSettleApply apply);

    boolean submitRegisterApply(XfptSupplierSettleApply apply);

    boolean submitLoginBoundApply(XfptSupplierSettleApply apply);

    SupplierSettleEntryContextVo getPublicEntryContext(Long noticeId);

    SupplierSettleEntryContextVo getLoginEntryContext(Long noticeId);

    /**
     * 审核通过入驻申请，创建用户账号和厂商信息。
     *
     * @param id 申请ID
     */
    void approveApply(Long id);

    /**
     * 审核驳回入驻申请。
     *
     * @param id     申请ID
     * @param remark 驳回备注
     */
    void rejectApply(Long id, String remark);

    /**
     * 校验账号并查询绑定厂商信息
     */
    SupplierSettleEntryContextVo checkBinding(String username, String password);

    /**
     * 查询当前公告发布人名下待审核的入驻记录
     */
    List<XfptSupplierSettleApply> listPendingByPublisher(XfptSupplierSettleApply query);

    /**
     * 查询当前公告发布人名下已审核通过的入驻记录
     */
    List<XfptSupplierSettleApply> listApprovedByPublisher(XfptSupplierSettleApply query);

    /**
     * 查询当前公告发布人名下已处理的入驻记录
     */
    List<XfptSupplierSettleApply> listProcessedByPublisher(XfptSupplierSettleApply query);
}
