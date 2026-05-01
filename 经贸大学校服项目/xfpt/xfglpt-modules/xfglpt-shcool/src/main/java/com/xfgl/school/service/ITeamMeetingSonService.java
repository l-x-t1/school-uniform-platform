package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.TeamMeetingSon;

/**
 * 选购需求Service接口
 *
 * @author wxm
 * @date 2025-04-22
 */
public interface ITeamMeetingSonService extends IService<TeamMeetingSon> {

    /**
     * 查询选购需求列表
     *
     * @param teamMeetingSon 选购需求
     * @return 选购需求集合
     */
    public List<TeamMeetingSon> selectTeamMeetingSonList(TeamMeetingSon teamMeetingSon);

    int deleteTeamMeetingSon(TeamMeetingSon teamMeetingSon);

}
