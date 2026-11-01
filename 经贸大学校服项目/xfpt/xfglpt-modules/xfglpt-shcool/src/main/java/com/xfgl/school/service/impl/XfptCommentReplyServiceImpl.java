package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptComment;
import com.xfgl.school.domain.XfptCommentReply;
import com.xfgl.school.mapper.XfptCommentMapper;
import com.xfgl.school.mapper.XfptCommentReplyMapper;
import com.xfgl.school.service.IXfptCommentReplyService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.io.Serializable;
import java.util.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private XfptCommentMapper xfptCommentMapper;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;

    /**
     * 查询商品评论回复列表
     *
     * @param xfptCommentReply 商品评论回复
     * @return 商品评论回复
     */
    @Override
    public List<XfptCommentReply> selectXfptCommentReplyList(XfptCommentReply xfptCommentReply)
    {
        if (isSupplierScopedUser() && getCurrentSupplierIdIfScoped() == null) {
            return Collections.emptyList();
        }
        applyCurrentSupplierScope(xfptCommentReply);
        return xfptCommentReplyMapper.selectXfptCommentReplyList(xfptCommentReply);
    }

    @Override
    public XfptCommentReply getById(Serializable id)
    {
        XfptCommentReply reply = super.getById(id);
        requireAccessibleReply(reply);
        return reply;
    }

    @Override
    public boolean save(XfptCommentReply entity)
    {
        fillReplySnapshotAndValidate(entity, null);
        boolean saved = super.save(entity);
        if (saved) {
            syncCommentReplySnapshot(entity.getCommentId());
        }
        return saved;
    }

    @Override
    public boolean updateById(XfptCommentReply entity)
    {
        XfptCommentReply existing = requireAccessibleReply(super.getById(entity.getId()));
        fillReplySnapshotAndValidate(entity, existing);
        boolean updated = super.updateById(entity);
        if (updated) {
            syncCommentReplySnapshot(entity.getCommentId());
        }
        return updated;
    }

    @Override
    public boolean removeByIds(Collection<?> list)
    {
        if (list != null) {
            for (Object idObj : list) {
                if (idObj == null) {
                    continue;
                }
                Long id = Long.valueOf(String.valueOf(idObj));
                requireAccessibleReply(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void fillReplySnapshotAndValidate(XfptCommentReply entity, XfptCommentReply existing)
    {
        if (entity == null) {
            throw new ServiceException("回复信息不能为空");
        }
        Long commentId = entity.getCommentId();
        if (commentId == null && existing != null) {
            commentId = existing.getCommentId();
            entity.setCommentId(commentId);
        }
        if (commentId == null) {
            throw new ServiceException("评论不存在");
        }
        XfptComment comment = requireAccessibleComment(commentId);
        entity.setOrderId(comment.getOrderId());
        entity.setOrderItemId(comment.getOrderItemId());
        entity.setGoodsId(comment.getGoodsId());
        entity.setSupplierId(comment.getSupplierId());
        if (entity.getToUserId() == null) {
            entity.setToUserId(comment.getUserId());
        }
        if (entity.getToUserName() == null) {
            entity.setToUserName(comment.getUserName());
        }
        if (entity.getToRoleType() == null) {
            entity.setToRoleType("1");
        }
        if (entity.getToRoleTitle() == null) {
            entity.setToRoleTitle("家长");
        }
        if (entity.getStatus() == null) {
            entity.setStatus(existing == null ? "0" : existing.getStatus());
        }
        if (entity.getSortNum() == null) {
            entity.setSortNum(existing == null ? null : existing.getSortNum());
        }
        if (entity.getDelFlag() == null) {
            entity.setDelFlag(existing == null ? "0" : existing.getDelFlag());
        }
    }

    private void applyCurrentSupplierScope(XfptCommentReply xfptCommentReply)
    {
        if (xfptCommentReply == null) {
            return;
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作评论回复");
        }
        if (currentSupplierId != null) {
            xfptCommentReply.setSupplierId(currentSupplierId);
        }
    }

    private XfptCommentReply requireAccessibleReply(XfptCommentReply reply)
    {
        if (reply == null) {
            throw new ServiceException("评论回复不存在");
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作评论回复");
        }
        if (currentSupplierId != null && !currentSupplierId.equals(reply.getSupplierId())) {
            throw new ServiceException("无权操作其他厂商的评论回复");
        }
        return reply;
    }

    private XfptComment requireAccessibleComment(Long commentId)
    {
        XfptComment comment = xfptCommentMapper.selectById(commentId);
        if (comment == null || !"0".equals(comment.getDelFlag())) {
            throw new ServiceException("评论不存在");
        }
        Long currentSupplierId = getCurrentSupplierIdIfScoped();
        if (isSupplierScopedUser() && currentSupplierId == null) {
            throw new ServiceException("当前登录账号未关联厂商，无法操作评论回复");
        }
        if (currentSupplierId != null && !currentSupplierId.equals(comment.getSupplierId())) {
            throw new ServiceException("无权回复其他厂商的评价");
        }
        return comment;
    }

    private Long getCurrentSupplierIdIfScoped()
    {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return null;
        }
        return supplierAccessSupport.resolveSupplierId(currentUser);
    }

    private boolean isSupplierScopedUser()
    {
        return supplierAccessSupport.isSupplierUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private SysUser getCurrentUser()
    {
        if (SecurityUtils.getLoginUser() == null) {
            return null;
        }
        return SecurityUtils.getLoginUser().getSysUser();
    }

    private void syncCommentReplySnapshot(Long commentId)
    {
        if (commentId == null) {
            return;
        }
        XfptComment comment = xfptCommentMapper.selectById(commentId);
        if (comment == null || !"0".equals(String.valueOf(comment.getDelFlag()))) {
            return;
        }
        List<XfptCommentReply> replies = xfptCommentMapper.selectXfptCommentReplyById(commentId);
        XfptCommentReply latestSupplierReply = findLatestSupplierReply(replies);
        if (latestSupplierReply == null || latestSupplierReply.getContent() == null || latestSupplierReply.getContent().trim().isEmpty()) {
            return;
        }
        XfptComment updateEntity = new XfptComment();
        updateEntity.setId(commentId);
        updateEntity.setReplyContent(latestSupplierReply.getContent());
        updateEntity.setReplyTime(latestSupplierReply.getUpdateTime() != null
                ? latestSupplierReply.getUpdateTime()
                : latestSupplierReply.getCreateTime());
        updateEntity.setUpdateBy(SecurityUtils.getUsername());
        updateEntity.setUpdateTime(new Date());
        xfptCommentMapper.updateById(updateEntity);
    }

    private XfptCommentReply findLatestSupplierReply(List<XfptCommentReply> replies)
    {
        if (replies == null || replies.isEmpty()) {
            return null;
        }
        XfptCommentReply latestReply = null;
        for (XfptCommentReply reply : replies) {
            if (reply == null) {
                continue;
            }
            if (!"2".equals(String.valueOf(reply.getFromRoleType()))) {
                continue;
            }
            if (!"0".equals(String.valueOf(reply.getDelFlag()))) {
                continue;
            }
            if (latestReply == null || compareReplyPriority(reply, latestReply) > 0) {
                latestReply = reply;
            }
        }
        return latestReply;
    }

    private int compareReplyPriority(XfptCommentReply current, XfptCommentReply target)
    {
        Date currentTime = current.getUpdateTime() != null ? current.getUpdateTime() : current.getCreateTime();
        Date targetTime = target.getUpdateTime() != null ? target.getUpdateTime() : target.getCreateTime();
        if (currentTime == null && targetTime == null) {
            return Long.compare(current.getId() == null ? 0L : current.getId(), target.getId() == null ? 0L : target.getId());
        }
        if (currentTime == null) {
            return -1;
        }
        if (targetTime == null) {
            return 1;
        }
        int timeCompare = currentTime.compareTo(targetTime);
        if (timeCompare != 0) {
            return timeCompare;
        }
        return Long.compare(current.getId() == null ? 0L : current.getId(), target.getId() == null ? 0L : target.getId());
    }

}
