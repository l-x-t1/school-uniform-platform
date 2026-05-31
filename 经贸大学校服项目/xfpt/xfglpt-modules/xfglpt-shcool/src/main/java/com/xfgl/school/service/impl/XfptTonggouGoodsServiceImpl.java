package com.xfgl.school.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.domain.vo.XfptTonggouGoodsVo;
import com.xfgl.school.mapper.XfptGoodsSizeMapper;
import com.xfgl.school.mapper.XfptTonggouGoodsMapper;
import com.xfgl.school.service.IXfptTonggouGoodsService;

/**
 * 学校端统购上下架 Service
 *
 * @author Codex
 */
@Service
public class XfptTonggouGoodsServiceImpl implements IXfptTonggouGoodsService {
    @Autowired
    private XfptTonggouGoodsMapper xfptTonggouGoodsMapper;

    @Autowired
    private XfptGoodsSizeMapper xfptGoodsSizeMapper;

    @Override
    public List<XfptTonggouGoodsVo> selectXfptTonggouGoodsList(XfptTonggouGoodsVo query) {
        return xfptTonggouGoodsMapper.selectXfptTonggouGoodsList(query);
    }

    @Override
    public XfptTonggouGoodsVo selectXfptTonggouGoodsById(Long id, Long schoolId, Long supplierId) {
        XfptTonggouGoodsVo tonggouGoods = xfptTonggouGoodsMapper.selectXfptTonggouGoodsById(id, schoolId, supplierId);
        if (tonggouGoods == null) {
            return null;
        }
        XfptGoodsSize query = new XfptGoodsSize();
        query.setGoodsId(tonggouGoods.getGoodsId());
        tonggouGoods.setXfptGoodsSizeList(xfptGoodsSizeMapper.selectXfptGoodsSizeList(query));
        return tonggouGoods;
    }

    @Override
    public int updateXfptTonggouGoodsStatus(Long id, Long schoolId, Long supplierId, String status, String updateBy) {
        if (!"0".equals(status) && !"1".equals(status)) {
            throw new ServiceException("上下架状态只能为0或1");
        }
        return xfptTonggouGoodsMapper.updateXfptTonggouGoodsStatus(id, schoolId, supplierId, status, updateBy);
    }
}
