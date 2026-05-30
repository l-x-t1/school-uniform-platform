package com.xfgl.school.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptHmd;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.school.mapper.SysUserRoleMapper;
import com.xfgl.school.mapper.XfptSupplierSettleApplyMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptHmdService;
import com.xfgl.school.service.IXfptSupplierSettleApplyService;
import com.xfgl.system.api.RemoteUserService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商入驻申请Service业务层处理
 */
@Service
public class XfptSupplierSettleApplyServiceImpl
        extends ServiceImpl<XfptSupplierSettleApplyMapper, XfptSupplierSettleApply>
        implements IXfptSupplierSettleApplyService {

    private static final String AUDIT_STATUS_PENDING = "0";
    private static final String AUDIT_STATUS_APPROVED = "1";
    private static final String AUDIT_STATUS_REJECTED = "2";
    private static final Long SUPPLIER_ROLE_ID = 115L;
    private static final String SUPPLIER_ROLE_KEY = "gys";
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    private static final String INVALID_PASSWORD_PATTERN = ".*[<>\"'|\\\\].*";

    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private IXfptHmdService xfptHmdService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public boolean submitApply(XfptSupplierSettleApply apply) {
        normalize(apply);
        validateApply(apply);
        checkDuplicate(apply);

        apply.setPasswordHash(SecurityUtils.encryptPassword(apply.getPassword()));
        apply.setPassword(null);
        apply.setAuditStatus(AUDIT_STATUS_PENDING);
        apply.setTargetRoleId(SUPPLIER_ROLE_ID);
        apply.setTargetRoleKey(SUPPLIER_ROLE_KEY);
        apply.setCreateBy("supplier_settle");
        apply.setCreateTime(new Date());
        apply.setDelFlag("0");
        return save(apply);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveApply(Long id) {
        XfptSupplierSettleApply apply = getById(id);
        if (apply == null) {
            throw new ServiceException("入驻申请不存在");
        }
        if (!AUDIT_STATUS_PENDING.equals(apply.getAuditStatus())) {
            throw new ServiceException("该申请已审核，不可重复操作");
        }

        // 1. 注册系统用户
        SysUser sysUser = new SysUser();
        sysUser.setUserName(apply.getUsername());
        sysUser.setNickName(apply.getSupplierName());
        sysUser.setPassword(apply.getPasswordHash());
        sysUser.setPhonenumber(apply.getPhone());
        sysUser.setSex("0");
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");
        sysUser.setCreateTime(new Date());
        sysUser.setCreateBy((StringUtils.isNotBlank(SecurityUtils.getUsername()) ? SecurityUtils.getUsername() : "supplier_settle"));

        R<Boolean> registerResult = remoteUserService.addInnerUser(sysUser, SecurityConstants.INNER);
        if (registerResult == null || R.FAIL == registerResult.getCode()) {
            String msg = registerResult != null ? registerResult.getMsg() : "用户注册失败";
            // 如果用户已存在（之前审批失败残留的分布式事务问题），跳过创建继续后续步骤
            if (msg == null || !msg.contains("注册账号已存在")) {
                throw new ServiceException(msg);
            }
        }

        // 2. 查询新用户ID并分配供应商角色
        R<LoginUser> userResult = remoteUserService.getUserInfo(apply.getUsername(), SecurityConstants.INNER);
        if (userResult == null || R.FAIL == userResult.getCode()) {
            throw new ServiceException("获取用户信息失败，无法分配角色");
        }
        Long userId = userResult.getData().getSysUser().getUserId();
        sysUserRoleMapper.insertUserRole(userId, SUPPLIER_ROLE_ID);

        // 3. 写入厂商信息并关联用户
        SupplierMessage supplierMessage = buildSupplierMessage(apply);
        supplierMessage.setUserId(userId);
        supplierMessageService.save(supplierMessage);

        // 4. 更新申请状态为通过
        apply.setAuditStatus(AUDIT_STATUS_APPROVED);
        apply.setAuditTime(new Date());
        apply.setUpdateBy(SecurityUtils.getUsername());
        updateById(apply);
    }

    @Override
    public void rejectApply(Long id, String remark) {
        XfptSupplierSettleApply apply = getById(id);
        if (apply == null) {
            throw new ServiceException("入驻申请不存在");
        }
        if (!AUDIT_STATUS_PENDING.equals(apply.getAuditStatus())) {
            throw new ServiceException("该申请已审核，不可重复操作");
        }
        apply.setAuditStatus(AUDIT_STATUS_REJECTED);
        apply.setAuditTime(new Date());
        apply.setAuditRemark(remark);
        apply.setUpdateBy(SecurityUtils.getUsername());
        updateById(apply);
    }

    private SupplierMessage buildSupplierMessage(XfptSupplierSettleApply apply) {
        SupplierMessage sm = new SupplierMessage();
        sm.setSupplierName(apply.getSupplierName());
        sm.setSupplierPhone(apply.getSupplierPhone());
        sm.setSupplierType(apply.getSupplierType());
        sm.setLegalRepresentative(apply.getLegalRepresentative());
        sm.setStaffNumber(apply.getStaffNumber());
        sm.setEstablishedTime(apply.getEstablishedTime());
        sm.setSupplierAddress(apply.getSupplierAddress());
        sm.setSupplierIntroduce(apply.getSupplierIntroduce());
        sm.setBusinessLicense(apply.getBusinessLicense());
        sm.setRegisteredCapital(apply.getRegisteredCapital());
        sm.setXypj(apply.getXypj());
        sm.setCreateTime(new Date());
        sm.setCreateBy(SecurityUtils.getUsername());
        sm.setDelFlag("0");
        return sm;
    }

    private void normalize(XfptSupplierSettleApply apply) {
        if (apply == null) {
            throw new ServiceException("申请信息不能为空");
        }
        apply.setUsername(StringUtils.trim(apply.getUsername()));
        apply.setPhone(StringUtils.trim(apply.getPhone()));
        apply.setSupplierName(StringUtils.trim(apply.getSupplierName()));
        apply.setSupplierPhone(StringUtils.trim(apply.getSupplierPhone()));
        apply.setLegalRepresentative(StringUtils.trim(apply.getLegalRepresentative()));
        apply.setStaffNumber(StringUtils.trim(apply.getStaffNumber()));
        apply.setSupplierAddress(StringUtils.trim(apply.getSupplierAddress()));
        apply.setRegisteredCapital(StringUtils.trim(apply.getRegisteredCapital()));
        apply.setSupplierType(StringUtils.trim(apply.getSupplierType()));
        apply.setBusinessLicense(StringUtils.trim(apply.getBusinessLicense()));
        apply.setXypj(StringUtils.trim(apply.getXypj()));
    }

    private void validateApply(XfptSupplierSettleApply apply) {
        if (StringUtils.isBlank(apply.getPhone()) || !apply.getPhone().matches(PHONE_PATTERN)) {
            throw new ServiceException("手机号格式不正确");
        }
        if (StringUtils.isBlank(apply.getUsername()) || apply.getUsername().length() < 2
                || apply.getUsername().length() > 20) {
            throw new ServiceException("用户名长度应为2到20位");
        }
        if (StringUtils.isBlank(apply.getPassword()) || apply.getPassword().length() < 5
                || apply.getPassword().length() > 20) {
            throw new ServiceException("密码长度应为5到20位");
        }
        if (apply.getPassword().matches(INVALID_PASSWORD_PATTERN)) {
            throw new ServiceException("密码不能包含 < > \" ' | \\ 等字符");
        }
        if (StringUtils.isBlank(apply.getSupplierName())) {
            throw new ServiceException("厂商名称不能为空");
        }
        if (StringUtils.isBlank(apply.getSupplierPhone()) || !apply.getSupplierPhone().matches(PHONE_PATTERN)) {
            throw new ServiceException("联系电话格式不正确");
        }
        if (StringUtils.isBlank(apply.getLegalRepresentative())) {
            throw new ServiceException("法定代表人不能为空");
        }
        if (StringUtils.isBlank(apply.getStaffNumber())) {
            throw new ServiceException("职工人数不能为空");
        }
        if (StringUtils.isBlank(apply.getSupplierAddress())) {
            throw new ServiceException("厂商地址不能为空");
        }
        if (StringUtils.isBlank(apply.getRegisteredCapital())) {
            throw new ServiceException("注册资金不能为空");
        }
        if (apply.getEstablishedTime() == null) {
            throw new ServiceException("成立日期不能为空");
        }
        if (StringUtils.isBlank(apply.getSupplierType())) {
            throw new ServiceException("企业类型不能为空");
        }
        if (StringUtils.isBlank(apply.getSupplierIntroduce())) {
            throw new ServiceException("厂商介绍不能为空");
        }
        if (StringUtils.isBlank(apply.getBusinessLicense())) {
            throw new ServiceException("厂商资质不能为空");
        }
    }

    private void checkDuplicate(XfptSupplierSettleApply apply) {
        R<LoginUser> userResult = remoteUserService.getUserInfo(apply.getUsername(), SecurityConstants.INNER);
        if (userResult != null && R.isSuccess(userResult)) {
            throw new ServiceException("用户名已存在，请更换后提交");
        }
        if (lambdaQuery().eq(XfptSupplierSettleApply::getUsername, apply.getUsername())
                .eq(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_PENDING).count() > 0) {
            throw new ServiceException("该用户名已提交入驻申请，请勿重复提交");
        }
        if (lambdaQuery().eq(XfptSupplierSettleApply::getPhone, apply.getPhone())
                .eq(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_PENDING).count() > 0) {
            throw new ServiceException("该手机号已提交入驻申请，请勿重复提交");
        }
        if (lambdaQuery().eq(XfptSupplierSettleApply::getSupplierName, apply.getSupplierName())
                .eq(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_PENDING).count() > 0) {
            throw new ServiceException(apply.getSupplierName() + "供应商已提交入驻申请，请勿重复提交");
        }
        if (supplierMessageService.lambdaQuery()
                .eq(SupplierMessage::getSupplierName, apply.getSupplierName()).count() > 0) {
            throw new ServiceException(apply.getSupplierName() + "供应商已存在，不可重复提交");
        }
        if (xfptHmdService.lambdaQuery().eq(XfptHmd::getName, apply.getSupplierName()).count() > 0) {
            throw new ServiceException("此供应商已加入黑名单，请勿提交入驻申请");
        }
    }
}
