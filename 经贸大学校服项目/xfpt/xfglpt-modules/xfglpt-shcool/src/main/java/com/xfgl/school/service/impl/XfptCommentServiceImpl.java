package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.mapper.XfptCommentMapper;
import com.xfgl.school.mapper.XfptCommentReplyMapper;
import com.xfgl.school.domain.XfptComment;
import com.xfgl.school.service.IXfptCommentService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;

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

    @Autowired
    private XfptCommentReplyMapper xfptCommentReplyMapper;

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
         * 查询评价管理列表
         *
         * @param xfptComment 商品评价
         * @return 评价管理集合
         */
        @Override
        public List<XfptComment> selectEvaluationCommentList(XfptComment xfptComment)
        {
            return xfptCommentMapper.selectEvaluationCommentList(xfptComment);
        }

        /**
         * 查询评价管理详情
         *
         * @param id 商品评价主键
         * @param scopeSchoolId 学校数据范围
         * @return 评价管理详情
         */
        @Override
        public XfptComment selectEvaluationCommentById(Long id, Long scopeSchoolId)
        {
            return xfptCommentMapper.selectEvaluationCommentById(id, scopeSchoolId);
        }

        /**
         * 回复评价
         *
         * @param id 商品评价主键
         * @param xfptCommentReply 回复内容
         * @param scopeSchoolId 学校数据范围
         * @return 结果
         */
        @Transactional
        @Override
        public int replyEvaluationComment(Long id, XfptCommentReply xfptCommentReply, Long scopeSchoolId)
        {
            XfptComment xfptComment = xfptCommentMapper.selectEvaluationCommentById(id, scopeSchoolId);
            if (StringUtils.isNull(xfptComment)) {
                throw new ServiceException("评价不存在或无权操作");
            }

            Date now = new Date();
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser == null ? null : loginUser.getSysUser();
            Long fromUserId = currentUser == null ? SecurityUtils.getUserId() : currentUser.getUserId();
            String userName = currentUser == null ? SecurityUtils.getUsername() : currentUser.getUserName();
            String nickName = currentUser == null ? "" : currentUser.getNickName();
            String replyUserName = StringUtils.isNotEmpty(nickName) ? nickName : userName;

            xfptCommentReply.setCommentId(id);
            xfptCommentReply.setOrderId(xfptComment.getOrderId());
            xfptCommentReply.setOrderItemId(xfptComment.getOrderItemId());
            xfptCommentReply.setGoodsId(xfptComment.getGoodsId());
            xfptCommentReply.setSupplierId(xfptComment.getSupplierId());
            xfptCommentReply.setFromUserId(fromUserId);
            xfptCommentReply.setFromUserName(replyUserName);
            xfptCommentReply.setFromRoleType(resolveReplyRoleType(loginUser));
            xfptCommentReply.setFromRoleTitle(resolveReplyRoleTitle(loginUser));
            xfptCommentReply.setToUserId(xfptComment.getUserId());
            xfptCommentReply.setToUserName(xfptComment.getUserName());
            xfptCommentReply.setToRoleType("1");
            xfptCommentReply.setToRoleTitle("家长");
            xfptCommentReply.setStatus("0");
            xfptCommentReply.setSortNum(xfptComment.getReplyCount() == null ? 1 : xfptComment.getReplyCount() + 1);
            xfptCommentReply.setCreateBy(replyUserName);
            xfptCommentReply.setCreateTime(now);
            xfptCommentReply.setDelFlag("0");
            int rows = xfptCommentReplyMapper.insert(xfptCommentReply);

            XfptComment updateComment = new XfptComment();
            updateComment.setId(id);
            updateComment.setReplyContent(xfptCommentReply.getContent());
            updateComment.setReplyTime(now);
            updateComment.setUpdateBy(replyUserName);
            updateComment.setUpdateTime(now);
            xfptCommentMapper.updateById(updateComment);
            return rows;
        }

        /**
         * 更新评价管理状态
         *
         * @param id 商品评价主键
         * @param status 评价状态，0正常 1隐藏
         * @param scopeSchoolId 学校数据范围
         * @return 结果
         */
        @Override
        public int updateEvaluationCommentStatus(Long id, String status, Long scopeSchoolId)
        {
            XfptComment xfptComment = xfptCommentMapper.selectEvaluationCommentById(id, scopeSchoolId);
            if (StringUtils.isNull(xfptComment)) {
                throw new ServiceException("评价不存在或无权操作");
            }

            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser == null ? null : loginUser.getSysUser();
            String userName = currentUser == null ? SecurityUtils.getUsername() : currentUser.getUserName();
            String nickName = currentUser == null ? "" : currentUser.getNickName();
            String operatorName = StringUtils.isNotEmpty(nickName) ? nickName : userName;

            XfptComment updateComment = new XfptComment();
            updateComment.setId(id);
            updateComment.setStatus(status);
            updateComment.setUpdateBy(operatorName);
            updateComment.setUpdateTime(new Date());
            return xfptCommentMapper.updateById(updateComment);
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

        private String resolveReplyRoleType(LoginUser loginUser)
        {
            if (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || hasRole(loginUser, "admin") || hasRole(loginUser, "gly")) {
                return "4";
            }
            return "3";
        }

        private String resolveReplyRoleTitle(LoginUser loginUser)
        {
            if (SecurityUtils.isAdmin(SecurityUtils.getUserId()) || hasRole(loginUser, "admin") || hasRole(loginUser, "gly")) {
                return "管理员";
            }
            return "学校";
        }

        private boolean hasRole(LoginUser loginUser, String roleKey)
        {
            return loginUser != null && loginUser.getRoles() != null && loginUser.getRoles().contains(roleKey);
        }

}
