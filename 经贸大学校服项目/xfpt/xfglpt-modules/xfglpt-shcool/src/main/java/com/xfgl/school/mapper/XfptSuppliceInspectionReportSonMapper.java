package com.xfgl.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;

import java.util.List;

/**
 * 学校与供应商检验报告从Mapper接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface XfptSuppliceInspectionReportSonMapper extends BaseMapper<XfptSuppliceInspectionReportSon> {
    /**
     * 查询学校与供应商检验报告从列表
     *
     * @param xfptSuppliceInspectionReportSon 学校与供应商检验报告从
     * @return 学校与供应商检验报告从集合
     */
    public List<XfptSuppliceInspectionReportSon> selectXfptSuppliceInspectionReportSonList(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon);
}
