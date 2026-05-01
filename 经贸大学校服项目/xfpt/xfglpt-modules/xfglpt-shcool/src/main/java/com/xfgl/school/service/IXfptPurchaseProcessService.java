package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptPurchaseProcess;

/**
 * 采购流程Service接口
 *
 * @author yinet
 * @date 2025-08-06
 */
public interface IXfptPurchaseProcessService extends IService<XfptPurchaseProcess> {

    /**
     * 查询采购流程列表
     *
     * @param xfptPurchaseProcess 采购流程
     * @return 采购流程集合
     */
    public List<XfptPurchaseProcess> selectXfptPurchaseProcessList(XfptPurchaseProcess xfptPurchaseProcess);

}
