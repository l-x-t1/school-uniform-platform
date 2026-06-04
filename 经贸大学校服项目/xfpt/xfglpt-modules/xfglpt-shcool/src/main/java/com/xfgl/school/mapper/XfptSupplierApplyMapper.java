package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSupplierApply;

/**
 * 供应商报名Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptSupplierApplyMapper extends BaseMapper<XfptSupplierApply> {
    /**
     * 查询供应商报名列表
     *
     * @param xfptSupplierApply 供应商报名
     * @return 供应商报名集合
     */
    public List<XfptSupplierApply> selectXfptSupplierApplyList(XfptSupplierApply xfptSupplierApply);

    /**
     * 查询供应商报名详情
     *
     * @param id 主键
     * @return 供应商报名
     */
    public XfptSupplierApply selectXfptSupplierApplyById(Long id);
}
