package com.xfgl.school.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.vo.XfptRetailGoodsVo;
import com.xfgl.school.mapper.XfptGoodsSizeMapper;
import com.xfgl.school.mapper.XfptRetailGoodsMapper;
import com.xfgl.school.service.IXfptRetailGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学校端零售商品上下架Service业务层处理
 */
@Service
public class XfptRetailGoodsServiceImpl implements IXfptRetailGoodsService {
    @Autowired
    private XfptRetailGoodsMapper xfptRetailGoodsMapper;

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    @Override
    public List<XfptRetailGoodsVo> selectXfptRetailGoodsList(XfptRetailGoodsVo query) {
        return xfptRetailGoodsMapper.selectXfptRetailGoodsList(query);
    }

    @Override
    public XfptRetailGoodsVo selectXfptRetailGoodsById(Long id, Long schoolId) {
        XfptRetailGoodsVo retailGoodsVo = xfptRetailGoodsMapper.selectXfptRetailGoodsById(id, schoolId);
        if (StringUtils.isNull(retailGoodsVo)) {
            throw new ServiceException("商品不存在或无权限查看");
        }
        List<XfptGoodsSize> goodsSizeList = xfptGoodsSizeMapper.selectList(new LambdaQueryWrapper<XfptGoodsSize>()
                .eq(XfptGoodsSize::getGoodsId, retailGoodsVo.getGoodsId())
                .eq(XfptGoodsSize::getDelFlag, "0")
                .orderByAsc(XfptGoodsSize::getSortNum)
                .orderByAsc(XfptGoodsSize::getId));
        retailGoodsVo.setXfptGoodsSizeList(goodsSizeList);
        return retailGoodsVo;
    }

    @Override
    public int updateXfptRetailGoodsStatus(Long id, Long schoolId, String status, String updateBy) {
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new ServiceException("上下架状态不合法");
        }
        int rows = xfptRetailGoodsMapper.updateXfptRetailGoodsStatus(id, schoolId, status, updateBy);
        if (rows <= 0) {
            throw new ServiceException("商品不存在或无权限操作");
        }
        return rows;
    }
}
