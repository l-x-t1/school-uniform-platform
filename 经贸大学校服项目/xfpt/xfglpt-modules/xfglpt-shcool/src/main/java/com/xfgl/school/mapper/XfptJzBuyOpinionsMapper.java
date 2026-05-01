package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptJzBuyOpinions;

/**
 * 家长购买意见征集Mapper接口
 *
 * @author wxm
 * @date 2025-04-25
 */
public interface XfptJzBuyOpinionsMapper extends BaseMapper<XfptJzBuyOpinions> {
    /**
     * 查询家长购买意见征集列表
     *
     * @param xfptJzBuyOpinions 家长购买意见征集
     * @return 家长购买意见征集集合
     */
    public List<XfptJzBuyOpinions> selectXfptJzBuyOpinionsList(XfptJzBuyOpinions xfptJzBuyOpinions);

    public XfptJzBuyOpinions selectXfptJzBuyOpinionsById(Long id);

    int insertXfptJzBuyOpinions(XfptJzBuyOpinions xfptJzBuyOpinions);

    int updateXfptJzBuyOpinions(XfptJzBuyOpinions xfptJzBuyOpinions);

    int deleteXfptJzBuyOpinionsById(Long id);
}
