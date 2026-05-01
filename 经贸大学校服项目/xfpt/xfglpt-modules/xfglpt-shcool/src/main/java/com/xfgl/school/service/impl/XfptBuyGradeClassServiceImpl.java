package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.xfgl.school.mapper.XfptBuyGradeClassMapper;
import com.xfgl.school.domain.XfptBuyGradeClass;
import com.xfgl.school.service.IXfptBuyGradeClassService;

/**
 * 采购年级Service业务层处理
 *
 * @author wxm
 * @date 2025-04-27
 */
@Service
public class XfptBuyGradeClassServiceImpl extends ServiceImpl<XfptBuyGradeClassMapper, XfptBuyGradeClass> implements IXfptBuyGradeClassService {

    @Autowired
    private XfptBuyGradeClassMapper xfptBuyGradeClassMapper;

    /**
     * 查询采购年级列表
     *
     * @param xfptBuyGradeClass 采购年级
     * @return 采购年级
     */
    @Override
    public List<XfptBuyGradeClass> selectXfptBuyGradeClassList(XfptBuyGradeClass xfptBuyGradeClass) {
        return xfptBuyGradeClassMapper.selectXfptBuyGradeClassList(xfptBuyGradeClass);
    }

    @Override
    public XfptBuyGradeClass selectXfptBuyGradeClassById(Long id) {
        return xfptBuyGradeClassMapper.selectXfptBuyGradeClassById(id);
    }

    @Override
    public int insertXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass) {
        Objects.requireNonNull(xfptBuyGradeClass.getCategory(),"category can not is null");
        Objects.requireNonNull(xfptBuyGradeClass.getPid(),"pid can not is null");
        xfptBuyGradeClass.setCreateTime(new Date());
        return xfptBuyGradeClassMapper.insertXfptBuyGradeClass(xfptBuyGradeClass);
    }

    @Override
    public int updateXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass) {
        xfptBuyGradeClass.setUpdateTime(new Date());
        if (StringUtils.isBlank(xfptBuyGradeClass.getUpdateBy())) {
            String username = SecurityUtils.getUsername();
            if (StringUtils.isBlank(username)) {
                username = "系统";
            }
            xfptBuyGradeClass.setUpdateBy(username);
        }
        return xfptBuyGradeClassMapper.updateXfptBuyGradeClass(xfptBuyGradeClass);
    }

    @Override
    public int deleteXfptBuyGradeClassById(Long id) {
        return xfptBuyGradeClassMapper.deleteXfptBuyGradeClassById(id);
    }

    @Override
    public int deleteXfptBuyGradeClass(XfptBuyGradeClass xfptBuyGradeClass) {
        Objects.requireNonNull(xfptBuyGradeClass.getCategory(), "category can not is null");
        Long pid = xfptBuyGradeClass.getPid();
        List<Long> pidList = xfptBuyGradeClass.getPidList();
        if (pid == null && CollectionUtils.isEmpty(pidList)) {
            Objects.requireNonNull(null, "pid or pidList can not both null");
        }
        if (StringUtils.isBlank(xfptBuyGradeClass.getUpdateBy())) {
            String username = SecurityUtils.getUsername();
            if (StringUtils.isBlank(username)) {
                username = "系统";
            }
            xfptBuyGradeClass.setUpdateBy(username);
        }
        return xfptBuyGradeClassMapper.deleteXfptBuyGradeClass(xfptBuyGradeClass);
    }

    @Override
    public int deleteXfptBuyGradeClassByIds(Long[] ids) {
        return xfptBuyGradeClassMapper.deleteXfptBuyGradeClassByIds(ids);
    }

    @Override
    public int physicDeleteDataWithGradeClassIdList(List<Long> gradeClassIdList, Long pid, Long category) {
        return xfptBuyGradeClassMapper.physicDeleteDataWithGradeClassIdList(gradeClassIdList,pid,category);
    }
}
