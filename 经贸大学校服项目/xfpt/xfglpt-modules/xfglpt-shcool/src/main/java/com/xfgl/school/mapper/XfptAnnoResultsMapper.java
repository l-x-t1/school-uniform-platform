package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptAnnoResults;

/**
 * 选用组织选购结果公示报备与公开Mapper接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface XfptAnnoResultsMapper extends BaseMapper<XfptAnnoResults> {
    /**
     * 查询选用组织选购结果公示报备与公开列表
     *
     * @param xfptAnnoResults 选用组织选购结果公示报备与公开
     * @return 选用组织选购结果公示报备与公开集合
     */
    public List<XfptAnnoResults> selectXfptAnnoResultsList(XfptAnnoResults xfptAnnoResults);
}
