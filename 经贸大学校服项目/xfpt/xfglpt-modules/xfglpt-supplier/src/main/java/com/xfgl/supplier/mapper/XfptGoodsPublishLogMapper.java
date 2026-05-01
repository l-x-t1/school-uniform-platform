package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptGoodsPublishLog;

/**
 * 商品上下架日志Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptGoodsPublishLogMapper extends BaseMapper<XfptGoodsPublishLog> {
    /**
     * 查询商品上下架日志列表
     *
     * @param xfptGoodsPublishLog 商品上下架日志
     * @return 商品上下架日志集合
     */
    public List<XfptGoodsPublishLog> selectXfptGoodsPublishLogList(XfptGoodsPublishLog xfptGoodsPublishLog);
}
