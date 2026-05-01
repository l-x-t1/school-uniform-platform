package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptGroupNoticeItemMapper;
import com.xfgl.supplier.domain.XfptGroupNoticeItem;
import com.xfgl.supplier.service.IXfptGroupNoticeItemService;

/**
 * 统购公告商品明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptGroupNoticeItemServiceImpl extends ServiceImpl<XfptGroupNoticeItemMapper, XfptGroupNoticeItem> implements IXfptGroupNoticeItemService {

    @Autowired
    private XfptGroupNoticeItemMapper xfptGroupNoticeItemMapper;

    /**
     * 查询统购公告商品明细列表
     *
     * @param xfptGroupNoticeItem 统购公告商品明细
     * @return 统购公告商品明细
     */
    @Override
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem)
    {
        return xfptGroupNoticeItemMapper.selectXfptGroupNoticeItemList(xfptGroupNoticeItem);
    }


}
