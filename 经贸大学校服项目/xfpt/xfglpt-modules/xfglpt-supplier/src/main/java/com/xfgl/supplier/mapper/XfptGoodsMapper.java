package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptGoods;

/**
 * 商品信息Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptGoodsMapper extends BaseMapper<XfptGoods> {
    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息集合
     */
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods);
}
