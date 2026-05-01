package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptGoodsSize;

/**
 * 商品尺码Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptGoodsSizeMapper extends BaseMapper<XfptGoodsSize> {
    /**
     * 查询商品尺码列表
     *
     * @param xfptGoodsSize 商品尺码
     * @return 商品尺码集合
     */
    public List<XfptGoodsSize> selectXfptGoodsSizeList(XfptGoodsSize xfptGoodsSize);
}
