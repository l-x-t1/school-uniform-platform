package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.xfgl.school.mapper.XfptGroupNoticeItemMapper;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.service.IXfptGroupNoticeItemService;

/**
 * 统购公告商品Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGroupNoticeItemServiceImpl extends ServiceImpl<XfptGroupNoticeItemMapper, XfptGroupNoticeItem> implements IXfptGroupNoticeItemService {

    @Autowired
    private XfptGroupNoticeItemMapper xfptGroupNoticeItemMapper;

    /**
     * 查询统购公告商品列表
     *
     * @param xfptGroupNoticeItem 统购公告商品
     * @return 统购公告商品
     */
    @Override
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem)
    {
        return xfptGroupNoticeItemMapper.selectXfptGroupNoticeItemList(xfptGroupNoticeItem);
    }

    @Override
    public XfptGroupNoticeItem selectXfptGroupNoticeItemById(Long id)
    {
        return xfptGroupNoticeItemMapper.selectXfptGroupNoticeItemById(id);
    }

}
