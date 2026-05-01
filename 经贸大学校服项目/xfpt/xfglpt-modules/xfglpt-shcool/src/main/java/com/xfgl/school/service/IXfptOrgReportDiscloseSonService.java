package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptOrgReportDiscloseSon;

/**
 * 选用组织名单报备与公开从Service接口
 *
 * @author wxm
 * @date 2025-04-23
 */
public interface IXfptOrgReportDiscloseSonService extends IService<XfptOrgReportDiscloseSon> {

    /**
     * 查询选用组织名单报备与公开从列表
     *
     * @param xfptOrgReportDiscloseSon 选用组织名单报备与公开从
     * @return 选用组织名单报备与公开从集合
     */
    public List<XfptOrgReportDiscloseSon> selectXfptOrgReportDiscloseSonList(XfptOrgReportDiscloseSon xfptOrgReportDiscloseSon);

    int deleteOrgReportDiscloseSon(XfptOrgReportDiscloseSon orgReportDiscloseSon);

    int insertXfptOrgReportDiscloseSon(XfptOrgReportDiscloseSon xfptOrgReportDiscloseSon);

}
