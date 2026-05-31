package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptGroupNoticeSupplier;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
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
 * 缁熻喘鍏憡Service涓氬姟灞傚鐞?
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

    @Autowired
    private XfptDeliveryOrderMapper xfptDeliveryOrderMapper;

    /**
     * 鏌ヨ缁熻喘鍏憡鍒楄〃
     *
     * @param xfptGroupNotice 缁熻喘鍏憡
     * @return 缁熻喘鍏憡
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
     * 鏌ヨ缁熻喘鍏憡
     *
     * @param id 缁熻喘鍏憡涓婚敭
     * @return 缁熻喘鍏憡
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
     * 鏂板缁熻喘鍏憡
     *
     * @param xfptGroupNotice 缁熻喘鍏憡
     * @return 缁撴灉
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
     * 淇敼缁熻喘鍏憡
     *
     * @param xfptGroupNotice 缁熻喘鍏憡
     * @return 缁撴灉
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
     * 鎵归噺鍒犻櫎缁熻喘鍏憡
     *
     * @param ids 闇€瑕佸垹闄ょ殑缁熻喘鍏憡涓婚敭
     * @return 缁撴灉
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
     * 鍒犻櫎缁熻喘鍏憡淇℃伅
     *
     * @param id 缁熻喘鍏憡涓婚敭
     * @return 缁撴灉
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
        if (isAdminUser()) {
            query.setDeptId(null);
            query.setSchoolId(null);
            return xfptGroupNoticeMapper.selectGroupNoticeProgressList(query);
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        query.setDeptId(currentSchool.getDeptId());
        query.setSchoolId(currentSchool.getId());
        return xfptGroupNoticeMapper.selectGroupNoticeProgressList(query);
    }

    @Override
    public List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(Long orderId, Long supplierId)
    {
        XfptOrderInfo orderInfo = xfptGroupNoticeMapper.selectProgressOrderById(orderId);
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new ServiceException("统购订单不存在");
        }
        if (!"1".equals(orderInfo.getOrderType())) {
            throw new ServiceException("只能查看统购订单明细");
        }
        if (isAdminUser()) {
            return xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(orderId, supplierId, null, null);
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        if (!Objects.equals(orderInfo.getSchoolId(), currentSchool.getId())
                || !Objects.equals(orderInfo.getDeptId(), currentSchool.getDeptId())) {
            throw new ServiceException("只能查看当前学校的订单明细");
        }
        return xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(orderId, supplierId, currentSchool.getId(), currentSchool.getDeptId());
    }

    @Override
    public XfptOrderInfo viewGroupNoticeOrder(Long orderId)
    {
        XfptOrderInfo orderInfo = getSupplierProgressOrder(orderId);
        if ("0".equals(normalizeProgressStatus(orderInfo))) {
            xfptGroupNoticeMapper.updateProgressStatus(orderId, "1");
            orderInfo.setProgressStatus("1");
        }
        return orderInfo;
    }

    @Override
    public int startGroupNoticeOrder(Long orderId)
    {
        XfptOrderInfo orderInfo = getSupplierProgressOrder(orderId);
        requireProgressStatus(orderInfo, "1", "订单需先查看后才能开始制作");
        return xfptGroupNoticeMapper.updateProgressStatus(orderId, "2");
    }

    @Override
    public int finishGroupNoticeOrder(Long orderId)
    {
        XfptOrderInfo orderInfo = getSupplierProgressOrder(orderId);
        requireProgressStatus(orderInfo, "2", "订单需处于制作中才能标记完成");
        return xfptGroupNoticeMapper.updateProgressStatus(orderId, "3");
    }

    @Transactional
    @Override
    public int fillGroupNoticeOrderLogistics(Long orderId, String logisticsNo)
    {
        if (StringUtils.isBlank(logisticsNo)) {
            throw new ServiceException("物流单号不能为空");
        }
        XfptOrderInfo orderInfo = getSupplierProgressOrder(orderId);
        requireProgressStatus(orderInfo, "3", "订单需制作完成后才能填写物流单号");
        String trimmedLogisticsNo = logisticsNo.trim();
        saveProgressDeliveryOrder(orderInfo, trimmedLogisticsNo);
        int rows = xfptGroupNoticeMapper.updateProgressStatus(orderId, "4");
        if (rows <= 0) {
            throw new ServiceException("failed to update group order progress");
        }
        return rows;
    }

    private XfptOrderInfo getSupplierProgressOrder(Long orderId)
    {
        XfptOrderInfo orderInfo = xfptGroupNoticeMapper.selectProgressOrderById(orderId);
        if (orderInfo == null || orderInfo.getId() == null) {
            throw new ServiceException("统购订单不存在");
        }
        if (!"1".equals(orderInfo.getOrderType())) {
            throw new ServiceException("只能操作统购订单");
        }
        Long supplierId = getCurrentSupplierId();
        if (!Objects.equals(orderInfo.getSupplierId(), supplierId)) {
            throw new ServiceException("只能操作当前供应商自己的订单");
        }
        if (StringUtils.isNotBlank(orderInfo.getLogisticsNo()) && !"4".equals(orderInfo.getProgressStatus())) {
            orderInfo.setProgressStatus("4");
        }
        return orderInfo;
    }

    private String normalizeProgressStatus(XfptOrderInfo orderInfo)
    {
        if (StringUtils.isNotBlank(orderInfo.getLogisticsNo())) {
            return "4";
        }
        return StringUtils.isBlank(orderInfo.getProgressStatus()) ? "0" : orderInfo.getProgressStatus();
    }

    private void requireProgressStatus(XfptOrderInfo orderInfo, String expectedStatus, String message)
    {
        String progressStatus = normalizeProgressStatus(orderInfo);
        if (!expectedStatus.equals(progressStatus)) {
            throw new ServiceException(message);
        }
    }

    /**
     * 鏂板缁熻喘鍏憡鍟嗗搧淇℃伅
     *
     * @param xfptGroupNotice 缁熻喘鍏憡瀵硅薄
     */
    // Keep logistics in delivery orders and read them back from delivery data.
    private void saveProgressDeliveryOrder(XfptOrderInfo orderInfo, String logisticsNo)
    {
        XfptDeliveryOrder deliveryOrder = xfptDeliveryOrderMapper.selectOne(
                Wrappers.<XfptDeliveryOrder>lambdaQuery()
                        .eq(XfptDeliveryOrder::getOrderId, orderInfo.getId())
                        .eq(XfptDeliveryOrder::getDelFlag, "0")
                        .orderByDesc(XfptDeliveryOrder::getId)
                        .last("limit 1"));
        Date now = new Date();
        String username = SecurityUtils.getUsername();
        if (deliveryOrder == null) {
            deliveryOrder = new XfptDeliveryOrder();
            deliveryOrder.setDeliveryNo(buildProgressDeliveryNo(orderInfo.getId()));
            deliveryOrder.setNoticeId(orderInfo.getNoticeId());
            deliveryOrder.setOrderId(orderInfo.getId());
            deliveryOrder.setOrderNo(orderInfo.getOrderNo());
            deliveryOrder.setSchoolId(orderInfo.getSchoolId());
            deliveryOrder.setSupplierId(orderInfo.getSupplierId());
            deliveryOrder.setReceiverName(defaultReceiverValue(orderInfo.getReceiverName(), orderInfo.getSchoolName()));
            deliveryOrder.setReceiverPhone(defaultReceiverValue(orderInfo.getReceiverPhone(), ""));
            deliveryOrder.setReceiverAddress(defaultReceiverValue(orderInfo.getReceiverAddress(), ""));
            deliveryOrder.setLogisticsNo(logisticsNo);
            deliveryOrder.setDeliveryStatus("1");
            deliveryOrder.setDeliveryTime(now);
            deliveryOrder.setDeptId(orderInfo.getDeptId());
            deliveryOrder.setCreateBy(username);
            deliveryOrder.setCreateTime(now);
            deliveryOrder.setUpdateBy(username);
            deliveryOrder.setUpdateTime(now);
            deliveryOrder.setDelFlag("0");
            xfptDeliveryOrderMapper.insert(deliveryOrder);
            return;
        }
        deliveryOrder.setLogisticsNo(logisticsNo);
        deliveryOrder.setDeliveryStatus("1");
        if (deliveryOrder.getDeliveryTime() == null) {
            deliveryOrder.setDeliveryTime(now);
        }
        deliveryOrder.setUpdateBy(username);
        deliveryOrder.setUpdateTime(now);
        xfptDeliveryOrderMapper.updateById(deliveryOrder);
    }

    private String buildProgressDeliveryNo(Long orderId)
    {
        return "DEL-" + System.currentTimeMillis() + "-" + orderId;
    }

    private String defaultReceiverValue(String value, String fallback)
    {
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return fallback == null ? "" : fallback;
    }

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
            throw new ServiceException("未找到当前供应商");
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
