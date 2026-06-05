package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.mapper.XfptSchoolSupplierGoodsRelMapper;
import com.xfgl.school.service.IXfptSchoolSupplierGoodsRelService;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学校供应商合作商品Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptSchoolSupplierGoodsRelServiceImpl extends ServiceImpl<XfptSchoolSupplierGoodsRelMapper, XfptSchoolSupplierGoodsRel> implements IXfptSchoolSupplierGoodsRelService {

    @Autowired
    private XfptSchoolSupplierGoodsRelMapper xfptSchoolSupplierGoodsRelMapper;
    @Autowired
    private IXfptSchoolSupplierRelService schoolSupplierRelService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询学校供应商合作商品列表
     *
     * @param xfptSchoolSupplierGoodsRel 学校供应商合作商品
     * @return 学校供应商合作商品
     */
    @Override
    public List<XfptSchoolSupplierGoodsRel> selectXfptSchoolSupplierGoodsRelList(XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel)
    {
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptSchoolSupplierGoodsRel.setSchoolId(currentSchool.getId());
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            return Collections.emptyList();
        }
        if (currentSupplierId != null) {
            xfptSchoolSupplierGoodsRel.setSupplierId(currentSupplierId);
        }
        return xfptSchoolSupplierGoodsRelMapper.selectXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRel);
    }

    @Override
    public XfptSchoolSupplierGoodsRel getById(java.io.Serializable id)
    {
        XfptSchoolSupplierGoodsRel goodsRel = super.getById(id);
        requireAccessibleGoodsRel(goodsRel);
        return goodsRel;
    }

    @Override
    public boolean save(XfptSchoolSupplierGoodsRel entity)
    {
        fillRelSnapshotAndValidate(entity, null);
        return super.save(entity);
    }

    @Override
    public boolean updateById(XfptSchoolSupplierGoodsRel entity)
    {
        XfptSchoolSupplierGoodsRel existing = requireAccessibleGoodsRel(super.getById(entity.getId()));
        fillRelSnapshotAndValidate(entity, existing);
        return super.updateById(entity);
    }

    @Override
    public boolean removeByIds(Collection<?> list)
    {
        if (list != null) {
            for (Object idObj : list) {
                if (idObj == null) {
                    continue;
                }
                Long id = Long.valueOf(String.valueOf(idObj));
                requireAccessibleGoodsRel(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void fillRelSnapshotAndValidate(XfptSchoolSupplierGoodsRel entity, XfptSchoolSupplierGoodsRel existing)
    {
        if (entity == null) {
            throw new ServiceException("合作商品信息不能为空");
        }
        Long relId = entity.getRelId();
        if (relId == null && existing != null) {
            relId = existing.getRelId();
            entity.setRelId(relId);
        }
        if (relId == null) {
            throw new ServiceException("合作关系不能为空");
        }
        XfptSchoolSupplierRel rel = schoolSupplierRelService.selectXfptSchoolSupplierRelById(relId);
        if (StringUtils.isNull(rel)) {
            throw new ServiceException("学校供应商合作关系不存在");
        }
        entity.setSchoolId(rel.getSchoolId());
        entity.setSupplierId(rel.getSupplierId());
        if (StringUtils.isEmpty(entity.getStatus())) {
            entity.setStatus(existing == null ? "1" : existing.getStatus());
        }
        if (StringUtils.isEmpty(entity.getDelFlag())) {
            entity.setDelFlag(existing == null ? "0" : existing.getDelFlag());
        }
    }

    private XfptSchoolSupplierGoodsRel requireAccessibleGoodsRel(XfptSchoolSupplierGoodsRel goodsRel)
    {
        if (StringUtils.isNull(goodsRel)) {
            throw new ServiceException("学校供应商合作商品不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser()) {
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法操作合作商品");
            }
            if (!currentSchool.getId().equals(goodsRel.getSchoolId())) {
                throw new ServiceException("无权操作其他学校的合作商品");
            }
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser()) {
            if (currentSupplierId == null) {
                throw new ServiceException("当前登录账号未关联厂商，无法操作合作商品");
            }
            if (!currentSupplierId.equals(goodsRel.getSupplierId())) {
                throw new ServiceException("无权操作其他厂商的合作商品");
            }
        }
        return goodsRel;
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
