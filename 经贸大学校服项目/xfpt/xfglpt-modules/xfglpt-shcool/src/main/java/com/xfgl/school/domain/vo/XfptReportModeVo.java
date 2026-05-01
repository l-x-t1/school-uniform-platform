package com.xfgl.school.domain.vo;

import com.xfgl.school.domain.XfptReportMode;
import com.xfgl.school.domain.XfptReportModeOper;
import lombok.Data;

import java.util.List;

@Data
public class XfptReportModeVo {

    private XfptReportMode xfptReportMode;

    private List<XfptReportModeOper> operList;
}
