package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptProcSummaryMapper;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.service.IXfptProcSummaryService;

/**
 * 采购汇总信息Service业务层处理
 *
 * @author yinet
 * @date 2025-05-09
 */
@Service
public class XfptProcSummaryServiceImpl extends ServiceImpl<XfptProcSummaryMapper, XfptProcSummary> implements IXfptProcSummaryService {

    @Autowired
    private XfptProcSummaryMapper xfptProcSummaryMapper;

    /**
     * 查询采购汇总信息列表
     *
     * @param xfptProcSummary 采购汇总信息
     * @return 采购汇总信息
     */
    @Override
    public List<XfptProcSummary> selectXfptProcSummaryList(XfptProcSummary xfptProcSummary)
    {
        return xfptProcSummaryMapper.selectXfptProcSummaryList(xfptProcSummary);
    }



    public Boolean setDate(XfptProcSummary xfptProcSummary){
        //根据班子会议id判断是否存在数据
        Long count = lambdaQuery().eq(XfptProcSummary::getTeamId, xfptProcSummary.getTeamId()).count();
        if(count==0){
            //新增
            boolean save = save(xfptProcSummary);
            return save;
        }else {
            //修改
            boolean update = lambdaUpdate().eq(XfptProcSummary::getTeamId, xfptProcSummary.getTeamId()).update(xfptProcSummary);
            return update;
        }

    }

}
