package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptGoodsPublishLogMapper;
import com.xfgl.supplier.domain.XfptGoodsPublishLog;
import com.xfgl.supplier.service.IXfptGoodsPublishLogService;

/**
 * 商品上下架记录Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptGoodsPublishLogServiceImpl extends ServiceImpl<XfptGoodsPublishLogMapper, XfptGoodsPublishLog> implements IXfptGoodsPublishLogService {

    @Autowired
    private XfptGoodsPublishLogMapper xfptGoodsPublishLogMapper;

    /**
     * 查询商品上下架记录列表
     *
     * @param xfptGoodsPublishLog 商品上下架记录
     * @return 商品上下架记录
     */
    @Override
    public List<XfptGoodsPublishLog> selectXfptGoodsPublishLogList(XfptGoodsPublishLog xfptGoodsPublishLog)
    {
        return xfptGoodsPublishLogMapper.selectXfptGoodsPublishLogList(xfptGoodsPublishLog);
    }


}
