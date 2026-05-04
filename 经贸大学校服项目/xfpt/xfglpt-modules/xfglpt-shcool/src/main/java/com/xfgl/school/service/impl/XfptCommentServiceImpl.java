package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.mapper.XfptCommentMapper;
import com.xfgl.school.domain.XfptComment;
import com.xfgl.school.service.IXfptCommentService;

/**
 * 商品评价Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptCommentServiceImpl extends ServiceImpl<XfptCommentMapper, XfptComment> implements IXfptCommentService {

    @Autowired
    private XfptCommentMapper xfptCommentMapper;

    /**
     * 查询商品评价列表
     *
     * @param xfptComment 商品评价
     * @return 商品评价
     */
    @Override
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment)
    {
        return xfptCommentMapper.selectXfptCommentList(xfptComment);
    }

        /**
         * 查询商品评价
         *
         * @param id 商品评价主键
         * @return 商品评价
         */
        @Override
        public XfptComment selectXfptCommentById(Long id)
        {
            XfptComment xfptComment = xfptCommentMapper.selectById(id);
            List<XfptCommentReply> xfptCommentReplyList = xfptCommentMapper.selectXfptCommentReplyById(id);
            xfptComment.setXfptCommentReplyList(xfptCommentReplyList);
            return xfptComment;
        }

        /**
         * 新增商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptComment(XfptComment xfptComment)
        {
            int rows = xfptCommentMapper.insert(xfptComment);
            insertXfptCommentReply(xfptComment);
            return rows;
        }

        /**
         * 修改商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptComment(XfptComment xfptComment)
        {
                xfptCommentMapper.deleteXfptCommentReplyByCommentId(xfptComment.getId());
            insertXfptCommentReply(xfptComment);
            return xfptCommentMapper.updateById(xfptComment);
        }

        /**
         * 批量删除商品评价
         *
         * @param ids 需要删除的商品评价主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptCommentByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptCommentMapper.deleteXfptCommentReplyByCommentId(id);
        }
            return xfptCommentMapper.deleteBatchIds(idList);
        }

        /**
         * 删除商品评价信息
         *
         * @param id 商品评价主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptCommentById(Long id)
        {
                xfptCommentMapper.deleteXfptCommentReplyByCommentId(id);
            return xfptCommentMapper.deleteById(id);
        }

        /**
         * 新增商品评论回复信息
         *
         * @param xfptComment 商品评价对象
         */
        private void insertXfptCommentReply(XfptComment xfptComment)
        {
            List<XfptCommentReply> xfptCommentReplyList = xfptComment.getXfptCommentReplyList();
            Long id = xfptComment.getId();
            if (StringUtils.isNotNull(xfptCommentReplyList)) {
                for (XfptCommentReply xfptCommentReply : xfptCommentReplyList) {
                    xfptCommentReply.setCommentId(id);
                }
                    xfptCommentMapper.batchInsertXfptCommentReply(xfptCommentReplyList);
            }
        }

}
