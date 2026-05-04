package com.xfgl.supplier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.supplier.mapper.XfptCommentMapper;
import com.xfgl.supplier.domain.XfptComment;
import com.xfgl.supplier.service.IXfptCommentService;

/**
 * 订单评价Service业务层处理
 *
 * @author yinet
 * @date 2026-05-01
 */
@Service
public class XfptCommentServiceImpl extends ServiceImpl<XfptCommentMapper, XfptComment> implements IXfptCommentService {

    @Autowired
    private XfptCommentMapper xfptCommentMapper;

    /**
     * 查询订单评价列表
     *
     * @param xfptComment 订单评价
     * @return 订单评价
     */
    @Override
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment)
    {
        return xfptCommentMapper.selectXfptCommentList(xfptComment);
    }


}
