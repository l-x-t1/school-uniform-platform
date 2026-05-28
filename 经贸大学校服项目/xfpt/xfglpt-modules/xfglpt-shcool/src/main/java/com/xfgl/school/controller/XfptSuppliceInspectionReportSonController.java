package com.xfgl.school.controller;

import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.XfptSuppliceInspectionReport;
import com.xfgl.school.domain.XfptSuppliceContractSon;
import com.xfgl.school.domain.XfptSuppliceInspectionReportSon;
import com.xfgl.school.service.IXfptSuppliceInspectionReportService;
import com.xfgl.school.service.IXfptSuppliceInspectionReportSonService;
import com.xfgl.school.support.SupplierAccessSupport;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 学校与供应商检验报告从Controller
 *
 * @author yinet
 * @date 2025-04-22
 */
@Tag(name = "学校与供应商检验报告从管理")
@RestController
@RequestMapping("/suppliceInspectionReportSon")
public class XfptSuppliceInspectionReportSonController extends BaseController {
    @Autowired
    private IXfptSuppliceInspectionReportSonService xfptSuppliceInspectionReportSonService;
    @Autowired
    private IXfptSuppliceInspectionReportService xfptSuppliceInspectionReportService;
    @Autowired
    private SupplierAccessSupport supplierAccessSupport;
    @Value("${fileUrl:}")
    public String fileUrl;
    /**
     * 查询学校与供应商检验报告从列表
     */

    @Operation(summary = "查询学校与供应商检验报告从列表")
    @RequiresPermissions("school:zjbg:query")
    @GetMapping("/list")
    public TableDataInfo list(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon) {
        ensureReportAccessible(xfptSuppliceInspectionReportSon == null ? null : xfptSuppliceInspectionReportSon.getReportId());
        startPage();
        List<XfptSuppliceInspectionReportSon> list = xfptSuppliceInspectionReportSonService.selectXfptSuppliceInspectionReportSonList(xfptSuppliceInspectionReportSon);
        return getDataTable(list);
    }

    /**
     * 导出学校与供应商检验报告从列表
     */
    @Operation(summary = "导出学校与供应商检验报告从列表")
    @RequiresPermissions("school:zjbg:query")
    @Log(title = "学校与供应商检验报告从", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon) {
        ensureReportAccessible(xfptSuppliceInspectionReportSon == null ? null : xfptSuppliceInspectionReportSon.getReportId());
        List<XfptSuppliceInspectionReportSon> list = xfptSuppliceInspectionReportSonService.selectXfptSuppliceInspectionReportSonList(xfptSuppliceInspectionReportSon);
        ExcelUtil<XfptSuppliceInspectionReportSon> util = new ExcelUtil<XfptSuppliceInspectionReportSon>(XfptSuppliceInspectionReportSon.class);
        util.exportExcel(response, list, "学校与供应商检验报告从数据");
    }

    /**
     * 获取学校与供应商检验报告从详细信息
     */
    @Operation(summary = "获取学校与供应商检验报告从详细信息")
    @RequiresPermissions("school:zjbg:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptSuppliceInspectionReportSon reportSon = xfptSuppliceInspectionReportSonService.getById(id);
        ensureReportAccessible(reportSon == null ? null : reportSon.getReportId());
        return success(reportSon);
    }

    /**
     * 新增学校与供应商检验报告从
     */
    @Operation(summary = "新增学校与供应商检验报告从")
    @RequiresPermissions("school:zjbg:upload")
    @Log(title = "学校与供应商检验报告从", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon) {
        ensureReportAccessible(xfptSuppliceInspectionReportSon == null ? null : xfptSuppliceInspectionReportSon.getReportId());
        //查询是否有数据
        Long count = xfptSuppliceInspectionReportSonService.lambdaQuery().eq(XfptSuppliceInspectionReportSon::getReportId
                , xfptSuppliceInspectionReportSon.getReportId()).count();
        xfptSuppliceInspectionReportSon.setType(String.valueOf(count+1));
        return toAjax(xfptSuppliceInspectionReportSonService.save(xfptSuppliceInspectionReportSon));
    }

