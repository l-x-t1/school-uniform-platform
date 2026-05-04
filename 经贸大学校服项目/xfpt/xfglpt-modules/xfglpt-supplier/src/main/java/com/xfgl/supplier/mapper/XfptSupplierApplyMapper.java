package com.xfgl.supplier.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.supplier.domain.XfptSupplierApply;

/**
 * 企业报名及资格审查Mapper接口
 *
 * @author yinet
 * @date 2026-05-01
 */
public interface XfptSupplierApplyMapper extends BaseMapper<XfptSupplierApply> {
    /**
     * 查询企业报名及资格审查列表
     *
     * @param xfptSupplierApply 企业报名及资格审查
     * @return 企业报名及资格审查集合
     */
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply);
}
