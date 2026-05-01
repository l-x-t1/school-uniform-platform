package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptGroupNotice;

/**
 * 统购公告Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptGroupNoticeService extends IService<XfptGroupNotice> {

    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告集合
     */
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice);

}
