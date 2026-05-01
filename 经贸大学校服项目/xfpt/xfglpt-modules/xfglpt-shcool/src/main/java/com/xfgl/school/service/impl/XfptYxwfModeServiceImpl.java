package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.system.api.RemoteNoticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptYxwfModeMapper;
import com.xfgl.school.domain.XfptYxwfMode;
import com.xfgl.school.service.IXfptYxwfModeService;

/**
 * 一校为主模式（模式二）Service业务层处理
 *
 * @author yinet
 * @date 2025-05-23
 */
@Service
@Slf4j
public class XfptYxwfModeServiceImpl extends ServiceImpl<XfptYxwfModeMapper, XfptYxwfMode> implements IXfptYxwfModeService {

    @Autowired
    private XfptYxwfModeMapper xfptYxwfModeMapper;
    @Autowired
    private RemoteNoticService remoteNoticService;

    /**
     * 查询一校为主模式（模式二）列表
     *
     * @param xfptYxwfMode 一校为主模式（模式二）
     * @return 一校为主模式（模式二）
     */
    @Override
    public List<XfptYxwfMode> selectXfptYxwfModeList(XfptYxwfMode xfptYxwfMode)
    {
        return xfptYxwfModeMapper.selectXfptYxwfModeList(xfptYxwfMode);
    }


    @Override
    public Boolean withdraw(Long id,String status) {
        //修改公告状态
        boolean update = lambdaUpdate().eq(XfptYxwfMode::getId, id).set(XfptYxwfMode::getIsRelease, status).update();
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
