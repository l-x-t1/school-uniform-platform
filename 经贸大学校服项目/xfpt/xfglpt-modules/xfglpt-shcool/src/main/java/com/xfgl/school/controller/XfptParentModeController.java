package com.xfgl.school.controller;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.ProceConstants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.YesOrNoStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptOrganizationMode;
import com.xfgl.school.domain.XfptQuestionnaireConfig;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import com.xfgl.school.service.*;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.school.domain.XfptParentMode;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 选用组织组织家长投票确定企业模式(二)Controller
 *
 * @author yinet
 * @date 2025-05-09
 */
@Tag(name = "选用组织组织家长投票确定企业模式(二)管理")
@RestController
@RequestMapping("/parent")
@Slf4j
public class XfptParentModeController extends BaseController {
    @Autowired
    private IXfptParentModeService xfptParentModeService;

    @Autowired
    private RemoteNoticService remoteNoticService;
    @Autowired
    private ITeamMeetingService iTeamMeetingService;
    @Autowired
    private IXfptQuestionnaireSurveyService iXfptQuestionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireConfigService iXfptQuestionnaireConfigService;

    /**
     * 查询选用组织组织家长投票确定企业模式(二)列表
     */

    @Operation(summary = "查询选用组织组织家长投票确定企业模式(二)列表")
    @RequiresPermissions("school:parent:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptParentMode xfptParentMode) {
        startPage();
        List<XfptParentMode> list = xfptParentModeService.selectXfptParentModeList(xfptParentMode);
        return getDataTable(list);
    }

    /**
     * 导出选用组织组织家长投票确定企业模式(二)列表
     */
    @Operation(summary = "导出选用组织组织家长投票确定企业模式(二)列表")
    @RequiresPermissions("school:parent:export")
    @Log(title = "选用组织组织家长投票确定企业模式(二)", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptParentMode xfptParentMode) {
        List<XfptParentMode> list = xfptParentModeService.selectXfptParentModeList(xfptParentMode);
        ExcelUtil<XfptParentMode> util = new ExcelUtil<XfptParentMode>(XfptParentMode.class);
        util.exportExcel(response, list, "选用组织组织家长投票确定企业模式(二)数据");
    }

    /**
     * 获取选用组织组织家长投票确定企业模式(二)详细信息
     */
    @Operation(summary = "获取选用组织组织家长投票确定企业模式(二)详细信息")
    @RequiresPermissions("school:parent:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptParentModeService.getById(id));
    }

    /**
     * 新增选用组织组织家长投票确定企业模式(二)
     */
    @Operation(summary = "新增选用组织组织家长投票确定企业模式(二)")
    @RequiresPermissions("school:parent:add")
    @Log(title = "选用组织组织家长投票确定企业模式(二)", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptParentMode xfptParentMode) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        xfptParentMode.setDeptId(dept.getDeptId());
        xfptParentMode.setAncestors(dept.getAncestors());
        //保存调查问卷
        //问卷模版id
        Long quesId = xfptParentMode.getQuesId();
        XfptQuestionnaireConfig xfptQuestionnaireConfig = new XfptQuestionnaireConfig();
        xfptQuestionnaireConfig.setTitle(xfptParentMode.getTitle());
        xfptQuestionnaireConfig.setBeginTime(xfptParentMode.getStartTime());
        xfptQuestionnaireConfig.setEndTime(xfptParentMode.getEndTime());
        xfptQuestionnaireConfig.setCategory("2");
        xfptQuestionnaireConfig.setType(ProceConstants.ZZQDMS_TYPE);
        xfptQuestionnaireConfig.setDeptId(dept.getDeptId());
        xfptQuestionnaireConfig.setAncestors(dept.getAncestors());
        xfptQuestionnaireConfig.setFbStatus(Long.valueOf(NoticeStatus.FB.getCode()));
        boolean save = iXfptQuestionnaireConfigService.save(xfptQuestionnaireConfig);
        if(!save){
            throw new ServiceException("保存问卷失败");
        }
        //保存新增问卷的id
        xfptParentMode.setQuesId(xfptQuestionnaireConfig.getId());
        List<XfptQuestionnaireSurvey> xfptQuestionnaireSurveys = iXfptQuestionnaireSurveyService.lambdaQuery()
                .eq(XfptQuestionnaireSurvey::getQuestionConfigId, quesId)
                .list();
        xfptQuestionnaireSurveys.stream().forEach(e -> {
            e.setQuestionConfigId(xfptQuestionnaireConfig.getId());
            e.setId(null);
        });
        boolean b = iXfptQuestionnaireSurveyService.saveBatch(xfptQuestionnaireSurveys);
        if(!b){
            throw new ServiceException("保存问卷题目失败");
        }
        boolean save1 = xfptParentModeService.save(xfptParentMode);
        if(!save1){
            throw new ServiceException("保存失败");
        }
        //判断是否发布到公告页面
        if(NoticeStatus.FB.getCode().equals(xfptParentMode.getIsRelease())){
            XfptNotice xfptNotice = getXfptNotice(xfptParentMode);
            R<Boolean> save2 = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
            if (save2 == null || save2.getCode() != 200) {
                log.error("远程调用保存公告失败：{}", save2.getMsg());
                throw new ServiceException("远程调用保存公告失败");
            }
        }
        //保存时间轴
        String format = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        int i = iTeamMeetingService.updateTeamMeetingCurrOrder(xfptParentMode.getTeamId(), ProceConstants.JZTP + "#" + format);
        if(i==0){
            log.error("保存时间轴失败：{}",xfptParentMode.toString());
            throw new ServiceException("系统错误");
        }
        return toAjax(save1);
    }

    /**
     * 修改选用组织组织家长投票确定企业模式(二)
     */
    @Operation(summary = "修改选用组织组织家长投票确定企业模式(二)")
    @RequiresPermissions("school:parent:edit")
    @Log(title = "选用组织组织家长投票确定企业模式(二)", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptParentMode xfptParentMode) {
        if(StrUtil.isNotEmpty(xfptParentMode.getIsRelease())){
            remoteNoticService.updateStatus(xfptParentMode.getId(),xfptParentMode.getIsRelease(), SecurityConstants.INNER);
        }
        return toAjax(xfptParentModeService.updateById(xfptParentMode));
    }


    /**
     * 删除选用组织组织家长投票确定企业模式(二)
     */
    @Operation(summary = "删除选用组织组织家长投票确定企业模式(二)")
    @RequiresPermissions("school:parent:remove")
    @Log(title = "选用组织组织家长投票确定企业模式(二)", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptParentModeService.removeByIds(Arrays.asList(ids)));
    }

    private static XfptNotice getXfptNotice(XfptParentMode xfptParentMode) {
        //保存公告信息
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptParentMode.getTitle());
        xfptNotice.setCreateTime(new Date());
        xfptNotice.setType(NoticeTypeStatus.CGGG.getCode());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        xfptNotice.setXgType(PurchaseType.JZXJ.getCode());
        xfptNotice.setReadCount(0L);
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setGgFile(xfptParentMode.getNoticeFile());
        xfptNotice.setFile(xfptParentMode.getOtherFile());
        xfptNotice.setCatagory(Constants.CGGG);
        if(YesOrNoStatus.YES.getCode().equals(xfptParentMode.getIsSamplesPic())){
            xfptNotice.setSamplesPic(xfptParentMode.getSamplesPic());
        }
        xfptNotice.setEnteId(xfptParentMode.getId());
        return xfptNotice;
    }
}
