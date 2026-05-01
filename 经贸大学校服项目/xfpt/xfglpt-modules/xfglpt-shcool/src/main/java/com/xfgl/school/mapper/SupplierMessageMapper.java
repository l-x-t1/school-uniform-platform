package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.SupplierMessage;


/**
 * 供应商信息Mapper接口
 *
 * @author yinet
 * @date 2025-04-22
 */
public interface SupplierMessageMapper extends BaseMapper<SupplierMessage> {
    /**
     * 查询供应商信息列表
     *
     * @param supplierMessage 供应商信息
     * @return 供应商信息集合
     */
    public List<SupplierMessage> selectSupplierMessageList(SupplierMessage supplierMessage);
}
