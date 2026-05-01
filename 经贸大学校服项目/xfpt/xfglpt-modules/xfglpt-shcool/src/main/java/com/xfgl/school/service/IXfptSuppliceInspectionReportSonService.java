package com.xfgl.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;

import java.util.List;

/**
 * 学校与供应商检验报告从Service接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface IXfptSuppliceInspectionReportSonService extends IService<XfptSuppliceInspectionReportSon> {

    /**
     * 查询学校与供应商检验报告从列表
     *
     * @param xfptSuppliceInspectionReportSon 学校与供应商检验报告从
     * @return 学校与供应商检验报告从集合
     */
    public List<XfptSuppliceInspectionReportSon> selectXfptSuppliceInspectionReportSonList(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon);

}
