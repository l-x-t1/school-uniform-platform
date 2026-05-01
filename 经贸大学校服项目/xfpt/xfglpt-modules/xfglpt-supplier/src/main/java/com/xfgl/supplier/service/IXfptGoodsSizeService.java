package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptGoodsSize;

/**
 * 商品尺码Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptGoodsSizeService extends IService<XfptGoodsSize> {

    /**
     * 查询商品尺码列表
     *
     * @param xfptGoodsSize 商品尺码
     * @return 商品尺码集合
     */
    public List<XfptGoodsSize> selectXfptGoodsSizeList(XfptGoodsSize xfptGoodsSize);

}
