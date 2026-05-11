package com.xfgl.school.service;

import java.util.List;
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;

/**
 * 学校端零售商品Service接口
 *
 * @author yinet
 */
public interface IXfptRetailGoodsService {
    /**
     * 查询学校端零售商品列表
     *
     * @param query 查询条件
     * @return 零售商品集合
     */
    List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query);

    /**
     * 查询学校端零售商品详情
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @return 零售商品详情
     */
    XfptRetailGoodsVo selectXfptRetailGoodsById(Long id, Long schoolId);

    /**
     * 更新学校端零售商品上下架状态
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @param status 上下架状态
     * @param updateBy 操作人
     * @return 影响行数
     */
    int updateXfptRetailGoodsStatus(Long id, Long schoolId, String status, String updateBy);
}
