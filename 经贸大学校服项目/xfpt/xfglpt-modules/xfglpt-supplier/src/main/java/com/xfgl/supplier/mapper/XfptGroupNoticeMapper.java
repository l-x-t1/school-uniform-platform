package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptGroupNotice;

/**
 * 统购公告Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptGroupNoticeMapper extends BaseMapper<XfptGroupNotice> {
    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告集合
     */
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice);
}
