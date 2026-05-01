package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptGroupNoticeMapper;
import com.xfgl.supplier.domain.XfptGroupNotice;
import com.xfgl.supplier.service.IXfptGroupNoticeService;

/**
 * 统购公告Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptGroupNoticeServiceImpl extends ServiceImpl<XfptGroupNoticeMapper, XfptGroupNotice> implements IXfptGroupNoticeService {

    @Autowired
    private XfptGroupNoticeMapper xfptGroupNoticeMapper;

    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告
     */
    @Override
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        return xfptGroupNoticeMapper.selectXfptGroupNoticeList(xfptGroupNotice);
    }


}
