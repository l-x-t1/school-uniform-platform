package com.xfgl.school.service;

import java.util.List;
import com.xfgl.school.domain.vo.XfptTonggouGoodsVo;

/**
 * 学校端统购上下架 Service 接口
 *
 * @author Codex
 */
public interface IXfptTonggouGoodsService {
    List<XfptTonggouGoodsVo> selectXfptTonggouGoodsList(XfptTonggouGoodsVo query);

    XfptTonggouGoodsVo selectXfptTonggouGoodsById(Long id, Long schoolId, Long supplierId);

    int updateXfptTonggouGoodsStatus(Long id, Long schoolId, Long supplierId, String status, String updateBy);
}
