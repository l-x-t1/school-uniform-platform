package com.xfgl.school.mapper;

import com.xfgl.school.domain.vo.XfptJYNoticeSchoolStatisticsVO;
import com.xfgl.school.domain.vo.XfptJYNoticeStatisticsVO;

import java.util.List;

public interface XfptSchoolStatisticsMapper {

    /**
     * 校服交易管理：公告统计
     *
     * @param jYNoticeStatisticsVO
     * @return
     */
    public List<XfptJYNoticeStatisticsVO> getJYNoticeStatisticsList(XfptJYNoticeStatisticsVO jYNoticeStatisticsVO);

    /**
     * 校服交易管理：学校统计
     * 由于没有进行过质检的，对应数据表就不会有对应的数据，无法进行条件过滤。
     * 所以处理没有进行过质检的数量 = 总数量 - 已经进行过质检的数量（且质检的类型为首次质检即可）
     * 由于对公告进行每一次质检时，就会创建一行质检子表记录，但这里统计的是有质检公告的数量，而不是质检次数。
     * 因此有质检公告数量：只需要判断是否存在过为首次质检即可，所以对应的xml中，没有 in(1,2)的过滤操作
     * 由上可知一个隐含条件：有质检公告数量 其实完全等价于 首次有质检公告数量
     * 注：这里没有使用除法计算，因为不做配置参数设置的话 2/7 = 0 而不是 0.2857142857142857
     * @param jYNoticeSchoolStatisticsVO
     * @return
     */
    List<XfptJYNoticeSchoolStatisticsVO> getJYNoticeSchoolStatisticsList(XfptJYNoticeSchoolStatisticsVO jYNoticeSchoolStatisticsVO);
}
