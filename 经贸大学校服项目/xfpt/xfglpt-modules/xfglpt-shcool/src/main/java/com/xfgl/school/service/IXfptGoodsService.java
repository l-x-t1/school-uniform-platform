package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptGoods;

/**
 * 商品信息Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptGoodsService extends IService<XfptGoods> {

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
        public XfptGoods selectXfptGoodsById(Long id);

        /**
         * 新增商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        public int insertXfptGoods(XfptGoods xfptGoods);

        /**
         * 导入商品信息
         *
         * @param goodsList 商品信息列表
         * @param updateSupport 是否更新已存在数据
         * @param operName 操作人
         * @return 导入结果
         */
        public String importGoods(List<XfptGoods> goodsList, boolean updateSupport, String operName);

        /**
         * 修改商品信息
         *
         * @param xfptGoods 商品信息
         * @return 结果
         */
        public int updateXfptGoods(XfptGoods xfptGoods);

        /**
         * 批量删除商品信息
         *
         * @param ids 需要删除的商品信息主键集合
         * @return 结果
         */
        public int deleteXfptGoodsByIds(Long[] ids);

        /**
         * 删除商品信息信息
         *
         * @param id 商品信息主键
         * @return 结果
         */
        public int deleteXfptGoodsById(Long id);
}
