package com.xfgl.system.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.domain.vo.ReleaseSupplementVo;

/**
 * 首页公告Service接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface IXfptNoticeService extends IService<XfptNotice> {

    /**
     * 查询首页公告列表
     *
     * @param xfptNotice 首页公告
     * @return 首页公告集合
     */
    public List<XfptNotice> selectXfptNoticeList(XfptNotice xfptNotice);

    List<XfptNotice>  appList(XfptNotice xfptNotice);

    AjaxResult releaseSupplement(ReleaseSupplementVo releaseSupplementVo);

    AjaxResult selectReleaseSupplement(String id,String type,String catagory);
}
