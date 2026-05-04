package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptGroupNoticeItem;

/**
 * 统购公告商品Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptGroupNoticeItemMapper extends BaseMapper<XfptGroupNoticeItem> {
    /**
     * 查询统购公告商品列表
     *
     * @param xfptGroupNoticeItem 统购公告商品
     * @return 统购公告商品集合
     */
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem);
}