    /**
     * 修改学校与供应商检验报告从
     */
    @Operation(summary = "修改学校与供应商检验报告从")
    @RequiresPermissions("school:zjbg:upload")
    @Log(title = "学校与供应商检验报告从", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon) {
        String reportId = xfptSuppliceInspectionReportSon == null ? null : xfptSuppliceInspectionReportSon.getReportId();
        if (reportId == null && xfptSuppliceInspectionReportSon != null && xfptSuppliceInspectionReportSon.getId() != null) {
            XfptSuppliceInspectionReportSon existing = xfptSuppliceInspectionReportSonService.getById(xfptSuppliceInspectionReportSon.getId());
            reportId = existing == null ? null : existing.getReportId();
        }
        ensureReportAccessible(reportId);
        return toAjax(xfptSuppliceInspectionReportSonService.updateById(xfptSuppliceInspectionReportSon));
    }


    /**
     * 删除学校与供应商检验报告从
     */
    @Operation(summary = "删除学校与供应商检验报告从")
//    @RequiresPermissions("system:son:remove")
    @Log(title = "学校与供应商检验报告从", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSuppliceInspectionReportSonService.removeByIds(Arrays.asList(ids)));
    }



    @PostMapping("/downloadZip")
    public void downloadZip(XfptSuppliceInspectionReportSon xfptSuppliceInspectionReportSon, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        String deptName = SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName();
        String[] urls = xfptSuppliceInspectionReportSon.getUrl().split("&&&");
        String[] fileNames = xfptSuppliceInspectionReportSon.getFileName().split("&&&");
        String zipFilePath =deptName+"_"+xfptSuppliceInspectionReportSon.getInspectionReportTitle()+ ".zip";
        // 设置响应头
        response.setContentType("application/zip; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(zipFilePath,"UTF-8"));

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; i < urls.length; i++) {
                try {
                    URL url = new URL(fileUrl+urls[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    // 检查响应状态码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 创建临时文件
                        File tempFile = File.createTempFile("temp", null);
                        try (BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                             FileOutputStream fos = new FileOutputStream(tempFile);
                             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = bis.read(buffer)) != -1) {
                                bos.write(buffer, 0, bytesRead);
                            }
                            bos.flush();
                        }

                        // 对文件名进行编码处理
                        String fileName = URLEncoder.encode(fileNames[i], "UTF-8");
                        ZipEntry zipEntry = new ZipEntry(fileName);
                        zipOut.putNextEntry(zipEntry);

                        try (FileInputStream fis = new FileInputStream(tempFile);
                             BufferedInputStream bis = new BufferedInputStream(fis)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = bis.read(buffer)) != -1) {
                                zipOut.write(buffer, 0, bytesRead);
                            }
                        }
                        zipOut.closeEntry();

                        // 删除临时文件
                        tempFile.delete();
                    } else {
                        System.err.println("Error downloading file from URL: " + fileUrl + ", Response code: " + responseCode);
                    }
                } catch (IOException e) {
                    System.err.println("Error downloading file from URL: " + fileUrl);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating zip file");
            e.printStackTrace();
        }
    }

    private void ensureReportAccessible(String reportId) {
        SysUser currentUser = getCurrentUser();
        if (!supplierAccessSupport.isSupplierUser(currentUser)) {
            return;
        }
        if (reportId == null) {
            return;
        }
        XfptSuppliceInspectionReport parentReport = xfptSuppliceInspectionReportService.getById(reportId);
        Long supplierId = supplierAccessSupport.resolveSupplierId(currentUser);
        if (supplierId != null && parentReport != null && !String.valueOf(supplierId).equals(parentReport.getSupplierId())) {
            throw new com.xfgl.common.core.exception.ServiceException("当前操作没有权限");
        }
    }

    private SysUser getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return loginUser == null ? null : loginUser.getSysUser();
    }


}
