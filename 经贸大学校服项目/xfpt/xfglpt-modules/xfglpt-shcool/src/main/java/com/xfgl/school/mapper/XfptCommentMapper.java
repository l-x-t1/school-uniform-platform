package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptComment;
import org.apache.ibatis.annotations.Param;
import com.xfgl.school.domain.XfptCommentReply;

/**
 * 商品评价Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptCommentMapper extends BaseMapper<XfptComment> {
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
    public XfptComment selectEvaluationCommentById(@Param("id") Long id, @Param("scopeSchoolId") Long scopeSchoolId);

        /**
         * 查询商品评价
         *
         * @param id 商品评价主键
         * @return 商品评价
         */
        List<XfptCommentReply> selectXfptCommentReplyById(@Param("id")Long id);

        /**
         * 删除商品评价信息
         *
         * @param id 商品评价主键
         * @return 结果
         */
        int deleteXfptCommentReplyByCommentId(@Param("id")Long id);

        /**
         * 批量新增商品评论回复
         *
         * @param xfptCommentReplyList 商品评论回复列表
         * @return 结果
         */
        int batchInsertXfptCommentReply(List<XfptCommentReply> xfptCommentReplyList);
}
