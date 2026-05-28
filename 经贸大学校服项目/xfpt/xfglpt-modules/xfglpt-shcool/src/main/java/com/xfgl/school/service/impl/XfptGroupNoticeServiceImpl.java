package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptGroupNoticeSupplier;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import com.xfgl.school.mapper.XfptGroupNoticeMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptGroupNoticeService;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 统购公告Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGroupNoticeServiceImpl extends ServiceImpl<XfptGroupNoticeMapper, XfptGroupNotice> implements IXfptGroupNoticeService {
    private static final DateTimeFormatter NOTICE_CODE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Autowired
    private XfptGroupNoticeMapper xfptGroupNoticeMapper;

    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;

    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告
     */
    @Override
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        if (!isAdminUser()) {
            XfptSchoolMessage currentSchool = getCurrentSchool();
            xfptGroupNotice.setDeptId(currentSchool.getDeptId());
            xfptGroupNotice.setSchoolId(currentSchool.getId());
        }
        return xfptGroupNoticeMapper.selectXfptGroupNoticeList(xfptGroupNotice);
    }

    @Override
    public List<XfptGroupNotice> selectSupplierGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        xfptGroupNotice.setSupplierId(getCurrentSupplierId());
        return xfptGroupNoticeMapper.selectSupplierGroupNoticeList(xfptGroupNotice);
    }

    /**
     * 查询统购公告
     *
     * @param id 统购公告主键
     * @return 统购公告
     */
    @Override
    public XfptGroupNotice selectXfptGroupNoticeById(Long id)
    {
        XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectById(id);
        if (!isAdminUser()) {
            checkNoticeBelongsToCurrentSchool(xfptGroupNotice);
        } else if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
        fillNoticeSupplierIds(xfptGroupNotice);
        return xfptGroupNotice;
    }

    @Override
    public XfptGroupNotice selectSupplierGroupNoticeById(Long id)
    {
        Long supplierId = getCurrentSupplierId();
        XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectSupplierGroupNoticeById(id, supplierId);
        if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
        return xfptGroupNotice;
    }

    /**
     * 新增统购公告
     *
     * @param xfptGroupNotice 统购公告
     * @return 结果
     */
    @Transactional
    @Override
    public int insertXfptGroupNotice(XfptGroupNotice xfptGroupNotice)
    {
        fillGroupNoticeDefaults(xfptGroupNotice);
        fillNoticeSchoolInfo(xfptGroupNotice, null);
        int rows = xfptGroupNoticeMapper.insert(xfptGroupNotice);
        insertXfptGroupNoticeItem(xfptGroupNotice);
        if (isAdminUser()) {
            saveXfptGroupNoticeSupplier(xfptGroupNotice);
        }
        return rows;
    }

    /**
     * 修改统购公告
     *
     * @param xfptGroupNotice 统购公告
     * @return 结果
     */
    @Transactional
    @Override
    public int updateXfptGroupNotice(XfptGroupNotice xfptGroupNotice)
    {
        XfptGroupNotice oldNotice = xfptGroupNoticeMapper.selectById(xfptGroupNotice.getId());
        if (!isAdminUser()) {
            checkNoticeBelongsToCurrentSchool(oldNotice);
        } else if (oldNotice == null) {
            throw new ServiceException("公告不存在");
        }
        fillGroupNoticeDefaults(xfptGroupNotice);
        fillNoticeSchoolInfo(xfptGroupNotice, oldNotice);
        xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(xfptGroupNotice.getId());
        insertXfptGroupNoticeItem(xfptGroupNotice);
        if (isAdminUser()) {
            saveXfptGroupNoticeSupplier(xfptGroupNotice);
        }
        return xfptGroupNoticeMapper.updateById(xfptGroupNotice);
    }

    /**
     * 批量删除统购公告
     *
     * @param ids 需要删除的统购公告主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteXfptGroupNoticeByIds(Long[] ids)
    {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            if (!isAdminUser()) {
                checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(id));
            }
            xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
        }
        xfptGroupNoticeMapper.deleteXfptGroupNoticeSupplierByNoticeIds(idList);
        return xfptGroupNoticeMapper.deleteBatchIds(idList);
    }

    /**
     * 删除统购公告信息
     *
     * @param id 统购公告主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteXfptGroupNoticeById(Long id)
    {
        if (!isAdminUser()) {
            checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(id));
        }
        xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
        xfptGroupNoticeMapper.deleteXfptGroupNoticeSupplierByNoticeId(id);
        return xfptGroupNoticeMapper.deleteById(id);
    }

    @Override
    public List<GroupNoticeProgressVo> selectGroupNoticeProgressList(GroupNoticeProgressQuery query)
    {
        XfptSchoolMessage currentSchool = getCurrentSchool();
        query.setDeptId(currentSchool.getDeptId());
        query.setSchoolId(currentSchool.getId());
        return xfptGroupNoticeMapper.selectGroupNoticeProgressList(query);
    }

    @Override
    public List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(Long noticeId, Long supplierId)
    {
        checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(noticeId));
        XfptSchoolMessage currentSchool = getCurrentSchool();
        return xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(noticeId, supplierId, currentSchool.getDeptId());
    }

    /**
     * 新增统购公告商品信息
     *
     * @param xfptGroupNotice 统购公告对象
     */
    private void insertXfptGroupNoticeItem(XfptGroupNotice xfptGroupNotice)
    {
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNotice.getXfptGroupNoticeItemList();
        Long id = xfptGroupNotice.getId();
        if (StringUtils.isNotEmpty(xfptGroupNoticeItemList)) {
            for (XfptGroupNoticeItem xfptGroupNoticeItem : xfptGroupNoticeItemList) {
                xfptGroupNoticeItem.setNoticeId(id);
                xfptGroupNoticeItem.setDeptId(xfptGroupNotice.getDeptId());
                if (xfptGroupNoticeItem.getDelFlag() == null || "".equals(xfptGroupNoticeItem.getDelFlag())) {
                    xfptGroupNoticeItem.setDelFlag("0");
                }
            }
            xfptGroupNoticeMapper.batchInsertXfptGroupNoticeItem(xfptGroupNoticeItemList);
        }
    }

    private void fillNoticeSupplierIds(XfptGroupNotice xfptGroupNotice)
    {
        if (xfptGroupNotice != null && xfptGroupNotice.getId() != null) {
            xfptGroupNotice.setSupplierIds(xfptGroupNoticeMapper.selectSupplierIdsByNoticeId(xfptGroupNotice.getId()));
        }
    }

    private void saveXfptGroupNoticeSupplier(XfptGroupNotice xfptGroupNotice)
    {
        Long noticeId = xfptGroupNotice.getId();
        xfptGroupNoticeMapper.deleteXfptGroupNoticeSupplierByNoticeId(noticeId);
        List<Long> supplierIds = xfptGroupNotice.getSupplierIds();
        if (supplierIds == null || supplierIds.isEmpty()) {
            return;
        }

        Set<Long> uniqueSupplierIds = supplierIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (uniqueSupplierIds.isEmpty()) {
            return;
        }

        List<SupplierMessage> suppliers = supplierMessageService.listByIds(uniqueSupplierIds);
        Map<Long, SupplierMessage> supplierMap = suppliers.stream()
                .collect(Collectors.toMap(SupplierMessage::getId, Function.identity(), (left, right) -> left));
        Date now = new Date();
        String username = SecurityUtils.getUsername();
        List<XfptGroupNoticeSupplier> supplierList = new ArrayList<>();
        for (Long supplierId : uniqueSupplierIds) {
            SupplierMessage supplier = supplierMap.get(supplierId);
            XfptGroupNoticeSupplier relation = new XfptGroupNoticeSupplier();
            relation.setNoticeId(noticeId);
            relation.setSupplierId(supplierId);
            relation.setSupplierName(supplier == null ? null : supplier.getSupplierName());
            relation.setCreateBy(username);
            relation.setCreateTime(now);
            relation.setUpdateBy(username);
            relation.setUpdateTime(now);
            relation.setDelFlag("0");
            supplierList.add(relation);
        }
        xfptGroupNoticeMapper.batchInsertXfptGroupNoticeSupplier(supplierList);
    }

    private void fillGroupNoticeDefaults(XfptGroupNotice xfptGroupNotice)
    {
        if (xfptGroupNotice.getNoticeCode() == null || "".equals(xfptGroupNotice.getNoticeCode().trim())) {
            xfptGroupNotice.setNoticeCode(generateNoticeCode());
        } else {
            xfptGroupNotice.setNoticeCode(xfptGroupNotice.getNoticeCode().trim());
        }
        if (xfptGroupNotice.getNoticeType() == null || "".equals(xfptGroupNotice.getNoticeType())) {
            xfptGroupNotice.setNoticeType("1");
        }
        if (xfptGroupNotice.getStatus() == null || "".equals(xfptGroupNotice.getStatus())) {
            xfptGroupNotice.setStatus("0");
        }
        if (xfptGroupNotice.getApplyStatus() == null || "".equals(xfptGroupNotice.getApplyStatus())) {
            xfptGroupNotice.setApplyStatus("0");
        }
        if (xfptGroupNotice.getDelFlag() == null || "".equals(xfptGroupNotice.getDelFlag())) {
            xfptGroupNotice.setDelFlag("0");
        }
    }

    private String generateNoticeCode()
    {
        return "TG" + LocalDateTime.now().format(NOTICE_CODE_FORMATTER);
    }

    private void fillCurrentSchoolInfo(XfptGroupNotice xfptGroupNotice)
    {
        XfptSchoolMessage currentSchool = getCurrentSchool();
        xfptGroupNotice.setDeptId(currentSchool.getDeptId());
        xfptGroupNotice.setSchoolId(currentSchool.getId());
        xfptGroupNotice.setSchoolName(currentSchool.getSchoolName());
    }

    private void fillNoticeSchoolInfo(XfptGroupNotice xfptGroupNotice, XfptGroupNotice oldNotice)
    {
        if (!isAdminUser()) {
            fillCurrentSchoolInfo(xfptGroupNotice);
            return;
        }
        XfptSchoolMessage targetSchool = getTargetSchool(xfptGroupNotice, oldNotice);
        xfptGroupNotice.setDeptId(targetSchool.getDeptId());
        xfptGroupNotice.setSchoolId(targetSchool.getId());
        xfptGroupNotice.setSchoolName(targetSchool.getSchoolName());
    }

    private XfptSchoolMessage getTargetSchool(XfptGroupNotice xfptGroupNotice, XfptGroupNotice oldNotice)
    {
        Long schoolId = xfptGroupNotice.getSchoolId();
        Long deptId = xfptGroupNotice.getDeptId();
        if (schoolId == null && deptId == null && oldNotice != null) {
            schoolId = oldNotice.getSchoolId();
            deptId = oldNotice.getDeptId();
        }
        if (schoolId != null) {
            XfptSchoolMessage schoolMessage = xfptSchoolMessageService.getById(schoolId);
            if (schoolMessage != null && schoolMessage.getId() != null) {
                return schoolMessage;
            }
        }
        if (deptId != null) {
            List<XfptSchoolMessage> list = xfptSchoolMessageService.lambdaQuery()
                    .eq(XfptSchoolMessage::getDeptId, deptId)
                    .orderByDesc(XfptSchoolMessage::getId)
                    .last("limit 1")
                    .list();
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        throw new ServiceException("请选择公告所属学校");
    }

    private void checkNoticeBelongsToCurrentSchool(XfptGroupNotice xfptGroupNotice)
    {
        if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        if (!Objects.equals(xfptGroupNotice.getDeptId(), currentSchool.getDeptId())
                || !Objects.equals(xfptGroupNotice.getSchoolId(), currentSchool.getId())) {
            throw new ServiceException("只能操作当前学校的公告");
        }
    }

    private XfptSchoolMessage getCurrentSchool()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        Long deptId = sysUser == null ? null : sysUser.getDeptId();
        if (deptId == null) {
            throw new ServiceException("未获取到当前登录学校");
        }
        XfptSchoolMessage schoolMessage = xfptSchoolMessageService.lambdaQuery()
                .eq(XfptSchoolMessage::getDeptId, deptId)
                .one();
        if (schoolMessage == null || schoolMessage.getId() == null) {
            throw new ServiceException("当前登录学校信息不存在，请先完善学校信息");
        }
        return schoolMessage;
    }

    private Long getCurrentSupplierId()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        Long supplierId = supplierAccessSupport.resolveSupplierId(sysUser);
        if (supplierId == null) {
            throw new ServiceException("Current supplier not found");
        }
        return supplierId;
    }

    private boolean isAdminUser()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        if (SecurityUtils.isAdmin(loginUser.getUserid())) {
            return true;
        }
        Set<String> roles = loginUser.getRoles();
        return roles != null && roles.stream().anyMatch(role -> "admin".equalsIgnoreCase(role));
    }
}
