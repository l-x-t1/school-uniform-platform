package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Date;
import java.util.Collections;
import java.util.Objects;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptOrderItem;
import com.xfgl.school.mapper.XfptOrderInfoMapper;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.service.IXfptOrderInfoService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;

/**
 * 订单信息Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptOrderInfoServiceImpl extends ServiceImpl<XfptOrderInfoMapper, XfptOrderInfo> implements IXfptOrderInfoService {

    @Autowired
    private XfptOrderInfoMapper xfptOrderInfoMapper;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息
     */
    @Override
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo)
    {
        if (xfptOrderInfo == null) {
            xfptOrderInfo = new XfptOrderInfo();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptOrderInfo.setSchoolId(currentSchool.getId());
            xfptOrderInfo.setDeptId(currentSchool.getDeptId());
        }
        return xfptOrderInfoMapper.selectXfptOrderInfoList(xfptOrderInfo);
    }

        /**
         * 查询订单信息
         *
         * @param id 订单信息主键
         * @return 订单信息
         */
        @Override
        public XfptOrderInfo selectXfptOrderInfoById(Long id)
        {
            XfptOrderInfo xfptOrderInfo = requireAccessibleOrder(xfptOrderInfoMapper.selectById(id));
            List<XfptOrderItem> xfptOrderItemList = xfptOrderInfoMapper.selectXfptOrderItemList(id);
            xfptOrderInfo.setXfptOrderItemList(xfptOrderItemList);
            return xfptOrderInfo;
        }

        /**
         * 新增订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptOrderInfo(XfptOrderInfo xfptOrderInfo)
        {
            applyCurrentSchoolScopeForWrite(xfptOrderInfo, null);
            validateGroupPurchaseOrder(xfptOrderInfo);
            int rows = xfptOrderInfoMapper.insert(xfptOrderInfo);
            insertXfptOrderItem(xfptOrderInfo);
            return rows;
        }

        /**
         * 修改订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptOrderInfo(XfptOrderInfo xfptOrderInfo)
        {
            XfptOrderInfo existing = requireAccessibleOrder(xfptOrderInfoMapper.selectById(xfptOrderInfo.getId()));
            applyCurrentSchoolScopeForWrite(xfptOrderInfo, existing);
            xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(xfptOrderInfo.getId());
            insertXfptOrderItem(xfptOrderInfo);
            return xfptOrderInfoMapper.updateById(xfptOrderInfo);
        }

        /**
         * 批量删除订单信息
         *
         * @param ids 需要删除的订单信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptOrderInfoByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                requireAccessibleOrder(xfptOrderInfoMapper.selectById(id));
                xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(id);
        }
            return xfptOrderInfoMapper.deleteBatchIds(idList);
        }

        /**
         * 删除订单信息信息
         *
         * @param id 订单信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptOrderInfoById(Long id)
        {
            requireAccessibleOrder(xfptOrderInfoMapper.selectById(id));
            xfptOrderInfoMapper.deleteXfptOrderItemByOrderId(id);
            return xfptOrderInfoMapper.deleteById(id);
        }

        /**
         * 新增订单明细信息
         *
         * @param xfptOrderInfo 订单信息对象
         */
        private void insertXfptOrderItem(XfptOrderInfo xfptOrderInfo)
        {
            List<XfptOrderItem> xfptOrderItemList = xfptOrderInfo.getXfptOrderItemList();
            Long id = xfptOrderInfo.getId();
            if (StringUtils.isNotNull(xfptOrderItemList)) {
                for (XfptOrderItem xfptOrderItem : xfptOrderItemList) {
                    // 订单明细主键由数据库自增生成，避免把购物车/商品ID误当成明细ID插入
                    xfptOrderItem.setId(null);
                    xfptOrderItem.setOrderId(id);
                    fillOrderItemDefaults(xfptOrderItem, xfptOrderInfo.getOrderType());
                }
                    xfptOrderInfoMapper.batchInsertXfptOrderItem(xfptOrderItemList);
            }
        }

        private void fillOrderItemDefaults(XfptOrderItem xfptOrderItem, String orderType)
        {
            if (xfptOrderItem.getSaleType() == null || "".equals(xfptOrderItem.getSaleType())) {
                if (orderType != null && !"".equals(orderType)) {
                    xfptOrderItem.setSaleType(orderType);
                } else {
                    xfptOrderItem.setSaleType("1");
                }
            }
            if (xfptOrderItem.getDelFlag() == null || "".equals(xfptOrderItem.getDelFlag())) {
                xfptOrderItem.setDelFlag("0");
            }
        }

        private void validateGroupPurchaseOrder(XfptOrderInfo xfptOrderInfo)
        {
            if (xfptOrderInfo == null || !"1".equals(String.valueOf(xfptOrderInfo.getOrderType()))) {
                return;
            }
            if (xfptOrderInfo.getNoticeId() == null) {
                throw new ServiceException("统购下单必须携带统购公告ID");
            }
            XfptGroupNotice notice = xfptOrderInfoMapper.selectGroupNoticeForPurchase(xfptOrderInfo.getNoticeId());
            if (notice == null) {
                throw new ServiceException("统购公告不存在或已删除");
            }
            if (!"1".equals(String.valueOf(notice.getStatus()))) {
                throw new ServiceException("统购公告未发布，不能下单");
            }
            if (notice.getPurchaseStartTime() == null || notice.getPurchaseEndTime() == null) {
                throw new ServiceException("统购公告未设置可下单时间");
            }
            Date now = new Date();
            if (now.before(notice.getPurchaseStartTime())) {
                throw new ServiceException("统购尚未开始，不能下单");
            }
            if (now.after(notice.getPurchaseEndTime())) {
                throw new ServiceException("统购已结束，不能下单");
            }
            List<XfptOrderItem> orderItems = xfptOrderInfo.getXfptOrderItemList();
            if (StringUtils.isEmpty(orderItems)) {
                throw new ServiceException("统购订单明细不能为空");
            }
            for (XfptOrderItem item : orderItems) {
                if (item.getGoodsId() == null) {
                    throw new ServiceException("统购订单商品不能为空");
                }
                int count = xfptOrderInfoMapper.countGroupNoticeGoods(xfptOrderInfo.getNoticeId(), item.getGoodsId());
                if (count <= 0) {
                    throw new ServiceException("订单商品不属于当前统购公告，不能下单");
                }
            }
        }

        private void applyCurrentSchoolScopeForWrite(XfptOrderInfo xfptOrderInfo, XfptOrderInfo existing)
        {
            if (!isSchoolScopedUser()) {
                return;
            }
            XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法保存订单");
            }
            xfptOrderInfo.setSchoolId(currentSchool.getId());
            xfptOrderInfo.setSchoolName(currentSchool.getSchoolName());
            xfptOrderInfo.setDeptId(currentSchool.getDeptId());
            if (existing != null && !Objects.equals(existing.getSchoolId(), currentSchool.getId())) {
                throw new ServiceException("只能操作当前学校的订单");
            }
        }

        private XfptOrderInfo requireAccessibleOrder(XfptOrderInfo orderInfo)
        {
            if (orderInfo == null || !"0".equals(orderInfo.getDelFlag())) {
                throw new ServiceException("订单不存在");
            }
            XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
            if (isSchoolScopedUser()) {
                if (currentSchool == null) {
                    throw new ServiceException("当前登录学校信息不存在，无法操作订单");
                }
                if (!Objects.equals(orderInfo.getSchoolId(), currentSchool.getId())
                        || !Objects.equals(orderInfo.getDeptId(), currentSchool.getDeptId())) {
                    throw new ServiceException("只能操作当前学校的订单");
                }
            }
            return orderInfo;
        }

        private XfptSchoolMessage getCurrentSchoolIfScoped()
        {
            SysUser currentUser = getCurrentUser();
            if (!schoolAccessSupport.isSchoolUser(currentUser)) {
                return null;
            }
            return schoolAccessSupport.resolveCurrentSchool(currentUser);
        }

        private boolean isSchoolScopedUser()
        {
            return schoolAccessSupport.isSchoolUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
        }

        private SysUser getCurrentUser()
        {
            return SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getSysUser();
        }

}
