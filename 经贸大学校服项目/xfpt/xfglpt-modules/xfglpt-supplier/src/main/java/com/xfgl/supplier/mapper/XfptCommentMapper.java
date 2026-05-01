package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptComment;

/**
 * 商品评价Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptCommentMapper extends BaseMapper<XfptComment> {
    /**
     * 查询商品评价列表
     *
     * @param xfptComment 商品评价
     * @return 商品评价集合
     */
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment);
}
