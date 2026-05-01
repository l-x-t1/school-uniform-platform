package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.TeamMeeting;
import com.xfgl.school.domain.vo.XfptSchoolStatisticsVO;
import com.xfgl.school.mapper.TeamMeetingMapper;
import com.xfgl.school.service.ITeamMeetingService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 班子会议Service业务层处理
 *
 * @author wxm
 * @date 2025-04-22
 */
@Service
public class TeamMeetingServiceImpl extends ServiceImpl<TeamMeetingMapper, TeamMeeting> implements ITeamMeetingService {

    @Autowired
    private TeamMeetingMapper teamMeetingMapper;

    /**
     * 查询班子会议列表
     *
     * @param teamMeeting 班子会议
     * @return 班子会议
     */
    @Override
    public List<TeamMeeting> selectTeamMeetingList(TeamMeeting teamMeeting) {
        return teamMeetingMapper.selectTeamMeetingList(teamMeeting);
    }

    @Override
    public int updateTeamMeeting(TeamMeeting teamMeeting) {
        return teamMeetingMapper.updateTeamMeeting(teamMeeting);
    }

    @Override
    public int updateTeamMeetingCurrOrder(Long teamMeetingId, String timeTbCurr) {
        Objects.requireNonNull(teamMeetingId, "班子会议主键不能为空！");
        TeamMeeting entityBase = getById(teamMeetingId);
        if (entityBase == null) {
            throw new RuntimeException("没有获取到该班子会议数据！");
        }
        Long currOrder = entityBase.getCurrOrder();
        if (currOrder == null || currOrder == 0) {
            currOrder = 1L;
        } else {
            currOrder += 1L;
        }
        String timeTb = entityBase.getTimeTb();
        if (StringUtils.isBlank(timeTb)) {
            timeTb = timeTbCurr;
        } else {
            if (timeTb.contains(timeTbCurr.split("#")[0])) {
                //同一个班子会议中如果重复存在对应步骤的模块名称，则不进行二次存储
                return 1;
            }
            StringJoiner joiner = new StringJoiner(",");
            joiner.add(timeTb);
            joiner.add(currOrder + "#" + timeTbCurr);
            timeTb = joiner.toString();
        }
        TeamMeeting conditionEntity = TeamMeeting.builder().id(teamMeetingId).currOrder(currOrder).timeTb(timeTb).build();
        conditionEntity.setUpdateBy("系统");
        return teamMeetingMapper.updateTeamMeetingCurrOrder(conditionEntity);
    }

    @Override
    public List<XfptSchoolStatisticsVO> listSchoolStatistics(XfptSchoolStatisticsVO schoolStatisticsVO) {
        return teamMeetingMapper.listSchoolStatistics(schoolStatisticsVO);
    }
}
