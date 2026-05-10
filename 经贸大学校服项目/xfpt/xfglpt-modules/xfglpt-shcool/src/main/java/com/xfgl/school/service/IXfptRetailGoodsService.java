package com.xfgl.school.service;

import java.util.List;

import com.xfgl.school.domain.vo.XfptRetailGoodsVo;

/**
 * 学校端零售商品上下架Service接口
 */
public interface IXfptRetailGoodsService {
    /**
     * 查询学校端零售商品列表
     */
    List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query);

    /**
     * 查询学校端零售商品详情
     */
    XfptRetailGoodsVo selectXfptRetailGoodsById(Long id, Long schoolId);

    /**
     * 更新学校端零售商品上下架状态
     */
    int updateXfptRetailGoodsStatus(Long id, Long schoolId, String status, String updateBy);
}
