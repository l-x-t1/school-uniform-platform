package com.xfgl.school.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptSupplierApply;
import com.xfgl.school.domain.vo.SupplierApplyNoticeVo;

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

    /**
     * 查询供应商可报名公告列表
     */
    List<SupplierApplyNoticeVo> selectSupplierNoticeCenterList(@Param("supplierId") Long supplierId, @Param("query") XfptSupplierApply query);

    /**
     * 查询采购公告概要
     */
    SupplierApplyNoticeVo selectNoticeOverviewById(@Param("noticeId") Long noticeId);

    /**
     * 按公告和供应商查询报名详情
     */
    XfptSupplierApply selectSupplierApplyByNoticeIdAndSupplierId(@Param("noticeId") Long noticeId, @Param("supplierId") Long supplierId);
}
