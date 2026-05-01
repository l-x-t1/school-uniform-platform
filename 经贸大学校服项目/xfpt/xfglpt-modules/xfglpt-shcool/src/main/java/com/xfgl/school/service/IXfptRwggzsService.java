package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptRwggzs;

/**
 * 企业入围公告Service接口
 *
 * @author yinet
 * @date 2025-07-22
 */
public interface IXfptRwggzsService extends IService<XfptRwggzs> {

    /**
     * 查询企业入围公告列表
     *
     * @param xfptRwggzs 企业入围公告
     * @return 企业入围公告集合
     */
    public List<XfptRwggzs> selectXfptRwggzsList(XfptRwggzs xfptRwggzs);

}
