package com.xfgl.school.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.ReportModeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.*;
import com.xfgl.school.domain.vo.ProcurementNoticeVo;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteNoticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptAnnoResultsMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 选用组织选购结果公示报备与公开Service业务层处理
 *
 * @author yinet
 * @date 2025-04-23
 */
@Service
@Slf4j
public class XfptAnnoResultsServiceImpl extends ServiceImpl<XfptAnnoResultsMapper, XfptAnnoResults> implements IXfptAnnoResultsService {

    @Autowired
    private XfptAnnoResultsMapper xfptAnnoResultsMapper;
    @Autowired
    private IXfptReportModeService iXfptReportModeService;
    @Autowired
    private IXfptEnterpriseModeService iXfptEnterpriseModeService;;
    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private IXfptParentModeService iXfptParentModeService;
    @Autowired
    private IXfptOrganizationModeService iXfptOrganizationModeService;
    @Autowired
    private IXfptYxwfModeService iXfptYxwfModeService;


    /**
     * 查询选用组织选购结果公示报备与公开列表
     *
     * @param xfptAnnoResults 选用组织选购结果公示报备与公开
     * @return 选用组织选购结果公示报备与公开
     */
    @Override
    public List<XfptAnnoResults> selectXfptAnnoResultsList(XfptAnnoResults xfptAnnoResults)
    {
        return xfptAnnoResultsMapper.selectXfptAnnoResultsList(xfptAnnoResults);
    }

    @Override
    public List<ProcurementNoticeVo> procurementNoticeList(Long meetId) {
        List<XfptReportMode> list = iXfptReportModeService.lambdaQuery().eq(XfptReportMode::getTeamId, meetId).list();
        if(CollUtil.isEmpty(list)){
            log.error("未查询班子会议对应的选购模式：{}",meetId);
        }
        //确定选购模式
        XfptReportMode xfptReportMode = list.get(0);
        if(StrUtil.isEmpty(xfptReportMode.getPurchaseMode())){
            log.error("未查询到选购模式：{}",xfptReportMode.toString());
        }

        List<ProcurementNoticeVo> procurementNoticeVoList=new ArrayList<>();
        if(PurchaseType.DSFZB.getCode().equals(xfptReportMode.getPurchaseMode())){//第三方招标
            List<XfptEnterpriseMode> list1 = iXfptEnterpriseModeService.lambdaQuery()
                    .eq(XfptEnterpriseMode::getTeamId, meetId)
                    .eq(XfptEnterpriseMode::getType, ReportModeStatus.XGGG.getCode())
                    .list();
            if(CollUtil.isEmpty(list1)){
                log.warn("未查询到选购公告，招标模式：{}",meetId);
            }
            list1.stream().forEach(item->{
                ProcurementNoticeVo procurementNoticeVo=new ProcurementNoticeVo();
                procurementNoticeVo.setNoticeName(item.getNoticeName());
                procurementNoticeVo.setId(item.getId());
                procurementNoticeVo.setPurchaseMode(xfptReportMode.getPurchaseMode());
                procurementNoticeVo.setReleaseTime(item.getReleaseTime());
                procurementNoticeVoList.add(procurementNoticeVo);
            });

        }else if(PurchaseType.JZXJ.getCode().equals(xfptReportMode.getPurchaseMode())){//一校为主
            List<XfptYxwfMode> list1 = iXfptYxwfModeService.lambdaQuery().eq(XfptYxwfMode::getTeamId, meetId).list();
            list1.stream().forEach(item->{
                ProcurementNoticeVo procurementNoticeVo=new ProcurementNoticeVo();
                procurementNoticeVo.setNoticeName(item.getTitle());
                procurementNoticeVo.setId(item.getId());
                procurementNoticeVo.setPurchaseMode(xfptReportMode.getPurchaseMode());
                procurementNoticeVo.setReleaseTime(item.getCreateTime());
                procurementNoticeVoList.add(procurementNoticeVo);
            });
        }else if(PurchaseType.ZZXJ.getCode().equals(xfptReportMode.getPurchaseMode())){//组织选举
            List<XfptOrganizationMode> list1 = iXfptOrganizationModeService.lambdaQuery().eq(XfptOrganizationMode::getTeamId, meetId).list();
            list1.stream().forEach(item->{
                ProcurementNoticeVo procurementNoticeVo=new ProcurementNoticeVo();
                procurementNoticeVo.setNoticeName(item.getTitle());
                procurementNoticeVo.setId(item.getId());
                procurementNoticeVo.setPurchaseMode(xfptReportMode.getPurchaseMode());
                procurementNoticeVo.setReleaseTime(item.getCreateTime());
                procurementNoticeVoList.add(procurementNoticeVo);
            });
        }

        return procurementNoticeVoList;
    }

    @Override
    @Transactional
    public Boolean withdraw(Long id) {
        //修改公告状态
        boolean update = lambdaUpdate().eq(XfptAnnoResults::getId, id).set(XfptAnnoResults::getIsRelease, NoticeStatus.CX.getCode()).update();
        if(!update){
            log.error("修改选举公告状态错误：{}",id);
            throw new ServiceException("系统错误");
        }
        //修改公告表状态
        R<Boolean> booleanR = remoteNoticService.updateStatus(id, NoticeStatus.CX.getCode(), SecurityConstants.INNER);
        if(booleanR==null || booleanR.getCode()!=200){
            log.error("修改选举公告状态错误：{}",id);
            throw new ServiceException("系统错误");
        }
        return update;
    }


}
