package com.xfgl.school.support;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.xfgl.common.core.enums.RoleType;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchoolAccessSupport {
    @Autowired
    private IXfptSchoolMessageService xfptSchoolMessageService;

    public boolean isSchoolUser(SysUser sysUser) {
        if (sysUser == null || sysUser.getRoles() == null || sysUser.getRoles().isEmpty()) {
            return false;
        }
        for (SysRole role : sysUser.getRoles()) {
            if (role == null) {
                continue;
            }
            if (StringUtils.equalsAnyIgnoreCase(role.getRoleKey(), RoleType.XX.getCode(), RoleType.CSXX.getCode())) {
                return true;
            }
        }
        return false;
    }

    public Long resolveCurrentDeptId(SysUser sysUser) {
        return sysUser == null ? null : sysUser.getDeptId();
    }

    public XfptSchoolMessage resolveCurrentSchool(SysUser sysUser) {
        if (!isSchoolUser(sysUser)) {
            return null;
        }
        Long deptId = resolveCurrentDeptId(sysUser);
        if (deptId == null) {
            return null;
        }
        Page<?> currentPage = PageMethod.getLocalPage();
        try {
            PageMethod.clearPage();
            List<XfptSchoolMessage> schoolList = xfptSchoolMessageService.lambdaQuery()
                    .eq(XfptSchoolMessage::getDeptId, deptId)
                    .orderByDesc(XfptSchoolMessage::getId)
                    .last("limit 1")
                    .list();
            return schoolList.isEmpty() ? null : schoolList.get(0);
        } finally {
            if (currentPage != null) {
                PageMethod.setLocalPage(currentPage);
            }
        }
    }
}
