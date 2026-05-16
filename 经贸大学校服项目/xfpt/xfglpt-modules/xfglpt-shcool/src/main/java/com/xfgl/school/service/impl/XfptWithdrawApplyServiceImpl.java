package com.xfgl.school.service.impl;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.xfgl.school.domain.XfptFinanceFlow;
import com.xfgl.school.mapper.XfptWithdrawApplyMapper;
import com.xfgl.school.mapper.XfptFinanceFlowMapper;
import com.xfgl.school.domain.XfptWithdrawApply;
import com.xfgl.school.service.IXfptWithdrawApplyService;

/**
 * 供应商提现申请Service业务层处理
 *
 * @author yinet
 * @date 2026-05-04
 */
@Service
public class XfptWithdrawApplyServiceImpl extends ServiceImpl<XfptWithdrawApplyMapper, XfptWithdrawApply> implements IXfptWithdrawApplyService {

    @Autowired
    private XfptWithdrawApplyMapper xfptWithdrawApplyMapper;

    @Autowired
    private XfptFinanceFlowMapper xfptFinanceFlowMapper;

    /**
     * 查询供应商提现申请列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请
     */
    @Override
    public List<XfptWithdrawApply> selectXfptWithdrawApplyList(XfptWithdrawApply xfptWithdrawApply)
    {
        return xfptWithdrawApplyMapper.selectXfptWithdrawApplyList(xfptWithdrawApply);
    }

    /**
     * 查询结算管理列表
     *
     * @param xfptWithdrawApply 供应商提现申请
     * @return 供应商提现申请
     */
    @Override
    public List<XfptWithdrawApply> selectSettlementManageList(XfptWithdrawApply xfptWithdrawApply)
    {
        return xfptWithdrawApplyMapper.selectSettlementManageList(xfptWithdrawApply);
    }

    /**
     * 执行结算
     *
     * @param id 申请结算记录ID
     * @param payChannel 支付渠道
     * @param tradeNo 外部交易单号
     * @param remark 结算备注
     * @return 更新后的申请结算记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public XfptWithdrawApply settleSettlementManage(Long id, String payChannel, String tradeNo, String remark)
    {
        XfptWithdrawApply current = getById(id);
        if (current == null || !"0".equals(current.getDelFlag())) {
            throw new ServiceException("结算申请记录不存在");
        }
        if (!"1".equals(current.getStatus())) {
            throw new ServiceException("只有审核通过的申请可以结算");
        }
        if (current.getSupplierId() == null) {
            throw new ServiceException("供应商ID不能为空");
        }
        if (current.getApplyAmount() == null) {
            throw new ServiceException("结算金额不能为空");
        }

        Date now = new Date();
        String operator = SecurityUtils.getUsername();
        XfptFinanceFlow financeFlow = buildSettlementFinanceFlow(current, payChannel, tradeNo, remark, now, operator);
        xfptFinanceFlowMapper.insert(financeFlow);

        XfptWithdrawApply updateApply = new XfptWithdrawApply();
        updateApply.setId(id);
        updateApply.setStatus("3");
        updateApply.setPayTime(now);
        updateApply.setFinanceFlowId(financeFlow.getId());
        updateApply.setUpdateBy(operator);
        updateApply.setUpdateTime(now);

        boolean updated = update(
                updateApply,
                Wrappers.<XfptWithdrawApply>lambdaUpdate()
                        .eq(XfptWithdrawApply::getId, id)
                        .eq(XfptWithdrawApply::getStatus, "1")
                        .eq(XfptWithdrawApply::getDelFlag, "0")
        );
        if (!updated) {
            throw new ServiceException("只有审核通过的申请可以结算");
        }
        return getById(id);
    }

    private XfptFinanceFlow buildSettlementFinanceFlow(XfptWithdrawApply withdrawApply, String payChannel,
                                                       String tradeNo, String remark, Date now, String operator)
    {
        JSONObject bankInfo = parseBankInfo(withdrawApply.getBankInfoJson());
        XfptFinanceFlow financeFlow = new XfptFinanceFlow();
        financeFlow.setFlowNo(buildSettlementFlowNo(withdrawApply.getId(), now));
        financeFlow.setBusinessType("4");
        financeFlow.setFlowDirection("2");
        financeFlow.setSupplierId(withdrawApply.getSupplierId());
        financeFlow.setAmount(withdrawApply.getApplyAmount());
        financeFlow.setPayChannel(StringUtils.isNotEmpty(payChannel) ? payChannel : "线下转账");
        financeFlow.setTradeNo(tradeNo);
        financeFlow.setPayeeType("2");
        financeFlow.setPayeeId(withdrawApply.getSupplierId());
        financeFlow.setPayeeName(StringUtils.isNotEmpty(withdrawApply.getSupplierName())
                ? withdrawApply.getSupplierName() : "供应商" + withdrawApply.getSupplierId());
        financeFlow.setPayeeAccountName(getBankValue(bankInfo, "accountName"));
        financeFlow.setPayeeBankName(getBankValue(bankInfo, "bankName"));
        financeFlow.setPayeeBankAccountNo(firstNotEmpty(
                getBankValue(bankInfo, "accountNo"),
                getBankValue(bankInfo, "bankNo"),
                getBankValue(bankInfo, "bankAccountNo")
        ));
        financeFlow.setStatus("2");
        financeFlow.setTradeTime(now);
        financeFlow.setCreateBy(operator);
        financeFlow.setCreateTime(now);
        financeFlow.setRemark(StringUtils.isNotEmpty(remark) ? remark : "申请结算打款");
        financeFlow.setDelFlag("0");
        return financeFlow;
    }

    private JSONObject parseBankInfo(String bankInfoJson)
    {
        if (StringUtils.isEmpty(bankInfoJson)) {
            return null;
        }
        try {
            return JSONObject.parseObject(bankInfoJson);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getBankValue(JSONObject bankInfo, String key)
    {
        if (bankInfo == null) {
            return null;
        }
        return bankInfo.getString(key);
    }

    private String firstNotEmpty(String... values)
    {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }
        }
        return null;
    }

    private String buildSettlementFlowNo(Long applyId, Date now)
    {
        return "JS" + new SimpleDateFormat("yyyyMMddHHmmss").format(now) + applyId;
    }

}
