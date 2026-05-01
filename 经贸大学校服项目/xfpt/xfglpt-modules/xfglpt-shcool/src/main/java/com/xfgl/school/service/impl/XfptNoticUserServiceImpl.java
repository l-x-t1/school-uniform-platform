package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.school.domain.XfptNoticUser;
import com.xfgl.school.mapper.XfptNoticUserMapper;
import com.xfgl.school.service.IXfptNoticUserService;
import com.xfgl.system.api.domain.XfptNotice;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;


/**
 * 收藏Service业务层处理
 *
 * @author yinet
 * @date 2025-09-15
 */
@Service
public class XfptNoticUserServiceImpl extends ServiceImpl<XfptNoticUserMapper, XfptNoticUser> implements IXfptNoticUserService {

    @Autowired
    private XfptNoticUserMapper xfptNoticUserMapper;

    /**
     * 查询收藏列表
     *
     * @param xfptNoticUser 收藏
     * @return 收藏
     */
    @Override
    public List<XfptNoticUser> selectXfptNoticUserList(XfptNoticUser xfptNoticUser)
    {
        return xfptNoticUserMapper.selectXfptNoticUserList(xfptNoticUser);
    }

    @Override
    public List<XfptNotice> collectList(Long userId) {
        List<XfptNotice> list=xfptNoticUserMapper.collectList(userId);;
        return list;
    }


}
