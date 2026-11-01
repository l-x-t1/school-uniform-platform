package com.xfgl.school.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.domain.XfptSupplierApplyLog;
import com.xfgl.school.domain.XfptSupplierQualification;
import com.xfgl.school.domain.vo.SupplierApplyAuditRequest;
import com.xfgl.school.domain.vo.SupplierApplyNoticeVo;
import com.xfgl.school.domain.vo.SupplierApplySubmitRequest;
import com.xfgl.school.mapper.XfptSupplierApplyLogMapper;
import com.xfgl.school.mapper.XfptSupplierApplyMapper;
import com.xfgl.school.mapper.XfptSupplierQualificationMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptOrganizationModeService;
import com.xfgl.school.service.IXfptSupplierApplyService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XfptSupplierApplyServiceImpl extends ServiceImpl<XfptSupplierApplyMapper, XfptSupplierApply> implements IXfptSupplierApplyService {
    private static final String APPLY_STATUS_DRAFT = "0";
    private static final String APPLY_STATUS_SUBMITTED = "1";
    private static final String APPLY_STATUS_WITHDRAWN = "2";

    private static final String QUALIFICATION_PENDING = "0";
    private static final String QUALIFICATION_APPROVED = "1";
    private static final String QUALIFICATION_REJECTED = "2";

    private static final String MATERIAL_BUSINESS_LICENSE = "BUSINESS_LICENSE";
    private static final String MATERIAL_PRODUCTION_LICENSE = "PRODUCTION_LICENSE";
    private static final String MATERIAL_QUALITY_REPORT = "QUALITY_REPORT";
    private static final String MATERIAL_SAMPLE_IMAGE = "SAMPLE_IMAGE";
    private static final String MATERIAL_ATTACHMENT = "ATTACHMENT";

    private static final String ACTION_SAVE_DRAFT = "SAVE_DRAFT";
    private static final String ACTION_SUBMIT = "SUBMIT";
    private static final String ACTION_WITHDRAW = "WITHDRAW";
    private static final String ACTION_AUDIT_PASS = "AUDIT_PASS";
    private static final String ACTION_AUDIT_REJECT = "AUDIT_REJECT";

    @Autowired
    private XfptSupplierApplyMapper xfptSupplierApplyMapper;

    @Autowired
    private IXfptOrganizationModeService xfptOrganizationModeService;

    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private XfptSupplierQualificationMapper supplierQualificationMapper;

    @Autowired
    private XfptSupplierApplyLogMapper supplierApplyLogMapper;

    @Override
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply) {
        List<XfptSupplierApply> list = xfptSupplierApplyMapper.selectXfptSupplierApplyList(xfptSupplierApply);
        list.forEach(this::fillStatusText);
        return list;
    }

    @Override
    public XfptSupplierApply selectXfptSupplierApplyById(Long id) {
        XfptSupplierApply apply = xfptSupplierApplyMapper.selectXfptSupplierApplyById(id);
        fillStatusText(apply);
        return apply;
    }

    @Override
    public List<SupplierApplyNoticeVo> selectSupplierNoticeCenterList(Long supplierId, XfptSupplierApply query) {
        return xfptSupplierApplyMapper.selectSupplierNoticeCenterList(supplierId, query);
    }

    @Override
    public Map<String, Object> buildSupplierApplyContext(Long supplierId, Long noticeId) {
        SupplierMessage supplier = getSupplierOrThrow(supplierId);
        SupplierApplyNoticeVo notice = getNoticeOverviewOrThrow(noticeId);
        XfptSupplierApply apply = xfptSupplierApplyMapper.selectSupplierApplyByNoticeIdAndSupplierId(noticeId, supplierId);
        fillStatusText(apply);

        Map<String, Object> form = buildMaterialForm(supplier, notice, apply);
        boolean expired = isExpired(notice.getBmjzTime());
        Map<String, Object> permissions = new LinkedHashMap<>();
        permissions.put("canSaveDraft", !expired && (apply == null || !APPLY_STATUS_SUBMITTED.equals(apply.getApplyStatus())));
        permissions.put("canSubmit", !expired);
        permissions.put("canWithdraw", !expired && apply != null && !APPLY_STATUS_WITHDRAWN.equals(apply.getApplyStatus()));

        Map<String, Object> supplierInfo = new LinkedHashMap<>();
        supplierInfo.put("supplierId", supplier.getId());
        supplierInfo.put("supplierName", supplier.getSupplierName());
        supplierInfo.put("supplierPhone", supplier.getSupplierPhone());
        supplierInfo.put("businessLicense", supplier.getBusinessLicense());
        supplierInfo.put("legalRepresentative", supplier.getLegalRepresentative());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("notice", notice);
        result.put("supplier", supplierInfo);
        result.put("apply", apply);
        result.put("form", form);
        result.put("expired", expired);
        result.put("statusText", apply == null ? "未报名" : apply.getStatusText());
        result.put("permissions", permissions);
        if (apply != null) {
            result.put("logs", selectApplyLogs(apply.getId()));
        } else {
            result.put("logs", Collections.emptyList());
        }
        return result;
    }

    @Override
    public XfptSupplierApply saveSupplierDraft(Long supplierId, SupplierApplySubmitRequest request) {
        return saveOrSubmit(supplierId, request, false);
    }

    @Override
    public XfptSupplierApply submitSupplierApply(Long supplierId, SupplierApplySubmitRequest request) {
        return saveOrSubmit(supplierId, request, true);
    }

    @Override
    public void withdrawSupplierApply(Long supplierId, Long noticeId) {
        XfptSupplierApply apply = xfptSupplierApplyMapper.selectSupplierApplyByNoticeIdAndSupplierId(noticeId, supplierId);
        if (apply == null) {
            throw new ServiceException("未找到对应的报名记录");
        }
        SupplierApplyNoticeVo notice = getNoticeOverviewOrThrow(noticeId);
        validateNoticeEditable(notice);
        if (APPLY_STATUS_WITHDRAWN.equals(apply.getApplyStatus())) {
            return;
        }

        apply.setApplyStatus(APPLY_STATUS_WITHDRAWN);
        apply.setUpdateTime(new Date());
        updateById(apply);
        recordApplyLog(apply, ACTION_WITHDRAW, "供应商撤回报名", apply.getApplyFilesJson());
    }

    @Override
    public List<XfptSupplierApplyLog> selectApplyLogs(Long applyId) {
        if (applyId == null) {
            return Collections.emptyList();
        }
        return supplierApplyLogMapper.selectList(
                Wrappers.<XfptSupplierApplyLog>lambdaQuery()
                        .eq(XfptSupplierApplyLog::getApplyId, applyId)
                        .eq(XfptSupplierApplyLog::getDelFlag, "0")
                        .orderByAsc(XfptSupplierApplyLog::getVersionNo, XfptSupplierApplyLog::getId)
        );
    }

    @Override
    public void auditPass(Long schoolDeptId, SupplierApplyAuditRequest request) {
        auditSupplierApply(schoolDeptId, request, true);
    }

    @Override
    public void auditReject(Long schoolDeptId, SupplierApplyAuditRequest request) {
        auditSupplierApply(schoolDeptId, request, false);
    }

    private XfptSupplierApply saveOrSubmit(Long supplierId, SupplierApplySubmitRequest request, boolean submit) {
        SupplierMessage supplier = getSupplierOrThrow(supplierId);
        SupplierApplyNoticeVo notice = getNoticeOverviewOrThrow(request.getNoticeId());
        validateNoticeEditable(notice);

        XfptSupplierApply currentApply = xfptSupplierApplyMapper.selectSupplierApplyByNoticeIdAndSupplierId(request.getNoticeId(), supplierId);
        if (submit) {
            validateSubmitMaterials(notice, supplier, request);
            validateSignupLimit(notice, currentApply);
        }

        upsertQualification(supplierId, MATERIAL_PRODUCTION_LICENSE, "生产许可", request.getProductionLicenseUrls());
        upsertQualification(supplierId, MATERIAL_QUALITY_REPORT, "质检报告", request.getQualityReportUrls());

        String applyFilesJson = buildApplyFilesJson(supplier, notice, request);
        Date now = new Date();
        XfptSupplierApply apply = currentApply == null ? new XfptSupplierApply() : currentApply;
        apply.setNoticeId(notice.getNoticeId());
        apply.setSchoolId(notice.getSchoolId());
        apply.setDeptId(notice.getSchoolId());
        apply.setSupplierId(supplierId);
        apply.setSupplierName(supplier.getSupplierName());
        apply.setApplyFilesJson(applyFilesJson);
        apply.setRemark(request.getRemark());
        if (StringUtils.isBlank(apply.getApplyNo())) {
            apply.setApplyNo(buildApplyNo(notice.getNoticeId(), supplierId));
        }
        if (StringUtils.isBlank(apply.getBidStageStatus())) {
            apply.setBidStageStatus("0");
        }
        if (StringUtils.isBlank(apply.getBidResultStatus())) {
            apply.setBidResultStatus("0");
        }

        if (submit) {
            apply.setApplyStatus(APPLY_STATUS_SUBMITTED);
            apply.setQualificationStatus(QUALIFICATION_PENDING);
            apply.setApplyTime(now);
            apply.setAuditTime(null);
            apply.setAuditRemark(null);
        } else {
            apply.setApplyStatus(APPLY_STATUS_DRAFT);
            apply.setQualificationStatus(QUALIFICATION_PENDING);
        }

        if (apply.getId() == null) {
            save(apply);
        } else {
            updateById(apply);
            if (submit) {
                lambdaUpdate()
                        .eq(XfptSupplierApply::getId, apply.getId())
                        .set(XfptSupplierApply::getAuditTime, null)
                        .set(XfptSupplierApply::getAuditRemark, null)
                        .update();
            }
        }

        recordApplyLog(
                apply,
                submit ? ACTION_SUBMIT : ACTION_SAVE_DRAFT,
                StringUtils.defaultIfBlank(request.getRemark(), submit ? "供应商提交报名" : "供应商保存草稿"),
                applyFilesJson
        );
        return selectXfptSupplierApplyById(apply.getId());
    }

    private void auditSupplierApply(Long schoolDeptId, SupplierApplyAuditRequest request, boolean pass) {
        XfptSupplierApply apply = selectXfptSupplierApplyById(request.getApplyId());
        if (apply == null) {
            throw new ServiceException("报名记录不存在");
        }
        if (schoolDeptId != null && apply.getSchoolId() != null && !Objects.equals(apply.getSchoolId(), schoolDeptId)) {
            throw new ServiceException("无权审核该报名记录");
        }
        apply.setQualificationStatus(pass ? QUALIFICATION_APPROVED : QUALIFICATION_REJECTED);
        apply.setAuditTime(new Date());
        apply.setAuditRemark(StringUtils.defaultIfBlank(request.getAuditRemark(), pass ? "审核通过" : "审核驳回"));
        updateById(apply);
        recordApplyLog(
                apply,
                pass ? ACTION_AUDIT_PASS : ACTION_AUDIT_REJECT,
                apply.getAuditRemark(),
                apply.getApplyFilesJson()
        );
    }

    private SupplierMessage getSupplierOrThrow(Long supplierId) {
        SupplierMessage supplier = supplierMessageService.getById(supplierId);
        if (supplier == null) {
            throw new ServiceException("当前账号尚未完成供应商入驻或未绑定供应商档案");
        }
        return supplier;
    }

    private SupplierApplyNoticeVo getNoticeOverviewOrThrow(Long noticeId) {
        SupplierApplyNoticeVo notice = xfptSupplierApplyMapper.selectNoticeOverviewById(noticeId);
        if (notice == null) {
            XfptOrganizationMode organizationMode = xfptOrganizationModeService.getById(noticeId);
            if (organizationMode == null) {
                throw new ServiceException("采购公告不存在");
            }
            notice = new SupplierApplyNoticeVo();
            notice.setNoticeId(organizationMode.getId());
            notice.setSchoolId(organizationMode.getDeptId());
            notice.setNoticeTitle(organizationMode.getTitle());
            notice.setNoticeCode("CG-" + organizationMode.getId());
            notice.setBmjzTime(organizationMode.getBmjzTime());
            notice.setSignupLimit(organizationMode.getSignupLimit());
            notice.setIsSamplesPic(organizationMode.getIsSamplesPic());
            notice.setSamplesPic(organizationMode.getSamplesPic());
            notice.setNoticeFile(organizationMode.getNoticeFile());
            notice.setOtherFile(organizationMode.getOtherFile());
        }
        return notice;
    }

    private void validateSubmitMaterials(SupplierApplyNoticeVo notice, SupplierMessage supplier, SupplierApplySubmitRequest request) {
        if (StringUtils.isBlank(supplier.getBusinessLicense())) {
            throw new ServiceException("请先完善供应商营业执照后再报名");
        }
        if (StringUtils.isBlank(request.getProductionLicenseUrls())) {
            throw new ServiceException("请上传生产许可");
        }
        if (StringUtils.isBlank(request.getQualityReportUrls())) {
            throw new ServiceException("请上传质检报告");
        }
        if ("1".equals(StringUtils.defaultString(notice.getIsSamplesPic())) && StringUtils.isBlank(request.getSampleImageUrls())) {
            throw new ServiceException("该采购公告要求上传样衣图");
        }
    }

    private void validateSignupLimit(SupplierApplyNoticeVo notice, XfptSupplierApply currentApply) {
        Integer signupLimit = notice.getSignupLimit();
        if (signupLimit == null || signupLimit <= 0) {
            return;
        }
        long currentCount = lambdaQuery()
                .eq(XfptSupplierApply::getNoticeId, notice.getNoticeId())
                .ne(XfptSupplierApply::getApplyStatus, APPLY_STATUS_WITHDRAWN)
                .count();
        if (currentApply == null || APPLY_STATUS_WITHDRAWN.equals(currentApply.getApplyStatus())) {
            if (currentCount >= signupLimit) {
                throw new ServiceException("当前采购公告报名人数已满");
            }
        }
    }

    private void validateNoticeEditable(SupplierApplyNoticeVo notice) {
        if (notice == null || notice.getNoticeId() == null) {
            throw new ServiceException("采购公告不存在");
        }
        XfptOrganizationMode organizationMode = xfptOrganizationModeService.getById(notice.getNoticeId());
        if (organizationMode == null) {
            throw new ServiceException("采购公告不存在");
        }
        if (!"1".equals(StringUtils.defaultString(organizationMode.getIsRelease()))) {
            throw new ServiceException("当前采购公告未发布，暂不能报名");
        }
        if (isExpired(notice.getBmjzTime())) {
            throw new ServiceException("当前采购公告已截止报名");
        }
    }

    private boolean isExpired(Date deadline) {
        return deadline != null && deadline.before(new Date());
    }

    private void upsertQualification(Long supplierId, String type, String label, String fileUrls) {
        if (StringUtils.isBlank(fileUrls)) {
            return;
        }
        XfptSupplierQualification qualification = supplierQualificationMapper.selectOne(
                Wrappers.<XfptSupplierQualification>lambdaQuery()
                        .eq(XfptSupplierQualification::getSupplierId, supplierId)
                        .eq(XfptSupplierQualification::getQualificationType, type)
                        .eq(XfptSupplierQualification::getDelFlag, "0")
                        .last("limit 1")
        );
        Date now = new Date();
        if (qualification == null) {
            qualification = new XfptSupplierQualification();
            qualification.setSupplierId(supplierId);
            qualification.setQualificationType(type);
            qualification.setCreateTime(now);
            qualification.setCreateBy(SecurityUtils.getUsername());
            qualification.setDelFlag("0");
        }
        qualification.setQualificationLabel(label);
        qualification.setFileUrls(fileUrls);
        qualification.setSource("APPLY");
        qualification.setUpdateTime(now);
        qualification.setUpdateBy(SecurityUtils.getUsername());
        if (qualification.getId() == null) {
            supplierQualificationMapper.insert(qualification);
        } else {
            supplierQualificationMapper.updateById(qualification);
        }
    }

    private Map<String, Object> buildMaterialForm(SupplierMessage supplier, SupplierApplyNoticeVo notice, XfptSupplierApply apply) {
        Map<String, String> snapshotMap = parseMaterialMap(apply == null ? null : apply.getApplyFilesJson());
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("businessLicense", supplier.getBusinessLicense());
        form.put("productionLicenseUrls", getMaterialValue(snapshotMap, MATERIAL_PRODUCTION_LICENSE, getQualificationFileUrls(supplier.getId(), MATERIAL_PRODUCTION_LICENSE)));
        form.put("qualityReportUrls", getMaterialValue(snapshotMap, MATERIAL_QUALITY_REPORT, getQualificationFileUrls(supplier.getId(), MATERIAL_QUALITY_REPORT)));
        form.put("sampleImageUrls", getMaterialValue(snapshotMap, MATERIAL_SAMPLE_IMAGE, ""));
        form.put("attachmentUrls", getMaterialValue(snapshotMap, MATERIAL_ATTACHMENT, ""));
        form.put("remark", apply == null ? "" : apply.getRemark());
        form.put("sampleRequired", "1".equals(StringUtils.defaultString(notice.getIsSamplesPic())));
        return form;
    }

    private String getMaterialValue(Map<String, String> snapshotMap, String materialType, String fallback) {
        String value = snapshotMap.get(materialType);
        return StringUtils.isBlank(value) ? fallback : value;
    }

    private String getQualificationFileUrls(Long supplierId, String materialType) {
        XfptSupplierQualification qualification = supplierQualificationMapper.selectOne(
                Wrappers.<XfptSupplierQualification>lambdaQuery()
                        .eq(XfptSupplierQualification::getSupplierId, supplierId)
                        .eq(XfptSupplierQualification::getQualificationType, materialType)
                        .eq(XfptSupplierQualification::getDelFlag, "0")
                        .last("limit 1")
        );
        return qualification == null ? "" : StringUtils.defaultString(qualification.getFileUrls());
    }

    private String buildApplyFilesJson(SupplierMessage supplier, SupplierApplyNoticeVo notice, SupplierApplySubmitRequest request) {
        List<Map<String, Object>> items = new ArrayList<>();
        addMaterialItem(items, MATERIAL_BUSINESS_LICENSE, "营业执照", true, "PROFILE", supplier.getBusinessLicense());
        addMaterialItem(items, MATERIAL_PRODUCTION_LICENSE, "生产许可", true, "QUALIFICATION", request.getProductionLicenseUrls());
        addMaterialItem(items, MATERIAL_QUALITY_REPORT, "质检报告", true, "QUALIFICATION", request.getQualityReportUrls());
        if ("1".equals(StringUtils.defaultString(notice.getIsSamplesPic()))) {
            addMaterialItem(items, MATERIAL_SAMPLE_IMAGE, "样衣图", true, "NOTICE_RULE", request.getSampleImageUrls());
        } else if (StringUtils.isNotBlank(request.getSampleImageUrls())) {
            addMaterialItem(items, MATERIAL_SAMPLE_IMAGE, "样衣图", false, "NOTICE_RULE", request.getSampleImageUrls());
        }
        addMaterialItem(items, MATERIAL_ATTACHMENT, "补充附件", false, "UPLOAD", request.getAttachmentUrls());
        return JSON.toJSONString(items);
    }

    private void addMaterialItem(List<Map<String, Object>> items, String type, String label, boolean required, String source, String fileUrls) {
        List<String> urls = splitUrls(fileUrls);
        if (!required && urls.isEmpty()) {
            return;
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("type", type);
        item.put("label", label);
        item.put("required", required);
        item.put("source", source);
        item.put("fileUrls", urls);
        item.put("snapshotTime", formatDateTime(new Date()));
        items.add(item);
    }

    private Map<String, String> parseMaterialMap(String applyFilesJson) {
        Map<String, String> result = new LinkedHashMap<>();
        if (StringUtils.isBlank(applyFilesJson)) {
            return result;
        }
        try {
            JSONArray array = JSON.parseArray(applyFilesJson);
            if (array == null) {
                return result;
            }
            for (Object item : array) {
                if (item instanceof String) {
                    result.put("LEGACY_" + result.size(), String.valueOf(item));
                    continue;
                }
                JSONObject jsonObject = (JSONObject) item;
                String type = jsonObject.getString("type");
                Object fileUrls = jsonObject.get("fileUrls");
                if (StringUtils.isBlank(type)) {
                    continue;
                }
                if (fileUrls instanceof List) {
                    result.put(type, ((List<?>) fileUrls).stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(",")));
                } else if (fileUrls != null) {
                    result.put(type, String.valueOf(fileUrls));
                }
            }
        } catch (Exception ex) {
            List<String> legacyFiles = splitUrls(applyFilesJson);
            if (!legacyFiles.isEmpty()) {
                result.put("LEGACY_0", String.join(",", legacyFiles));
            }
        }
        return result;
    }

    private List<String> splitUrls(String urls) {
        if (StringUtils.isBlank(urls)) {
            return Collections.emptyList();
        }
        return Arrays.stream(String.valueOf(urls).split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private String buildApplyNo(Long noticeId, Long supplierId) {
        return "CGAPPLY-" + formatDateTime(new Date()).replaceAll("[^0-9]", "") + "-" + noticeId + "-" + supplierId;
    }

    private void recordApplyLog(XfptSupplierApply apply, String actionType, String actionRemark, String snapshotJson) {
        Integer nextVersion = 1;
        XfptSupplierApplyLog latestLog = supplierApplyLogMapper.selectOne(
                Wrappers.<XfptSupplierApplyLog>lambdaQuery()
                        .eq(XfptSupplierApplyLog::getApplyId, apply.getId())
                        .eq(XfptSupplierApplyLog::getDelFlag, "0")
                        .orderByDesc(XfptSupplierApplyLog::getVersionNo, XfptSupplierApplyLog::getId)
                        .last("limit 1")
        );
        if (latestLog != null && latestLog.getVersionNo() != null) {
            nextVersion = latestLog.getVersionNo() + 1;
        }

        XfptSupplierApplyLog log = new XfptSupplierApplyLog();
        Date now = new Date();
        log.setApplyId(apply.getId());
        log.setNoticeId(apply.getNoticeId());
        log.setSupplierId(apply.getSupplierId());
        log.setActionType(actionType);
        log.setVersionNo(nextVersion);
        log.setActionRemark(actionRemark);
        log.setActionUser(SecurityUtils.getUsername());
        log.setActionTime(now);
        log.setSnapshotJson(snapshotJson);
        log.setCreateBy(SecurityUtils.getUsername());
        log.setCreateTime(now);
        log.setDelFlag("0");
        supplierApplyLogMapper.insert(log);
    }

    private void fillStatusText(XfptSupplierApply apply) {
        if (apply == null) {
            return;
        }
        if (StringUtils.isBlank(apply.getStatusText())) {
            if (APPLY_STATUS_WITHDRAWN.equals(apply.getApplyStatus())) {
                apply.setStatusText("已撤回");
            } else if (QUALIFICATION_APPROVED.equals(apply.getQualificationStatus())) {
                apply.setStatusText("已通过");
            } else if (QUALIFICATION_REJECTED.equals(apply.getQualificationStatus())) {
                apply.setStatusText("已驳回");
            } else if (APPLY_STATUS_SUBMITTED.equals(apply.getApplyStatus())) {
                apply.setStatusText("待审核");
            } else {
                apply.setStatusText("待提交");
            }
        }
        if (StringUtils.isBlank(apply.getNoticeCode()) && apply.getNoticeId() != null) {
            apply.setNoticeCode("CG-" + apply.getNoticeId());
        }
    }

    private String formatDateTime(Date date) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
