package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;
import com.xfgl.school.mapper.XfptSuppliceInspectionReportSonMapper;
import com.xfgl.school.service.IXfptSuppliceInspectionReportSonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学校与供应商检验报告从Service业务层处理
 *
 * @author yinet
 * @date 2025-04-22
 */
@Service
public class XfptSuppliceInspectionReportSonServiceImpl extends ServiceImpl<XfptSuppliceInspectionReportSonMapper, XfptSuppliceInspectionReportSon> implements IXfptSuppliceInspectionReportSonService {

    @Autowired
    private XfptSuppliceInspectionReportSonMapper xfptSuppliceInspectionReportSonMapper;

    /**
     * 查询学校与供应商检验报告从列表
     *
     * @param xfptSuppliceInspectionReportSon 学校与供应商检验报告从
     * @return 学校与供应商检验报告从
     */
    @Override
    public List<XfptSuppliceInspectionReportSon> selectXfptSuppliceInspectionReportSonList(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon)
    {
        return xfptSuppliceInspectionReportSonMapper.selectXfptSuppliceInspectionReportSonList(xfptSuppliceInspectionReportSon);
    }


}
