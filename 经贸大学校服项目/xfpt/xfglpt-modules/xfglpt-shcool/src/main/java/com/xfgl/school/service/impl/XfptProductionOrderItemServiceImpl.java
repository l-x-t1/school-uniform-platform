package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.mapper.XfptProductionOrderItemMapper;
import com.xfgl.school.domain.XfptProductionOrderItem;
import com.xfgl.school.service.IXfptProductionOrderItemService;

/**
 * 生产单明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptProductionOrderItemServiceImpl extends ServiceImpl<XfptProductionOrderItemMapper, XfptProductionOrderItem> implements IXfptProductionOrderItemService {

    @Autowired
    private XfptProductionOrderItemMapper xfptProductionOrderItemMapper;

    @Override
    public boolean save(XfptProductionOrderItem xfptProductionOrderItem)
    {
        if (xfptProductionOrderItem.getGoodsId() == null) {
            throw new ServiceException("生产单明细商品不能为空");
        }
        initDefaultValue(xfptProductionOrderItem);
        return super.save(xfptProductionOrderItem);
    }

    /**
     * 查询生产单明细列表
     *
     * @param xfptProductionOrderItem 生产单明细
     * @return 生产单明细
     */
    @Override
    public List<XfptProductionOrderItem> selectXfptProductionOrderItemList(XfptProductionOrderItem xfptProductionOrderItem)
    {
        return xfptProductionOrderItemMapper.selectXfptProductionOrderItemList(xfptProductionOrderItem);
    }

    private void initDefaultValue(XfptProductionOrderItem xfptProductionOrderItem)
    {
        if (StringUtils.isEmpty(xfptProductionOrderItem.getStatus())) {
            xfptProductionOrderItem.setStatus("0");
        }
        if (xfptProductionOrderItem.getFinishNum() == null) {
            xfptProductionOrderItem.setFinishNum(0);
        }
        if (StringUtils.isEmpty(xfptProductionOrderItem.getDelFlag())) {
            xfptProductionOrderItem.setDelFlag("0");
        }
    }
}
