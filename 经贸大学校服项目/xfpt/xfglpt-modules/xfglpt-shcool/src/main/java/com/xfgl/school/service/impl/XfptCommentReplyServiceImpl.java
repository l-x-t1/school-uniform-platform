package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptCommentReplyMapper;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.service.IXfptCommentReplyService;

/**
 * 商品评论回复Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptCommentReplyServiceImpl extends ServiceImpl<XfptCommentReplyMapper, XfptCommentReply> implements IXfptCommentReplyService {

    @Autowired
    private XfptCommentReplyMapper xfptCommentReplyMapper;

    /**
     * 查询商品评论回复列表
     *
     * @param xfptCommentReply 商品评论回复
     * @return 商品评论回复
     */
    @Override
    public List<XfptCommentReply> selectXfptCommentReplyList(XfptCommentReply xfptCommentReply)
    {
        return xfptCommentReplyMapper.selectXfptCommentReplyList(xfptCommentReply);
    }

    @Override
    public boolean save(XfptCommentReply xfptCommentReply)
    {
        fillCommentReplyDefaults(xfptCommentReply);
        return super.save(xfptCommentReply);
    }

    @Override
    public boolean updateById(XfptCommentReply xfptCommentReply)
    {
        fillCommentReplyDefaults(xfptCommentReply);
        return super.updateById(xfptCommentReply);
    }

    private void fillCommentReplyDefaults(XfptCommentReply xfptCommentReply)
    {
        if (xfptCommentReply.getStatus() == null || "".equals(xfptCommentReply.getStatus())) {
            xfptCommentReply.setStatus("0");
        }
        if (xfptCommentReply.getDelFlag() == null || "".equals(xfptCommentReply.getDelFlag())) {
            xfptCommentReply.setDelFlag("0");
        }
    }

}
