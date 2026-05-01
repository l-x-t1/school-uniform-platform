package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptHmd;

/**
 * 黑名单Service接口
 *
 * @author yinet
 * @date 2025-10-23
 */
public interface IXfptHmdService extends IService<XfptHmd> {

    /**
     * 查询黑名单列表
     *
     * @param xfptHmd 黑名单
     * @return 黑名单集合
     */
    public List<XfptHmd> selectXfptHmdList(XfptHmd xfptHmd);

}
