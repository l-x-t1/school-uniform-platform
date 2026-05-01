package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptParentMode;

/**
 * 选用组织组织家长投票确定企业模式(二)Mapper接口
 *
 * @author yinet
 * @date 2025-05-09
 */
public interface XfptParentModeMapper extends BaseMapper<XfptParentMode> {
    /**
     * 查询选用组织组织家长投票确定企业模式(二)列表
     *
     * @param xfptParentMode 选用组织组织家长投票确定企业模式(二)
     * @return 选用组织组织家长投票确定企业模式(二)集合
     */
    public List<XfptParentMode> selectXfptParentModeList(XfptParentMode xfptParentMode);
}
