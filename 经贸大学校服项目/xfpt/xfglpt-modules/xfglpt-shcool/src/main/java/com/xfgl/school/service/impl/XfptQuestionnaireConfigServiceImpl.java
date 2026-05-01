package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import com.xfgl.school.mapper.XfptQuestionnaireConfigMapper;
import com.xfgl.school.domain.XfptQuestionnaireConfig;
import com.xfgl.school.service.IXfptQuestionnaireConfigService;

/**
 * 问卷调查主配置Service业务层处理
 *
 * @author wxm
 * @date 2025-04-27
 */
@Service
public class XfptQuestionnaireConfigServiceImpl extends ServiceImpl<XfptQuestionnaireConfigMapper, XfptQuestionnaireConfig> implements IXfptQuestionnaireConfigService {

    @Autowired
    private XfptQuestionnaireConfigMapper xfptQuestionnaireConfigMapper;

    /**
     * 查询问卷调查主配置列表
     *
     * @param xfptQuestionnaireConfig 问卷调查主配置
     * @return 问卷调查主配置
     */
    @Override
    public List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList(XfptQuestionnaireConfig xfptQuestionnaireConfig)
    {
        return xfptQuestionnaireConfigMapper.selectXfptQuestionnaireConfigList(xfptQuestionnaireConfig);
    }

    @Override
    public List<XfptQuestionnaireConfig> selectTemplateQuestionnaireConfigList() {
        boolean admin = SecurityUtils.isAdmin(SecurityUtils.getUserId());
        if (!admin) {
            Long deptId = SecurityUtils.getLoginUser().getSysUser().getDeptId();
            return xfptQuestionnaireConfigMapper.selectXfptQuestionnaireConfigList(XfptQuestionnaireConfig.builder().category("1").deptId(deptId).build());
        }
        return xfptQuestionnaireConfigMapper.selectXfptQuestionnaireConfigList(XfptQuestionnaireConfig.builder().category("1").build());
    }

    @Override
    public List<XfptQuestionnaireConfig> selectXfptQuestionnaireConfigList2(XfptQuestionnaireConfig xfptQuestionnaireConfig) {
        return xfptQuestionnaireConfigMapper.selectXfptQuestionnaireConfigList2(xfptQuestionnaireConfig);
    }
}
