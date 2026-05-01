package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptYxwfMode;

/**
 * 一校为主模式（模式二）Service接口
 *
 * @author yinet
 * @date 2025-05-23
 */
public interface IXfptYxwfModeService extends IService<XfptYxwfMode> {

    /**
     * 查询一校为主模式（模式二）列表
     *
     * @param xfptYxwfMode 一校为主模式（模式二）
     * @return 一校为主模式（模式二）集合
     */
    public List<XfptYxwfMode> selectXfptYxwfModeList(XfptYxwfMode xfptYxwfMode);

    Boolean withdraw(Long id, String isRelease);
}
