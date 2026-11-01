package com.xfgl.school.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.vo.XfptTonggouGoodsVo;

/**
 * 学校端统购上下架 Mapper
 *
 * @author Codex
 */
public interface XfptTonggouGoodsMapper {
    List<XfptTonggouGoodsVo> selectXfptTonggouGoodsList(XfptTonggouGoodsVo query);

    XfptTonggouGoodsVo selectXfptTonggouGoodsById(@Param("id") Long id,
            @Param("schoolId") Long schoolId,
            @Param("supplierId") Long supplierId);

    int updateXfptTonggouGoodsStatus(@Param("id") Long id,
            @Param("schoolId") Long schoolId,
            @Param("supplierId") Long supplierId,
            @Param("status") String status,
            @Param("updateBy") String updateBy);
}
