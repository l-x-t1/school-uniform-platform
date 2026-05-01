package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.TeamMeetingSon;
import com.xfgl.school.mapper.TeamMeetingSonMapper;
import com.xfgl.school.service.ITeamMeetingSonService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 选购需求Service业务层处理
 *
 * @author wxm
 * @date 2025-04-22
 */
@Service
public class TeamMeetingSonServiceImpl extends ServiceImpl<TeamMeetingSonMapper, TeamMeetingSon> implements ITeamMeetingSonService {

    @Autowired
    private TeamMeetingSonMapper teamMeetingSonMapper;

    /**
     * 查询选购需求列表
     *
     * @param teamMeetingSon 选购需求
     * @return 选购需求
     */
    @Override
    public List<TeamMeetingSon> selectTeamMeetingSonList(TeamMeetingSon teamMeetingSon) {
        return teamMeetingSonMapper.selectTeamMeetingSonList(teamMeetingSon);
    }

    @Override
    public int deleteTeamMeetingSon(TeamMeetingSon teamMeetingSon) {
        return teamMeetingSonMapper.deleteTeamMeetingSon(teamMeetingSon);
    }
}
