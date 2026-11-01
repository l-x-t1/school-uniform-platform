package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.mapper.XfptGoodsMapper;
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptGoodsService;
import com.xfgl.school.service.IXfptStudentInfoService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 商品信息Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGoodsServiceImpl extends ServiceImpl<XfptGoodsMapper, XfptGoods> implements IXfptGoodsService {

    @Autowired
    private XfptGoodsMapper xfptGoodsMapper;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private ISupplierMessageService supplierMessageService;
    @Autowired
    private IXfptStudentInfoService xfptStudentInfoService;
    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;

    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息
     */
    @Override
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods)
    {
        if (isSupplierScopedUser() && getCurrentSupplierIdIfScoped() == null) {
            return Collections.emptyList();
        }
        applyParentSchoolScope(xfptGoods);
        applyCurrentSupplierScope(xfptGoods);
        return xfptGoodsMapper.selectXfptGoodsList(xfptGoods);
    }

        /**
         * 查询商品信息
         *
         * @param id 商品信息主键
         * @return 商品信息
         */
        @Override
        public XfptGoods selectXfptGoodsById(Long id)
        {
            return selectXfptGoodsById(id, null, null);
        }

        @Override
        public XfptGoods selectXfptGoodsById(Long id, Long schoolId, String simId)
        {
            XfptGoods scopedQuery = new XfptGoods();
            scopedQuery.setSchoolId(schoolId);
            if (StringUtils.isNotBlank(simId)) {
                scopedQuery.getParams().put("simId", simId.trim());
            }
            applyParentSchoolScope(scopedQuery);
            if (StringUtils.isNotBlank(simId)
                    && scopedQuery.getSchoolId() == null
                    && scopedQuery.getParams().get("schoolIds") != null) {
                throw new ServiceException("当前账号绑定了多个学校，请先选择学生后再查看商品详情");
            }
            XfptGoods xfptGoods = requireAccessibleGoods(id, scopedQuery.getSchoolId());
            List<XfptGoodsSize> xfptGoodsSizeList = xfptGoodsMapper.selectXfptGoodsSizeById(id);
            xfptGoods.setXfptGoodsSizeList(xfptGoodsSizeList);
            return xfptGoods;
        }

        /**
         * 新增商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptGoods(XfptGoods xfptGoods)
        {
            applyCurrentSupplierScope(xfptGoods);
            fillGoodsDefaults(xfptGoods);
            int rows = xfptGoodsMapper.insert(xfptGoods);
            insertXfptGoodsSize(xfptGoods);
            return rows;
        }

        /**
         * 导入商品信息
         *
         * @param goodsList 商品信息列表
         * @param updateSupport 是否更新已存在数据
         * @param operName 操作人
         * @return 导入结果
         */
        @Override
        @Transactional
        public String importGoods(List<XfptGoods> goodsList, boolean updateSupport, String operName)
        {
            if (StringUtils.isEmpty(goodsList)) {
                throw new ServiceException("导入商品数据不能为空！");
            }
            int successNum = 0;
            int failureNum = 0;
            StringBuilder successMsg = new StringBuilder();
            StringBuilder failureMsg = new StringBuilder();
            for (XfptGoods goods : goodsList) {
                try {
                    applyCurrentSupplierScope(goods);
                    validateImportGoods(goods);
                    XfptGoods existing = lambdaQuery().eq(XfptGoods::getGoodsCode, goods.getGoodsCode()).one();
                    fillGoodsDefaults(goods);
                    if (StringUtils.isNull(existing)) {
                        goods.setCreateBy(operName);
                        insertXfptGoods(goods);
                        successNum++;
                        successMsg.append("<br/>").append(successNum).append("、商品 ").append(goods.getGoodsName()).append(" 导入成功");
                    } else if (updateSupport) {
                        requireAccessibleGoods(existing.getId());
                        goods.setId(existing.getId());
                        goods.setUpdateBy(operName);
                        updateXfptGoods(goods);
                        successNum++;
                        successMsg.append("<br/>").append(successNum).append("、商品 ").append(goods.getGoodsName()).append(" 更新成功");
                    } else {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、商品 ").append(goods.getGoodsName())
                                .append(" 导入失败：商品编码 ").append(goods.getGoodsCode()).append(" 已存在");
                    }
                } catch (Exception e) {
                    failureNum++;
                    String goodsName = goods == null ? "" : StringUtils.nvl(goods.getGoodsName(), "");
                    failureMsg.append("<br/>").append(failureNum).append("、商品 ").append(goodsName)
                            .append(" 导入失败：").append(e.getMessage());
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据不正确，错误如下：");
                throw new ServiceException(failureMsg.toString());
            }
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
            return successMsg.toString();
        }

        /**
         * 修改商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptGoods(XfptGoods xfptGoods)
        {
                requireAccessibleGoods(xfptGoods.getId());
                applyCurrentSupplierScope(xfptGoods);
                fillGoodsDefaults(xfptGoods);
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(xfptGoods.getId());
            insertXfptGoodsSize(xfptGoods);
            return xfptGoodsMapper.updateById(xfptGoods);
        }

        /**
         * 批量删除商品信息
         *
         * @param ids 需要删除的商品信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGoodsByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                requireAccessibleGoods(id);
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(id);
        }
            return xfptGoodsMapper.deleteBatchIds(idList);
        }

        /**
         * 删除商品信息信息
         *
         * @param id 商品信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGoodsById(Long id)
        {
                requireAccessibleGoods(id);
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(id);
            return xfptGoodsMapper.deleteById(id);
        }

        /**
         * 新增商品尺码信息
         *
         * @param xfptGoods 商品信息对象
         */
        private void insertXfptGoodsSize(XfptGoods xfptGoods)
        {
            List<XfptGoodsSize> xfptGoodsSizeList = xfptGoods.getXfptGoodsSizeList();
            Long id = xfptGoods.getId();
            if (StringUtils.isNotEmpty(xfptGoodsSizeList)) {
                for (XfptGoodsSize xfptGoodsSize : xfptGoodsSizeList) {
                    xfptGoodsSize.setGoodsId(id);
                    if (StringUtils.isEmpty(xfptGoodsSize.getGenderType())) {
                        xfptGoodsSize.setGenderType("0");
                    }
                    if (StringUtils.isEmpty(xfptGoodsSize.getStatus())) {
                        xfptGoodsSize.setStatus("1");
                    }
                    if (StringUtils.isEmpty(xfptGoodsSize.getDelFlag())) {
                        xfptGoodsSize.setDelFlag("0");
                    }
                }
                    xfptGoodsMapper.batchInsertXfptGoodsSize(xfptGoodsSizeList);
            }
        }

        private void fillGoodsDefaults(XfptGoods xfptGoods)
        {
            if (StringUtils.isEmpty(xfptGoods.getStatus())) {
                xfptGoods.setStatus("0");
            }
            if (StringUtils.isEmpty(xfptGoods.getPublishStatus())) {
                xfptGoods.setPublishStatus("0");
            }
            if (StringUtils.isEmpty(xfptGoods.getDelFlag())) {
                xfptGoods.setDelFlag("0");
            }
        }

        private void validateImportGoods(XfptGoods goods)
        {
            if (StringUtils.isNull(goods)) {
                throw new ServiceException("商品数据不能为空");
            }
            if (StringUtils.isEmpty(goods.getGoodsName())) {
                throw new ServiceException("商品名称不能为空");
            }
            if (StringUtils.isEmpty(goods.getGoodsCode())) {
                throw new ServiceException("商品编码不能为空");
            }
            if (StringUtils.isEmpty(goods.getCategoryName())) {
                throw new ServiceException("品类名称不能为空");
            }
            if (StringUtils.isNull(goods.getSupplierId())) {
                throw new ServiceException("供应商ID不能为空");
            }
            if (StringUtils.isEmpty(goods.getSupplierName())) {
                throw new ServiceException("供应商名称不能为空");
            }
            if (StringUtils.isEmpty(goods.getSaleType())) {
                throw new ServiceException("销售类型不能为空");
            }
            if (StringUtils.isEmpty(goods.getStatus())) {
                throw new ServiceException("商品状态不能为空");
            }
            if (StringUtils.isEmpty(goods.getPublishStatus())) {
                throw new ServiceException("上架状态不能为空");
            }
        }

        private void applyCurrentSupplierScope(XfptGoods xfptGoods)
        {
            if (xfptGoods == null) {
                return;
            }
            Long currentSupplierId = getCurrentSupplierIdIfScoped();
            if (isSupplierScopedUser() && currentSupplierId == null) {
                throw new ServiceException("当前登录账号未关联厂商，无法操作商品");
            }
            if (currentSupplierId == null) {
                return;
            }
            SupplierMessage supplier = supplierMessageService.getById(currentSupplierId);
            if (supplier == null) {
                throw new ServiceException("当前登录账号关联的厂商不存在");
            }
            xfptGoods.setSupplierId(currentSupplierId);
            xfptGoods.setSupplierName(supplier.getSupplierName());
        }

        private XfptGoods requireAccessibleGoods(Long id)
        {
            return requireAccessibleGoods(id, null);
        }

        private XfptGoods requireAccessibleGoods(Long id, Long schoolId)
        {
            if (id == null) {
                throw new ServiceException("商品不存在");
            }
            Long currentSupplierId = getCurrentSupplierIdIfScoped();
            if (isSupplierScopedUser() && currentSupplierId == null) {
                throw new ServiceException("当前登录账号未关联厂商，无法操作商品");
            }
            XfptGoods goods = currentSupplierId == null
                    ? xfptGoodsMapper.selectXfptGoodsById(id, schoolId)
                    : xfptGoodsMapper.selectXfptGoodsByIdAndSupplierId(id, currentSupplierId, schoolId);
            if (goods == null) {
                throw new ServiceException(currentSupplierId == null
                        ? (schoolId == null ? "商品不存在" : "商品不存在或未在当前学校统购上架")
                        : "无权操作其他厂商的商品");
            }
            return goods;
        }

        private void applyParentSchoolScope(XfptGoods xfptGoods)
        {
            if (xfptGoods == null) {
                return;
            }
            Object simIdValue = xfptGoods.getParams().get("simId");
            String simId = simIdValue == null ? null : String.valueOf(simIdValue).trim();
            if (StringUtils.isBlank(simId)) {
                return;
            }
            Set<Long> schoolIds = resolveParentSchoolIds(simId);
            if (xfptGoods.getSchoolId() != null) {
                if (!schoolIds.isEmpty() && !schoolIds.contains(xfptGoods.getSchoolId())) {
                    throw new ServiceException("只能查看当前家长绑定学生所属学校的商品");
                }
                xfptGoods.getParams().remove("schoolIds");
                return;
            }
            if (schoolIds.isEmpty()) {
                xfptGoods.setSchoolId(-1L);
                xfptGoods.getParams().remove("schoolIds");
                return;
            }
            xfptGoods.setSchoolId(resolveAccessibleSchoolId(schoolIds.iterator().next()));
            xfptGoods.getParams().remove("schoolIds");
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

        private Long resolveAccessibleSchoolId(Long rawSchoolId)
        {
            if (rawSchoolId == null) {
                return null;
            }
            XfptSchoolMessage schoolMessage = xfptSchoolMessageService.getById(rawSchoolId);
            if (schoolMessage != null) {
                return schoolMessage.getId();
            }
            XfptSchoolMessage mappedSchool = xfptSchoolMessageService.lambdaQuery()
                    .eq(XfptSchoolMessage::getDeptId, rawSchoolId)
                    .last("limit 1")
                    .one();
            return mappedSchool == null ? rawSchoolId : mappedSchool.getId();
        }

        private Long getCurrentSupplierIdIfScoped()
        {
            SysUser currentUser = getCurrentUser();
            if (!supplierAccessSupport.isSupplierUser(currentUser)) {
                return null;
            }
            return supplierAccessSupport.resolveSupplierId(currentUser);
        }

        private boolean isSupplierScopedUser()
        {
            return supplierAccessSupport.isSupplierUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
        }

        private SysUser getCurrentUser()
        {
            if (SecurityUtils.getLoginUser() == null) {
                return null;
            }
            return SecurityUtils.getLoginUser().getSysUser();
        }

}
