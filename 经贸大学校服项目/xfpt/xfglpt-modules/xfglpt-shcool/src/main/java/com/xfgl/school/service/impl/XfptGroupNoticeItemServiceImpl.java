package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.mapper.XfptGroupNoticeItemMapper;
import com.xfgl.school.mapper.XfptGroupNoticeMapper;
import com.xfgl.school.service.IXfptGroupNoticeItemService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统购公告商品Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGroupNoticeItemServiceImpl extends ServiceImpl<XfptGroupNoticeItemMapper, XfptGroupNoticeItem> implements IXfptGroupNoticeItemService {

    @Autowired
    private XfptGroupNoticeItemMapper xfptGroupNoticeItemMapper;
    @Autowired
    private XfptGroupNoticeMapper xfptGroupNoticeMapper;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询统购公告商品列表
     *
     * @param xfptGroupNoticeItem 统购公告商品
     * @return 统购公告商品
     */
    @Override
    public List<XfptGroupNoticeItem> selectXfptGroupNoticeItemList(XfptGroupNoticeItem xfptGroupNoticeItem)
    {
        if (xfptGroupNoticeItem == null) {
            xfptGroupNoticeItem = new XfptGroupNoticeItem();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptGroupNoticeItem.setDeptId(currentSchool.getDeptId());
        }
        return xfptGroupNoticeItemMapper.selectXfptGroupNoticeItemList(xfptGroupNoticeItem);
    }

    @Override
    public XfptGroupNoticeItem selectXfptGroupNoticeItemById(Long id)
    {
        return requireAccessibleItem(xfptGroupNoticeItemMapper.selectXfptGroupNoticeItemById(id));
    }

    @Override
    public XfptGroupNoticeItem getById(Serializable id)
    {
        return requireAccessibleItem(super.getById(id));
    }

    @Override
    public boolean save(XfptGroupNoticeItem entity)
    {
        applyCurrentSchoolScopeForWrite(entity, null);
        return super.save(entity);
    }

    @Override
    public boolean updateById(XfptGroupNoticeItem entity)
    {
        XfptGroupNoticeItem existing = requireAccessibleItem(super.getById(entity.getId()));
        applyCurrentSchoolScopeForWrite(entity, existing);
        return super.updateById(entity);
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
                requireAccessibleItem(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void applyCurrentSchoolScopeForWrite(XfptGroupNoticeItem entity, XfptGroupNoticeItem existing)
    {
        if (entity == null) {
            throw new ServiceException("统购公告商品不能为空");
        }
        XfptSchoolMessage currentSchool = getRequiredCurrentSchoolForWrite();
        if (currentSchool != null) {
            entity.setDeptId(currentSchool.getDeptId());
        }
        Long noticeId = entity.getNoticeId();
        if (noticeId == null && existing != null) {
            noticeId = existing.getNoticeId();
            entity.setNoticeId(noticeId);
        }
        validateNoticeScope(noticeId, currentSchool);
    }

    private void validateNoticeScope(Long noticeId, XfptSchoolMessage currentSchool)
    {
        if (noticeId == null || currentSchool == null) {
            return;
        }
        XfptGroupNotice notice = xfptGroupNoticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new ServiceException("所属公告不存在");
        }
        if (!Objects.equals(notice.getSchoolId(), currentSchool.getId())
                || !Objects.equals(notice.getDeptId(), currentSchool.getDeptId())) {
            throw new ServiceException("只能操作当前学校的统购公告商品");
        }
    }

    private XfptGroupNoticeItem requireAccessibleItem(XfptGroupNoticeItem item)
    {
        if (item == null || !"0".equals(item.getDelFlag())) {
            throw new ServiceException("统购公告商品不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            throw new ServiceException("当前登录学校信息不存在");
        }
        if (currentSchool != null && !Objects.equals(item.getDeptId(), currentSchool.getDeptId())) {
            throw new ServiceException("只能操作当前学校的统购公告商品");
        }
        return item;
    }

    private XfptSchoolMessage getRequiredCurrentSchoolForWrite()
    {
        if (!isSchoolScopedUser()) {
            return null;
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (currentSchool == null || currentSchool.getId() == null) {
            throw new ServiceException("当前登录学校信息不存在，请先完善学校信息");
        }
        return currentSchool;
    }

    private XfptSchoolMessage getCurrentSchoolIfScoped()
    {
        SysUser currentUser = getCurrentUser();
        if (!schoolAccessSupport.isSchoolUser(currentUser)) {
            return null;
        }
        return schoolAccessSupport.resolveCurrentSchool(currentUser);
    }

    private boolean isSchoolScopedUser()
    {
        return schoolAccessSupport.isSchoolUser(getCurrentUser()) && !SecurityUtils.isAdmin(SecurityUtils.getUserId());
    }

    private SysUser getCurrentUser()
    {
        return SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getSysUser();
    }
}
