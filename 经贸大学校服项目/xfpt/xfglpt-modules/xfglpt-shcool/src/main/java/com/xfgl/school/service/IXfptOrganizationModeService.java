package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptOrganizationMode;

/**
 * 模式一组织确认选购模式Service接口
 *
 * @author yinet
 * @date 2025-05-08
 */
public interface IXfptOrganizationModeService extends IService<XfptOrganizationMode> {

    /**
     * 查询模式一组织确认选购模式列表
     *
     * @param xfptOrganizationMode 模式一组织确认选购模式
     * @return 模式一组织确认选购模式集合
     */
    public List<XfptOrganizationMode> selectXfptOrganizationModeList(XfptOrganizationMode xfptOrganizationMode);

    Boolean withdraw(Long id,String status);
}
