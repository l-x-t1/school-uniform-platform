package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSupplierList;

/**
 * 确定供应商名单Mapper接口
 *
 * @author yinet
 * @date 2025-05-23
 */
public interface XfptSupplierListMapper extends BaseMapper<XfptSupplierList> {
    /**
     * 查询确定供应商名单列表
     *
     * @param xfptSupplierList 确定供应商名单
     * @return 确定供应商名单集合
     */
    public List<XfptSupplierList> selectXfptSupplierListList(XfptSupplierList xfptSupplierList);
}
