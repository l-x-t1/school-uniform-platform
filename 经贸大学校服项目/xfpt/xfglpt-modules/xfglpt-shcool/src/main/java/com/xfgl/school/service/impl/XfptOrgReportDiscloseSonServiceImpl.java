package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.school.domain.XfptOrgReportDiscloseSon;
import com.xfgl.school.mapper.XfptOrgReportDiscloseSonMapper;
import com.xfgl.school.service.IXfptOrgReportDiscloseSonService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 选用组织名单报备与公开从Service业务层处理
 *
 * @author wxm
 * @date 2025-04-23
 */
@Service
public class XfptOrgReportDiscloseSonServiceImpl extends ServiceImpl<XfptOrgReportDiscloseSonMapper, XfptOrgReportDiscloseSon> implements IXfptOrgReportDiscloseSonService {

    @Autowired
    private XfptOrgReportDiscloseSonMapper xfptOrgReportDiscloseSonMapper;

    /**
     * 查询选用组织名单报备与公开从列表
     *
     * @param xfptOrgReportDiscloseSon 选用组织名单报备与公开从
     * @return 选用组织名单报备与公开从
     */
    @Override
    public List<XfptOrgReportDiscloseSon> selectXfptOrgReportDiscloseSonList(XfptOrgReportDiscloseSon xfptOrgReportDiscloseSon) {
        return xfptOrgReportDiscloseSonMapper.selectXfptOrgReportDiscloseSonList(xfptOrgReportDiscloseSon);
    }


    @Override
    public int deleteOrgReportDiscloseSon(XfptOrgReportDiscloseSon orgReportDiscloseSon) {
        return xfptOrgReportDiscloseSonMapper.deleteOrgReportDiscloseSon(orgReportDiscloseSon);
    }

    @Override
    public int insertXfptOrgReportDiscloseSon(XfptOrgReportDiscloseSon xfptOrgReportDiscloseSon) {
        return xfptOrgReportDiscloseSonMapper.insertXfptOrgReportDiscloseSon(xfptOrgReportDiscloseSon);
    }
}
