package com.xfgl.school.service;

import com.xfgl.school.domain.vo.XfptJYNoticeSchoolStatisticsVO;
import com.xfgl.school.domain.vo.XfptJYNoticeStatisticsVO;

import java.util.List;

/**
 * 统计service
 */
public interface IXfptSchoolStatisticsService {

    /**
     * 获取校服交易管理-质量与监管-公告统计
     * @return
     */
    public List<XfptJYNoticeStatisticsVO> getJYNoticeStatisticsList(XfptJYNoticeStatisticsVO jYNoticeStatisticsVO);

    /**
     * 获取校服交易管理-质量与监管-学校统计
     * @param jYNoticeSchoolStatisticsVO
     * @return
     */
    public List<XfptJYNoticeSchoolStatisticsVO> getJYNoticeSchoolStatisticsList(XfptJYNoticeSchoolStatisticsVO jYNoticeSchoolStatisticsVO);
}
