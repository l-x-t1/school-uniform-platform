package com.xfgl.school.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.xfgl.school.domain.XfptSchoolSupplierGoodsRel;
import com.xfgl.school.mapper.XfptSchoolSupplierRelMapper;
import com.xfgl.school.domain.XfptSchoolSupplierRel;
import com.xfgl.school.service.IXfptSchoolSupplierRelService;

/**
 * 学校供应商合作关系Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptSchoolSupplierRelServiceImpl extends ServiceImpl<XfptSchoolSupplierRelMapper, XfptSchoolSupplierRel> implements IXfptSchoolSupplierRelService {

    @Autowired
    private XfptSchoolSupplierRelMapper xfptSchoolSupplierRelMapper;

    /**
     * 查询学校供应商合作关系列表
     *
     * @param xfptSchoolSupplierRel 学校供应商合作关系
     * @return 学校供应商合作关系
     */
    @Override
    public List<XfptSchoolSupplierRel> selectXfptSchoolSupplierRelList(XfptSchoolSupplierRel xfptSchoolSupplierRel)
    {
        return xfptSchoolSupplierRelMapper.selectXfptSchoolSupplierRelList(xfptSchoolSupplierRel);
    }

        /**
         * 查询学校供应商合作关系
         *
         * @param id 学校供应商合作关系主键
         * @return 学校供应商合作关系
         */
        @Override
        public XfptSchoolSupplierRel selectXfptSchoolSupplierRelById(Long id)
        {
            XfptSchoolSupplierRel xfptSchoolSupplierRel = xfptSchoolSupplierRelMapper.selectById(id);
            List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList = xfptSchoolSupplierRelMapper.selectXfptSchoolSupplierGoodsRelById(id);
            xfptSchoolSupplierRel.setXfptSchoolSupplierGoodsRelList(xfptSchoolSupplierGoodsRelList);
            return xfptSchoolSupplierRel;
        }

        /**
         * 新增学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        @Transactional
        @Override
        public int insertXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            fillRelDefaults(xfptSchoolSupplierRel);
            int rows = xfptSchoolSupplierRelMapper.insert(xfptSchoolSupplierRel);
            insertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierRel);
            return rows;
        }

        /**
         * 修改学校供应商合作关系
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系
         * @return 结果
         */
        @Transactional
        @Override
        public int updateXfptSchoolSupplierRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
                fillRelDefaults(xfptSchoolSupplierRel);
                xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(xfptSchoolSupplierRel.getId());
            insertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierRel);
            return xfptSchoolSupplierRelMapper.updateById(xfptSchoolSupplierRel);
        }

        /**
         * 取消合作
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        @Override
        public int cancelCooperation(Long id)
        {
            XfptSchoolSupplierRel rel = new XfptSchoolSupplierRel();
            rel.setId(id);
            rel.setCooperateStatus("2");
            return xfptSchoolSupplierRelMapper.updateById(rel);
        }

        /**
         * 设置服务电话
         *
         * @param id 学校供应商合作关系主键
         * @param servicePhone 服务电话
         * @return 结果
         */
        @Override
        public int updateServicePhone(Long id, String servicePhone)
        {
            XfptSchoolSupplierRel rel = new XfptSchoolSupplierRel();
            rel.setId(id);
            rel.setServicePhone(servicePhone);
            return xfptSchoolSupplierRelMapper.updateById(rel);
        }

        /**
         * 替换合作商品
         *
         * @param id 学校供应商合作关系主键
         * @param goodsRelList 合作商品列表
         * @return 结果
         */
        @Transactional
        @Override
        public int replaceGoodsRel(Long id, List<XfptSchoolSupplierGoodsRel> goodsRelList)
        {
            XfptSchoolSupplierRel rel = xfptSchoolSupplierRelMapper.selectById(id);
            if (StringUtils.isNull(rel)) {
                throw new ServiceException("学校供应商合作关系不存在");
            }
            validateGoodsRelList(goodsRelList);
            xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
            if (StringUtils.isNotEmpty(goodsRelList)) {
                for (XfptSchoolSupplierGoodsRel goodsRel : goodsRelList) {
                    goodsRel.setId(null);
                    goodsRel.setRelId(id);
                    goodsRel.setSchoolId(rel.getSchoolId());
                    goodsRel.setSupplierId(rel.getSupplierId());
                    if (StringUtils.isEmpty(goodsRel.getStatus())) {
                        goodsRel.setStatus("1");
                    }
                    if (StringUtils.isEmpty(goodsRel.getDelFlag())) {
                        goodsRel.setDelFlag("0");
                    }
                }
                xfptSchoolSupplierRelMapper.batchInsertXfptSchoolSupplierGoodsRel(goodsRelList);
            }
            return 1;
        }

        /**
         * 批量删除学校供应商合作关系
         *
         * @param ids 需要删除的学校供应商合作关系主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptSchoolSupplierRelByIds(Long[] ids)
        {
            List<Long> idList = Arrays.asList(ids);
            for (Long id : idList) {
                xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
        }
            return xfptSchoolSupplierRelMapper.deleteBatchIds(idList);
        }

        /**
         * 删除学校供应商合作关系信息
         *
         * @param id 学校供应商合作关系主键
         * @return 结果
         */
        @Transactional
        @Override
        public int deleteXfptSchoolSupplierRelById(Long id)
        {
                xfptSchoolSupplierRelMapper.deleteXfptSchoolSupplierGoodsRelByRelId(id);
            return xfptSchoolSupplierRelMapper.deleteById(id);
        }

        /**
         * 新增学校供应商合作商品信息
         *
         * @param xfptSchoolSupplierRel 学校供应商合作关系对象
         */
        private void insertXfptSchoolSupplierGoodsRel(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            List<XfptSchoolSupplierGoodsRel> xfptSchoolSupplierGoodsRelList = xfptSchoolSupplierRel.getXfptSchoolSupplierGoodsRelList();
            Long id = xfptSchoolSupplierRel.getId();
            if (StringUtils.isNotNull(xfptSchoolSupplierGoodsRelList) && !xfptSchoolSupplierGoodsRelList.isEmpty()) {
                validateGoodsRelList(xfptSchoolSupplierGoodsRelList);
                for (XfptSchoolSupplierGoodsRel xfptSchoolSupplierGoodsRel : xfptSchoolSupplierGoodsRelList) {
                    xfptSchoolSupplierGoodsRel.setRelId(id);
                    xfptSchoolSupplierGoodsRel.setSchoolId(xfptSchoolSupplierRel.getSchoolId());
                    xfptSchoolSupplierGoodsRel.setSupplierId(xfptSchoolSupplierRel.getSupplierId());
                    if (StringUtils.isEmpty(xfptSchoolSupplierGoodsRel.getStatus())) {
                        xfptSchoolSupplierGoodsRel.setStatus("1");
                    }
                    if (StringUtils.isEmpty(xfptSchoolSupplierGoodsRel.getDelFlag())) {
                        xfptSchoolSupplierGoodsRel.setDelFlag("0");
                    }
                }
                    xfptSchoolSupplierRelMapper.batchInsertXfptSchoolSupplierGoodsRel(xfptSchoolSupplierGoodsRelList);
            }
        }

        private void fillRelDefaults(XfptSchoolSupplierRel xfptSchoolSupplierRel)
        {
            if (StringUtils.isEmpty(xfptSchoolSupplierRel.getCooperateStatus())) {
                xfptSchoolSupplierRel.setCooperateStatus("1");
            }
            if (StringUtils.isEmpty(xfptSchoolSupplierRel.getDelFlag())) {
                xfptSchoolSupplierRel.setDelFlag("0");
            }
        }

        private void validateGoodsRelList(List<XfptSchoolSupplierGoodsRel> goodsRelList)
        {
            if (StringUtils.isEmpty(goodsRelList)) {
                return;
            }
            Set<Long> goodsIds = new HashSet<>();
            for (XfptSchoolSupplierGoodsRel goodsRel : goodsRelList) {
                if (StringUtils.isNull(goodsRel.getGoodsId())) {
                    throw new ServiceException("合作商品不能为空");
                }
                if (!goodsIds.add(goodsRel.getGoodsId())) {
                    throw new ServiceException("合作商品不能重复关联");
                }
            }
        }

}
