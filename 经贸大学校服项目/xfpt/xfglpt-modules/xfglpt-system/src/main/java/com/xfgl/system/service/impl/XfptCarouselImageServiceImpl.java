package com.xfgl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.system.mapper.XfptCarouselImageMapper;
import com.xfgl.system.domain.XfptCarouselImage;
import com.xfgl.system.service.IXfptCarouselImageService;

/**
 * 轮播图Service业务层处理
 *
 * @author yinet
 * @date 2025-05-19
 */
@Service
public class XfptCarouselImageServiceImpl extends ServiceImpl<XfptCarouselImageMapper, XfptCarouselImage> implements IXfptCarouselImageService {

    @Autowired
    private XfptCarouselImageMapper xfptCarouselImageMapper;

    /**
     * 查询轮播图列表
     *
     * @param xfptCarouselImage 轮播图
     * @return 轮播图
     */
    @Override
    public List<XfptCarouselImage> selectXfptCarouselImageList(XfptCarouselImage xfptCarouselImage)
    {
        return xfptCarouselImageMapper.selectXfptCarouselImageList(xfptCarouselImage);
    }


}
