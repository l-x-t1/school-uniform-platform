package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptGoods;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptGoodsSize;

/**
 * 商品信息Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptGoodsMapper extends BaseMapper<XfptGoods> {
    /**
     * 查询商品信息列表
     *
     * @param xfptGoods 商品信息
     * @return 商品信息集合
     */
    public List<XfptGoods> selectXfptGoodsList(XfptGoods xfptGoods);
        /**
         * 查询商品信息
         *
         * @param id 商品信息主键
         * @return 商品信息
         */
        List<XfptGoodsSize> selectXfptGoodsSizeById(@Param("id")Long id);

        /**
         * 删除商品信息信息
         *
         * @param id 商品信息主键
         * @return 结果
         */
        int deleteXfptGoodsSizeByGoodsId(@Param("id")Long id);

        /**
         * 批量新增商品尺码
         *
         * @param xfptGoodsSizeList 商品尺码列表
         * @return 结果
         */
        int batchInsertXfptGoodsSize(List<XfptGoodsSize> xfptGoodsSizeList);
}
