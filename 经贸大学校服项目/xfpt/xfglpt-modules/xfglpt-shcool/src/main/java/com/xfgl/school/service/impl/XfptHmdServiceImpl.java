package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptHmdMapper;
import com.xfgl.school.domain.XfptHmd;
import com.xfgl.school.service.IXfptHmdService;

/**
 * 黑名单Service业务层处理
 *
 * @author yinet
 * @date 2025-10-23
 */
@Service
public class XfptHmdServiceImpl extends ServiceImpl<XfptHmdMapper, XfptHmd> implements IXfptHmdService {

    @Autowired
    private XfptHmdMapper xfptHmdMapper;

    /**
     * 查询黑名单列表
     *
     * @param xfptHmd 黑名单
     * @return 黑名单
     */
    @Override
    public List<XfptHmd> selectXfptHmdList(XfptHmd xfptHmd)
    {
        return xfptHmdMapper.selectXfptHmdList(xfptHmd);
    }


}
