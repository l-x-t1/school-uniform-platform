package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.SupplierMessage;
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.mapper.XfptGoodsMapper;
import com.xfgl.school.mapper.XfptSchoolSupplierRelMapper;
import com.xfgl.school.service.ISupplierMessageService;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学校供应商合作关系Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptSchoolSupplierRelServiceImpl extends ServiceImpl<XfptSchoolSupplierRelMapper, XfptSchoolSupplierRel> implements IXfptSchoolSupplierRelService {

    @Autowired
    private XfptSchoolSupplierRelMapper xfptSchoolSupplierRelMapper;
    @Autowired
    private XfptGoodsMapper xfptGoodsMapper;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private ISupplierMessageService supplierMessageService;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询学校供应商合作关系列表
     *
     * @param xfptSchoolSupplierRel 学校供应商合作关系
     * @return 学校供应商合作关系
     */
    @Override
    public List<XfptSchoolSupplierRel> selectXfptSchoolSupplierRelList(XfptSchoolSupplierRel xfptSchoolSupplierRel)
    {
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptSchoolSupplierRel.setSchoolId(currentSchool.getId());
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            return Collections.emptyList();
        }
        if (currentSupplierId != null) {
            xfptSchoolSupplierRel.setSupplierId(currentSupplierId);
        }
        return xfptSchoolSupplierRelMapper.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
    }

        /**
         * 查询学校供应商合作关系
         *
         * @param id 学校供应商合作关系主键
         * @return 学校供应商合作关系
         */
        @Override
        public XfptSchoolSupplierRel selectXfptSchoolSupplierRelById(Long id)
        {
            XfptSchoolSupplierRel xfptSchoolSupplierRel = requireAccessibleRel(id);
            List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList = xfptSchoolSupplierRelMapper.selectXfptSchoolSupplierGoodsRelById(id);
            xfptSchoolSupplierRel.setXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRelList);
            return xfptSchoolSupplierRel;
        }

        /**
         * 新增学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            applyCurrentSchoolScopeForWrite(xfptSchoolSupplierRel);
            applyCurrentSupplierScopeForWrite(xfptSchoolSupplierRel);
            fillRelDefaults(xfptSchoolSupplierRel);
            int rows = xfptSchoolSupplierRelMapper.insert(xfptSchoolSupplierRel);
            insertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierRel);
            return rows;
        }

        /**
         * 修改学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            XfptSchoolSupplierRel existing = requireAccessibleRel(xfptSchoolSupplierRel.getId());
            applyCurrentSchoolScopeForWrite(xfptSchoolSupplierRel);
            applyCurrentSupplierScopeForWrite(xfptSchoolSupplierRel);
            if (StringUtils.isNull(xfptSchoolSupplierRel.getSchoolId())) {
                xfptSchoolSupplierRel.setSchoolId(existing.getSchoolId());
            }
            if (StringUtils.isEmpty(xfptSchoolSupplierRel.getSchoolName())) {
                xfptSchoolSupplierRel.setSchoolName(existing.getSchoolName());
            }
            fillRelDefaults(xfptSchoolSupplierRel);
            xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(xfptSchoolSupplierRel.getId());
            insertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierRel);
            return xfptSchoolSupplierRelMapper.updateById(xfptSchoolSupplierRel);
        }

        /**
         * 取消合作
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        @Override
        public int cancelCooperation(Long id)
        {
            requireAccessibleRel(id);
            XfptSchoolSupplierRel rel = new XfptSchoolSupplierRel();
            rel.setId(id);
            rel.setCooperateStatus("2");
            return xfptSchoolSupplierRelMapper.updateById(rel);
        }

        /**
         * 设置服务电话
         *
         * @param id 学校供应商合作关系主键
         * @param servicePhone 服务电话
         * @return 结果
         */
        @Override
        public int updateServicePhone(Long id, String servicePhone)
        {
            requireAccessibleRel(id);
            XfptSchoolSupplierRel rel = new XfptSchoolSupplierRel();
            rel.setId(id);
            rel.setServicePhone(servicePhone);
            return xfptSchoolSupplierRelMapper.updateById(rel);
        }

        /**
         * 替换合作商品
         *
         * @param id 学校供应商合作关系主键
         * @param goodsRelList 合作商品列表
         * @return 结果
         */
        @Transactional
        @Override
        public int replaceGoodsRel(Long id, List<XfptSchoolSupplierGoodsRel> goodsRelList)
        {
            XfptSchoolSupplierRel rel = requireAccessibleRel(id);
            validateGoodsRelList(goodsRelList, rel.getSupplierId());
            xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
            if (StringUtils.isNotEmpty(goodsRelList)) {
                for (XfptSchoolSupplierGoodsRel goodsRel : goodsRelList) {
                    goodsRel.setId(null);
                    goodsRel.setRelId(id);
                    goodsRel.setSchoolId(rel.getSchoolId());
                    goodsRel.setSupplierId(rel.getSupplierId());
                    if (StringUtils.isEmpty(goodsRel.getStatus())) {
                        goodsRel.setStatus("1");
                    }
                    if (StringUtils.isEmpty(goodsRel.getDelFlag())) {
                        goodsRel.setDelFlag("0");
                    }
                }
                xfptSchoolSupplierRelMapper.batchInsertXfptSchoolSupplierGoodsRel(goodsRelList);
            }
            return 1;
        }

        /**
         * 批量删除学校供应商合作关系
         *
         * @param ids 需要删除的学校供应商合作关系主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptSchoolSupplierRelByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                requireAccessibleRel(id);
                xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
            }
            return xfptSchoolSupplierRelMapper.deleteBatchIds(idList);
        }

        /**
         * 删除学校供应商合作关系信息
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptSchoolSupplierRelById(Long id)
        {
            requireAccessibleRel(id);
            xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
            return xfptSchoolSupplierRelMapper.deleteById(id);
        }

        /**
         * 新增学校供应商合作商品信息
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系对象
         */
        private void insertXfptSchoolSupplierGoodsRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList = xfptSchoolSupplierRel.getXfptSchoolSupplierGoodsRelList();
            Long id = xfptSchoolSupplierRel.getId();
            if (StringUtils.isNotNull(xfptSchoolSupplierGoodsRelList) && !xfptSchoolSupplierGoodsRelList.isEmpty()) {
                validateGoodsRelList(xfptSchoolSupplierGoodsRelList, xfptSchoolSupplierRel.getSupplierId());
                for (XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel : xfptSchoolSupplierGoodsRelList) {
                    xfptSchoolSupplierGoodsRel.setRelId(id);
                    xfptSchoolSupplierGoodsRel.setSchoolId(xfptSchoolSupplierRel.getSchoolId());
                    xfptSchoolSupplierGoodsRel.setSupplierId(xfptSchoolSupplierRel.getSupplierId());
                    if (StringUtils.isEmpty(xfptSchoolSupplierGoodsRel.getStatus())) {
                        xfptSchoolSupplierGoodsRel.setStatus("1");
                    }
                    if (StringUtils.isEmpty(xfptSchoolSupplierGoodsRel.getDelFlag())) {
                        xfptSchoolSupplierGoodsRel.setDelFlag("0");
                    }
                }
                    xfptSchoolSupplierRelMapper.batchInsertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierGoodsRelList);
            }
        }

        private void fillRelDefaults(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            if (StringUtils.isEmpty(xfptSchoolSupplierRel.getCooperateStatus())) {
                xfptSchoolSupplierRel.setCooperateStatus("1");
            }
            if (StringUtils.isEmpty(xfptSchoolSupplierRel.getDelFlag())) {
                xfptSchoolSupplierRel.setDelFlag("0");
            }
        }

        private void validateGoodsRelList(List<XfptSchoolSupplierGoodsRel> goodsRelList, Long supplierId)
        {
            if (StringUtils.isEmpty(goodsRelList)) {
                return;
            }
            Set<Long> goodsIds = new HashSet<>();
            for (XfptSchoolSupplierGoodsRel goodsRel : goodsRelList) {
                if (StringUtils.isNull(goodsRel.getGoodsId())) {
                    throw new ServiceException("合作商品不能为空");
                }
                validateGoodsOwnership(goodsRel.getGoodsId(), supplierId);
                if (!goodsIds.add(goodsRel.getGoodsId())) {
                    throw new ServiceException("合作商品不能重复关联");
                }
            }
        }

        private void validateGoodsOwnership(Long goodsId, Long supplierId)
        {
            XfptGoods goods = xfptGoodsMapper.selectById(goodsId);
            if (StringUtils.isNull(goods) || "1".equals(goods.getDelFlag())) {
                throw new ServiceException("鍚堜綔鍟嗗搧涓嶅瓨鍦ㄦ垨宸插垹闄?");
            }
            if (supplierId != null && !supplierId.equals(goods.getSupplierId())) {
                throw new ServiceException("鍚堜綔鍟嗗搧蹇呴』灞炰簬褰撳墠鍚堜綔渚涘簲鍟?");
            }
        }

        private XfptSchoolSupplierRel requireAccessibleRel(Long id)
        {
            XfptSchoolSupplierRel rel = xfptSchoolSupplierRelMapper.selectById(id);
            if (StringUtils.isNull(rel)) {
                throw new ServiceException("学校供应商合作关系不存在");
            }
            XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
            if (isSchoolScopedUser()) {
                if (currentSchool == null) {
                    throw new ServiceException("当前登录学校信息不存在，无法操作合作关系");
                }
                if (!currentSchool.getId().equals(rel.getSchoolId())) {
                    throw new ServiceException("无权操作其他学校的合作关系");
                }
            }
            Long currentSupplierId = getCurrentSupplierIdIfScoped();
            if (isSupplierScopedUser()) {
                if (currentSupplierId == null) {
                    throw new ServiceException("当前登录账号未关联厂商，无法操作合作关系");
                }
                if (!currentSupplierId.equals(rel.getSupplierId())) {
                    throw new ServiceException("无权操作其他厂商的合作关系");
                }
            }
            return rel;
        }

        private void applyCurrentSchoolScopeForWrite(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            if (!isSchoolScopedUser()) {
                return;
            }
            XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法保存合作关系");
            }
            xfptSchoolSupplierRel.setSchoolId(currentSchool.getId());
            xfptSchoolSupplierRel.setSchoolName(currentSchool.getSchoolName());
        }

        private void applyCurrentSupplierScopeForWrite(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            Long currentSupplierId = getCurrentSupplierIdIfScoped();
            if (isSupplierScopedUser()) {
                if (currentSupplierId == null) {
                    throw new ServiceException("当前登录账号未关联厂商，无法保存合作关系");
                }
                SupplierMessage supplier = supplierMessageService.getById(currentSupplierId);
                if (supplier == null) {
                    throw new ServiceException("当前登录账号关联的厂商不存在");
                }
                xfptSchoolSupplierRel.setSupplierId(currentSupplierId);
                xfptSchoolSupplierRel.setSupplierName(supplier.getSupplierName());
            }
        }

        private Long getCurrentSupplierIdIfScoped()
        {
            SysUser currentUser = getCurrentUser();
            if (!supplierAccessSupport.isSupplierUser(currentUser)) {
                return null;
            }
            return supplierAccessSupport.resolveSupplierId(currentUser);
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
