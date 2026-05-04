package com.xfgl.school.service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptComment;

/**
 * 商品评价Service接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface IXfptCommentService extends IService<XfptComment> {

    /**
     * 查询商品评价列表
     *
     * @param xfptComment 商品评价
     * @return 商品评价集合
     */
    public List<XfptComment> selectXfptCommentList(XfptComment xfptComment);

        /**
         * 查询商品评价
         *
         * @param id 商品评价主键
         * @return 商品评价
         */
        public XfptComment selectXfptCommentById(Long id);

        /**
         * 新增商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        public int insertXfptComment(XfptComment xfptComment);

        /**
         * 修改商品评价
         *
         * @param xfptComment 商品评价
         * @return 结果
         */
        public int updateXfptComment(XfptComment xfptComment);

        /**
         * 批量删除商品评价
         *
         * @param ids 需要删除的商品评价主键集合
         * @return 结果
         */
        public int deleteXfptCommentByIds(Long[] ids);

        /**
         * 删除商品评价信息
         *
         * @param id 商品评价主键
         * @return 结果
         */
        public int deleteXfptCommentById(Long id);
}
