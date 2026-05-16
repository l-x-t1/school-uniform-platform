package com.xfgl.school.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;

/**
 * 学校端零售商品Mapper接口
 *
 * @author yinet
 */
public interface XfptRetailGoodsMapper {
    /**
     * 查询当前学校可操作的零售商品列表
     *
     * @param query 查询条件
     * @return 零售商品集合
     */
    List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query);

    /**
     * 查询当前学校可操作的零售商品详情
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @return 零售商品详情
     */
    XfptRetailGoodsVo selectXfptRetailGoodsById(@Param("id") Long id, @Param("schoolId") Long schoolId);

    /**
     * 更新当前学校零售商品上下架状态
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @param status 上下架状态
     * @param updateBy 操作人
     * @return 影响行数
     */
    int updateXfptRetailGoodsStatus(@Param("id") Long id, @Param("schoolId") Long schoolId,
            @Param("status") String status, @Param("updateBy") String updateBy);
}
