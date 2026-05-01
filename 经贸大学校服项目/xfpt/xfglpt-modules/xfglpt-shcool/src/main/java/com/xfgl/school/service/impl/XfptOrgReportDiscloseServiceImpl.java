package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.school.domain.XfptOrgReportDisclose;
import com.xfgl.school.mapper.XfptOrgReportDiscloseMapper;
import com.xfgl.school.service.IXfptOrgReportDiscloseService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 选用组织名单报备与公开Service业务层处理
 *
 * @author wxm
 * @date 2025-04-23
 */
@Service
public class XfptOrgReportDiscloseServiceImpl extends ServiceImpl<XfptOrgReportDiscloseMapper, XfptOrgReportDisclose> implements IXfptOrgReportDiscloseService {

    @Autowired
    private XfptOrgReportDiscloseMapper xfptOrgReportDiscloseMapper;

    /**
     * 查询选用组织名单报备与公开列表
     *
     * @param xfptOrgReportDisclose 选用组织名单报备与公开
     * @return 选用组织名单报备与公开
     */
    @Override
    public List<XfptOrgReportDisclose> selectXfptOrgReportDiscloseList(XfptOrgReportDisclose xfptOrgReportDisclose) {
        return xfptOrgReportDiscloseMapper.selectXfptOrgReportDiscloseList(xfptOrgReportDisclose);
    }

    @Override
    public XfptOrgReportDisclose selectXfptOrgReportDiscloseById(Long tid) {
        return xfptOrgReportDiscloseMapper.selectXfptOrgReportDiscloseById(tid);
    }
}
