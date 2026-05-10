package com.xfgl.school.mapper;

import java.util.List;

import com.xfgl.school.domain.vo.XfptRetailGoodsVo;
import org.apache.ibatis.annotations.Param;

/**
 * 学校端零售商品上下架Mapper接口
 */
public interface XfptRetailGoodsMapper {
    /**
     * 查询学校端零售商品列表
     */
    List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query);

    /**
     * 查询学校端零售商品详情
     */
    XfptRetailGoodsVo selectXfptRetailGoodsById(@Param("id") Long id, @Param("schoolId") Long schoolId);

    /**
     * 更新学校端零售商品上下架状态
     */
    int updateXfptRetailGoodsStatus(@Param("id") Long id,
                                     @Param("schoolId") Long schoolId,
                                     @Param("status") String status,
                                     @Param("updateBy") String updateBy);
}
