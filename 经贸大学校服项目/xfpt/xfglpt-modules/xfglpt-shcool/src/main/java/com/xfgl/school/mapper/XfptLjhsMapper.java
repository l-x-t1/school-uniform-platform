package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptLjhs;

/**
 * 利旧回收Mapper接口
 *
 * @author yinet
 * @date 2025-09-15
 */
public interface XfptLjhsMapper extends BaseMapper<XfptLjhs> {
    /**
     * 查询利旧回收列表
     *
     * @param xfptLjhs 利旧回收
     * @return 利旧回收集合
     */
    public List<XfptLjhs> selectXfptLjhsList(XfptLjhs xfptLjhs);
}
