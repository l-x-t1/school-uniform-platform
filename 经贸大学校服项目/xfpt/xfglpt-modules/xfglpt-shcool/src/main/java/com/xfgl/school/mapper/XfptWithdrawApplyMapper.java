package com.xfgl.school.mapper;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptWithdrawApply;

/**
 * 供应商提现申请Mapper接口
 *
 * @author yinet
 * @date 2026-05-04
 */
public interface XfptWithdrawApplyMapper extends BaseMapper<XfptWithdrawApply> {
    /**
     * 查询供应商提现申请列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请集合
     */
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply);

    public XfptWithdrawApply selectXfptWithdrawApplyDetail(XfptWithdrawApply xfptWithdrawApply);
}
