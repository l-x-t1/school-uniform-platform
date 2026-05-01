package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptEnterpriseMode;

/**
 * 选用组织委托招标机构确定企业模式Service接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface IXfptEnterpriseModeService extends IService<XfptEnterpriseMode> {

    /**
     * 查询选用组织委托招标机构确定企业模式列表
     *
     * @param xfptEnterpriseMode 选用组织委托招标机构确定企业模式
     * @return 选用组织委托招标机构确定企业模式集合
     */
    public List<XfptEnterpriseMode> selectXfptEnterpriseModeList(XfptEnterpriseMode xfptEnterpriseMode);

    /**
     * 撤回公告
     * @param id
     * @return
     */
    Boolean withdraw(Long id,String isRelease);

    /**
     * 删除公告
     * @param id
     * @return
     */
    int removeNotice(Long id);
}
