package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptHmd;

/**
 * 黑名单Mapper接口
 *
 * @author yinet
 * @date 2025-10-23
 */
public interface XfptHmdMapper extends BaseMapper<XfptHmd> {
    /**
     * 查询黑名单列表
     *
     * @param xfptHmd 黑名单
     * @return 黑名单集合
     */
    public List<XfptHmd> selectXfptHmdList(XfptHmd xfptHmd);
}
