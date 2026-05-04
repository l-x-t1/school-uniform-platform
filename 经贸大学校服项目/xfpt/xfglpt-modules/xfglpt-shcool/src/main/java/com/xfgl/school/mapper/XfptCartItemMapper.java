package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptCartItem;

/**
 * 购物车明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptCartItemMapper extends BaseMapper<XfptCartItem> {
    /**
     * 查询购物车明细列表
     *
     * @param xfptCartItem 购物车明细
     * @return 购物车明细集合
     */
    public List<XfptCartItem> selectXfptCartItemList(XfptCartItem xfptCartItem);
}
