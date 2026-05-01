package com.xfgl.system.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.system.domain.XfptCategoryNotice;

/**
 * 栏目分类Service接口
 *
 * @author yinet
 * @date 2025-05-06
 */
public interface IXfptCategoryNoticeService extends IService<XfptCategoryNotice> {

    /**
     * 查询栏目分类列表
     *
     * @param xfptCategoryNotice 栏目分类
     * @return 栏目分类集合
     */
    public List<XfptCategoryNotice> selectXfptCategoryNoticeList(XfptCategoryNotice xfptCategoryNotice);

    /**
     * 查询除去采购公告的栏目分类列表
     * @param xfptCategoryNotice
     * @return
     */
    public List<XfptCategoryNotice> selectXfptCategoryNoticeList2(XfptCategoryNotice xfptCategoryNotice);

}
