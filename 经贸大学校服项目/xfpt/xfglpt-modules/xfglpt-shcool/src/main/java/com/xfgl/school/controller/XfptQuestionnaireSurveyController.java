package com.xfgl.school.controller;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptQuestionnaireResult;
import com.xfgl.school.domain.vo.DeptMesVo;
import com.xfgl.school.domain.vo.LableValue;
import com.xfgl.school.service.IXfptQuestionnaireConfigService;
import com.xfgl.school.service.IXfptQuestionnaireResultService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import com.xfgl.school.service.IXfptQuestionnaireSurveyService;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.xfgl.common.core.web.page.TableDataInfo;

/**
 * 问卷调查库Controller
 *
 * @author yinet
 * @date 2025-04-24
 */
@Tag(name = "问卷调查库管理")
@RestController
@RequestMapping("/questionnaireSurvey")
public class XfptQuestionnaireSurveyController extends BaseController {
    @Autowired
    private IXfptQuestionnaireSurveyService xfptQuestionnaireSurveyService;
    @Autowired
    private IXfptQuestionnaireConfigService iXfptQuestionnaireConfigService;
    @Autowired
    private IXfptQuestionnaireResultService iXfptQuestionnaireResultService;


    /**
     * 查询问卷调查库列表
     */

    @Operation(summary = "查询问卷调查库列表")
//@RequiresPermissions("school:survey:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptQuestionnaireSurvey xfptQuestionnaireSurvey) {
        startPage();
        List<XfptQuestionnaireSurvey> list = xfptQuestionnaireSurveyService.selectXfptQuestionnaireSurveyList(xfptQuestionnaireSurvey);
        return getDataTable(list);
    }

    /**
     * 导出问卷调查库列表
     */
    @Operation(summary = "导出问卷调查库列表")
//    @RequiresPermissions("school:survey:export")
    @Log(title = "问卷调查库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptQuestionnaireSurvey xfptQuestionnaireSurvey) {
        List<XfptQuestionnaireSurvey> list = xfptQuestionnaireSurveyService.selectXfptQuestionnaireSurveyList(xfptQuestionnaireSurvey);
        ExcelUtil<XfptQuestionnaireSurvey> util = new ExcelUtil<XfptQuestionnaireSurvey>(XfptQuestionnaireSurvey.class);
        util.exportExcel(response, list, "问卷调查库数据");
    }

    /**
     * 获取问卷调查库详细信息
     */
    @Operation(summary = "获取问卷调查库详细信息")
//    @RequiresPermissions("school:survey:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(xfptQuestionnaireSurveyService.getById(id));
    }

    /**
     * 新增问卷调查库
     */
    @Operation(summary = "新增问卷调查库")
//    @RequiresPermissions("school:survey:add")
    @Log(title = "问卷调查库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptQuestionnaireSurvey xfptQuestionnaireSurvey) {
        xfptQuestionnaireSurvey.setSurveyContent(null);
        xfptQuestionnaireSurvey.setSurveyResult(null);
        System.err.println("xfptQuestionnaireSurvey:" + xfptQuestionnaireSurvey);
        return toAjax(xfptQuestionnaireSurveyService.save(xfptQuestionnaireSurvey));
    }

    /**
     * 修改问卷调查库
     */
    @Operation(summary = "修改问卷调查库")
//    @RequiresPermissions("school:survey:edit")
    @Log(title = "问卷调查库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptQuestionnaireSurvey xfptQuestionnaireSurvey) {
        xfptQuestionnaireSurvey.setCreateBy(SecurityUtils.getLoginUser().getSysUser().getUserName());
        xfptQuestionnaireSurvey.setCreateTime(new Date());
        return toAjax(xfptQuestionnaireSurveyService.updateById(xfptQuestionnaireSurvey));
    }


    /**
     * 删除问卷调查库
     */
    @Operation(summary = "删除问卷调查库")
