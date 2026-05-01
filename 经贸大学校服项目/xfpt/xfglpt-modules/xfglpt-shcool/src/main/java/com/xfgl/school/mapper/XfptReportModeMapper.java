package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptReportMode;


/**
 * 选用组织确定选购模式会议Mapper接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface XfptReportModeMapper extends BaseMapper<XfptReportMode> {
    /**
     * 查询选用组织确定选购模式会议列表
     *
     * @param xfptReportMode 选用组织确定选购模式会议
     * @return 选用组织确定选购模式会议集合
     */
    public List<XfptReportMode> selectXfptReportModeList(XfptReportMode xfptReportMode);
}
