package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptHmd;
import com.xfgl.school.domain.XfptNoticeSupplierRel;
import com.xfgl.school.domain.XfptSupplierSettleApply;
import com.xfgl.school.domain.vo.SupplierSettleEntryContextVo;
import com.xfgl.school.mapper.SysUserRoleMapper;
import com.xfgl.school.mapper.XfptSupplierSettleApplyMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.ISupplierUserRelService;
import com.xfgl.school.service.IXfptHmdService;
import com.xfgl.school.service.IXfptNoticeSupplierRelService;
import com.xfgl.school.service.IXfptSupplierSettleApplyService;
import com.xfgl.school.mp.MpUtil;
import com.xfgl.school.utils.MailUtils;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.RemoteUserService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.api.model.LoginUser;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 供应商入驻申请Service业务层处理
 */
@Slf4j
@Service
public class XfptSupplierSettleApplyServiceImpl
        extends ServiceImpl<XfptSupplierSettleApplyMapper, XfptSupplierSettleApply>
        implements IXfptSupplierSettleApplyService {

    private static final String AUDIT_STATUS_PENDING = "0";
    private static final String AUDIT_STATUS_APPROVED = "1";
    private static final String AUDIT_STATUS_REJECTED = "2";
    private static final String SOURCE_LOGIN_BOUND = "LOGIN_BOUND";
    private static final String SOURCE_REGISTER = "REGISTER";
    private static final Long SUPPLIER_ROLE_ID = 115L;
    private static final String SUPPLIER_ROLE_KEY = "gys";
    private static final String PHONE_PATTERN = "^1[3-9]\\d{9}$";
    private static final String INVALID_PASSWORD_PATTERN = ".*[<>\"'|\\\\].*";
    private static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
    private static final String SUPPLIER_NOTICE_CATEGORY = "GYSRZGG";

    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private IXfptHmdService xfptHmdService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteNoticService remoteNoticService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private ISupplierUserRelService supplierUserRelService;

    @Autowired
    private IXfptNoticeSupplierRelService noticeSupplierRelService;

    @Autowired
    private MpUtil mpUtil;

    @Autowired
    private MailUtils mailUtils;

    @Override
    public boolean submitApply(XfptSupplierSettleApply apply) {
        return submitRegisterApply(apply);
    }

    @Override
    public boolean submitRegisterApply(XfptSupplierSettleApply apply) {
        ensureApplyPresent(apply);
        XfptNotice notice = validateNoticeOrThrow(apply.getNoticeId(), true);
        normalize(apply);
        apply.setSourceType(SOURCE_REGISTER);
        apply.setNoticeTitle(notice.getTitle());
        apply.setNoticeUserId(notice.getUserId());
        validateRegisterApply(apply);
        checkRegisterDuplicate(apply);
        apply.setPasswordHash(SecurityUtils.encryptPassword(apply.getPassword()));
        apply.setPassword(null);
        fillPendingMeta(apply, "supplier_settle_public");
        return save(apply);
    }

    @Override
    public boolean submitLoginBoundApply(XfptSupplierSettleApply apply) {
        ensureApplyPresent(apply);
        XfptNotice notice = validateNoticeOrThrow(apply.getNoticeId(), true);
        SysUser currentUser = resolveSubmitLoginUser(apply);
        SupplierMessage supplier = resolveBoundSupplier(currentUser);
        if (supplier == null) {
            throw new ServiceException("当前账号未绑定供应商，请联系管理员或改走注册入驻流程");
        }

        normalize(apply);
        validateSupplierFields(apply, false);
        checkSupplierBlocked(apply.getSupplierName());
        checkLoginBoundDuplicate(notice.getId(), supplier);

        apply.setSourceType(SOURCE_LOGIN_BOUND);
        apply.setNoticeTitle(notice.getTitle());
        apply.setNoticeUserId(notice.getUserId());
        apply.setApplyUserId(currentUser.getUserId());
        apply.setSupplierId(supplier.getId());
        apply.setUsername(currentUser.getUserName());
        apply.setPhone(StringUtils.trim(defaultString(currentUser.getPhonenumber(), currentUser.getPhone())));
        apply.setEmail(StringUtils.trim(currentUser.getEmail()));
        apply.setPassword(null);
        apply.setPasswordHash("");
        fillPendingMeta(apply, defaultString(SecurityUtils.getUsername(), "supplier_settle_login"));
        return save(apply);
    }

    @Override
    public SupplierSettleEntryContextVo getPublicEntryContext(Long noticeId) {
        SupplierSettleEntryContextVo vo = new SupplierSettleEntryContextVo();
        XfptNotice notice = validateNoticeOrNull(noticeId);
        vo.setNotice(notice);
        if (notice == null) {
            vo.setCanSubmit(false);
            vo.setMessage("未找到该入驻公告");
            return vo;
        }
        if (isNoticeExpired(notice)) {
            vo.setCanSubmit(false);
            vo.setMessage("该入驻公告已过期");
            return vo;
        }
        vo.setCanSubmit(true);
        return vo;
    }

    @Override
    public SupplierSettleEntryContextVo getLoginEntryContext(Long noticeId) {
        SupplierSettleEntryContextVo vo = getPublicEntryContext(noticeId);
        if (vo.getNotice() == null || !vo.isCanSubmit()) {
            return vo;
        }

        SysUser currentUser = currentLoginUser();
        SupplierMessage supplier = resolveBoundSupplier(currentUser);
        vo.setLoginUser(currentUser);
        vo.setSupplier(supplier);
        if (supplier == null) {
            vo.setCanSubmit(false);
            vo.setMessage("当前账号未绑定供应商，请联系管理员或改走注册入驻流程");
            return vo;
        }

        XfptSupplierSettleApply latestApply = findLatestApply(vo.getNotice().getId(), supplier.getId(), supplier.getSupplierName());
        vo.setLatestApply(latestApply);
        if (latestApply != null) {
            if (AUDIT_STATUS_PENDING.equals(latestApply.getAuditStatus())) {
                vo.setCanSubmit(false);
                vo.setMessage("当前公告已提交入驻申请，请等待审核");
                return vo;
            }
            if (AUDIT_STATUS_APPROVED.equals(latestApply.getAuditStatus())) {
                vo.setCanSubmit(false);
                vo.setMessage("当前公告的入驻申请已审核通过，请勿重复提交");
                return vo;
            }
        }
        vo.setCanSubmit(true);
        return vo;
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

        XfptNotice notice = validateNoticeOrThrow(apply.getNoticeId(), false);
        ensureCurrentUserCanAudit(apply, notice);
        SupplierMessage supplier;
        Long userId = apply.getApplyUserId();

        if (SOURCE_LOGIN_BOUND.equalsIgnoreCase(apply.getSourceType())) {
            supplier = resolveSupplierByApply(apply);
            if (supplier == null) {
                throw new ServiceException("未找到该申请绑定的供应商信息");
            }
            if (userId != null && !sysUserRoleMapper.isSupplierUser(userId)) {
                sysUserRoleMapper.insertUserRole(userId, SUPPLIER_ROLE_ID);
            }
            fillSupplierMessage(supplier, apply);
            supplier.setUpdateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
            supplier.setUpdateTime(new Date());
            supplierMessageService.updateById(supplier);
        } else {
            SysUser sysUser = buildRegisterUser(apply);
            R<Boolean> registerResult = remoteUserService.addInnerUser(sysUser, SecurityConstants.INNER);
            if (registerResult == null || R.FAIL == registerResult.getCode()) {
                String msg = registerResult != null ? registerResult.getMsg() : "用户注册失败";
                if (msg == null || !msg.contains("注册账号已存在")) {
                    throw new ServiceException(msg);
                }
            }

            R<LoginUser> userResult = remoteUserService.getUserInfo(apply.getUsername(), SecurityConstants.INNER);
            if (userResult == null || R.FAIL == userResult.getCode() || userResult.getData() == null) {
                throw new ServiceException("获取用户信息失败，无法分配角色");
            }
            userId = userResult.getData().getSysUser().getUserId();
            if (!sysUserRoleMapper.isSupplierUser(userId)) {
                sysUserRoleMapper.insertUserRole(userId, SUPPLIER_ROLE_ID);
            }

            supplier = findSupplierByExactName(apply.getSupplierName());
            if (supplier == null) {
                supplier = buildSupplierMessage(apply);
                supplierMessageService.save(supplier);
            } else {
                fillSupplierMessage(supplier, apply);
                supplier.setUpdateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
                supplier.setUpdateTime(new Date());
                supplierMessageService.updateById(supplier);
            }

            Long boundSupplierId = supplierUserRelService.findFirstSupplierIdByUserId(userId);
            if (boundSupplierId != null && !boundSupplierId.equals(supplier.getId())) {
                throw new ServiceException("该用户已绑定其他供应商，无法重复绑定");
            }
            if (boundSupplierId == null) {
                supplierUserRelService.createRel(supplier.getId(), userId);
            }
        }

        apply.setSupplierId(supplier.getId());
        apply.setApplyUserId(userId);
        apply.setAuditStatus(AUDIT_STATUS_APPROVED);
        apply.setAuditTime(new Date());
        apply.setUpdateBy(SecurityUtils.getUsername());
        updateById(apply);

        saveNoticeSupplierRel(notice.getId(), supplier.getId(), apply.getId(), apply.getAuditTime());

        if (StringUtils.isNotBlank(apply.getEmail())) {
            mailUtils.sendApproveEmail(apply.getEmail(), apply.getSupplierName());
        }
    }

    @Override
    public SupplierSettleEntryContextVo checkBinding(String username, String password) {
        SupplierSettleEntryContextVo vo = new SupplierSettleEntryContextVo();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            vo.setCanSubmit(false);
            vo.setMessage("用户名和密码不能为空");
            return vo;
        }
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        if (userResult == null || R.FAIL == userResult.getCode() || userResult.getData() == null) {
            vo.setCanSubmit(false);
            vo.setMessage("用户名不存在");
            return vo;
        }
        SysUser sysUser = userResult.getData().getSysUser();
        String storedPassword = sysUser.getPassword();
        // 如果存储密码不是 BCrypt 格式，则需要先通过 mpUtil 解密
        if (StringUtils.isNotEmpty(storedPassword) && !storedPassword.startsWith("$2a$") && !storedPassword.startsWith("$2b$") && !storedPassword.startsWith("$2y$")) {
            try {
                String token = mpUtil.getToken();
                storedPassword = mpUtil.decrypt(storedPassword, token);
            } catch (Exception e) {
                log.warn("checkBinding: 密码解密失败, username={}", username, e);
            }
        }
        if (!SecurityUtils.matchesPassword(password, storedPassword)) {
            vo.setCanSubmit(false);
            vo.setMessage("密码错误");
            return vo;
        }
        SupplierMessage supplier = resolveBoundSupplier(sysUser);
        if (supplier == null) {
            vo.setCanSubmit(false);
            vo.setMessage("该账号未绑定厂商信息");
            return vo;
        }
        vo.setLoginUser(sysUser);
        vo.setSupplier(supplier);
        vo.setCanSubmit(true);
        return vo;
    }

    @Override
    public List<XfptSupplierSettleApply> listApprovedByPublisher(XfptSupplierSettleApply query) {
        String supplierName = query != null ? StringUtils.trimToNull(query.getSupplierName()) : null;
        String noticeTitle = query != null ? StringUtils.trimToNull(query.getNoticeTitle()) : null;
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId == null) {
            throw new ServiceException("当前登录信息不存在，请重新登录后再试");
        }
        List<XfptSupplierSettleApply> list = lambdaQuery()
                .eq(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_APPROVED)
                .eq(XfptSupplierSettleApply::getNoticeUserId, currentUserId)
                .like(StringUtils.isNotBlank(supplierName),
                        XfptSupplierSettleApply::getSupplierName, supplierName)
                .like(StringUtils.isNotBlank(noticeTitle),
                        XfptSupplierSettleApply::getNoticeTitle, noticeTitle)
                .orderByDesc(XfptSupplierSettleApply::getAuditTime)
                .list();
        list.forEach(item -> item.setAuditBy(defaultString(item.getUpdateBy(), "")));
        return list;
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
        XfptNotice notice = validateNoticeOrThrow(apply.getNoticeId(), false);
        ensureCurrentUserCanAudit(apply, notice);
        apply.setAuditStatus(AUDIT_STATUS_REJECTED);
        apply.setAuditTime(new Date());
        apply.setAuditRemark(remark);
        apply.setUpdateBy(SecurityUtils.getUsername());
        updateById(apply);

        if (StringUtils.isNotBlank(apply.getEmail())) {
            mailUtils.sendRejectEmail(apply.getEmail(), apply.getSupplierName(), apply.getAuditRemark());
        }
    }

    private SupplierMessage buildSupplierMessage(XfptSupplierSettleApply apply) {
        SupplierMessage supplier = new SupplierMessage();
        fillSupplierMessage(supplier, apply);
        supplier.setCreateTime(new Date());
        supplier.setCreateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
        supplier.setDelFlag("0");
        return supplier;
    }

    private void fillSupplierMessage(SupplierMessage supplier, XfptSupplierSettleApply apply) {
        supplier.setSupplierName(apply.getSupplierName());
        supplier.setSupplierPhone(apply.getSupplierPhone());
        supplier.setSupplierType(apply.getSupplierType());
        supplier.setLegalRepresentative(apply.getLegalRepresentative());
        supplier.setStaffNumber(apply.getStaffNumber());
        supplier.setEstablishedTime(apply.getEstablishedTime());
        supplier.setSupplierAddress(apply.getSupplierAddress());
        supplier.setSupplierIntroduce(apply.getSupplierIntroduce());
        supplier.setBusinessLicense(apply.getBusinessLicense());
        supplier.setRegisteredCapital(apply.getRegisteredCapital());
        supplier.setXypj(apply.getXypj());
    }

    private SysUser buildRegisterUser(XfptSupplierSettleApply apply) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(apply.getUsername());
        sysUser.setNickName(apply.getSupplierName());
        sysUser.setPassword(apply.getPasswordHash());
        sysUser.setPhonenumber(apply.getPhone());
        sysUser.setEmail(apply.getEmail());
        sysUser.setSex("0");
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");
        sysUser.setCreateTime(new Date());
        sysUser.setCreateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle"));
        return sysUser;
    }

    private void saveNoticeSupplierRel(Long noticeId, Long supplierId, Long applyId, Date auditTime) {
        XfptNoticeSupplierRel rel = noticeSupplierRelService.lambdaQuery()
                .eq(XfptNoticeSupplierRel::getNoticeId, noticeId)
                .eq(XfptNoticeSupplierRel::getSupplierId, supplierId)
                .one();
        if (rel == null) {
            rel = new XfptNoticeSupplierRel();
            rel.setNoticeId(noticeId);
            rel.setSupplierId(supplierId);
            rel.setCreateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
            rel.setCreateTime(new Date());
            rel.setDelFlag("0");
        }
        rel.setApplyId(applyId);
        rel.setAuditTime(auditTime);
        rel.setAuditBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
        rel.setUpdateBy(defaultString(SecurityUtils.getUsername(), "supplier_settle_audit"));
        rel.setUpdateTime(new Date());
        noticeSupplierRelService.saveOrUpdate(rel);
    }

    private void fillPendingMeta(XfptSupplierSettleApply apply, String createBy) {
        apply.setAuditStatus(AUDIT_STATUS_PENDING);
        apply.setTargetRoleId(SUPPLIER_ROLE_ID);
        apply.setTargetRoleKey(SUPPLIER_ROLE_KEY);
        apply.setCreateBy(createBy);
        apply.setCreateTime(new Date());
        apply.setDelFlag("0");
    }

    private SupplierMessage resolveSupplierByApply(XfptSupplierSettleApply apply) {
        if (apply.getSupplierId() != null) {
            SupplierMessage byId = supplierMessageService.getById(apply.getSupplierId());
            if (byId != null) {
                return byId;
            }
        }
        return findSupplierByExactName(apply.getSupplierName());
    }

    private void ensureCurrentUserCanAudit(XfptSupplierSettleApply apply, XfptNotice notice) {
        Long publisherUserId = notice == null ? null : notice.getUserId();
        if (publisherUserId == null) {
            publisherUserId = apply.getNoticeUserId();
        }
        SysUser currentUser = currentLoginUser();
        Long currentUserId = currentUser.getUserId();
        if (publisherUserId != null && publisherUserId.equals(currentUserId)) {
            syncNoticePublisher(apply, notice);
            return;
        }

        String noticeCreator = notice == null ? null : StringUtils.trim(notice.getCreateBy());
        String currentUsername = StringUtils.trim(currentUser.getUserName());
        String currentNickname = StringUtils.trim(currentUser.getNickName());
        if (StringUtils.isNotBlank(noticeCreator)
                && (noticeCreator.equals(currentUsername) || noticeCreator.equals(currentNickname))) {
            log.info("供应商入驻审核命中发布人名称兜底校验, applyId={}, noticeId={}, currentUserId={}, username={}, nickName={}, noticeCreateBy={}, noticeUserId={}",
                    apply.getId(), apply.getNoticeId(), currentUserId, currentUsername, currentNickname,
                    noticeCreator, publisherUserId);
            syncNoticePublisher(apply, notice);
            return;
        }

        log.warn("供应商入驻审核权限校验失败, applyId={}, noticeId={}, currentUserId={}, username={}, nickName={}, noticeUserId={}, applyNoticeUserId={}, noticeCreateBy={}",
                apply.getId(), apply.getNoticeId(), currentUserId, currentUsername, currentNickname,
                publisherUserId, apply.getNoticeUserId(), noticeCreator);
        throw new ServiceException("仅公告发布人可审核该申请");
    }

    private void syncNoticePublisher(XfptSupplierSettleApply apply, XfptNotice notice) {
        if (notice != null && notice.getUserId() != null && !notice.getUserId().equals(apply.getNoticeUserId())) {
            apply.setNoticeUserId(notice.getUserId());
        }
    }

    private XfptNotice validateNoticeOrThrow(Long noticeId, boolean checkExpired) {
        XfptNotice notice = validateNoticeOrNull(noticeId);
        if (notice == null) {
            throw new ServiceException("未找到该入驻公告");
        }
        if (checkExpired && isNoticeExpired(notice)) {
            throw new ServiceException("该入驻公告已过期");
        }
        return notice;
    }

    private XfptNotice validateNoticeOrNull(Long noticeId) {
        if (noticeId == null) {
            return null;
        }
        R<XfptNotice> result = remoteNoticService.getNoticeInfo(noticeId, SecurityConstants.INNER);
        if (result == null || R.FAIL == result.getCode()) {
            return null;
        }
        XfptNotice notice = result.getData();
        if (notice == null) {
            return null;
        }
        if (!NoticeStatus.FB.getCode().equals(notice.getStatus())) {
            return null;
        }
        if (!SUPPLIER_NOTICE_CATEGORY.equals(notice.getCatagory())) {
            return null;
        }
        return notice;
    }

    private boolean isNoticeExpired(XfptNotice notice) {
        return notice.getEndTime() != null && notice.getEndTime().before(new Date());
    }

    private SupplierMessage resolveBoundSupplier(SysUser currentUser) {
        if (currentUser == null || currentUser.getUserId() == null) {
            return null;
        }
        Long supplierId = supplierUserRelService.findFirstSupplierIdByUserId(currentUser.getUserId());
        if (supplierId == null) {
            return null;
        }
        return supplierMessageService.getById(supplierId);
    }

    private SysUser currentLoginUser() {
        if (SecurityUtils.getLoginUser() == null || SecurityUtils.getLoginUser().getSysUser() == null) {
            throw new ServiceException("当前登录信息不存在，请重新登录后再试");
        }
        return SecurityUtils.getLoginUser().getSysUser();
    }

    private SysUser resolveSubmitLoginUser(XfptSupplierSettleApply apply) {
        if (SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getSysUser() != null) {
            return SecurityUtils.getLoginUser().getSysUser();
        }
        if (StringUtils.isBlank(apply.getUsername()) || StringUtils.isBlank(apply.getPassword())) {
            throw new ServiceException("当前登录信息不存在，请重新登录后再试");
        }
        R<LoginUser> userResult = remoteUserService.getUserInfo(apply.getUsername(), SecurityConstants.INNER);
        if (userResult == null || R.FAIL == userResult.getCode() || userResult.getData() == null
                || userResult.getData().getSysUser() == null) {
            throw new ServiceException("用户名不存在");
        }
        SysUser sysUser = userResult.getData().getSysUser();
        if (!SecurityUtils.matchesPassword(apply.getPassword(), sysUser.getPassword())) {
            throw new ServiceException("密码错误");
        }
        return sysUser;
    }

    private SupplierMessage findSupplierByExactName(String supplierName) {
        if (StringUtils.isBlank(supplierName)) {
            return null;
        }
        return supplierMessageService.lambdaQuery()
                .eq(SupplierMessage::getSupplierName, supplierName.trim())
                .last("limit 1")
                .one();
    }

    private XfptSupplierSettleApply findLatestApply(Long noticeId, Long supplierId, String supplierName) {
        if (noticeId == null) {
            return null;
        }
        if (supplierId != null) {
            return lambdaQuery()
                    .eq(XfptSupplierSettleApply::getNoticeId, noticeId)
                    .eq(XfptSupplierSettleApply::getSupplierId, supplierId)
                    .orderByDesc(XfptSupplierSettleApply::getCreateTime)
                    .last("limit 1")
                    .one();
        }
        if (StringUtils.isBlank(supplierName)) {
            return null;
        }
        return lambdaQuery()
                .eq(XfptSupplierSettleApply::getNoticeId, noticeId)
                .eq(XfptSupplierSettleApply::getSupplierName, supplierName.trim())
                .orderByDesc(XfptSupplierSettleApply::getCreateTime)
                .last("limit 1")
                .one();
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
        apply.setEmail(StringUtils.trim(apply.getEmail()));
        apply.setSupplierIntroduce(StringUtils.trim(apply.getSupplierIntroduce()));
    }

    private void ensureApplyPresent(XfptSupplierSettleApply apply) {
        if (apply == null) {
            throw new ServiceException("申请信息不能为空");
        }
    }

    private void validateRegisterApply(XfptSupplierSettleApply apply) {
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
        if (StringUtils.isBlank(apply.getEmail()) || !apply.getEmail().matches(EMAIL_PATTERN)) {
            throw new ServiceException("邮箱格式不正确");
        }
        validateSupplierFields(apply, true);
    }

    private void validateSupplierFields(XfptSupplierSettleApply apply, boolean requireEmail) {
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
        if (requireEmail && (StringUtils.isBlank(apply.getEmail()) || !apply.getEmail().matches(EMAIL_PATTERN))) {
            throw new ServiceException("邮箱格式不正确");
        }
    }

    private void checkRegisterDuplicate(XfptSupplierSettleApply apply) {
        checkSupplierBlocked(apply.getSupplierName());

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

        long duplicatedNoticeApply = lambdaQuery()
                .eq(XfptSupplierSettleApply::getNoticeId, apply.getNoticeId())
                .eq(XfptSupplierSettleApply::getSupplierName, apply.getSupplierName())
                .in(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_PENDING, AUDIT_STATUS_APPROVED)
                .count();
        if (duplicatedNoticeApply > 0) {
            throw new ServiceException("该供应商已针对当前公告提交申请，请勿重复提交");
        }
    }

    private void checkLoginBoundDuplicate(Long noticeId, SupplierMessage supplier) {
        long duplicatedNoticeApply = lambdaQuery()
                .eq(XfptSupplierSettleApply::getNoticeId, noticeId)
                .eq(XfptSupplierSettleApply::getSupplierId, supplier.getId())
                .in(XfptSupplierSettleApply::getAuditStatus, AUDIT_STATUS_PENDING, AUDIT_STATUS_APPROVED)
                .count();
        if (duplicatedNoticeApply > 0) {
            throw new ServiceException("当前公告已提交入驻申请，请勿重复提交");
        }
    }

    private void checkSupplierBlocked(String supplierName) {
        if (xfptHmdService.lambdaQuery().eq(XfptHmd::getName, supplierName).count() > 0) {
            throw new ServiceException("此供应商已加入黑名单，请勿提交入驻申请");
        }
    }

    private String defaultString(String first, String second) {
        return StringUtils.isNotBlank(first) ? first : second;
    }
}
