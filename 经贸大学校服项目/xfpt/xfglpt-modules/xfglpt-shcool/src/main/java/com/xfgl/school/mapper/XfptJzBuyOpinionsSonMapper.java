package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptJzBuyOpinionsSon;

/**
 * 家长购买意见征集活动子Mapper接口
 *
 * @author wxm
 * @date 2025-04-26
 */
public interface XfptJzBuyOpinionsSonMapper extends BaseMapper<XfptJzBuyOpinionsSon> {
    /**
     * 查询家长购买意见征集活动子列表
     *
     * @param xfptJzBuyOpinionsSon 家长购买意见征集活动子
     * @return 家长购买意见征集活动子集合
     */
    public List<XfptJzBuyOpinionsSon> selectXfptJzBuyOpinionsSonList(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon);

    XfptJzBuyOpinionsSon selectXfptJzBuyOpinionsSonById(Long id);

    int insertXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon);

    int updateXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon);

    int deleteXfptJzBuyOpinionsSonById(Long id);

    int deleteXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon);
}
