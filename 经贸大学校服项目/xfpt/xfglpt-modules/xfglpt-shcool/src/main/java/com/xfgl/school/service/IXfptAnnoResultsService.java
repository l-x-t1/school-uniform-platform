package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;

/**
 * 选用组织选购结果公示报备与公开Service接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface IXfptAnnoResultsService extends IService<XfptAnnoResults> {

    /**
     * 查询选用组织选购结果公示报备与公开列表
     *
     * @param xfptAnnoResults 选用组织选购结果公示报备与公开
     * @return 选用组织选购结果公示报备与公开集合
     */
    public List<XfptAnnoResults> selectXfptAnnoResultsList(XfptAnnoResults xfptAnnoResults);

    /**
     * 选购公告列表
     * @param meetId
     * @return
     */
    List<ProcurementNoticeVo> procurementNoticeList(Long meetId);

    /**
     * 撤回公告
     * @param id
     * @return
     */
    Boolean withdraw(Long id);
}
