package com.xfgl.system.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.system.domain.XfptCarouselImage;

/**
 * 轮播图Service接口
 *
 * @author yinet
 * @date 2025-05-19
 */
public interface IXfptCarouselImageService extends IService<XfptCarouselImage> {

    /**
     * 查询轮播图列表
     *
     * @param xfptCarouselImage 轮播图
     * @return 轮播图集合
     */
    public List<XfptCarouselImage> selectXfptCarouselImageList(XfptCarouselImage xfptCarouselImage);

}
