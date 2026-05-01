package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptRwggzs;

/**
 * 企业入围公告Mapper接口
 *
 * @author yinet
 * @date 2025-07-22
 */
public interface XfptRwggzsMapper extends BaseMapper<XfptRwggzs> {
    /**
     * 查询企业入围公告列表
     *
     * @param xfptRwggzs 企业入围公告
     * @return 企业入围公告集合
     */
    public List<XfptRwggzs> selectXfptRwggzsList(XfptRwggzs xfptRwggzs);
}
