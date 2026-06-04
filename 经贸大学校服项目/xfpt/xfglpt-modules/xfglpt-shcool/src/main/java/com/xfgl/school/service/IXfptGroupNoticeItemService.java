package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptGroupNoticeItem;

/**
 * 统购公告商品Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptGroupNoticeItemService extends IService<XfptGroupNoticeItem> {

    /**
     * 查询统购公告商品列表
     *
     * @param xfptGroupNoticeItem 统购公告商品
     * @return 统购公告商品集合
     */
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem);

    /**
     * 查询统购公告商品详情
     *
     * @param id 主键
     * @return 统购公告商品
     */
    public XfptGroupNoticeItem selectXfptGroupNoticeItemById(Long id);

}
