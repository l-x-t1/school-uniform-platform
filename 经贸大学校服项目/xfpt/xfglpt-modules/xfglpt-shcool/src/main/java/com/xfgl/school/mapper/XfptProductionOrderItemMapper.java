package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptProductionOrderItem;

/**
 * 生产单明细Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptProductionOrderItemMapper extends BaseMapper<XfptProductionOrderItem> {
    /**
     * 查询生产单明细列表
     *
     * @param xfptProductionOrderItem 生产单明细
     * @return 生产单明细集合
     */
    public List<XfptProductionOrderItem> selectXfptProductionOrderItemList(XfptProductionOrderItem xfptProductionOrderItem);
}
