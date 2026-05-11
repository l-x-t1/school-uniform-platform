package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptGroupNoticeItem;

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
         * 查询统购公告
         *
         * @param id 统购公告主键
         * @return 统购公告
         */
        List<XfptGroupNoticeItem> selectXfptGroupNoticeItemById(@Param("id")Long id);

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

        List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(@Param("noticeId") Long noticeId,
                                                                    @Param("supplierId") Long supplierId,
                                                                    @Param("deptId") Long deptId);
}
