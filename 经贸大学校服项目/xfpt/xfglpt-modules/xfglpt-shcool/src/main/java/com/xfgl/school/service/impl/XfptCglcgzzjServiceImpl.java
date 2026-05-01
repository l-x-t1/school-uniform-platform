package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptCglcgzzjMapper;
import com.xfgl.school.domain.XfptCglcgzzj;
import com.xfgl.school.service.IXfptCglcgzzjService;

/**
 * 学校校服采购工作总结Service业务层处理
 *
 * @author yinet
 * @date 2025-07-23
 */
@Service
public class XfptCglcgzzjServiceImpl extends ServiceImpl<XfptCglcgzzjMapper, XfptCglcgzzj> implements IXfptCglcgzzjService {

    @Autowired
    private XfptCglcgzzjMapper xfptCglcgzzjMapper;

    /**
     * 查询学校校服采购工作总结列表
     *
     * @param xfptCglcgzzj 学校校服采购工作总结
     * @return 学校校服采购工作总结
     */
    @Override
    public List<XfptCglcgzzj> selectXfptCglcgzzjList(XfptCglcgzzj xfptCglcgzzj)
    {
        return xfptCglcgzzjMapper.selectXfptCglcgzzjList(xfptCglcgzzj);
    }


}
