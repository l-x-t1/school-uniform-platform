package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.school.domain.XfptNoticUser;
import com.xfgl.system.api.domain.XfptNotice;


/**
 * 收藏Service接口
 *
 * @author yinet
 * @date 2025-09-15
 */
public interface IXfptNoticUserService extends IService<XfptNoticUser> {

    /**
     * 查询收藏列表
     *
     * @param xfptNoticUser 收藏
     * @return 收藏集合
     */
    public List<XfptNoticUser> selectXfptNoticUserList(XfptNoticUser xfptNoticUser);

    List<XfptNotice> collectList(Long userId);
}
