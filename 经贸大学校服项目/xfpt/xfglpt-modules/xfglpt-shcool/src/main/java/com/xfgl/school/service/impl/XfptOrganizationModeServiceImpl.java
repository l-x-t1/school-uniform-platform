package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.XfptEnterpriseMode;
import com.xfgl.system.api.RemoteNoticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptOrganizationModeMapper;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.service.IXfptOrganizationModeService;

/**
 * 模式一组织确认选购模式Service业务层处理
 *
 * @author yinet
 * @date 2025-05-08
 */
@Service
@Slf4j
public class XfptOrganizationModeServiceImpl extends ServiceImpl<XfptOrganizationModeMapper, XfptOrganizationMode> implements IXfptOrganizationModeService {

    @Autowired
    private XfptOrganizationModeMapper xfptOrganizationModeMapper;
    @Autowired
    private RemoteNoticService remoteNoticService;

    /**
     * 查询模式一组织确认选购模式列表
     *
     * @param xfptOrganizationMode 模式一组织确认选购模式
     * @return 模式一组织确认选购模式
     */
    @Override
    public List<XfptOrganizationMode> selectXfptOrganizationModeList(XfptOrganizationMode xfptOrganizationMode)
    {
        return xfptOrganizationModeMapper.selectXfptOrganizationModeList(xfptOrganizationMode);
    }

    @Override
    public Boolean withdraw(Long id,String status) {
        //修改公告状态
        boolean update = lambdaUpdate().eq(XfptOrganizationMode::getId, id).set(XfptOrganizationMode::getIsRelease, status).update();
        if(!update){
            log.error("修改选举公告状态错误：{}",id);
            throw new ServiceException("修改选举公告状态错误");
        }
        R<Boolean> booleanR = remoteNoticService.updateStatus(id, NoticeStatus.CX.getCode(), SecurityConstants.INNER);
        if(booleanR==null || booleanR.getCode()!=200){
            log.error("远程调用修改选举公告状态错误：{}",booleanR.getMsg());
            throw new ServiceException("远程调用修改选举公告状态错误");
        }
        //修改公告表状态
        return true;

    }


}
