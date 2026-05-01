package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptGroupNoticeItem;

/**
 * 统购公告商品明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptGroupNoticeItemMapper extends BaseMapper<XfptGroupNoticeItem> {
    /**
     * 查询统购公告商品明细列表
     *
     * @param xfptGroupNoticeItem 统购公告商品明细
     * @return 统购公告商品明细集合
     */
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem);
}
