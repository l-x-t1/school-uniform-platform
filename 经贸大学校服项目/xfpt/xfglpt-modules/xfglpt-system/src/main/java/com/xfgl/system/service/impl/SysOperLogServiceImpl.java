package com.xfgl.system.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.system.mp.MpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xfgl.system.api.domain.SysOperLog;
import com.xfgl.system.mapper.SysOperLogMapper;
import com.xfgl.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService
{
    @Autowired
    private SysOperLogMapper operLogMapper;
    @Autowired
    private MpUtil mpUtil;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     * @return 结果
     */
    @Override
    public int insertOperlog(SysOperLog operLog)
    {
        String mac = getMac(operLog,null);
        operLog.setIntegrity(mac);
        operLog.setOperTime(new Date());
        return operLogMapper.insertOperlog(operLog);
    }

    private String getMac(SysOperLog operLog,String token) {
        String text="";
        //增加完整性
        if(StrUtil.isNotEmpty(operLog.getTitle())){
            text=text+ operLog.getTitle();
        }
        if(StrUtil.isNotEmpty(operLog.getMethod())){
            text=text+ operLog.getMethod();
        }
        if(StrUtil.isNotEmpty(operLog.getRequestMethod())){
            text=text+ operLog.getRequestMethod();
        }
        if(StrUtil.isNotEmpty(operLog.getOperUrl())){
            text=text+ operLog.getOperUrl();
        }
        if(StrUtil.isNotEmpty(operLog.getOperParam())){
            text=text+ operLog.getOperParam();
        }
        if(StrUtil.isEmpty(token)){
           token=mpUtil.getToken();
        }
        String mac = mpUtil.mac(text, token);
        return mac;
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog)
    {

        List<SysOperLog> sysOperLogs = operLogMapper.selectOperLogList(operLog);
        String token = mpUtil.getToken();
        for (SysOperLog sysOperLog : sysOperLogs) {
            if(StrUtil.isNotEmpty(sysOperLog.getIntegrity())){
                String mac = getMac(sysOperLog,token);
                if(!sysOperLog.getIntegrity().equals(mac)){
                    throw new ServiceException("数据被篡改，完整性校验失败,id="+sysOperLog.getOperId());
                }
            }

        }

        return sysOperLogs;
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return operLogMapper.selectOperLogById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        operLogMapper.cleanOperLog();
    }
}
