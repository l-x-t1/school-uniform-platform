package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptAfterSale;

/**
 * 售后主Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptAfterSaleService extends IService<XfptAfterSale> {

    /**
     * 查询售后主列表
     *
     * @param xfptAfterSale 售后主
     * @return 售后主集合
     */
    public List<XfptAfterSale> selectXfptAfterSaleList(XfptAfterSale xfptAfterSale);

}
