package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptFinanceFlow;

/**
 * 财务流水Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptFinanceFlowMapper extends BaseMapper<XfptFinanceFlow> {
    /**
     * 查询财务流水列表
     *
     * @param xfptFinanceFlow 财务流水
     * @return 财务流水集合
     */
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow);
}
