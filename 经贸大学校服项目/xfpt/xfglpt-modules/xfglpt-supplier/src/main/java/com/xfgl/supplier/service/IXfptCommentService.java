package com.xfgl.supplier.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.supplier.domain.XfptComment;

/**
 * 订单评价Service接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface IXfptCommentService extends IService<XfptComment> {

    /**
     * 查询订单评价列表
     *
     * @param xfptComment 订单评价
     * @return 订单评价集合
     */
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment);

}
