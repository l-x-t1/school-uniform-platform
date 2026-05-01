package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptRwggzsMapper;
import com.xfgl.school.domain.XfptRwggzs;
import com.xfgl.school.service.IXfptRwggzsService;

/**
 * 企业入围公告Service业务层处理
 *
 * @author yinet
 * @date 2025-07-22
 */
@Service
public class XfptRwggzsServiceImpl extends ServiceImpl<XfptRwggzsMapper, XfptRwggzs> implements IXfptRwggzsService {

    @Autowired
    private XfptRwggzsMapper xfptRwggzsMapper;

    /**
     * 查询企业入围公告列表
     *
     * @param xfptRwggzs 企业入围公告
     * @return 企业入围公告
     */
    @Override
    public List<XfptRwggzs> selectXfptRwggzsList(XfptRwggzs xfptRwggzs)
    {
        return xfptRwggzsMapper.selectXfptRwggzsList(xfptRwggzs);
    }


}
