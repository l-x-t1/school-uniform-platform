package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptPurchaseProcess;

/**
 * 采购流程Mapper接口
 *
 * @author yinet
 * @date 2025-08-06
 */
public interface XfptPurchaseProcessMapper extends BaseMapper<XfptPurchaseProcess> {
    /**
     * 查询采购流程列表
     *
     * @param xfptPurchaseProcess 采购流程
     * @return 采购流程集合
     */
    public List<XfptPurchaseProcess> selectXfptPurchaseProcessList(XfptPurchaseProcess xfptPurchaseProcess);
}
