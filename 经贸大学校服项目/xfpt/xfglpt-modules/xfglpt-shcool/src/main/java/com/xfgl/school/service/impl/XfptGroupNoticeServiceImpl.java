package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson2.JSON;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptGroupNoticeSupplier;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.mapper.XfptGoodsMapper;
import com.xfgl.school.mapper.XfptGoodsSizeMapper;
import com.xfgl.school.mapper.XfptGroupNoticeMapper;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.mapper.XfptSchoolSupplierGoodsRelMapper;
import com.xfgl.school.mapper.XfptSchoolSupplierRelMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptGroupNoticeService;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.service.IXfptStudentInfoService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
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

    @Autowired
    private XfptSchoolSupplierRelMapper xfptSchoolSupplierRelMapper;

    @Autowired
    private XfptSchoolSupplierGoodsRelMapper xfptSchoolSupplierGoodsRelMapper;

    @Autowired
    private XfptGoodsMapper xfptGoodsMapper;

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;

    @Autowired
    private IXfptStudentInfoService xfptStudentInfoService;

    /**
     * 鏌ヨ缁熻喘鍏憡鍒楄〃
     *
     * @param xfptGroupNotice 缁熻喘鍏憡
     * @return 缁熻喘鍏憡
     */
    @Override
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        if (applyParentSchoolScope(xfptGroupNotice)) {
            return xfptGroupNoticeMapper.selectXfptGroupNoticeList(xfptGroupNotice);
        }
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
        xfptGroupNotice.setNoticeType("1");
        xfptGroupNotice.setSupplierId(getCurrentSupplierId());
        return xfptGroupNoticeMapper.selectSupplierGroupNoticeList(xfptGroupNotice);
    }

    @Override
    public List<XfptGroupNotice> selectSupplierCooperationGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        xfptGroupNotice.setNoticeType("1");
        xfptGroupNotice.setSupplierId(getCurrentSupplierId());
        return xfptGroupNoticeMapper.selectSupplierCooperationGroupNoticeList(xfptGroupNotice);
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
        return selectXfptGroupNoticeById(id, null);
    }

    @Override
    public XfptGroupNotice selectXfptGroupNoticeById(Long id, String simId)
    {
        XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectById(id);
        if (StringUtils.isNotBlank(simId)) {
            checkNoticeBelongsToParentSchools(xfptGroupNotice, simId);
        } else if (!isAdminUser()) {
            checkNoticeBelongsToCurrentSchool(xfptGroupNotice);
        } else if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        enrichNoticeItems(xfptGroupNoticeItemList);
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
        if (!"1".equals(xfptGroupNotice.getNoticeType())) {
            throw new ServiceException("供应商端只能查看学校发布的选购公告");
        }
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        enrichNoticeItems(xfptGroupNoticeItemList);
        xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
        markSupplierViewedProgress(id, supplierId);
        return xfptGroupNotice;
    }

    @Override
    public XfptGroupNotice selectSupplierCooperationGroupNoticeById(Long id)
    {
        Long supplierId = getCurrentSupplierId();
        XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectSupplierCooperationGroupNoticeById(id, supplierId);
        if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        if (!"1".equals(xfptGroupNotice.getNoticeType())) {
            throw new ServiceException("供应商端只能查看学校发布的选购公告");
        }
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        enrichNoticeItems(xfptGroupNoticeItemList);
        xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
        return xfptGroupNotice;
    }

    @Override
    public List<XfptSchoolSupplierRel> selectNoticeSupplierOptions(Long schoolId)
    {
        XfptSchoolMessage targetSchool = resolveNoticeOptionSchool(schoolId);
        CooperationScope scope = loadActiveCooperationScope(targetSchool.getId());
        return new ArrayList<>(scope.supplierRelBySupplierId.values());
    }

    @Override
    public List<XfptGoods> selectNoticeGoodsOptions(Long schoolId, String supplierIds)
    {
        XfptSchoolMessage targetSchool = resolveNoticeOptionSchool(schoolId);
        CooperationScope scope = loadActiveCooperationScope(targetSchool.getId());
        Set<Long> selectedSupplierIds = normalizeIdSet(parseIdList(supplierIds));
        if (selectedSupplierIds.isEmpty()) {
            return new ArrayList<>();
        }
        ensureSelectedSuppliersAllowed(selectedSupplierIds, scope);
        Set<Long> allowedGoodsIds = collectAllowedGoodsIds(scope, selectedSupplierIds);
        if (allowedGoodsIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<XfptGoods> goodsList = xfptGoodsMapper.selectBatchIds(allowedGoodsIds);
        if (goodsList == null || goodsList.isEmpty()) {
            return new ArrayList<>();
        }
        goodsList.removeIf(goods -> goods == null
                || !"0".equals(goods.getDelFlag())
                || !"1".equals(goods.getStatus())
                || !isGroupNoticeGoods(goods)
                || !selectedSupplierIds.contains(goods.getSupplierId()));
        goodsList.sort((left, right) -> {
            int supplierCompare = compareNullableLong(left.getSupplierId(), right.getSupplierId());
            if (supplierCompare != 0) {
                return supplierCompare;
            }
            int nameCompare = compareNullableString(left.getGoodsName(), right.getGoodsName());
            if (nameCompare != 0) {
                return nameCompare;
            }
            return compareNullableLong(left.getId(), right.getId());
        });
        return goodsList;
    }

    @Override
    public List<XfptGoodsSize> selectNoticeGoodsSizeOptions(Long schoolId, String supplierIds)
    {
        XfptSchoolMessage targetSchool = resolveNoticeOptionSchool(schoolId);
        CooperationScope scope = loadActiveCooperationScope(targetSchool.getId());
        Set<Long> selectedSupplierIds = normalizeIdSet(parseIdList(supplierIds));
        if (selectedSupplierIds.isEmpty()) {
            return new ArrayList<>();
        }
        ensureSelectedSuppliersAllowed(selectedSupplierIds, scope);
        Set<Long> allowedGoodsIds = collectAllowedGoodsIds(scope, selectedSupplierIds);
        if (allowedGoodsIds.isEmpty()) {
            return new ArrayList<>();
        }
        return xfptGoodsSizeMapper.selectList(
                Wrappers.<XfptGoodsSize>lambdaQuery()
                        .in(XfptGoodsSize::getGoodsId, allowedGoodsIds)
                        .eq(XfptGoodsSize::getDelFlag, "0")
                        .eq(XfptGoodsSize::getStatus, "1")
                        .orderByAsc(XfptGoodsSize::getGoodsId)
                        .orderByAsc(XfptGoodsSize::getSortNum)
                        .orderByAsc(XfptGoodsSize::getId));
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
        validateNoticeScopeAndItems(xfptGroupNotice);
        int rows = xfptGroupNoticeMapper.insert(xfptGroupNotice);
        insertXfptGroupNoticeItem(xfptGroupNotice);
        saveXfptGroupNoticeSupplier(xfptGroupNotice);
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
        validateNoticeScopeAndItems(xfptGroupNotice);
        xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(xfptGroupNotice.getId());
        insertXfptGroupNoticeItem(xfptGroupNotice);
        saveXfptGroupNoticeSupplier(xfptGroupNotice);
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
        if (orderId == null) {
            throw new ServiceException("统购公告不存在");
        }
        if (isAdminUser()) {
            return fillProgressDetailFallback(orderId, supplierId, xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(orderId, supplierId, null, null));
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        XfptGroupNotice groupNotice = xfptGroupNoticeMapper.selectById(orderId);
        if (groupNotice == null || groupNotice.getId() == null) {
            throw new ServiceException("统购公告不存在");
        }
        if (!Objects.equals(groupNotice.getSchoolId(), currentSchool.getId())
                || !Objects.equals(groupNotice.getDeptId(), currentSchool.getDeptId())) {
            throw new ServiceException("只能查看当前学校的订购明细");
        }
        return fillProgressDetailFallback(orderId, supplierId,
                xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(orderId, supplierId, currentSchool.getId(), currentSchool.getDeptId()));
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

    private void validateNoticeScopeAndItems(XfptGroupNotice xfptGroupNotice)
    {
        if (xfptGroupNotice == null || xfptGroupNotice.getSchoolId() == null) {
            return;
        }
        CooperationScope scope = loadActiveCooperationScope(xfptGroupNotice.getSchoolId());
        Set<Long> selectedSupplierIds = normalizeIdSet(xfptGroupNotice.getSupplierIds());
        if (selectedSupplierIds.isEmpty()) {
            throw new ServiceException("请选择发布供应商");
        }
        ensureSelectedSuppliersAllowed(selectedSupplierIds, scope);
        xfptGroupNotice.setSupplierIds(new ArrayList<>(selectedSupplierIds));

        List<XfptGroupNoticeItem> noticeItems = xfptGroupNotice.getXfptGroupNoticeItemList();
        boolean goodsRequired = !"2".equals(String.valueOf(xfptGroupNotice.getNoticeType()));
        if (noticeItems == null || noticeItems.isEmpty()) {
            if (goodsRequired) {
                throw new ServiceException("请选择关联商品");
            }
            xfptGroupNotice.setXfptGroupNoticeItemList(new ArrayList<>());
            return;
        }
        Set<Long> goodsIds = noticeItems.stream()
                .map(XfptGroupNoticeItem::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (goodsIds.isEmpty()) {
            throw new ServiceException("请选择关联商品");
        }
        if (goodsIds.size() != noticeItems.size()) {
            throw new ServiceException("公告商品不能重复");
        }
        Set<Long> allowedGoodsIds = collectAllowedGoodsIds(scope, selectedSupplierIds);
        if (allowedGoodsIds.isEmpty()) {
            throw new ServiceException("当前选择的合作供应商下没有启用中的合作商品，无法创建统购公告");
        }
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            if (noticeItem.getGoodsId() == null) {
                throw new ServiceException("关联商品不能为空");
            }
            if (!allowedGoodsIds.contains(noticeItem.getGoodsId())) {
                throw new ServiceException("商品【" + defaultGoodsName(noticeItem.getGoodsName(), noticeItem.getGoodsId()) + "】不属于当前学校与所选供应商合作中的启用商品");
            }
        }
        hydrateNoticeItems(xfptGroupNotice, goodsIds, selectedSupplierIds);
    }

    private String defaultGoodsName(String goodsName, Long goodsId)
    {
        if (StringUtils.isNotBlank(goodsName)) {
            return goodsName;
        }
        return String.valueOf(goodsId);
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

    private void hydrateNoticeItems(XfptGroupNotice xfptGroupNotice, Set<Long> goodsIds, Set<Long> selectedSupplierIds)
    {
        List<XfptGroupNoticeItem> noticeItems = xfptGroupNotice.getXfptGroupNoticeItemList();
        if (noticeItems == null || noticeItems.isEmpty() || goodsIds.isEmpty()) {
            return;
        }
        Map<Long, XfptGoods> goodsMap = xfptGoodsMapper.selectBatchIds(goodsIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(XfptGoods::getId, Function.identity(), (left, right) -> left));
        Map<Long, List<XfptGoodsSize>> goodsSizeMap = xfptGoodsSizeMapper.selectList(
                        Wrappers.<XfptGoodsSize>lambdaQuery()
                                .in(XfptGoodsSize::getGoodsId, goodsIds)
                                .eq(XfptGoodsSize::getDelFlag, "0")
                                .eq(XfptGoodsSize::getStatus, "1")
                                .orderByAsc(XfptGoodsSize::getSortNum)
                                .orderByAsc(XfptGoodsSize::getId))
                .stream()
                .collect(Collectors.groupingBy(XfptGoodsSize::getGoodsId, LinkedHashMap::new, Collectors.toList()));
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            XfptGoods goods = goodsMap.get(noticeItem.getGoodsId());
            if (goods == null || !"0".equals(goods.getDelFlag()) || !"1".equals(goods.getStatus()) || !isGroupNoticeGoods(goods)) {
                throw new ServiceException("商品【" + defaultGoodsName(noticeItem.getGoodsName(), noticeItem.getGoodsId()) + "】不存在或不可用于统购公告");
            }
            if (!selectedSupplierIds.contains(goods.getSupplierId())) {
                throw new ServiceException("商品【" + defaultGoodsName(goods.getGoodsName(), goods.getId()) + "】不属于当前选中的供应商");
            }
            List<XfptGoodsSize> goodsSizes = goodsSizeMap.getOrDefault(goods.getId(), new ArrayList<>());
            noticeItem.setGoodsName(goods.getGoodsName());
            noticeItem.setSupplierId(goods.getSupplierId());
            noticeItem.setSupplierName(goods.getSupplierName());
            noticeItem.setGoodsCode(goods.getGoodsCode());
            noticeItem.setDeptId(xfptGroupNotice.getDeptId());
            String normalizedSizeRuleJson = normalizeNoticeItemSizeRuleJson(noticeItem, goodsSizes);
            if (StringUtils.isNotBlank(normalizedSizeRuleJson)) {
                noticeItem.setSizeRuleJson(normalizedSizeRuleJson);
                noticeItem.setBuyNum(sumSizeRuleBuyNum(parseSizeRuleJson(normalizedSizeRuleJson)));
            } else {
                noticeItem.setSizeRuleJson(buildSizeRuleJson(goodsSizes));
                if (noticeItem.getBuyNum() == null || noticeItem.getBuyNum() <= 0) {
                    noticeItem.setBuyNum(1);
                }
            }
            if (StringUtils.isBlank(noticeItem.getGradeIds())) {
                noticeItem.setGradeIds(xfptGroupNotice.getGradeIds());
            }
            if (StringUtils.isBlank(noticeItem.getGradeNames())) {
                noticeItem.setGradeNames(xfptGroupNotice.getGradeNames());
            }
            if (StringUtils.isBlank(noticeItem.getPriceDesc())) {
                noticeItem.setPriceDesc(buildPriceDesc(goodsSizes));
            }
            if (StringUtils.isBlank(noticeItem.getDelFlag())) {
                noticeItem.setDelFlag("0");
            }
        }
    }

    private void enrichNoticeItems(List<XfptGroupNoticeItem> noticeItems)
    {
        if (noticeItems == null || noticeItems.isEmpty()) {
            return;
        }
        Set<Long> goodsIds = noticeItems.stream()
                .map(XfptGroupNoticeItem::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (goodsIds.isEmpty()) {
            return;
        }
        Map<Long, XfptGoods> goodsMap = xfptGoodsMapper.selectBatchIds(goodsIds).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(XfptGoods::getId, Function.identity(), (left, right) -> left));
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            XfptGoods goods = goodsMap.get(noticeItem.getGoodsId());
            if (goods == null) {
                continue;
            }
            noticeItem.setSupplierId(goods.getSupplierId());
            noticeItem.setSupplierName(goods.getSupplierName());
            noticeItem.setGoodsCode(goods.getGoodsCode());
            if (StringUtils.isBlank(noticeItem.getGoodsName())) {
                noticeItem.setGoodsName(goods.getGoodsName());
            }
        }
    }

    private String buildSizeRuleJson(List<XfptGoodsSize> goodsSizes)
    {
        if (goodsSizes == null || goodsSizes.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> sizeRules = new ArrayList<>();
        for (XfptGoodsSize goodsSize : goodsSizes) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", goodsSize.getId());
            row.put("sizeCode", goodsSize.getSizeCode());
            row.put("genderType", goodsSize.getGenderType());
            row.put("measureJson", goodsSize.getMeasureJson());
            row.put("groupPrice", goodsSize.getGroupPrice());
            row.put("retailPrice", goodsSize.getRetailPrice());
            sizeRules.add(row);
        }
        return JSON.toJSONString(sizeRules);
    }

    private String normalizeNoticeItemSizeRuleJson(XfptGroupNoticeItem noticeItem, List<XfptGoodsSize> goodsSizes)
    {
        List<Map<String, Object>> parsedRules = parseSizeRuleJson(noticeItem == null ? null : noticeItem.getSizeRuleJson());
        if (parsedRules.isEmpty()) {
            return null;
        }
        boolean useParentBuyNumFallback = parsedRules.size() == 1;
        Map<Long, XfptGoodsSize> sizeMap = goodsSizes == null ? new LinkedHashMap<>() : goodsSizes.stream()
                .filter(Objects::nonNull)
                .filter(size -> size.getId() != null)
                .collect(Collectors.toMap(XfptGoodsSize::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        List<Map<String, Object>> normalizedRules = new ArrayList<>();
        Integer fallbackBuyNum = noticeItem == null ? null : noticeItem.getBuyNum();
        for (Map<String, Object> rule : parsedRules) {
            Long sizeId = getRuleLongValue(rule.get("id"));
            if (sizeId == null) {
                continue;
            }
            XfptGoodsSize goodsSize = sizeMap.get(sizeId);
            if (goodsSize == null) {
                throw new ServiceException("商品【" + defaultGoodsName(noticeItem == null ? null : noticeItem.getGoodsName(), noticeItem == null ? null : noticeItem.getGoodsId()) + "】存在无效尺码，请重新选择");
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", goodsSize.getId());
            row.put("sizeCode", goodsSize.getSizeCode());
            row.put("genderType", goodsSize.getGenderType());
            row.put("measureJson", goodsSize.getMeasureJson());
            row.put("groupPrice", goodsSize.getGroupPrice());
            row.put("retailPrice", goodsSize.getRetailPrice());
            Integer buyNum = getRuleIntValue(rule.get("buyNum"));
            if ((buyNum == null || buyNum <= 0) && useParentBuyNumFallback) {
                buyNum = fallbackBuyNum != null && fallbackBuyNum > 0 ? fallbackBuyNum : 1;
            }
            if (buyNum == null || buyNum <= 0) {
                throw new ServiceException("商品【" + defaultGoodsName(noticeItem == null ? null : noticeItem.getGoodsName(), noticeItem == null ? null : noticeItem.getGoodsId()) + "】的尺码数量不能为空");
            }
            row.put("buyNum", buyNum);
            normalizedRules.add(row);
        }
        return normalizedRules.isEmpty() ? null : JSON.toJSONString(normalizedRules);
    }

    private List<Map<String, Object>> parseSizeRuleJson(String sizeRuleJson)
    {
        if (StringUtils.isBlank(sizeRuleJson)) {
            return new ArrayList<>();
        }
        try {
            List<Object> parsed = JSON.parseArray(sizeRuleJson);
            List<Map<String, Object>> result = new ArrayList<>();
            if (parsed == null) {
                return result;
            }
            for (Object item : parsed) {
                if (item instanceof Map) {
                    result.add((Map<String, Object>) item);
                }
            }
            return result;
        } catch (Exception ex) {
            throw new ServiceException("公告商品尺码配置格式不正确");
        }
    }

    private Integer sumSizeRuleBuyNum(List<Map<String, Object>> sizeRules)
    {
        if (sizeRules == null || sizeRules.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Map<String, Object> rule : sizeRules) {
            Integer buyNum = getRuleIntValue(rule.get("buyNum"));
            if (buyNum != null && buyNum > 0) {
                total += buyNum;
            }
        }
        return total;
    }

    private Long getRuleLongValue(Object value)
    {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value).trim();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer getRuleIntValue(Object value)
    {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        String text = String.valueOf(value).trim();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            BigDecimal decimal = new BigDecimal(text);
            return decimal.setScale(0, RoundingMode.DOWN).intValue();
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String buildPriceDesc(List<XfptGoodsSize> goodsSizes)
    {
        if (goodsSizes == null || goodsSizes.isEmpty()) {
            return null;
        }
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        for (XfptGoodsSize goodsSize : goodsSizes) {
            BigDecimal groupPrice = goodsSize.getGroupPrice();
            if (groupPrice == null) {
                continue;
            }
            if (minPrice == null || groupPrice.compareTo(minPrice) < 0) {
                minPrice = groupPrice;
            }
            if (maxPrice == null || groupPrice.compareTo(maxPrice) > 0) {
                maxPrice = groupPrice;
            }
        }
        if (minPrice == null) {
            return null;
        }
        if (minPrice.compareTo(maxPrice) == 0) {
            return "统购价：" + formatPrice(minPrice) + "元";
        }
        return "统购价：" + formatPrice(minPrice) + "-" + formatPrice(maxPrice) + "元";
    }

    private String formatPrice(BigDecimal value)
    {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private void markSupplierViewedProgress(Long noticeId, Long supplierId)
    {
        if (noticeId == null || supplierId == null) {
            return;
        }
        List<XfptOrderInfo> orderInfos = xfptOrderInfoMapper.selectList(Wrappers.<XfptOrderInfo>lambdaQuery()
                .eq(XfptOrderInfo::getNoticeId, noticeId)
                .eq(XfptOrderInfo::getSupplierId, supplierId)
                .eq(XfptOrderInfo::getOrderType, "1")
                .eq(XfptOrderInfo::getDelFlag, "0"));
        if (orderInfos == null || orderInfos.isEmpty()) {
            return;
        }
        Date now = new Date();
        String username = SecurityUtils.getUsername();
        for (XfptOrderInfo orderInfo : orderInfos) {
            if (orderInfo == null || orderInfo.getId() == null) {
                continue;
            }
            if (!"0".equals(StringUtils.defaultIfBlank(orderInfo.getProgressStatus(), "0"))) {
                continue;
            }
            XfptOrderInfo updateEntity = new XfptOrderInfo();
            updateEntity.setId(orderInfo.getId());
            updateEntity.setProgressStatus("1");
            updateEntity.setUpdateBy(username);
            updateEntity.setUpdateTime(now);
            xfptOrderInfoMapper.updateById(updateEntity);
        }
    }

    private List<GroupNoticeOrderDetailVo> fillProgressDetailFallback(Long noticeId, Long supplierId, List<GroupNoticeOrderDetailVo> detailList)
    {
        if (detailList != null && !detailList.isEmpty()) {
            return detailList;
        }
        List<XfptGroupNoticeItem> noticeItems = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(noticeId);
        if (noticeItems == null || noticeItems.isEmpty()) {
            return new ArrayList<>();
        }
        List<GroupNoticeOrderDetailVo> fallbackList = new ArrayList<>();
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            if (noticeItem == null) {
                continue;
            }
            GroupNoticeOrderDetailVo detailVo = new GroupNoticeOrderDetailVo();
            detailVo.setProductName(noticeItem.getGoodsName());
            detailVo.setPrice(parseGroupNoticeItemPrice(noticeItem.getPriceDesc()));
            detailVo.setQuantity(noticeItem.getBuyNum());
            detailVo.setOrderPerson("公告关联商品");
            detailVo.setOrderTime(null);
            fallbackList.add(detailVo);
        }
        return fallbackList;
    }

    private BigDecimal parseGroupNoticeItemPrice(String priceDesc)
    {
        if (StringUtils.isBlank(priceDesc)) {
            return null;
        }
        String normalized = priceDesc.replace("统购价：", "")
                .replace("元", "")
                .trim();
        if (StringUtils.isBlank(normalized)) {
            return null;
        }
        String[] parts = normalized.split("-");
        String firstPart = parts.length == 0 ? normalized : parts[0];
        if (StringUtils.isBlank(firstPart)) {
            return null;
        }
        try {
            return new BigDecimal(firstPart.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
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

    private XfptSchoolMessage resolveNoticeOptionSchool(Long schoolId)
    {
        if (!isAdminUser()) {
            return getCurrentSchool();
        }
        if (schoolId == null) {
            throw new ServiceException("请选择公告所属学校");
        }
        XfptSchoolMessage schoolMessage = xfptSchoolMessageService.getById(schoolId);
        if (schoolMessage == null || schoolMessage.getId() == null) {
            throw new ServiceException("公告所属学校不存在");
        }
        return schoolMessage;
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

    private boolean applyParentSchoolScope(XfptGroupNotice xfptGroupNotice)
    {
        if (xfptGroupNotice == null) {
            return false;
        }
        Object simIdValue = xfptGroupNotice.getParams().get("simId");
        String simId = simIdValue == null ? null : String.valueOf(simIdValue).trim();
        if (StringUtils.isBlank(simId)) {
            return false;
        }
        Set<Long> schoolIds = resolveParentSchoolIds(simId);
        xfptGroupNotice.setDeptId(null);
        if (schoolIds.isEmpty()) {
            xfptGroupNotice.setSchoolId(-1L);
            xfptGroupNotice.getParams().remove("schoolIds");
            return true;
        }
        if (schoolIds.size() == 1) {
            xfptGroupNotice.setSchoolId(schoolIds.iterator().next());
            xfptGroupNotice.getParams().remove("schoolIds");
            return true;
        }
        xfptGroupNotice.setSchoolId(null);
        xfptGroupNotice.getParams().put("schoolIds", new ArrayList<>(schoolIds));
        return true;
    }

    private void checkNoticeBelongsToParentSchools(XfptGroupNotice xfptGroupNotice, String simId)
    {
        if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        Set<Long> schoolIds = resolveParentSchoolIds(simId);
        if (schoolIds.isEmpty() || !schoolIds.contains(xfptGroupNotice.getSchoolId())) {
            throw new ServiceException("只能查看当前家长绑定学生所属学校的结果公告");
        }
    }

    private Set<Long> resolveParentSchoolIds(String simId)
    {
        if (StringUtils.isBlank(simId)) {
            return new LinkedHashSet<>();
        }
        List<XfptStudentInfo> studentInfos = xfptStudentInfoService.lambdaQuery()
                .eq(XfptStudentInfo::getSimId, simId)
                .list();
        Set<Long> schoolIds = new LinkedHashSet<>();
        if (studentInfos == null || studentInfos.isEmpty()) {
            return schoolIds;
        }
        for (XfptStudentInfo studentInfo : studentInfos) {
            Long schoolId = parseStudentSchoolId(studentInfo);
            if (schoolId != null) {
                schoolIds.add(schoolId);
            }
        }
        return schoolIds;
    }

    private Long parseStudentSchoolId(XfptStudentInfo studentInfo)
    {
        if (studentInfo == null || StringUtils.isBlank(studentInfo.getShcoolId())) {
            return null;
        }
        try {
            return Long.valueOf(studentInfo.getShcoolId().trim());
        } catch (NumberFormatException ex) {
            return null;
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

    private CooperationScope loadActiveCooperationScope(Long schoolId)
    {
        List<XfptSchoolSupplierRel> schoolSupplierRels = xfptSchoolSupplierRelMapper.selectList(
                Wrappers.<XfptSchoolSupplierRel>lambdaQuery()
                        .eq(XfptSchoolSupplierRel::getSchoolId, schoolId)
                        .eq(XfptSchoolSupplierRel::getCooperateStatus, "1")
                        .eq(XfptSchoolSupplierRel::getDelFlag, "0")
                        .orderByAsc(XfptSchoolSupplierRel::getSupplierName)
                        .orderByAsc(XfptSchoolSupplierRel::getId));
        if (schoolSupplierRels == null || schoolSupplierRels.isEmpty()) {
            throw new ServiceException("当前学校没有合作中的供应商，无法创建统购公告");
        }
        Map<Long, XfptSchoolSupplierRel> supplierRelBySupplierId = new LinkedHashMap<>();
        Set<Long> relIds = new LinkedHashSet<>();
        for (XfptSchoolSupplierRel schoolSupplierRel : schoolSupplierRels) {
            if (schoolSupplierRel.getSupplierId() == null || schoolSupplierRel.getId() == null) {
                continue;
            }
            supplierRelBySupplierId.putIfAbsent(schoolSupplierRel.getSupplierId(), schoolSupplierRel);
            relIds.add(schoolSupplierRel.getId());
        }
        if (supplierRelBySupplierId.isEmpty() || relIds.isEmpty()) {
            throw new ServiceException("当前学校没有有效的合作供应商，无法创建统购公告");
        }
        List<XfptSchoolSupplierGoodsRel> goodsRelList = xfptSchoolSupplierGoodsRelMapper.selectList(
                Wrappers.<XfptSchoolSupplierGoodsRel>lambdaQuery()
                        .in(XfptSchoolSupplierGoodsRel::getRelId, relIds)
                        .eq(XfptSchoolSupplierGoodsRel::getSchoolId, schoolId)
                        .eq(XfptSchoolSupplierGoodsRel::getStatus, "1")
                        .eq(XfptSchoolSupplierGoodsRel::getDelFlag, "0"));
        CooperationScope scope = new CooperationScope();
        scope.supplierRelBySupplierId = supplierRelBySupplierId;
        scope.goodsRelList = goodsRelList == null ? new ArrayList<>() : goodsRelList.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getSupplierId() != null && item.getGoodsId() != null)
                .collect(Collectors.toList());
        return scope;
    }

    private void ensureSelectedSuppliersAllowed(Set<Long> selectedSupplierIds, CooperationScope scope)
    {
        for (Long supplierId : selectedSupplierIds) {
            if (!scope.supplierRelBySupplierId.containsKey(supplierId)) {
                throw new ServiceException("供应商ID【" + supplierId + "】不属于当前学校合作中的供应商");
            }
        }
    }

    private Set<Long> collectAllowedGoodsIds(CooperationScope scope, Set<Long> selectedSupplierIds)
    {
        return scope.goodsRelList.stream()
                .filter(item -> selectedSupplierIds.contains(item.getSupplierId()))
                .map(XfptSchoolSupplierGoodsRel::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Long> normalizeIdSet(List<Long> ids)
    {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<Long> parseIdList(String rawText)
    {
        if (StringUtils.isBlank(rawText)) {
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        String[] tokens = rawText.split(",");
        for (String token : tokens) {
            String value = token == null ? null : token.trim();
            if (StringUtils.isBlank(value)) {
                continue;
            }
            try {
                result.add(Long.valueOf(value));
            } catch (NumberFormatException ex) {
                throw new ServiceException("供应商参数格式不正确");
            }
        }
        return result;
    }

    private boolean isGroupNoticeGoods(XfptGoods goods)
    {
        String saleType = goods == null ? null : goods.getSaleType();
        return "1".equals(saleType) || "3".equals(saleType);
    }

    private int compareNullableLong(Long left, Long right)
    {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return left.compareTo(right);
    }

    private int compareNullableString(String left, String right)
    {
        if (left == null && right == null) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return left.compareTo(right);
    }

    private static class CooperationScope
    {
        private Map<Long, XfptSchoolSupplierRel> supplierRelBySupplierId = new LinkedHashMap<>();
        private List<XfptSchoolSupplierGoodsRel> goodsRelList = new ArrayList<>();
    }
}
