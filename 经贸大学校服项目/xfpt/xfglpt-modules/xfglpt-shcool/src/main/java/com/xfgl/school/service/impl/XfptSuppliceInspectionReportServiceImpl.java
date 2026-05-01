package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;
import com.xfgl.school.mapper.XfptSuppliceInspectionReportMapper;
import com.xfgl.school.service.IXfptSuppliceInspectionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校与供应商检验报告关联Service业务层处理
 *
 * @author yinet
 * @date 2025-04-22
 */
@Service
public class XfptSuppliceInspectionReportServiceImpl extends ServiceImpl<XfptSuppliceInspectionReportMapper, XfptSuppliceInspectionReport> implements IXfptSuppliceInspectionReportService {

    @Autowired
    private XfptSuppliceInspectionReportMapper xfptSuppliceInspectionReportMapper;

    /**
     * 查询学校与供应商检验报告关联列表
     *
     * @param xfptSuppliceInspectionReport 学校与供应商检验报告关联
     * @return 学校与供应商检验报告关联
     */
    @Override
    public List<XfptSuppliceInspectionReport> selectXfptSuppliceInspectionReportList(XfptSuppliceInspectionReport xfptSuppliceInspectionReport)
    {
        return xfptSuppliceInspectionReportMapper.selectXfptSuppliceInspectionReportList(xfptSuppliceInspectionReport);
    }


}
