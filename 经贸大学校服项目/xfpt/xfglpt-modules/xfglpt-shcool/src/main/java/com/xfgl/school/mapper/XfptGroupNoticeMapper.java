package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptOrderInfo;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptGroupNoticeSupplier;

/**
 * 统购公告Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptGroupNoticeMapper extends BaseMapper<XfptGroupNotice> {
    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告集合
     */
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice);

    /**
     * 供应商查询学校发布的统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告集合
     */
    public List<XfptGroupNotice> selectSupplierGroupNoticeList(XfptGroupNotice xfptGroupNotice);
        /**
         * 查询统购公告
         *
         * @param id 统购公告主键
         * @return 统购公告
         */
        XfptGroupNotice selectSupplierGroupNoticeById(@Param("id") Long id, @Param("supplierId") Long supplierId);
        List<XfptGroupNoticeItem> selectXfptGroupNoticeItemById(@Param("id")Long id);

        List<Long> selectSupplierIdsByNoticeId(@Param("noticeId") Long noticeId);

        int deleteXfptGroupNoticeSupplierByNoticeId(@Param("noticeId") Long noticeId);

        int deleteXfptGroupNoticeSupplierByNoticeIds(@Param("noticeIds") List<Long> noticeIds);

        int batchInsertXfptGroupNoticeSupplier(List<XfptGroupNoticeSupplier> supplierList);

        /**
         * 删除统购公告信息
         *
         * @param id 统购公告主键
         * @return 结果
         */
        int deleteXfptGroupNoticeItemByNoticeId(@Param("id")Long id);

        /**
         * 批量新增统购公告商品
         *
         * @param xfptGroupNoticeItemList 统购公告商品列表
         * @return 结果
         */
        int batchInsertXfptGroupNoticeItem(List<XfptGroupNoticeItem> xfptGroupNoticeItemList);

        List<GroupNoticeProgressVo> selectGroupNoticeProgressList(GroupNoticeProgressQuery query);

        List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(@Param("orderId") Long orderId,
                                                                    @Param("supplierId") Long supplierId,
                                                                    @Param("schoolId") Long schoolId,
                                                                    @Param("deptId") Long deptId);

        XfptOrderInfo selectProgressOrderById(@Param("orderId") Long orderId);

        int updateProgressStatus(@Param("orderId") Long orderId,
                                 @Param("progressStatus") String progressStatus);

}
