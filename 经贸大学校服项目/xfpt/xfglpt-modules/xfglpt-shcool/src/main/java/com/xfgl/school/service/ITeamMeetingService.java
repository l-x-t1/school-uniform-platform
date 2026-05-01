package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.TeamMeeting;
import com.xfgl.school.domain.vo.XfptSchoolStatisticsVO;

/**
 * 班子会议Service接口
 *
 * @author wxm
 * @date 2025-04-22
 */
public interface ITeamMeetingService extends IService<TeamMeeting> {

    /**
     * 查询班子会议列表
     *
     * @param teamMeeting 班子会议
     * @return 班子会议集合
     */
    public List<TeamMeeting> selectTeamMeetingList(TeamMeeting teamMeeting);

    int updateTeamMeeting(TeamMeeting teamMeeting);

    /**
     * 更新当前最新所处执行步骤 与时间轴的记录处理
     * @param teamMeetingId  班子会议表主键id
     * @param timeTb 当前步骤时间轴数据
     * @return
     */
    int updateTeamMeetingCurrOrder(Long teamMeetingId,String timeTb);

    /**
     * 查询教育局-校服选用采购规范流程统计：区县下对应学校的报备情况统计
     * @param schoolStatisticsVO
     * @return
     */
    List<XfptSchoolStatisticsVO> listSchoolStatistics(XfptSchoolStatisticsVO schoolStatisticsVO);

}
