package com.xfgl.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;

import java.util.List;

/**
 * 学校与供应商检验报告关联Mapper接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface XfptSuppliceInspectionReportMapper extends BaseMapper<XfptSuppliceInspectionReport> {
    /**
     * 查询学校与供应商检验报告关联列表
     *
     * @param xfptSuppliceInspectionReport 学校与供应商检验报告关联
     * @return 学校与供应商检验报告关联集合
     */
    public List<XfptSuppliceInspectionReport> selectXfptSuppliceInspectionReportList(XfptSuppliceInspectionReport xfptSuppliceInspectionReport);
}
