package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;

/**
 * 统购公告Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptGroupNoticeService extends IService<XfptGroupNotice> {

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
        public XfptGroupNotice selectXfptGroupNoticeById(Long id);

        /**
         * 供应商查询学校发布的统购公告详情
         *
         * @param id 统购公告主键
         * @return 统购公告
         */
        public XfptGroupNotice selectSupplierGroupNoticeById(Long id);

        /**
         * 新增统购公告
         *
         * @param xfptGroupNotice 统购公告
         * @return 结果
         */
        public int insertXfptGroupNotice(XfptGroupNotice xfptGroupNotice);

        /**
         * 修改统购公告
         *
         * @param xfptGroupNotice 统购公告
         * @return 结果
         */
        public int updateXfptGroupNotice(XfptGroupNotice xfptGroupNotice);

        /**
         * 批量删除统购公告
         *
         * @param ids 需要删除的统购公告主键集合
         * @return 结果
         */
        public int deleteXfptGroupNoticeByIds(Long[] ids);

        /**
         * 删除统购公告信息
         *
         * @param id 统购公告主键
         * @return 结果
         */
        public int deleteXfptGroupNoticeById(Long id);

        public List<GroupNoticeProgressVo> selectGroupNoticeProgressList(GroupNoticeProgressQuery query);

        public List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(Long noticeId, Long supplierId);
}
