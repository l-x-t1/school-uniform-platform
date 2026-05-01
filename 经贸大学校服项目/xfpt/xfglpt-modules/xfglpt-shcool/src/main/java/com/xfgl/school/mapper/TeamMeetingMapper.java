package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.TeamMeeting;
import com.xfgl.school.domain.vo.XfptSchoolStatisticsVO;
import org.apache.ibatis.annotations.Param;

/**
 * 班子会议Mapper接口
 *
 * @author wxm
 * @date 2025-04-22
 */
public interface TeamMeetingMapper extends BaseMapper<TeamMeeting> {

    /**
     * 查询班子会议列表
     *
     * @param teamMeeting 班子会议
     * @return 班子会议集合
     */
    public List<TeamMeeting> selectTeamMeetingList(TeamMeeting teamMeeting);

    TeamMeeting selectTeamMeetingById(TeamMeeting teamMeeting);

    int insertTeamMeeting(TeamMeeting teamMeeting);

    int updateTeamMeeting(@Param("teamMeeting") TeamMeeting teamMeeting);

    /**
     * 更新当前最新所处执行步骤
     * @param teamMeeting
     * @return
     */
    int updateTeamMeetingCurrOrder(TeamMeeting teamMeeting);

    int deleteTeamMeetingById(Long id);

    int deleteTeamMeetingByIds(String[] ids);

    List<XfptSchoolStatisticsVO> listSchoolStatistics(XfptSchoolStatisticsVO schoolStatisticsVO);
}
