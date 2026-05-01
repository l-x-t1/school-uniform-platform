package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptEnterpriseMode;

/**
 * 选用组织委托招标机构确定企业模式Mapper接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface XfptEnterpriseModeMapper extends BaseMapper<XfptEnterpriseMode> {
    /**
     * 查询选用组织委托招标机构确定企业模式列表
     *
     * @param xfptEnterpriseMode 选用组织委托招标机构确定企业模式
     * @return 选用组织委托招标机构确定企业模式集合
     */
    public List<XfptEnterpriseMode> selectXfptEnterpriseModeList(XfptEnterpriseMode xfptEnterpriseMode);
}
