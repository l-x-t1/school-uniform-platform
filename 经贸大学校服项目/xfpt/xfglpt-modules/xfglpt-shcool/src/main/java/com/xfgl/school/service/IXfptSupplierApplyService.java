package com.xfgl.school.service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.domain.XfptSupplierApplyLog;
import com.xfgl.school.domain.vo.SupplierApplyAuditRequest;
import com.xfgl.school.domain.vo.SupplierApplyNoticeVo;
import com.xfgl.school.domain.vo.SupplierApplySubmitRequest;

/**
 * 供应商报名Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptSupplierApplyService extends IService<XfptSupplierApply> {

    /**
     * 查询供应商报名列表
     *
     * @param xfptSupplierApply 供应商报名
     * @return 供应商报名集合
     */
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply);

    /**
     * 查询供应商报名详情
     *
     * @param id 主键
     * @return 供应商报名
     */
    public XfptSupplierApply selectXfptSupplierApplyById(Long id);

    List<SupplierApplyNoticeVo> selectSupplierNoticeCenterList(Long supplierId, XfptSupplierApply query);

    Map<String, Object> buildSupplierApplyContext(Long supplierId, Long noticeId);

    XfptSupplierApply saveSupplierDraft(Long supplierId, SupplierApplySubmitRequest request);

    XfptSupplierApply submitSupplierApply(Long supplierId, SupplierApplySubmitRequest request);

    void withdrawSupplierApply(Long supplierId, Long noticeId);

    List<XfptSupplierApplyLog> selectApplyLogs(Long applyId);

    void auditPass(Long schoolDeptId, SupplierApplyAuditRequest request);

    void auditReject(Long schoolDeptId, SupplierApplyAuditRequest request);

}
