package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptCommentReply;

/**
 * 商品评论回复Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptCommentReplyMapper extends BaseMapper<XfptCommentReply> {
    /**
     * 查询商品评论回复列表
     *
     * @param xfptCommentReply 商品评论回复
     * @return 商品评论回复集合
     */
    public List<XfptCommentReply> selectXfptCommentReplyList(XfptCommentReply xfptCommentReply);
}
