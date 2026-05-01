package com.xfgl.system.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.system.mp.MpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xfgl.system.api.domain.SysLogininfor;
import com.xfgl.system.mapper.SysLogininforMapper;
import com.xfgl.system.service.ISysLogininforService;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author ruoyi
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService
{

    @Autowired
    private SysLogininforMapper logininforMapper;
    @Autowired
    private MpUtil mpUtil;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public int insertLogininfor(SysLogininfor logininfor)
    {
        String token = mpUtil.getToken();
        String mac = getMac(logininfor,token);
        logininfor.setIntegrity(mac);
        logininfor.setAccessTime(new Date());
        return logininforMapper.insertLogininfor(logininfor);
    }

    private String getMac(SysLogininfor logininfor,String token) {
        String text="";
        if(StrUtil.isNotEmpty(logininfor.getUserName())){
            text=text+ logininfor.getUserName();
        }

        if(StrUtil.isNotEmpty(logininfor.getIpaddr())){
            text=text+ logininfor.getIpaddr();
        }

        if(StrUtil.isNotEmpty(logininfor.getMsg())){
            text=text+ logininfor.getMsg();
        }
        if(StrUtil.isEmpty(token)){
            token=mpUtil.getToken();
        }
        String mac = mpUtil.mac(text, token);
        return mac;
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor)
    {
        List<SysLogininfor> sysLogininfors = logininforMapper.selectLogininforList(logininfor);
        String token = mpUtil.getToken();
        for (SysLogininfor sysLogininfor : sysLogininfors) {
            if(StrUtil.isNotEmpty(sysLogininfor.getIntegrity())){
                String mac = getMac(sysLogininfor,token);
                if(!sysLogininfor.getIntegrity().equals(mac)){
                    throw new ServiceException("数据被篡改，完整性校验失败,id="+sysLogininfor.getInfoId());
                }
            }
        }
        return sysLogininfors;
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds)
    {
        return logininforMapper.deleteLogininforByIds(infoIds);
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        logininforMapper.cleanLogininfor();
    }
}
