package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptAfterSale;

/**
 * 售后单Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptAfterSaleMapper extends BaseMapper<XfptAfterSale> {
    /**
     * 查询售后单列表
     *
     * @param xfptAfterSale 售后单
     * @return 售后单集合
     */
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale);
}
