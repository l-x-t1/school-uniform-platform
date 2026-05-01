package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptJzBuyOpinions;

/**
 * 家长购买意见征集Service接口
 *
 * @author wxm
 * @date 2025-04-25
 */
public interface IXfptJzBuyOpinionsService extends IService<XfptJzBuyOpinions> {

    /**
     * 查询家长购买意见征集列表
     *
     * @param xfptJzBuyOpinions 家长购买意见征集
     * @return 家长购买意见征集集合
     */
    public List<XfptJzBuyOpinions> selectXfptJzBuyOpinionsList(XfptJzBuyOpinions xfptJzBuyOpinions);

    int updateXfptJzBuyOpinions(XfptJzBuyOpinions xfptJzBuyOpinions);

}
