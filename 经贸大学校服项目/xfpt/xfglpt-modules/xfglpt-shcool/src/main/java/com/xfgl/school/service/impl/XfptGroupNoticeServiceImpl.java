package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptGroupNoticeItem;
import com.xfgl.school.mapper.XfptGroupNoticeMapper;
import com.xfgl.school.domain.XfptGroupNotice;
import com.xfgl.school.service.IXfptGroupNoticeService;

/**
 * 统购公告Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptGroupNoticeServiceImpl extends ServiceImpl<XfptGroupNoticeMapper, XfptGroupNotice> implements IXfptGroupNoticeService {

    @Autowired
    private XfptGroupNoticeMapper xfptGroupNoticeMapper;

    /**
     * 查询统购公告列表
     *
     * @param xfptGroupNotice 统购公告
     * @return 统购公告
     */
    @Override
    public List<XfptGroupNotice> selectXfptGroupNoticeList(XfptGroupNotice xfptGroupNotice)
    {
        return xfptGroupNoticeMapper.selectXfptGroupNoticeList(xfptGroupNotice);
    }

        /**
         * 查询统购公告
         *
         * @param id 统购公告主键
         * @return 统购公告
         */
        @Override
        public XfptGroupNotice selectXfptGroupNoticeById(Long id)
        {
            XfptGroupNotice xfptGroupNotice = xfptGroupNoticeMapper.selectById(id);
            List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNoticeMapper.selectXfptGroupNoticeItemById(id);
            xfptGroupNotice.setXfptGroupNoticeItemList(xfptGroupNoticeItemList);
            return xfptGroupNotice;
        }

        /**
         * 新增统购公告
         *
         * @param xfptGroupNotice 统购公告
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptGroupNotice(XfptGroupNotice xfptGroupNotice)
        {
            fillGroupNoticeDefaults(xfptGroupNotice);
            int rows = xfptGroupNoticeMapper.insert(xfptGroupNotice);
            insertXfptGroupNoticeItem(xfptGroupNotice);
            return rows;
        }

        /**
         * 修改统购公告
         *
         * @param xfptGroupNotice 统购公告
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptGroupNotice(XfptGroupNotice xfptGroupNotice)
        {
            fillGroupNoticeDefaults(xfptGroupNotice);
                xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(xfptGroupNotice.getId());
            insertXfptGroupNoticeItem(xfptGroupNotice);
            return xfptGroupNoticeMapper.updateById(xfptGroupNotice);
        }

        /**
         * 批量删除统购公告
         *
         * @param ids 需要删除的统购公告主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGroupNoticeByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
        }
            return xfptGroupNoticeMapper.deleteBatchIds(idList);
        }

        /**
         * 删除统购公告信息
         *
         * @param id 统购公告主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptGroupNoticeById(Long id)
        {
                xfptGroupNoticeMapper.deleteXfptGroupNoticeItemByNoticeId(id);
            return xfptGroupNoticeMapper.deleteById(id);
        }

        /**
         * 新增统购公告商品信息
         *
         * @param xfptGroupNotice 统购公告对象
         */
        private void insertXfptGroupNoticeItem(XfptGroupNotice xfptGroupNotice)
        {
            List<XfptGroupNoticeItem> xfptGroupNoticeItemList = xfptGroupNotice.getXfptGroupNoticeItemList();
            Long id = xfptGroupNotice.getId();
            if (StringUtils.isNotNull(xfptGroupNoticeItemList)) {
                for (XfptGroupNoticeItem xfptGroupNoticeItem : xfptGroupNoticeItemList) {
                    xfptGroupNoticeItem.setNoticeId(id);
                    if (xfptGroupNoticeItem.getDelFlag() == null || "".equals(xfptGroupNoticeItem.getDelFlag())) {
                        xfptGroupNoticeItem.setDelFlag("0");
                    }
                }
                    xfptGroupNoticeMapper.batchInsertXfptGroupNoticeItem(xfptGroupNoticeItemList);
            }
        }

        private void fillGroupNoticeDefaults(XfptGroupNotice xfptGroupNotice)
        {
            if (xfptGroupNotice.getNoticeType() == null || "".equals(xfptGroupNotice.getNoticeType())) {
                xfptGroupNotice.setNoticeType("1");
            }
            if (xfptGroupNotice.getStatus() == null || "".equals(xfptGroupNotice.getStatus())) {
                xfptGroupNotice.setStatus("0");
            }
            if (xfptGroupNotice.getApplyStatus() == null || "".equals(xfptGroupNotice.getApplyStatus())) {
                xfptGroupNotice.setApplyStatus("0");
            }
            if (xfptGroupNotice.getDelFlag() == null || "".equals(xfptGroupNotice.getDelFlag())) {
                xfptGroupNotice.setDelFlag("0");
            }
        }

}
