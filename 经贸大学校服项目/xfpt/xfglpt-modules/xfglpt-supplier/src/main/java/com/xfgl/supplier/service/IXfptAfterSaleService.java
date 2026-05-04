package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptAfterSale;

/**
 * 售后申请Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptAfterSaleService extends IService<XfptAfterSale> {

    /**
     * 查询售后申请列表
     *
     * @param xfptAfterSale 售后申请
     * @return 售后申请集合
     */
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale);

}
