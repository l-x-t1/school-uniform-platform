package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptOrgReportDisclose;

/**
 * 选用组织名单报备与公开Service接口
 *
 * @author wxm
 * @date 2025-04-23
 */
public interface IXfptOrgReportDiscloseService extends IService<XfptOrgReportDisclose> {

    /**
     * 查询选用组织名单报备与公开列表
     *
     * @param xfptOrgReportDisclose 选用组织名单报备与公开
     * @return 选用组织名单报备与公开集合
     */
    public List<XfptOrgReportDisclose> selectXfptOrgReportDiscloseList(XfptOrgReportDisclose xfptOrgReportDisclose);

    XfptOrgReportDisclose selectXfptOrgReportDiscloseById(Long tid);

}
