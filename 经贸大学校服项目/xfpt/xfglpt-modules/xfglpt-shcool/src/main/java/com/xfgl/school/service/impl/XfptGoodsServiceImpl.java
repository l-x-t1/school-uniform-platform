package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptGoodsSize;
import com.xfgl.school.mapper.XfptGoodsMapper;
import com.xfgl.school.domain.XfptGoods;
import com.xfgl.school.service.IXfptGoodsService;

/**
 * 商品信息Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGoodsServiceImpl extends ServiceImpl<XfptGoodsMapper, XfptGoods> implements IXfptGoodsService {

    @Autowired
    private XfptGoodsMapper xfptGoodsMapper;

    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息
     */
    @Override
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods)
    {
        return xfptGoodsMapper.selectXfptGoodsList(xfptGoods);
    }

        /**
         * 查询商品信息
         *
         * @param id 商品信息主键
         * @return 商品信息
         */
        @Override
        public XfptGoods selectXfptGoodsById(Long id)
        {
            XfptGoods xfptGoods = xfptGoodsMapper.selectById(id);
            List<XfptGoodsSize> xfptGoodsSizeList = xfptGoodsMapper.selectXfptGoodsSizeById(id);
            xfptGoods.setXfptGoodsSizeList(xfptGoodsSizeList);
            return xfptGoods;
        }

        /**
         * 新增商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptGoods(XfptGoods xfptGoods)
        {
            fillGoodsDefaults(xfptGoods);
            int rows = xfptGoodsMapper.insert(xfptGoods);
            insertXfptGoodsSize(xfptGoods);
            return rows;
        }

        /**
         * 修改商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptGoods(XfptGoods xfptGoods)
        {
            fillGoodsDefaults(xfptGoods);
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(xfptGoods.getId());
            insertXfptGoodsSize(xfptGoods);
            return xfptGoodsMapper.updateById(xfptGoods);
        }

        /**
         * 批量删除商品信息
         *
         * @param ids 需要删除的商品信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGoodsByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(id);
        }
            return xfptGoodsMapper.deleteBatchIds(idList);
        }

        /**
         * 删除商品信息信息
         *
         * @param id 商品信息主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGoodsById(Long id)
        {
                xfptGoodsMapper.deleteXfptGoodsSizeByGoodsId(id);
            return xfptGoodsMapper.deleteById(id);
        }

        /**
         * 新增商品尺码信息
         *
         * @param xfptGoods 商品信息对象
         */
        private void insertXfptGoodsSize(XfptGoods xfptGoods)
        {
            List<XfptGoodsSize> xfptGoodsSizeList = xfptGoods.getXfptGoodsSizeList();
            Long id = xfptGoods.getId();
            if (StringUtils.isNotNull(xfptGoodsSizeList)) {
                for (XfptGoodsSize xfptGoodsSize : xfptGoodsSizeList) {
                    xfptGoodsSize.setGoodsId(id);
                    fillGoodsSizeDefaults(xfptGoodsSize);
                }
                    xfptGoodsMapper.batchInsertXfptGoodsSize(xfptGoodsSizeList);
            }
        }

        private void fillGoodsDefaults(XfptGoods xfptGoods)
        {
            if (xfptGoods.getSaleType() == null || "".equals(xfptGoods.getSaleType())) {
                xfptGoods.setSaleType("3");
            }
            if (xfptGoods.getStatus() == null || "".equals(xfptGoods.getStatus())) {
                xfptGoods.setStatus("1");
            }
            if (xfptGoods.getPublishStatus() == null || "".equals(xfptGoods.getPublishStatus())) {
                xfptGoods.setPublishStatus("0");
            }
            if (xfptGoods.getDelFlag() == null || "".equals(xfptGoods.getDelFlag())) {
                xfptGoods.setDelFlag("0");
            }
        }

        private void fillGoodsSizeDefaults(XfptGoodsSize xfptGoodsSize)
        {
            if (xfptGoodsSize.getStatus() == null || "".equals(xfptGoodsSize.getStatus())) {
                xfptGoodsSize.setStatus("1");
            }
            if (xfptGoodsSize.getDelFlag() == null || "".equals(xfptGoodsSize.getDelFlag())) {
                xfptGoodsSize.setDelFlag("0");
            }
        }

}
