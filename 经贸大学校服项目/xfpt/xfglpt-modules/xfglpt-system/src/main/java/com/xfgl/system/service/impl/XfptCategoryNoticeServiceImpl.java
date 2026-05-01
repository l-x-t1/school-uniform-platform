package com.xfgl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.system.mapper.XfptCategoryNoticeMapper;
import com.xfgl.system.domain.XfptCategoryNotice;
import com.xfgl.system.service.IXfptCategoryNoticeService;

/**
 * 栏目分类Service业务层处理
 *
 * @author yinet
 * @date 2025-05-06
 */
@Service
public class XfptCategoryNoticeServiceImpl extends ServiceImpl<XfptCategoryNoticeMapper, XfptCategoryNotice> implements IXfptCategoryNoticeService {

    @Autowired
    private XfptCategoryNoticeMapper xfptCategoryNoticeMapper;

    /**
     * 查询栏目分类列表
     *
     * @param xfptCategoryNotice 栏目分类
     * @return 栏目分类
     */
    @Override
    public List<XfptCategoryNotice> selectXfptCategoryNoticeList(XfptCategoryNotice xfptCategoryNotice)
    {
        return xfptCategoryNoticeMapper.selectXfptCategoryNoticeList(xfptCategoryNotice);
    }

    @Override
    public List<XfptCategoryNotice> selectXfptCategoryNoticeList2(XfptCategoryNotice xfptCategoryNotice)
    {
        return xfptCategoryNoticeMapper.selectXfptCategoryNoticeList2(xfptCategoryNotice);
    }
}
