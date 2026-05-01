package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.TeamMeetingSon;

/**
 * 选购需求Mapper接口
 *
 * @author wxm
 * @date 2025-04-22
 */
public interface TeamMeetingSonMapper extends BaseMapper<TeamMeetingSon> {

    /**
     * 查询选购需求列表
     *
     * @param teamMeetingSon 选购需求
     * @return 选购需求集合
     */
    public List<TeamMeetingSon> selectTeamMeetingSonList(TeamMeetingSon teamMeetingSon);

    public TeamMeetingSon selectTeamMeetingSonById(String id);

    int insertTeamMeetingSon(TeamMeetingSon teamMeetingSon);

    int updateTeamMeetingSon(TeamMeetingSon teamMeetingSon);

    int deleteTeamMeetingSon(TeamMeetingSon teamMeetingSon);

    int deleteTeamMeetingSonByIds(String[] ids);
}
