package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptGoodsPublishLog;

/**
 * 商品上下架日志Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptGoodsPublishLogService extends IService<XfptGoodsPublishLog> {

    /**
     * 查询商品上下架日志列表
     *
     * @param xfptGoodsPublishLog 商品上下架日志
     * @return 商品上下架日志集合
     */
    public List<XfptGoodsPublishLog> selectXfptGoodsPublishLogList(XfptGoodsPublishLog xfptGoodsPublishLog);

}
