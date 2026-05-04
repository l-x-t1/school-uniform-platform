package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptCartItem;

/**
 * 购物车明细Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptCartItemService extends IService<XfptCartItem> {

    /**
     * 查询购物车明细列表
     *
     * @param xfptCartItem 购物车明细
     * @return 购物车明细集合
     */
    public List<XfptCartItem> selectXfptCartItemList(XfptCartItem xfptCartItem);

}
