package com.xfgl.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.domain.vo.ReleaseSupplementVo;
import com.xfgl.system.service.ISysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import com.xfgl.system.mapper.XfptNoticeMapper;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.service.IXfptNoticeService;

/**
 * 首页公告Service业务层处理
 *
 * @author yinet
 * @date 2025-04-23
 */
@Service
public class XfptNoticeServiceImpl extends ServiceImpl<XfptNoticeMapper, XfptNotice> implements IXfptNoticeService {

    @Autowired
    private XfptNoticeMapper xfptNoticeMapper;

    @Autowired
    private ISysDeptService iSysDeptService;

    /**
     * 查询首页公告列表
     *
     * @param xfptNotice 首页公告
     * @return 首页公告
     */
    @Override
    public List<XfptNotice> selectXfptNoticeList(XfptNotice xfptNotice)
    {
        return xfptNoticeMapper.selectXfptNoticeList(xfptNotice);
    }

    @Override
    public List<XfptNotice> appList(XfptNotice xfptNotice) {


        return xfptNoticeMapper.appList(xfptNotice);
    }

    @Override
    public AjaxResult releaseSupplement(ReleaseSupplementVo releaseSupplementVo) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        XfptNotice xfptNotice=new XfptNotice();
        xfptNotice.setTitle(releaseSupplementVo.getTitle());
        xfptNotice.setGgFile(releaseSupplementVo.getFile());
        xfptNotice.setCatagory(releaseSupplementVo.getCatagory());
        SysDept sysDept=new SysDept();
        sysDept.setDeptId(dept.getParentId());
        List<SysDept> list = iSysDeptService.selectDeptList2(sysDept);
        SysDept sysDept1 = list.get(0);
        xfptNotice.setArea(String.valueOf(sysDept1.getDeptId()));
        xfptNotice.setAreaname(sysDept1.getDeptName());
        xfptNotice.setDeptname(dept.getDeptName());
        xfptNotice.setIsBc("1");
        xfptNotice.setCreateBy(SecurityUtils.getLoginUser().getSysUser().getNickName());
        xfptNotice.setRemark(String.valueOf(dept.getDeptId()));
        xfptNotice.setXgType(releaseSupplementVo.getType());
        xfptNotice.setEnteId(releaseSupplementVo.getId());
        xfptNotice.setType(releaseSupplementVo.getType());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        boolean save = save(xfptNotice);
        if(!save){
            throw new RuntimeException("发布失败");
        }
        return AjaxResult.success("发布成功");
    }

    @Override
    public AjaxResult selectReleaseSupplement(String id,String type,String catagory) {
        List<XfptNotice> list = lambdaQuery()
                .eq(XfptNotice::getEnteId, id)
                .eq(XfptNotice::getCatagory, catagory)
                .eq(XfptNotice::getType, type)
                .eq(XfptNotice::getIsBc, "1")
                .orderByDesc(XfptNotice::getCreateTime)
                .list();

        return AjaxResult.success(list);
    }


}