//    @RequiresPermissions("school:survey:remove")
    @Log(title = "问卷调查库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptQuestionnaireSurveyService.removeByIds(Arrays.asList(ids)));
    }

    @GetMapping("/reeee")
    public void test(HttpServletResponse response) {
        try {
            exportDynamicExcel(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询表头信息
     *
     * @param questionId 问卷iD
     * @return
     */
    @GetMapping("/headingList")
    public AjaxResult headingList(Long questionId) {
        List<XfptQuestionnaireSurvey> list = xfptQuestionnaireSurveyService.lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, questionId).list();
        if(CollUtil.isEmpty(list)){
            return AjaxResult.error("没有数据");
        }
        List<LableValue> lableValues=new ArrayList<>();
        List<String> result=new ArrayList<>();
        LableValue lableValue=new LableValue();
        lableValue.setLabel("姓名");
        lableValue.setValue("name");
        lableValues.add(lableValue);
        LableValue lableValue2=new LableValue();
        lableValue2.setLabel("IP");
        lableValue2.setValue("ip");
        lableValues.add(lableValue2);
        List<String> collect = list.stream().map(item -> item.getSurveyContent()).collect(Collectors.toList());
        Map<Long, List<XfptQuestionnaireSurvey>> collect1 = list.stream().collect(Collectors.groupingBy(XfptQuestionnaireSurvey::getId));
        //int flag=1;
        //for (String s : collect) {
        //    LableValue lableValue3=new LableValue();
        //    lableValue3.setLabel(s);
        //    lableValue3.setValue("value"+flag);
        //    lableValues.add(lableValue3);
        //    flag++;
        //}
        for (Long l : collect1.keySet()) {
            LableValue lableValue3=new LableValue();
            lableValue3.setLabel(collect1.get(l).get(0).getSurveyContent());
            lableValue3.setValue("value-"+l);
            lableValues.add(lableValue3);
        }

        LableValue lableValue4=new LableValue();
        lableValue4.setLabel("提交时间");
        lableValue4.setValue("createTime");
        lableValues.add(lableValue4);
        LableValue lableValue5=new LableValue();
        lableValue5.setLabel("签名");
        lableValue5.setValue("signature");
        lableValues.add(lableValue5);

        return AjaxResult.success(lableValues);
    }

    @PostMapping("/exportResultExcel")
    public void exportResultExcel(HttpServletResponse response,Long questionId) throws IOException {
        // 设置响应头
        String fileName = "调查结果";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                java.net.URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("问卷调查结果");
        //创建表头
        //查询选项
        List<XfptQuestionnaireSurvey> list = xfptQuestionnaireSurveyService.lambdaQuery().eq(XfptQuestionnaireSurvey::getQuestionConfigId, questionId).list();
        if(CollUtil.isEmpty(list)){
            throw new RuntimeException("没有数据");
        }
        Row headerRow = sheet.createRow(0);
        int cellIndex = 0;
        // 创建表头
        // 添加问题列
        CellStyle style = workbook.createCellStyle();
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // 添加固定表头 - 序号列
        Cell indexCell = headerRow.createCell(cellIndex++);
        indexCell.setCellValue("学生姓名");
        indexCell.setCellStyle(style);
        // 添加固定表头 - 序号列
        Cell indexCell2 = headerRow.createCell(cellIndex++);
        indexCell2.setCellValue("IP");
        indexCell2.setCellStyle(style);

        // 设置背景
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i = 0; i < list.size(); i++) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(list.get(i).getSurveyContent());
            cell.setCellStyle(style);
        }
        // 添加固定表头 - 序号列
        Cell indexCell3 = headerRow.createCell(cellIndex++);
        indexCell3.setCellValue("提交时间");
        indexCell3.setCellStyle(style);
        //填充数据
        List<XfptQuestionnaireResult> list1 = iXfptQuestionnaireResultService.lambdaQuery().eq(XfptQuestionnaireResult::getQuestionnaireSurveyId, questionId).list();
        if(CollUtil.isEmpty(list1)){
            throw new RuntimeException("没有数据");
        }
        Map<String, List<XfptQuestionnaireResult>> collect = list1.stream().collect(Collectors.groupingBy(XfptQuestionnaireResult::getImage));
        Set<String> strings = collect.keySet();
        int i = 0;
        for (String string : strings) {
            i++;
            List<XfptQuestionnaireResult> xfptQuestionnaireResults = collect.get(string);
            if(CollUtil.isEmpty(xfptQuestionnaireResults)){
                continue;
            }
            Map<String, List<XfptQuestionnaireResult>> collect1 = xfptQuestionnaireResults.stream().collect(Collectors.groupingBy(XfptQuestionnaireResult::getSurveyId));
            Row row = sheet.createRow(i);
            int cellIndex2 = 0;
            // 添加序号
            row.createCell(cellIndex2++).setCellValue(xfptQuestionnaireResults.get(0).getName());
            // 添加序号
            row.createCell(cellIndex2++).setCellValue(xfptQuestionnaireResults.get(0).getIpAddress());
            for (XfptQuestionnaireSurvey xfptQuestionnaireSurvey : list) {
                List<XfptQuestionnaireResult> xfptQuestionnaireResults1 = collect1.get(xfptQuestionnaireSurvey.getId().toString());

                if(CollUtil.isNotEmpty(xfptQuestionnaireResults1)){
                    row.createCell(cellIndex2++).setCellValue(xfptQuestionnaireResults1.get(0).getQuestionnaireResult());
                }else {
                    row.createCell(cellIndex2++).setCellValue("");
                }
            }
            // 添加序号
            row.createCell(cellIndex2++).setCellValue(DateUtil.format(xfptQuestionnaireResults.get(0).getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        // 自适应列宽
        for (int columnIndex = 0; columnIndex < cellIndex; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
            // 为了防止列宽过窄，设置一个最小宽度
            int columnWidth = sheet.getColumnWidth(columnIndex);
            if (columnWidth < 20 * 256) { // 最小宽度约20个字符
                sheet.setColumnWidth(columnIndex, 20 * 256);
            } else {
                // 增加一点宽度以确保内容不会太拥挤
                sheet.setColumnWidth(columnIndex, columnWidth + 10 * 256);
            }
        }
        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 动态生成Excel文件
     */
    private void exportDynamicExcel(HttpServletResponse response) throws IOException {

        // 设置响应头
        String fileName = "调查结果";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                java.net.URLEncoder.encode(fileName, "UTF-8") + ".xlsx");

        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("问卷调查结果");

        Row headerRow = sheet.createRow(0);
        int cellIndex = 0;
        // 创建表头
        // 添加问题列
        CellStyle style = workbook.createCellStyle();
        // 设置边框
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // 设置北京
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i = 0; i < 10; i++) {
            Cell cell = headerRow.createCell(cellIndex++);
            cell.setCellValue(i + "测试");
            cell.setCellStyle(style);
        }
        // 填充数据
        for (int i = 0; i < 1; i++) {
            Row row = sheet.createRow(i + 1);
            int cellIndex2 = 0;
            for (int i2 = 0; i2 < 10; i2++) {

                row.createCell(cellIndex2++).setCellValue(i2 + "答案");
            }

        }
        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
