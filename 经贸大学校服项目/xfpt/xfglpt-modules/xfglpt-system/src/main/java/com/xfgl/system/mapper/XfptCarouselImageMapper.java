package com.xfgl.system.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.system.domain.XfptCarouselImage;

/**
 * 轮播图Mapper接口
 *
 * @author yinet
 * @date 2025-05-19
 */
public interface XfptCarouselImageMapper extends BaseMapper<XfptCarouselImage> {
    /**
     * 查询轮播图列表
     *
     * @param xfptCarouselImage 轮播图
     * @return 轮播图集合
     */
    public List<XfptCarouselImage> selectXfptCarouselImageList(XfptCarouselImage xfptCarouselImage);
}
