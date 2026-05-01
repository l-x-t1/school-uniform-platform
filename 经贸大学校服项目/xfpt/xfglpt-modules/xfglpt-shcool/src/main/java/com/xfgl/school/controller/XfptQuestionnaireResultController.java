package com.xfgl.school.controller;

import java.util.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.xfgl.common.core.utils.ip.IpUtils;
import com.xfgl.school.domain.XfptQuestionnaireSignature;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import com.xfgl.school.domain.vo.QmListVo;
import com.xfgl.school.domain.vo.QuestionVo;
import com.xfgl.school.service.IXfptQuestionnaireConfigService;
import com.xfgl.school.service.IXfptQuestionnaireSignatureService;
import com.xfgl.school.service.IXfptQuestionnaireSurveyService;
import org.apache.poi.ss.formula.functions.T;
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
import com.xfgl.school.domain.XfptQuestionnaireResult;
import com.xfgl.school.service.IXfptQuestionnaireResultService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 问卷调查结果Controller
 *
 * @author yinet
 * @date 2025-05-07
 */
@Tag(name = "问卷调查结果管理")
@RestController
@RequestMapping("/questionnaireResult")
public class XfptQuestionnaireResultController extends BaseController {
    @Autowired
    private IXfptQuestionnaireResultService xfptQuestionnaireResultService;

    @Autowired
    private IXfptQuestionnaireConfigService xfptQuestionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireSignatureService iXfptQuestionnaireSignatureService;

    /**
     * 查询问卷调查结果列表
     */

    @Operation(summary = "查询问卷调查结果列表")
//@RequiresPermissions("school:questionnaireResult:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptQuestionnaireResult xfptQuestionnaireResult) {
        startPage();
        List<XfptQuestionnaireResult> list = xfptQuestionnaireResultService.selectXfptQuestionnaireResultList(xfptQuestionnaireResult);
        return getDataTable(list);
    }

    /**
     * 导出问卷调查结果列表
     */
    @Operation(summary = "导出问卷调查结果列表")
//    @RequiresPermissions("school:questionnaireResult:export")
    @Log(title = "问卷调查结果", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptQuestionnaireResult xfptQuestionnaireResult) {
        List<XfptQuestionnaireResult> list = xfptQuestionnaireResultService.selectXfptQuestionnaireResultList(xfptQuestionnaireResult);
        ExcelUtil<XfptQuestionnaireResult> util = new ExcelUtil<XfptQuestionnaireResult>(XfptQuestionnaireResult.class);
        util.exportExcel(response, list, "问卷调查结果数据");
    }

    /**
     * 获取问卷调查结果详细信息
     */
    @Operation(summary = "获取问卷调查结果详细信息")
//    @RequiresPermissions("school:questionnaireResult:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptQuestionnaireResultService.getById(id));
    }

    /**
     * 新增问卷调查结果
     */
    @Operation(summary = "新增问卷调查结果")
//    @RequiresPermissions("school:questionnaireResult:add")
    @Log(title = "问卷调查结果", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody QuestionVo questionVo, HttpServletRequest request) {
        if (questionVo.getXfptQuestionnaireResult() == null) {
            return AjaxResult.warn("参数不能为空");
        }

        String ipAddress = IpUtils.getIpAddr();
        for (XfptQuestionnaireResult questionnaireResult : questionVo.getXfptQuestionnaireResult()) {
            questionnaireResult.setDelFlag("0");
            questionnaireResult.setIpAddress(ipAddress);
            questionnaireResult.setImage(questionVo.getSignature());
        }
        //添加手写签名
        if (questionVo.getSignature() != null) {
            XfptQuestionnaireSignature xfptQuestionnaireSignature = new XfptQuestionnaireSignature();
            xfptQuestionnaireSignature.setImage(questionVo.getSignature());
            xfptQuestionnaireSignature.setQuestionnaireSurveyId(Long.valueOf(questionVo.getXfptQuestionnaireResult().get(0).getQuestionnaireSurveyId()));
            boolean save = iXfptQuestionnaireSignatureService.save(xfptQuestionnaireSignature);
            if (!save) {
                throw new RuntimeException("添加签名失败");
            }

        }
        boolean b = xfptQuestionnaireResultService.saveBatch(questionVo.getXfptQuestionnaireResult());
        if (!b) {
            throw new RuntimeException("添加失败");
        }
        return toAjax(b);
    }

    /**
     * 修改问卷调查结果
     */
    @Operation(summary = "修改问卷调查结果")
