package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptPdxzSureSupplier;

/**
 * 评定小组确定供应商Mapper接口
 *
 * @author yinet
 * @date 2025-05-23
 */
public interface XfptPdxzSureSupplierMapper extends BaseMapper<XfptPdxzSureSupplier> {
    /**
     * 查询评定小组确定供应商列表
     *
     * @param xfptPdxzSureSupplier 评定小组确定供应商
     * @return 评定小组确定供应商集合
     */
    public List<XfptPdxzSureSupplier> selectXfptPdxzSureSupplierList(XfptPdxzSureSupplier xfptPdxzSureSupplier);
}
