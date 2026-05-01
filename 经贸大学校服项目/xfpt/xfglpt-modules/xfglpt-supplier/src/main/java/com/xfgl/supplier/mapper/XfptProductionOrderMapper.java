package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptProductionOrder;

/**
 * 生产单主Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptProductionOrderMapper extends BaseMapper<XfptProductionOrder> {
    /**
     * 查询生产单主列表
     *
     * @param xfptProductionOrder 生产单主
     * @return 生产单主集合
     */
    public List<XfptProductionOrder> selectXfptProductionOrderList(XfptProductionOrder xfptProductionOrder);
}
