package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptLjhsMapper;
import com.xfgl.school.domain.XfptLjhs;
import com.xfgl.school.service.IXfptLjhsService;

/**
 * 利旧回收Service业务层处理
 *
 * @author yinet
 * @date 2025-09-15
 */
@Service
public class XfptLjhsServiceImpl extends ServiceImpl<XfptLjhsMapper, XfptLjhs> implements IXfptLjhsService {

    @Autowired
    private XfptLjhsMapper xfptLjhsMapper;

    /**
     * 查询利旧回收列表
     *
     * @param xfptLjhs 利旧回收
     * @return 利旧回收
     */
    @Override
    public List<XfptLjhs> selectXfptLjhsList(XfptLjhs xfptLjhs)
    {
        return xfptLjhsMapper.selectXfptLjhsList(xfptLjhs);
    }


}
