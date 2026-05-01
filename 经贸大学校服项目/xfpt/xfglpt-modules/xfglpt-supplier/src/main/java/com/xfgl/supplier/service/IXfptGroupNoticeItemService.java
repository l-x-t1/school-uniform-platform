package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptGroupNoticeItem;

/**
 * 统购公告商品Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptGroupNoticeItemService extends IService<XfptGroupNoticeItem> {

    /**
     * 查询统购公告商品列表
     *
     * @param xfptGroupNoticeItem 统购公告商品
     * @return 统购公告商品集合
     */
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem);

}
