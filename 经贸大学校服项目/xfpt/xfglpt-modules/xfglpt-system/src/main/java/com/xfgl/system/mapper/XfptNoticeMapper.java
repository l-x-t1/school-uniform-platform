package com.xfgl.system.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.domain.XfptNotice;

/**
 * 首页公告Mapper接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface XfptNoticeMapper extends BaseMapper<XfptNotice> {
    /**
     * 查询首页公告列表
     *
     * @param xfptNotice 首页公告
     * @return 首页公告集合
     */
    public List<XfptNotice> selectXfptNoticeList(XfptNotice xfptNotice);

    List<XfptNotice>  appList(XfptNotice xfptNotice);
}
