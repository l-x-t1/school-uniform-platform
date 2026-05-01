package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptPurchaseProcessMapper;
import com.xfgl.school.domain.XfptPurchaseProcess;
import com.xfgl.school.service.IXfptPurchaseProcessService;

/**
 * 采购流程Service业务层处理
 *
 * @author yinet
 * @date 2025-08-06
 */
@Service
public class XfptPurchaseProcessServiceImpl extends ServiceImpl<XfptPurchaseProcessMapper, XfptPurchaseProcess> implements IXfptPurchaseProcessService {

    @Autowired
    private XfptPurchaseProcessMapper xfptPurchaseProcessMapper;

    /**
     * 查询采购流程列表
     *
     * @param xfptPurchaseProcess 采购流程
     * @return 采购流程
     */
    @Override
    public List<XfptPurchaseProcess> selectXfptPurchaseProcessList(XfptPurchaseProcess xfptPurchaseProcess)
    {
        return xfptPurchaseProcessMapper.selectXfptPurchaseProcessList(xfptPurchaseProcess);
    }


}
