package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptAnnoResults;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;

/**
 * 选用组织选购结果公示报备与公开 Service 接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface IXfptAnnoResultsService extends IService<XfptAnnoResults> {

    /**
     * 查询选用组织选购结果公示报备与公开列表
     *
     * @param xfptAnnoResults 查询条件
     * @return 列表数据
     */
    List<XfptAnnoResults> selectXfptAnnoResultsList(XfptAnnoResults xfptAnnoResults);

    /**
     * 解析班子会议对应的选购模式；当 report_mode 缺失时回退到实际业务数据推断。
     *
     * @param meetId 班子会议 id
     * @return 选购模式编码，无法确定时返回 null
     */
    String resolvePurchaseMode(Long meetId);

    /**
     * 查询选购公告列表
     *
     * @param meetId 班子会议 id
     * @return 公告来源列表
     */
    List<ProcurementNoticeVo> procurementNoticeList(Long meetId);

    /**
     * 撤回公告
     *
     * @param id 主键 id
     * @return 是否成功
     */
    Boolean withdraw(Long id);
}
