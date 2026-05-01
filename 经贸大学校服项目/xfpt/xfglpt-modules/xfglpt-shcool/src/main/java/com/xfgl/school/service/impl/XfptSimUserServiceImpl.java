package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSimUserMapper;
import com.xfgl.school.domain.XfptSimUser;
import com.xfgl.school.service.IXfptSimUserService;

/**
 * 石i民用户Service业务层处理
 *
 * @author yinet
 * @date 2025-05-13
 */
@Service
public class XfptSimUserServiceImpl extends ServiceImpl<XfptSimUserMapper, XfptSimUser> implements IXfptSimUserService {

    @Autowired
    private XfptSimUserMapper xfptSimUserMapper;

    /**
     * 查询石i民用户列表
     *
     * @param xfptSimUser 石i民用户
     * @return 石i民用户
     */
    @Override
    public List<XfptSimUser> selectXfptSimUserList(XfptSimUser xfptSimUser)
    {
        return xfptSimUserMapper.selectXfptSimUserList(xfptSimUser);
    }


}
