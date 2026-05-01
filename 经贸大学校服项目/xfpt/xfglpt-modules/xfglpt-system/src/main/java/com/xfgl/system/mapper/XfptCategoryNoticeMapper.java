package com.xfgl.system.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.system.domain.XfptCategoryNotice;

/**
 * 栏目分类Mapper接口
 *
 * @author yinet
 * @date 2025-05-06
 */
public interface XfptCategoryNoticeMapper extends BaseMapper<XfptCategoryNotice> {
    /**
     * 查询栏目分类列表
     *
     * @param xfptCategoryNotice 栏目分类
     * @return 栏目分类集合
     */
    public List<XfptCategoryNotice> selectXfptCategoryNoticeList(XfptCategoryNotice xfptCategoryNotice);

    public List<XfptCategoryNotice> selectXfptCategoryNoticeList2(XfptCategoryNotice xfptCategoryNotice);
}
