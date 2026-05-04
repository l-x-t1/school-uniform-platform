package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptComment;

/**
 * 订单评价Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptCommentMapper extends BaseMapper<XfptComment> {
    /**
     * 查询订单评价列表
     *
     * @param xfptComment 订单评价
     * @return 订单评价集合
     */
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment);
}
