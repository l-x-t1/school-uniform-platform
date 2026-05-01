package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptOrganizationMode;

/**
 * 模式一组织确认选购模式Mapper接口
 *
 * @author yinet
 * @date 2025-05-08
 */
public interface XfptOrganizationModeMapper extends BaseMapper<XfptOrganizationMode> {
    /**
     * 查询模式一组织确认选购模式列表
     *
     * @param xfptOrganizationMode 模式一组织确认选购模式
     * @return 模式一组织确认选购模式集合
     */
    public List<XfptOrganizationMode> selectXfptOrganizationModeList(XfptOrganizationMode xfptOrganizationMode);
}
