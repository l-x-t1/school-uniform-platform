package com.xfgl.school.controller;

import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.vo.XfptDeliveryStatisticsVo;
import com.xfgl.school.service.IXfptDeliveryStatisticsService;
import com.xfgl.school.support.SchoolAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 发货单统计Controller
 */
@Tag(name = "发货单统计管理")
@RestController
@RequestMapping("/deliveryStatistics")
public class XfptDeliveryStatisticsController extends BaseController {

    @Autowired
    private IXfptDeliveryStatisticsService xfptDeliveryStatisticsService;
    @Autowired
    private SchoolAccessSupport schoolAccessSupport;

    /**
     * 查询发货单统计列表
     */
    @Operation(summary = "查询发货单统计列表")
    @RequiresPermissions("school:tgfh:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptDeliveryStatisticsVo query) {
        if (query == null) {
            query = new XfptDeliveryStatisticsVo();
        }
        applySchoolScope(query);
        startPage();
        List<XfptDeliveryStatisticsVo> list = xfptDeliveryStatisticsService.selectXfptDeliveryStatisticsList(query);
        return getDataTable(list);
    }

    private void applySchoolScope(XfptDeliveryStatisticsVo query) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser == null ? null : loginUser.getSysUser();
        if (sysUser == null) {
            throw new ServiceException("当前登录用户信息不存在");
        }
        if (schoolAccessSupport.isSchoolUser(sysUser) && !SecurityUtils.isAdmin(sysUser.getUserId())) {
            XfptSchoolMessage currentSchool = schoolAccessSupport.resolveCurrentSchool(sysUser);
            if (currentSchool == null) {
                throw new ServiceException("当前登录学校信息不存在");
            }
            query.setSchoolId(currentSchool.getId());
        }
    }
}
