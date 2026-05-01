package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;

import java.util.List;

/**
 * 学校与供应商检验报告关联Service接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface IXfptSuppliceInspectionReportService extends IService<XfptSuppliceInspectionReport> {

    /**
     * 查询学校与供应商检验报告关联列表
     *
     * @param xfptSuppliceInspectionReport 学校与供应商检验报告关联
     * @return 学校与供应商检验报告关联集合
     */
    public List<XfptSuppliceInspectionReport> selectXfptSuppliceInspectionReportList(XfptSuppliceInspectionReport xfptSuppliceInspectionReport);

}
