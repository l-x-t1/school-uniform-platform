package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptOrgReportDiscloseSon;

/**
 * 选用组织名单报备与公开从Mapper接口
 *
 * @author wxm
 * @date 2025-04-23
 */
public interface XfptOrgReportDiscloseSonMapper extends BaseMapper<XfptOrgReportDiscloseSon> {
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
