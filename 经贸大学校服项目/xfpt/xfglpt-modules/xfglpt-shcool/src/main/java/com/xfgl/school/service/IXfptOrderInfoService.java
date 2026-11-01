package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptOrderInfo;

/**
 * 订单信息Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptOrderInfoService extends IService<XfptOrderInfo> {

    /**
     * 查询订单信息列表
     *
     * @param xfptOrderInfo 订单信息
     * @return 订单信息集合
     */
    public List<XfptOrderInfo> selectXfptOrderInfoList(XfptOrderInfo xfptOrderInfo);

        /**
         * 查询订单信息
         *
         * @param id 订单信息主键
         * @return 订单信息
         */
        public XfptOrderInfo selectXfptOrderInfoById(Long id);

        /**
         * 新增订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        public int insertXfptOrderInfo(XfptOrderInfo xfptOrderInfo);

        /**
         * 修改订单信息
         *
         * @param xfptOrderInfo 订单信息
         * @return 结果
         */
        public int updateXfptOrderInfo(XfptOrderInfo xfptOrderInfo);

        /**
         * 批量删除订单信息
         *
         * @param ids 需要删除的订单信息主键集合
         * @return 结果
         */
        public int deleteXfptOrderInfoByIds(Long[] ids);

        /**
         * 删除订单信息信息
         *
         * @param id 订单信息主键
         * @return 结果
         */
        public int deleteXfptOrderInfoById(Long id);

        /**
         * 零售订单发货
         *
         * @param id 订单信息主键
         * @return 结果
         */
        public int shipRetailOrder(Long id);
}
