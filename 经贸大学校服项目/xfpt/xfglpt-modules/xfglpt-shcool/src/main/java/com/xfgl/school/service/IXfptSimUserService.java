package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSimUser;

/**
 * 石i民用户Service接口
 *
 * @author yinet
 * @date 2025-05-13
 */
public interface IXfptSimUserService extends IService<XfptSimUser> {

    /**
     * 查询石i民用户列表
     *
     * @param xfptSimUser 石i民用户
     * @return 石i民用户集合
     */
    public List<XfptSimUser> selectXfptSimUserList(XfptSimUser xfptSimUser);

}
