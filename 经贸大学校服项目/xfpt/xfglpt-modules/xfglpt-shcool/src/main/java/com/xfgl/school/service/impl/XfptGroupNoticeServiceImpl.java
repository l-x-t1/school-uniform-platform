package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.GroupNoticeOrderDetailVo;
import com.xfgl.school.domain.vo.GroupNoticeProgressQuery;
import com.xfgl.school.domain.vo.GroupNoticeProgressVo;
import com.xfgl.school.mapper.XfptGroupNoticeMapper;
import com.xfgl.school.service.IXfptGroupNoticeService;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 统购公告Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGroupNoticeServiceImpl extends ServiceImpl<XfptGroupNoticeMapper, XfptGroupNotice> implements IXfptGroupNoticeService {

    @Autowired
    private XfptGroupNoticeMapper xfptGroupNoticeMapper;

    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;

    @Override
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice) {
        if (!isAdmin()) {
            XfptSchoolMessage currentSchool = getCurrentSchool();
            xfptGroupNotice.setDeptId(currentSchool.getDeptId());
            xfptGroupNotice.setSchoolId(currentSchool.getId());
        }
        return xfptGroupNoticeMapper.selectXfptGroupNoticeList(xfptGroupNotice);
    }

    @Override
    public XfptGroupNotice selectXfptGroupNoticeById(Long id) {
        XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectById(id);
        checkNoticeBelongsToCurrentSchool(xfptGroupNotice);
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
        xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
        return xfptGroupNotice;
    }

    @Transactional
    @Override
    public int insertXfptGroupNotice(XfptGroupNotice xfptGroupNotice) {
        fillGroupNoticeDefaults(xfptGroupNotice);
        fillNoticeSchoolInfo(xfptGroupNotice);
        int rows = xfptGroupNoticeMapper.insert(xfptGroupNotice);
        insertXfptGroupNoticeItem(xfptGroupNotice);
        return rows;
    }

    @Transactional
    @Override
    public int updateXfptGroupNotice(XfptGroupNotice xfptGroupNotice) {
        checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(xfptGroupNotice.getId()));
        fillGroupNoticeDefaults(xfptGroupNotice);
        fillNoticeSchoolInfo(xfptGroupNotice);
        xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(xfptGroupNotice.getId());
        insertXfptGroupNoticeItem(xfptGroupNotice);
        return xfptGroupNoticeMapper.updateById(xfptGroupNotice);
    }

    @Transactional
    @Override
    public int deleteXfptGroupNoticeByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        for (Long id : idList) {
            checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(id));
            xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
        }
        return xfptGroupNoticeMapper.deleteBatchIds(idList);
    }

    @Transactional
    @Override
    public int deleteXfptGroupNoticeById(Long id) {
        checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(id));
        xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
        return xfptGroupNoticeMapper.deleteById(id);
    }

    @Override
    public List<GroupNoticeProgressVo> selectGroupNoticeProgressList(GroupNoticeProgressQuery query) {
        if (!isAdmin()) {
            XfptSchoolMessage currentSchool = getCurrentSchool();
            query.setDeptId(currentSchool.getDeptId());
            query.setSchoolId(currentSchool.getId());
        }
        return xfptGroupNoticeMapper.selectGroupNoticeProgressList(query);
    }

    @Override
    public List<GroupNoticeOrderDetailVo> selectGroupNoticeOrderDetail(Long noticeId, Long supplierId) {
        checkNoticeBelongsToCurrentSchool(xfptGroupNoticeMapper.selectById(noticeId));
        Long deptId = null;
        if (!isAdmin()) {
            deptId = getCurrentSchool().getDeptId();
        }
        return xfptGroupNoticeMapper.selectGroupNoticeOrderDetail(noticeId, supplierId, deptId);
    }

    private void insertXfptGroupNoticeItem(XfptGroupNotice xfptGroupNotice) {
        List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNotice.getXfptGroupNoticeItemList();
        Long id = xfptGroupNotice.getId();
        if (StringUtils.isNotEmpty(xfptGroupNoticeItemList)) {
            for (XfptGroupNoticeItem xfptGroupNoticeItem : xfptGroupNoticeItemList) {
                xfptGroupNoticeItem.setNoticeId(id);
                xfptGroupNoticeItem.setDeptId(xfptGroupNotice.getDeptId());
                if (StringUtils.isEmpty(xfptGroupNoticeItem.getDelFlag())) {
                    xfptGroupNoticeItem.setDelFlag("0");
                }
            }
            xfptGroupNoticeMapper.batchInsertXfptGroupNoticeItem(xfptGroupNoticeItemList);
        }
    }

    private void fillGroupNoticeDefaults(XfptGroupNotice xfptGroupNotice) {
        if (StringUtils.isEmpty(xfptGroupNotice.getNoticeType())) {
            xfptGroupNotice.setNoticeType("1");
        }
        if (StringUtils.isEmpty(xfptGroupNotice.getStatus())) {
            xfptGroupNotice.setStatus("0");
        }
        if (StringUtils.isEmpty(xfptGroupNotice.getApplyStatus())) {
            xfptGroupNotice.setApplyStatus("0");
        }
        if (StringUtils.isEmpty(xfptGroupNotice.getDelFlag())) {
            xfptGroupNotice.setDelFlag("0");
        }
    }

    private void fillNoticeSchoolInfo(XfptGroupNotice xfptGroupNotice) {
        if (!isAdmin()) {
            XfptSchoolMessage currentSchool = getCurrentSchool();
            xfptGroupNotice.setDeptId(currentSchool.getDeptId());
            xfptGroupNotice.setSchoolId(currentSchool.getId());
            xfptGroupNotice.setSchoolName(currentSchool.getSchoolName());
            return;
        }

        if (xfptGroupNotice.getSchoolId() == null) {
            throw new ServiceException("请选择学校");
        }
        XfptSchoolMessage schoolMessage = xfptSchoolMessageService.lambdaQuery()
                .eq(XfptSchoolMessage::getId, xfptGroupNotice.getSchoolId())
                .one();
        if (schoolMessage == null || schoolMessage.getDeptId() == null) {
            throw new ServiceException("选择的学校信息不存在");
        }
        xfptGroupNotice.setDeptId(schoolMessage.getDeptId());
        xfptGroupNotice.setSchoolId(schoolMessage.getId());
        xfptGroupNotice.setSchoolName(schoolMessage.getSchoolName());
    }

    private void checkNoticeBelongsToCurrentSchool(XfptGroupNotice xfptGroupNotice) {
        if (xfptGroupNotice == null) {
            throw new ServiceException("公告不存在");
        }
        if (isAdmin()) {
            return;
        }
        XfptSchoolMessage currentSchool = getCurrentSchool();
        if (!Objects.equals(xfptGroupNotice.getDeptId(), currentSchool.getDeptId())
                || !Objects.equals(xfptGroupNotice.getSchoolId(), currentSchool.getId())) {
            throw new ServiceException("只能操作当前学校的公告");
        }
    }

    private XfptSchoolMessage getCurrentSchool() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        Long deptId = sysUser == null ? null : sysUser.getDeptId();
        if (deptId == null) {
            throw new ServiceException("未获取到当前登录学校");
        }
        XfptSchoolMessage schoolMessage = xfptSchoolMessageService.lambdaQuery()
                .eq(XfptSchoolMessage::getDeptId, deptId)
                .one();
        if (schoolMessage == null || schoolMessage.getId() == null) {
            throw new ServiceException("当前登录学校信息不存在，请先完善学校信息");
        }
        return schoolMessage;
    }

    private boolean isAdmin() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        if (sysUser == null) {
            return false;
        }
        if (sysUser.isAdmin() || "admin".equals(sysUser.getUserName())) {
            return true;
        }
        List<SysRole> roles = sysUser.getRoles();
        if (roles == null) {
            return false;
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(SysRole::getRoleKey)
                .filter(StringUtils::isNotEmpty)
                .anyMatch(roleKey -> "admin".equals(roleKey) || RoleType.SJYJ.getCode().equals(roleKey));
    }
}
