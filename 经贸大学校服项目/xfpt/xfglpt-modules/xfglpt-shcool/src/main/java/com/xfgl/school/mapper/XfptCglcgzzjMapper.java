package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptCglcgzzj;

/**
 * 学校校服采购工作总结Mapper接口
 *
 * @author yinet
 * @date 2025-07-23
 */
public interface XfptCglcgzzjMapper extends BaseMapper<XfptCglcgzzj> {
    /**
     * 查询学校校服采购工作总结列表
     *
     * @param xfptCglcgzzj 学校校服采购工作总结
     * @return 学校校服采购工作总结集合
     */
    public List<XfptCglcgzzj> selectXfptCglcgzzjList(XfptCglcgzzj xfptCglcgzzj);
}
