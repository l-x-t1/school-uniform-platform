package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptAfterSale;

/**
 * 售后主Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptAfterSaleMapper extends BaseMapper<XfptAfterSale> {
    /**
     * 查询售后主列表
     *
     * @param xfptAfterSale 售后主
     * @return 售后主集合
     */
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale);
}
