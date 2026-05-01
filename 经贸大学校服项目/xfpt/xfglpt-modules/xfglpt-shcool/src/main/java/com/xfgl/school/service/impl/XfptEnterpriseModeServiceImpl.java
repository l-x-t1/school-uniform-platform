package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.system.api.RemoteNoticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptEnterpriseModeMapper;
import com.xfgl.school.domain.XfptEnterpriseMode;
import com.xfgl.school.service.IXfptEnterpriseModeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 选用组织委托招标机构确定企业模式Service业务层处理
 *
 * @author yinet
 * @date 2025-04-23
 */
@Service
@Slf4j
public class XfptEnterpriseModeServiceImpl extends ServiceImpl<XfptEnterpriseModeMapper, XfptEnterpriseMode> implements IXfptEnterpriseModeService {

    @Autowired
    private XfptEnterpriseModeMapper xfptEnterpriseModeMapper;
    @Autowired
    private RemoteNoticService remoteNoticService;

    /**
     * 查询选用组织委托招标机构确定企业模式列表
     *
     * @param xfptEnterpriseMode 选用组织委托招标机构确定企业模式
     * @return 选用组织委托招标机构确定企业模式
     */
    @Override
    public List<XfptEnterpriseMode> selectXfptEnterpriseModeList(XfptEnterpriseMode xfptEnterpriseMode)
    {
        return xfptEnterpriseModeMapper.selectXfptEnterpriseModeList(xfptEnterpriseMode);
    }

    /**
     * 撤回公告
     * @param id
     * @return
     */
    @Override
    @Transactional
    public Boolean withdraw(Long id,String isRelease) {
        //修改公告状态
        boolean update = lambdaUpdate().eq(XfptEnterpriseMode::getId, id).set(XfptEnterpriseMode::getIsRelease, isRelease).update();
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

    @Override
    @Transactional
    public int removeNotice(Long id) {
        boolean b = removeById(id);
        if(b){
            R<Boolean> booleanR = remoteNoticService.removeNotice(id, SecurityConstants.INNER);
            if(booleanR==null || booleanR.getCode()!=200){
                log.error("远程调用删除选举公告错误：{}",booleanR.getMsg());
                throw new ServiceException("远程调用删除选举公告错误");
            }
            return 1;
        }
        return 0;
    }


}
