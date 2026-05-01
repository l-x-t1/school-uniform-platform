package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptAfterSale;

/**
 * 售后申请Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptAfterSaleMapper extends BaseMapper<XfptAfterSale> {
    /**
     * 查询售后申请列表
     *
     * @param xfptAfterSale 售后申请
     * @return 售后申请集合
     */
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale);
}