//    @RequiresPermissions("school:questionnaireResult:edit")
    @Log(title = "问卷调查结果", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptQuestionnaireResult xfptQuestionnaireResult) {
        return toAjax(xfptQuestionnaireResultService.updateById(xfptQuestionnaireResult));
    }


    /**
     * 删除问卷调查结果
     */
    @Operation(summary = "删除问卷调查结果")
//    @RequiresPermissions("school:questionnaireResult:remove")
    @Log(title = "问卷调查结果", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptQuestionnaireResultService.removeByIds(Arrays.asList(ids)));
    }

    @PostMapping("/selIpAddress")
    public AjaxResult selIpAddress(String id, HttpServletRequest request) {
        String ipAddress = IpUtils.getIpAddr();
        System.err.println("ipAddress:" + ipAddress);
        System.err.println("id:" + id);
        List<XfptQuestionnaireResult> lists = xfptQuestionnaireResultService.lambdaQuery()
                .eq(XfptQuestionnaireResult::getQuestionnaireSurveyId, id)
                .eq(XfptQuestionnaireResult::getIpAddress, ipAddress)
                .list();
        return success(xfptQuestionnaireConfigService.getById(id));
//        if (lists.isEmpty()) {

//        } else {
//            return error("您已提交过，不可再次调查问卷！");
//        }
    }

    @Autowired
    private IXfptQuestionnaireSurveyService iXfptQuestionnaireSurveyService;

    @GetMapping("/signatureList")
    public TableDataInfo signatureList(Long questId, Long pageNum, Long pageSize) {
        List<XfptQuestionnaireSurvey> list1 = iXfptQuestionnaireSurveyService.lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, questId).list();
        if (CollUtil.isEmpty(list1)) {
            return getDataTable(new ArrayList<>());
        }
        List<Long> collect = list1.stream().map(XfptQuestionnaireSurvey::getId).collect(Collectors.toList());
        List<HashMap<String, Object>> result = new ArrayList<>();
        List<XfptQuestionnaireResult> list = xfptQuestionnaireResultService.lambdaQuery()
                .eq(XfptQuestionnaireResult::getQuestionnaireSurveyId, questId).orderByDesc(XfptQuestionnaireResult::getCreateTime).list();
        Map<String, List<XfptQuestionnaireResult>> collect1 = list.stream().collect(Collectors.groupingBy(XfptQuestionnaireResult::getImage));

        for (String s : collect1.keySet()) {
            int flag = 1;
            List<XfptQuestionnaireResult> xfptQuestionnaireResults = collect1.get(s)
                    .stream().sorted(Comparator.comparing(XfptQuestionnaireResult::getSurveyId)).collect(Collectors.toList());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", xfptQuestionnaireResults.get(0).getName());
            hashMap.put("ip", xfptQuestionnaireResults.get(0).getIpAddress());

            for (XfptQuestionnaireResult xfptQuestionnaireResult : xfptQuestionnaireResults) {

                if (collect.contains(Long.valueOf(xfptQuestionnaireResult.getSurveyId()))) {
                    hashMap.put("value-" + xfptQuestionnaireResult.getSurveyId(), xfptQuestionnaireResult.getQuestionnaireResult());
                } else {
                    hashMap.put("value-" + xfptQuestionnaireResult.getSurveyId(), "");
                }


                flag++;
            }
            hashMap.put("createTime", DateUtil.format(xfptQuestionnaireResults.get(0).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            hashMap.put("signature", xfptQuestionnaireResults.get(0).getImage());
            result.add(hashMap);
        }
        result = result.stream().skip((pageNum - 1) * pageSize) // 跳过前面无用的记录
                .limit(pageSize) // 获取指定记录数
                .collect(Collectors.toList());
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(result);
        Long count = iXfptQuestionnaireSignatureService.lambdaQuery().eq(XfptQuestionnaireSignature::getQuestionnaireSurveyId, questId).count();
        rspData.setTotal(count);

        return rspData;
    }

    /**
     * 列表对象去重
     */
    public <K, T> Predicate<K> distinctPredicate(Function<K, T> function) {
        // 因为stream流是多线程操作所以需要使用线程安全的ConcurrentHashMap
        ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();
        return t -> null == map.putIfAbsent(function.apply(t), true);
    }

}
