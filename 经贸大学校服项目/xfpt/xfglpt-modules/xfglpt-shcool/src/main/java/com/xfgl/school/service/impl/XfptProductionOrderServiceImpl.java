package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson2.JSON;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptDeliveryOrder;
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.XfptProductionOrderItem;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.mapper.XfptDeliveryOrderMapper;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.mapper.XfptProductionOrderMapper;
import com.xfgl.school.domain.XfptProductionOrder;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptGroupNoticeService;
import com.xfgl.school.service.IXfptProductionOrderService;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;

@Service
public class XfptProductionOrderServiceImpl extends ServiceImpl<XfptProductionOrderMapper, XfptProductionOrder> implements IXfptProductionOrderService {

    @Autowired
    private XfptProductionOrderMapper xfptProductionOrderMapper;

    @Autowired
    private XfptDeliveryOrderMapper xfptDeliveryOrderMapper;

    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;

    @Autowired
    private IXfptGroupNoticeService xfptGroupNoticeService;

    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    @Autowired
    private ISupplierMessageService supplierMessageService;

    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;

    @Override
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder)
    {
        applyAccessScope(xfptProductionOrder);
        return xfptProductionOrderMapper.selectXfptProductionOrderList(xfptProductionOrder);
    }

    @Override
    public XfptProductionOrder selectXfptProductionOrderById(Long id)
    {
        XfptProductionOrder xfptProductionOrder = xfptProductionOrderMapper.selectXfptProductionOrderById(id);
        if (xfptProductionOrder == null) {
            return null;
        }
        validateProductionOrderAccess(xfptProductionOrder);
        List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrderMapper.selectXfptProductionOrderItemById(id);
        xfptProductionOrder.setXfptProductionOrderItemList(xfptProductionOrderItemList);
        return xfptProductionOrder;
    }

    @Transactional
    @Override
    public int insertXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
    {
        if (xfptProductionOrder.getStartTime() != null
                && xfptProductionOrder.getFinishTime() != null
                && !xfptProductionOrder.getStartTime().before(xfptProductionOrder.getFinishTime())) {
            throw new ServiceException("startTime must be before finishTime");
        }
        if (StringUtils.isEmpty(xfptProductionOrder.getOrderType())) {
            xfptProductionOrder.setOrderType("1");
        }
        if (StringUtils.isEmpty(xfptProductionOrder.getStatus())) {
            xfptProductionOrder.setStatus("0");
        }
        if (xfptProductionOrder.getNoticeId() != null || isSupplierUser()) {
            fillProductionOrderByNotice(xfptProductionOrder, false);
        } else {
            applyAccessScope(xfptProductionOrder);
        }
        fillDefaultAuditFields(xfptProductionOrder, true);
        int rows = xfptProductionOrderMapper.insert(xfptProductionOrder);
        insertXfptProductionOrderItem(xfptProductionOrder);
        return rows;
    }

    @Transactional
    @Override
    public int updateXfptProductionOrder(XfptProductionOrder xfptProductionOrder)
    {
        XfptProductionOrder currentOrder = selectXfptProductionOrderById(xfptProductionOrder.getId());
        if (currentOrder == null) {
            throw new ServiceException("production order not found");
        }
        if (xfptProductionOrder.getNoticeId() != null || isSupplierUser()) {
            fillProductionOrderByNotice(xfptProductionOrder, true);
        } else {
            applyAccessScope(xfptProductionOrder);
        }
        fillDefaultAuditFields(xfptProductionOrder, false);
        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(xfptProductionOrder.getId());
        insertXfptProductionOrderItem(xfptProductionOrder);
        return xfptProductionOrderMapper.updateById(xfptProductionOrder);
    }

    @Transactional
    @Override
    public int finishProductionOrder(Long id)
    {
        XfptProductionOrder productionOrder = selectXfptProductionOrderById(id);
        if (productionOrder == null) {
            throw new ServiceException("production order not found");
        }
        if ("2".equals(productionOrder.getStatus())) {
            return 1;
        }

        Date now = new Date();
        productionOrder.setStatus("2");
        productionOrder.setFinishTime(now);

        List<XfptProductionOrderItem> itemList = productionOrder.getXfptProductionOrderItemList();
        if (itemList == null || itemList.isEmpty()) {
            throw new ServiceException("production order item list cannot be empty");
        }
        for (XfptProductionOrderItem item : itemList) {
            item.setProductionId(id);
            item.setStatus("2");
            item.setFinishNum(item.getBuyNum() == null ? 0 : item.getBuyNum());
            item.setUpdateTime(now);
        }

        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(itemList);
        return xfptProductionOrderMapper.updateById(productionOrder);
    }

    @Transactional
    @Override
    public XfptDeliveryOrder generateDeliveryOrder(Long id)
    {
        XfptProductionOrder productionOrder = selectXfptProductionOrderById(id);
        if (productionOrder == null) {
            throw new ServiceException("production order not found");
        }
        if (!"2".equals(productionOrder.getStatus())) {
            throw new ServiceException("production order is not finished");
        }

        XfptDeliveryOrder existsDeliveryOrder = xfptDeliveryOrderMapper.selectOne(Wrappers.<XfptDeliveryOrder>lambdaQuery()
                .eq(XfptDeliveryOrder::getProductionId, id)
                .eq(XfptDeliveryOrder::getDelFlag, "0")
                .last("limit 1"));
        if (existsDeliveryOrder != null) {
            throw new ServiceException("该生产单已生成发货单，请勿重复创建");
        }

        XfptDeliveryOrder deliveryOrder = new XfptDeliveryOrder();
        deliveryOrder.setDeliveryNo("DEL-" + System.currentTimeMillis() + "-" + id);
        deliveryOrder.setNoticeId(productionOrder.getNoticeId());
        deliveryOrder.setOrderId(productionOrder.getOrderId());
        deliveryOrder.setOrderNo(productionOrder.getOrderNo());
        deliveryOrder.setOrderType(productionOrder.getOrderType());
        deliveryOrder.setProductionId(productionOrder.getId());
        deliveryOrder.setSchoolId(productionOrder.getSchoolId());
        deliveryOrder.setSchoolName(productionOrder.getSchoolName());
        deliveryOrder.setSupplierId(productionOrder.getSupplierId());
        deliveryOrder.setSupplierName(productionOrder.getSupplierName());
        fillReceiverInfo(deliveryOrder, productionOrder);
        deliveryOrder.setDeliveryStatus("0");
        deliveryOrder.setDeptId(productionOrder.getDeptId());
        deliveryOrder.setDelFlag("0");

        List<XfptProductionOrderItem> productionItemList = productionOrder.getXfptProductionOrderItemList();
        if (productionItemList == null || productionItemList.isEmpty()) {
            throw new ServiceException("production order item list cannot be empty");
        }
        List<XfptDeliveryOrderItem> deliveryItemList = new java.util.ArrayList<>();
        for (XfptProductionOrderItem productionItem : productionItemList) {
            XfptDeliveryOrderItem deliveryItem = new XfptDeliveryOrderItem();
            deliveryItem.setOrderItemId(productionItem.getOrderItemId());
            deliveryItem.setGoodsId(productionItem.getGoodsId());
            deliveryItem.setSizeId(productionItem.getSizeId());
            deliveryItem.setGoodsName(productionItem.getGoodsName());
            deliveryItem.setSizeName(productionItem.getSizeName());
            deliveryItem.setDeliveryNum(productionItem.getFinishNum() == null ? productionItem.getBuyNum() : productionItem.getFinishNum());
            deliveryItem.setSignNum(0);
            deliveryItem.setStatus("0");
            deliveryItem.setDelFlag("0");
            deliveryItemList.add(deliveryItem);
        }
        deliveryOrder.setXfptDeliveryOrderItemList(deliveryItemList);
        return deliveryOrder;
    }

    @Override
    public List<XfptGroupNotice> selectProductionNoticeOptions()
    {
        if (isSupplierUser()) {
            XfptGroupNotice query = new XfptGroupNotice();
            query.setStatus("1");
            List<XfptGroupNotice> list = xfptGroupNoticeService.selectSupplierGroupNoticeList(query);
            return list == null ? new ArrayList<>() : list;
        }

        XfptProductionOrder query = new XfptProductionOrder();
        applyAccessScope(query);
        List<XfptGroupNotice> notices = new ArrayList<>();
        Long schoolId = query.getSchoolId();
        if (schoolId == null) {
            return notices;
        }
        XfptGroupNotice noticeQuery = new XfptGroupNotice();
        noticeQuery.setSchoolId(schoolId);
        noticeQuery.setDeptId(query.getDeptId());
        noticeQuery.setStatus("1");
        List<XfptGroupNotice> list = xfptGroupNoticeService.selectXfptGroupNoticeList(noticeQuery);
        return list == null ? new ArrayList<>() : list;
    }

    @Override
    public XfptGroupNotice selectProductionNoticeDetail(Long noticeId)
    {
        if (noticeId == null) {
            throw new ServiceException("请选择统购公告");
        }
        XfptGroupNotice notice = isSupplierUser()
                ? xfptGroupNoticeService.selectSupplierGroupNoticeById(noticeId)
                : xfptGroupNoticeService.selectXfptGroupNoticeById(noticeId);
        if (notice == null) {
            throw new ServiceException("公告不存在");
        }
        if (!"1".equals(notice.getStatus()) && !"2".equals(notice.getStatus())) {
            throw new ServiceException("只能选择已发布或已结束的统购公告");
        }
        if (!isSupplierUser()) {
            XfptProductionOrder query = new XfptProductionOrder();
            applyAccessScope(query);
            if (query.getSchoolId() != null && !Objects.equals(query.getSchoolId(), notice.getSchoolId())) {
                throw new ServiceException("只能选择当前学校的统购公告");
            }
        }
        return notice;
    }

    private void fillReceiverInfo(XfptDeliveryOrder deliveryOrder, XfptProductionOrder productionOrder)
    {
        XfptOrderInfo orderInfo = productionOrder.getOrderId() == null ? null : xfptOrderInfoMapper.selectById(productionOrder.getOrderId());
        if (orderInfo != null) {
            deliveryOrder.setReceiverName(orderInfo.getReceiverName());
            deliveryOrder.setReceiverPhone(orderInfo.getReceiverPhone());
            deliveryOrder.setReceiverAddress(orderInfo.getReceiverAddress());
        }
        XfptSchoolMessage schoolMessage = productionOrder.getSchoolId() == null
                ? null
                : xfptSchoolMessageService.getById(productionOrder.getSchoolId());
        if (schoolMessage != null) {
            deliveryOrder.setReceiverName(StringUtils.defaultIfEmpty(deliveryOrder.getReceiverName(),
                    StringUtils.defaultIfEmpty(schoolMessage.getPrincipalName(), schoolMessage.getSchoolName())));
            deliveryOrder.setReceiverPhone(StringUtils.defaultIfEmpty(deliveryOrder.getReceiverPhone(), schoolMessage.getPrincipalPhone()));
            deliveryOrder.setReceiverAddress(StringUtils.defaultIfEmpty(deliveryOrder.getReceiverAddress(), schoolMessage.getDetailedAddress()));
        }
    }

    @Transactional
    @Override
    public int deleteXfptProductionOrderByIds(Long[] ids)
    {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            XfptProductionOrder currentOrder = selectXfptProductionOrderById(id);
            if (currentOrder == null) {
                continue;
            }
            xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        }
        return xfptProductionOrderMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public int deleteXfptProductionOrderById(Long id)
    {
        XfptProductionOrder currentOrder = selectXfptProductionOrderById(id);
        if (currentOrder == null) {
            return 0;
        }
        xfptProductionOrderMapper.deleteXfptProductionOrderItemByProductionId(id);
        return xfptProductionOrderMapper.deleteById(id);
    }

    private void insertXfptProductionOrderItem(XfptProductionOrder xfptProductionOrder)
    {
        List<XfptProductionOrderItem> xfptProductionOrderItemList = xfptProductionOrder.getXfptProductionOrderItemList();
        Long id = xfptProductionOrder.getId();

        if (StringUtils.isNotNull(xfptProductionOrderItemList)) {
            if (xfptProductionOrderItemList.isEmpty()) {
                throw new ServiceException("production order item list cannot be empty");
            }

            long generatedOrderItemIdBase = System.currentTimeMillis() * 10000;
            int generatedOrderItemIndex = 0;
            for (XfptProductionOrderItem xfptProductionOrderItem : xfptProductionOrderItemList) {
                if (xfptProductionOrderItem.getGoodsId() == null) {
                    throw new ServiceException("production order item goodsId cannot be null");
                }

                xfptProductionOrderItem.setProductionId(id);
                xfptProductionOrderItem.setId(null);
                if (xfptProductionOrderItem.getOrderItemId() == null) {
                    xfptProductionOrderItem.setOrderItemId(generatedOrderItemIdBase + generatedOrderItemIndex++);
                }
                if (xfptProductionOrderItem.getBuyNum() == null || xfptProductionOrderItem.getBuyNum() <= 0) {
                    throw new ServiceException("production order item buyNum must be greater than 0");
                }

                if (StringUtils.isEmpty(xfptProductionOrderItem.getStatus())) {
                    xfptProductionOrderItem.setStatus("0");
                }
                if (xfptProductionOrderItem.getFinishNum() == null) {
                    xfptProductionOrderItem.setFinishNum(0);
                }
                if (StringUtils.isEmpty(xfptProductionOrderItem.getDelFlag())) {
                    xfptProductionOrderItem.setDelFlag("0");
                }
            }

            xfptProductionOrderMapper.batchInsertXfptProductionOrderItem(xfptProductionOrderItemList);
        }
    }

    private void applyAccessScope(XfptProductionOrder query)
    {
        if (query == null) {
            return;
        }
        if (isSupplierUser()) {
            query.setSupplierId(getCurrentSupplierId());
            return;
        }
        if (isAdminUser()) {
            return;
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        query.setSchoolId(currentSchool.getId());
        query.setDeptId(currentSchool.getDeptId());
    }

    private void validateProductionOrderAccess(XfptProductionOrder productionOrder)
    {
        if (productionOrder == null) {
            return;
        }
        if (isSupplierUser()) {
            Long supplierId = getCurrentSupplierId();
            if (!Objects.equals(supplierId, productionOrder.getSupplierId())) {
                throw new ServiceException("只能操作当前供应商的生产单");
            }
            return;
        }
        if (isAdminUser()) {
            return;
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        if (!Objects.equals(currentSchool.getId(), productionOrder.getSchoolId())
                || !Objects.equals(currentSchool.getDeptId(), productionOrder.getDeptId())) {
            throw new ServiceException("只能操作当前学校的生产单");
        }
    }

    private void fillProductionOrderByNotice(XfptProductionOrder productionOrder, boolean keepCurrentStatus)
    {
        if (productionOrder == null) {
            return;
        }
        if (productionOrder.getNoticeId() == null) {
            throw new ServiceException("请选择统购公告");
        }
        XfptGroupNotice notice = selectProductionNoticeDetail(productionOrder.getNoticeId());
        Long currentSupplierId = resolveNoticeSupplierId(notice, productionOrder.getSupplierId());
        if (currentSupplierId == null) {
            throw new ServiceException("统购公告未关联供应商，无法创建生产单");
        }

        productionOrder.setNoticeId(notice.getId());
        productionOrder.setOrderType("1");
        productionOrder.setSchoolId(notice.getSchoolId());
        productionOrder.setDeptId(notice.getDeptId());
        productionOrder.setSupplierId(currentSupplierId);
        productionOrder.setSchoolName(notice.getSchoolName());
        productionOrder.setNoticeTitle(notice.getTitle());
        productionOrder.setNoticeCode(notice.getNoticeCode());
        productionOrder.setSupplierName(resolveSupplierName(currentSupplierId, notice));

        if (!keepCurrentStatus || StringUtils.isEmpty(productionOrder.getStatus())) {
            productionOrder.setStatus("0");
        }
        if (StringUtils.isEmpty(productionOrder.getDelFlag())) {
            productionOrder.setDelFlag("0");
        }
        if (StringUtils.isEmpty(productionOrder.getProductionNo())) {
            productionOrder.setProductionNo(buildProductionNo(notice.getId()));
        } else {
            productionOrder.setProductionNo(productionOrder.getProductionNo().trim());
        }
        normalizeProductionItems(productionOrder, notice, currentSupplierId);
    }

    private void normalizeProductionItems(XfptProductionOrder productionOrder, XfptGroupNotice notice, Long currentSupplierId)
    {
        List<XfptProductionOrderItem> items = productionOrder.getXfptProductionOrderItemList();
        if (items == null || items.isEmpty()) {
            throw new ServiceException("production order item list cannot be empty");
        }
        List<XfptGroupNoticeItem> noticeItems = notice == null ? null : notice.getXfptGroupNoticeItemList();
        if (noticeItems == null || noticeItems.isEmpty()) {
            throw new ServiceException("所选统购公告未配置关联商品");
        }

        Set<Long> allowedGoodsIds = new LinkedHashSet<>();
        Map<Long, XfptGroupNoticeItem> noticeItemMap = new LinkedHashMap<>();
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            if (noticeItem == null || noticeItem.getGoodsId() == null) {
                continue;
            }
            if (currentSupplierId.equals(noticeItem.getSupplierId())) {
                allowedGoodsIds.add(noticeItem.getGoodsId());
                noticeItemMap.put(noticeItem.getGoodsId(), noticeItem);
            }
        }
        if (allowedGoodsIds.isEmpty()) {
            throw new ServiceException("所选统购公告中没有当前供应商可生产的商品");
        }

        for (XfptProductionOrderItem item : items) {
            if (item == null || item.getGoodsId() == null) {
                throw new ServiceException("production order item goodsId cannot be null");
            }
            if (!allowedGoodsIds.contains(item.getGoodsId())) {
                throw new ServiceException("生产单明细中存在不属于当前供应商公告商品的数据");
            }
            XfptGroupNoticeItem noticeItem = noticeItemMap.get(item.getGoodsId());
            if (noticeItem == null) {
                throw new ServiceException("生产单明细商品不存在对应的公告商品");
            }
            if (item.getSizeId() != null && !isAllowedSizeId(noticeItem, item.getSizeId())) {
                throw new ServiceException("生产单明细中存在不属于公告配置尺码的数据");
            }
            if (StringUtils.isBlank(item.getGoodsName())) {
                item.setGoodsName(noticeItem.getGoodsName());
            }
            if (StringUtils.isBlank(item.getSizeName())) {
                item.setSizeName(findSizeNameById(noticeItem, item.getSizeId()));
            }
        }
    }

    private Long resolveNoticeSupplierId(XfptGroupNotice notice, Long inputSupplierId)
    {
        if (notice == null) {
            return null;
        }
        if (isSupplierUser()) {
            return getCurrentSupplierId();
        }
        Set<Long> allowedSupplierIds = new LinkedHashSet<>();
        List<XfptGroupNoticeItem> items = notice.getXfptGroupNoticeItemList();
        if (items != null) {
            for (XfptGroupNoticeItem item : items) {
                if (item != null && item.getSupplierId() != null) {
                    allowedSupplierIds.add(item.getSupplierId());
                }
            }
        }
        List<Long> supplierIds = notice.getSupplierIds();
        if (supplierIds != null) {
            for (Long supplierId : supplierIds) {
                if (supplierId != null) {
                    allowedSupplierIds.add(supplierId);
                }
            }
        }
        if (notice.getSupplierId() != null) {
            allowedSupplierIds.add(notice.getSupplierId());
        }
        if (inputSupplierId != null) {
            if (!allowedSupplierIds.contains(inputSupplierId)) {
                throw new ServiceException("所选供应商不在当前统购公告的发布范围内");
            }
            return inputSupplierId;
        }
        if (allowedSupplierIds.isEmpty()) {
            return null;
        }
        if (allowedSupplierIds.size() > 1) {
            throw new ServiceException("当前统购公告关联了多个供应商，请先明确选择供应商");
        }
        return allowedSupplierIds.iterator().next();
    }

    private String resolveSupplierName(Long supplierId, XfptGroupNotice notice)
    {
        if (notice != null) {
            List<XfptGroupNoticeItem> items = notice.getXfptGroupNoticeItemList();
            if (items != null) {
                for (XfptGroupNoticeItem item : items) {
                    if (item != null && Objects.equals(item.getSupplierId(), supplierId) && StringUtils.isNotBlank(item.getSupplierName())) {
                        return item.getSupplierName();
                    }
                }
            }
        }
        SupplierMessage supplier = supplierMessageService.getById(supplierId);
        return supplier == null ? null : supplier.getSupplierName();
    }

    private String findGoodsNameById(List<XfptGroupNoticeItem> noticeItems, Long goodsId)
    {
        if (noticeItems == null || goodsId == null) {
            return null;
        }
        for (XfptGroupNoticeItem noticeItem : noticeItems) {
            if (noticeItem != null && Objects.equals(goodsId, noticeItem.getGoodsId())) {
                return noticeItem.getGoodsName();
            }
        }
        return null;
    }

    private boolean isAllowedSizeId(XfptGroupNoticeItem noticeItem, Long sizeId)
    {
        if (noticeItem == null || sizeId == null) {
            return true;
        }
        List<Map<String, Object>> sizeRules = parseSizeRules(noticeItem.getSizeRuleJson());
        if (sizeRules.isEmpty()) {
            return true;
        }
        for (Map<String, Object> sizeRule : sizeRules) {
            Long ruleId = parseLongValue(sizeRule.get("id"));
            if (Objects.equals(ruleId, sizeId)) {
                return true;
            }
        }
        return false;
    }

    private String findSizeNameById(XfptGroupNoticeItem noticeItem, Long sizeId)
    {
        if (noticeItem == null || sizeId == null) {
            return null;
        }
        List<Map<String, Object>> sizeRules = parseSizeRules(noticeItem.getSizeRuleJson());
        for (Map<String, Object> sizeRule : sizeRules) {
            Long ruleId = parseLongValue(sizeRule.get("id"));
            if (Objects.equals(ruleId, sizeId)) {
                Object sizeCode = sizeRule.get("sizeCode");
                return sizeCode == null ? null : String.valueOf(sizeCode);
            }
        }
        return null;
    }

    private List<Map<String, Object>> parseSizeRules(String sizeRuleJson)
    {
        if (StringUtils.isBlank(sizeRuleJson)) {
            return new ArrayList<>();
        }
        try {
            List<Map<String, Object>> rules = JSON.parseObject(sizeRuleJson, List.class);
            return rules == null ? new ArrayList<>() : rules;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private Long parseLongValue(Object value)
    {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void fillDefaultAuditFields(XfptProductionOrder productionOrder, boolean create)
    {
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        if (create) {
            if (productionOrder.getCreateTime() == null) {
                productionOrder.setCreateTime(now);
            }
            if (StringUtils.isBlank(productionOrder.getCreateBy())) {
                productionOrder.setCreateBy(username);
            }
        }
        productionOrder.setUpdateTime(now);
        productionOrder.setUpdateBy(username);
    }

    private String buildProductionNo(Long noticeId)
    {
        return "PRO-" + System.currentTimeMillis() + "-" + (noticeId == null ? 0L : noticeId);
    }

    private boolean isSupplierUser()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        return supplierAccessSupport.isSupplierUser(sysUser);
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
}
