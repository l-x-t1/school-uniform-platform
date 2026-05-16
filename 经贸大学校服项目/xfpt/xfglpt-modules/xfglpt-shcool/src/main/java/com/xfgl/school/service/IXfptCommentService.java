package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptComment;
import com.xfgl.school.domain.XfptCommentReply;

/**
 * 商品评价Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptCommentService extends IService<XfptComment> {

    /**
     * 查询商品评价列表
     *
     * @param xfptComment 商品评价
     * @return 商品评价集合
     */
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment);

        /**
         * 查询评价管理列表
         *
         * @param xfptComment 商品评价
         * @return 评价管理集合
         */
        public List<XfptComment> selectEvaluationCommentList(XfptComment xfptComment);

        /**
         * 查询评价管理详情
         *
         * @param id 商品评价主键
         * @param scopeSchoolId 学校数据范围
         * @return 评价管理详情
         */
        public XfptComment selectEvaluationCommentById(Long id, Long scopeSchoolId);

        /**
         * 回复评价
         *
         * @param id 商品评价主键
         * @param xfptCommentReply 回复内容
         * @param scopeSchoolId 学校数据范围
         * @return 结果
         */
        public int replyEvaluationComment(Long id, XfptCommentReply xfptCommentReply, Long scopeSchoolId);

        /**
         * 更新评价管理状态
         *
         * @param id 商品评价主键
         * @param status 评价状态，0正常 1隐藏
         * @param scopeSchoolId 学校数据范围
         * @return 结果
         */
        public int updateEvaluationCommentStatus(Long id, String status, Long scopeSchoolId);

        /**
         * 查询商品评价
         *
         * @param id 商品评价主键
         * @return 商品评价
         */
        public XfptComment selectXfptCommentById(Long id);

        /**
         * 新增商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        public int insertXfptComment(XfptComment xfptComment);

        /**
         * 修改商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        public int updateXfptComment(XfptComment xfptComment);

        /**
         * 批量删除商品评价
         *
         * @param ids 需要删除的商品评价主键集合
         * @return 结果
         */
        public int deleteXfptCommentByIds(Long[] ids);

        /**
         * 删除商品评价信息
         *
         * @param id 商品评价主键
         * @return 结果
         */
        public int deleteXfptCommentById(Long id);
}
