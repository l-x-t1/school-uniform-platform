package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptParentModeMapper;
import com.xfgl.school.domain.XfptParentMode;
import com.xfgl.school.service.IXfptParentModeService;

/**
 * 选用组织组织家长投票确定企业模式(二)Service业务层处理
 *
 * @author yinet
 * @date 2025-05-09
 */
@Service
public class XfptParentModeServiceImpl extends ServiceImpl<XfptParentModeMapper, XfptParentMode> implements IXfptParentModeService {

    @Autowired
    private XfptParentModeMapper xfptParentModeMapper;

    /**
     * 查询选用组织组织家长投票确定企业模式(二)列表
     *
     * @param xfptParentMode 选用组织组织家长投票确定企业模式(二)
     * @return 选用组织组织家长投票确定企业模式(二)
     */
    @Override
    public List<XfptParentMode> selectXfptParentModeList(XfptParentMode xfptParentMode)
    {
        return xfptParentModeMapper.selectXfptParentModeList(xfptParentMode);
    }


}
