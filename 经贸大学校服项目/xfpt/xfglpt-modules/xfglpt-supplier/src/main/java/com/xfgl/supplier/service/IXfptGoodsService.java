package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptGoods;

/**
 * 商品信息Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptGoodsService extends IService<XfptGoods> {

    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息集合
     */
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods);

}
