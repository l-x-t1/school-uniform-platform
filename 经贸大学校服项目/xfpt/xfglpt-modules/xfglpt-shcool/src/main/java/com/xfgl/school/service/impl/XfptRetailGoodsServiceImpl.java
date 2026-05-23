package com.xfgl.school.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;
import com.xfgl.school.mapper.XfptGoodsSizeMapper;
import com.xfgl.school.mapper.XfptRetailGoodsMapper;
import com.xfgl.school.service.IXfptRetailGoodsService;

/**
 * 学校端零售商品Service业务层处理
 *
 * @author yinet
 */
@Service
public class XfptRetailGoodsServiceImpl implements IXfptRetailGoodsService {
    @Autowired
    private XfptRetailGoodsMapper xfptRetailGoodsMapper;

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    /**
     * 查询学校端零售商品列表
     *
     * @param query 查询条件
     * @return 零售商品集合
     */
    @Override
    public List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query) {
        return xfptRetailGoodsMapper.selectXfptRetailGoodsList(query);
    }

    /**
     * 查询学校端零售商品详情
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @param supplierId 供应商ID
     * @return 零售商品详情
     */
    @Override
    public XfptRetailGoodsVo selectXfptRetailGoodsById(Long id, Long schoolId, Long supplierId) {
        XfptRetailGoodsVo retailGoods = xfptRetailGoodsMapper.selectXfptRetailGoodsById(id, schoolId, supplierId);
        if (retailGoods == null) {
            return null;
        }
        XfptGoodsSize query = new XfptGoodsSize();
        query.setGoodsId(retailGoods.getGoodsId());
        retailGoods.setXfptGoodsSizeList(xfptGoodsSizeMapper.selectXfptGoodsSizeList(query));
        return retailGoods;
    }

    /**
     * 更新学校端零售商品上下架状态
     *
     * @param id 学校商品关联主键
     * @param schoolId 学校ID
     * @param supplierId 供应商ID
     * @param status 上下架状态
     * @param updateBy 操作人
     * @return 影响行数
     */
    @Override
    public int updateXfptRetailGoodsStatus(Long id, Long schoolId, Long supplierId, String status, String updateBy) {
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new ServiceException("上下架状态只能为0或1");
        }
        return xfptRetailGoodsMapper.updateXfptRetailGoodsStatus(id, schoolId, supplierId, status, updateBy);
    }
}
