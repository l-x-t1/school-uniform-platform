package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.xfgl.school.mapper.XfptFinanceFlowMapper;
import com.xfgl.school.domain.XfptFinanceFlow;
import com.xfgl.school.service.IXfptFinanceFlowService;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;

/**
 * 资金流水Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptFinanceFlowServiceImpl extends ServiceImpl<XfptFinanceFlowMapper, XfptFinanceFlow> implements IXfptFinanceFlowService {

    @Autowired
    private XfptFinanceFlowMapper xfptFinanceFlowMapper;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询资金流水列表
     *
     * @param xfptFinanceFlow 资金流水
     * @return 资金流水
     */
    @Override
    public List<XfptFinanceFlow> selectXfptFinanceFlowList(XfptFinanceFlow xfptFinanceFlow)
    {
        if (xfptFinanceFlow == null) {
            xfptFinanceFlow = new XfptFinanceFlow();
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser() && currentSchool == null) {
            return Collections.emptyList();
        }
        if (currentSchool != null) {
            xfptFinanceFlow.setSchoolId(currentSchool.getId());
            xfptFinanceFlow.setDeptId(currentSchool.getDeptId());
        }
        return xfptFinanceFlowMapper.selectXfptFinanceFlowList(xfptFinanceFlow);
    }

    @Override
    public XfptFinanceFlow getById(Serializable id)
    {
        return requireAccessibleFinanceFlow(super.getById(id));
    }

    @Override
    public boolean save(XfptFinanceFlow entity)
    {
        applyCurrentSchoolScopeForWrite(entity, null);
        return super.save(entity);
    }

    @Override
    public boolean updateById(XfptFinanceFlow entity)
    {
        XfptFinanceFlow existing = requireAccessibleFinanceFlow(super.getById(entity.getId()));
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
                requireAccessibleFinanceFlow(super.getById(id));
            }
        }
        return super.removeByIds(list);
    }

    private void applyCurrentSchoolScopeForWrite(XfptFinanceFlow financeFlow, XfptFinanceFlow existing)
    {
        if (!isSchoolScopedUser()) {
            return;
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (currentSchool == null) {
            throw new ServiceException("当前登录学校信息不存在，无法保存资金流水");
        }
        financeFlow.setSchoolId(currentSchool.getId());
        financeFlow.setDeptId(currentSchool.getDeptId());
        if (existing != null && !Objects.equals(existing.getSchoolId(), currentSchool.getId())) {
            throw new ServiceException("只能操作当前学校的资金流水");
        }
    }

    private XfptFinanceFlow requireAccessibleFinanceFlow(XfptFinanceFlow financeFlow)
    {
        if (financeFlow == null || !"0".equals(financeFlow.getDelFlag())) {
            throw new ServiceException("资金流水不存在");
        }
        XfptSchoolMessage currentSchool = getCurrentSchoolIfScoped();
        if (isSchoolScopedUser()) {
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在，无法操作资金流水");
            }
            if (!Objects.equals(financeFlow.getSchoolId(), currentSchool.getId())
                    || !Objects.equals(financeFlow.getDeptId(), currentSchool.getDeptId())) {
                throw new ServiceException("只能操作当前学校的资金流水");
            }
        }
        return financeFlow;
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
