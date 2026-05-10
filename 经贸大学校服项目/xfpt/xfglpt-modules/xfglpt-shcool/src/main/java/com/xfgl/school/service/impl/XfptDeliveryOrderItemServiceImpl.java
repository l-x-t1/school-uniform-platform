package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.mapper.XfptDeliveryOrderItemMapper;
import com.xfgl.school.domain.XfptDeliveryOrderItem;
import com.xfgl.school.service.IXfptDeliveryOrderItemService;

/**
 * 发货单明细Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptDeliveryOrderItemServiceImpl extends ServiceImpl<XfptDeliveryOrderItemMapper, XfptDeliveryOrderItem> implements IXfptDeliveryOrderItemService {

    @Autowired
    private XfptDeliveryOrderItemMapper xfptDeliveryOrderItemMapper;

    /**
     * 查询发货单明细列表
     *
     * @param xfptDeliveryOrderItem 发货单明细
     * @return 发货单明细
     */
    @Override
    public List<XfptDeliveryOrderItem> selectXfptDeliveryOrderItemList(XfptDeliveryOrderItem xfptDeliveryOrderItem)
    {
        return xfptDeliveryOrderItemMapper.selectXfptDeliveryOrderItemList(xfptDeliveryOrderItem);
    }

    @Override
    public boolean save(XfptDeliveryOrderItem xfptDeliveryOrderItem)
    {
        if (StringUtils.isEmpty(xfptDeliveryOrderItem.getStatus())) {
            xfptDeliveryOrderItem.setStatus("0");
        }
        if (xfptDeliveryOrderItem.getSignNum() == null) {
            xfptDeliveryOrderItem.setSignNum(0);
        }
        if (StringUtils.isEmpty(xfptDeliveryOrderItem.getDelFlag())) {
            xfptDeliveryOrderItem.setDelFlag("0");
        }
        return super.save(xfptDeliveryOrderItem);
    }


}
