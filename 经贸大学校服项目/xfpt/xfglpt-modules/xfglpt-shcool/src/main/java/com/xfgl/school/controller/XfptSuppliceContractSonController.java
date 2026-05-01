package com.xfgl.school.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.constant.Constants;
import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.core.enums.NoticeTypeStatus;
import com.xfgl.common.core.enums.PurchaseType;
import com.xfgl.common.core.enums.YesOrNoStatus;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.school.domain.TeamMeetingSon;
import com.xfgl.school.domain.XfptProcSummary;
import com.xfgl.school.domain.XfptSuppliceContract;
import com.xfgl.school.domain.XfptSuppliceContractSon;
import com.xfgl.school.mp.MpUtil;
import com.xfgl.school.service.ITeamMeetingSonService;
import com.xfgl.school.service.IXfptProcSummaryService;
import com.xfgl.school.service.IXfptSuppliceContractService;
import com.xfgl.school.service.IXfptSuppliceContractSonService;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 学校与供应商采购合同从Controller
 *
 * @author yinet
 * @date 2025-04-22
 */
@Tag(name = "学校与供应商采购合同从管理")
@RestController
@RequestMapping("/suppliceContractSon")
@Slf4j
public class XfptSuppliceContractSonController extends BaseController {
    @Autowired
    private IXfptSuppliceContractSonService xfptSuppliceContractSonService;
    @Autowired
    private IXfptSuppliceContractService iXfptSuppliceContractService;

    @Autowired
    private IXfptProcSummaryService iXfptProcSummaryService;
    @Autowired
    private ITeamMeetingSonService iTeamMeetingSonService;

    @Value("${fileUrl}")
    public String fileUrl;
    @Autowired
    private MpUtil mpUtil;
    /**
     * 查询学校与供应商采购合同从列表
     */

    @Operation(summary = "查询学校与供应商采购合同从列表")
//    @RequiresPermissions("system:son:list")
    @GetMapping("/list")
    public TableDataInfo list(XfptSuppliceContractSon xfptSuppliceContractSon) {
        startPage();
        List<XfptSuppliceContractSon> list = xfptSuppliceContractSonService.selectXfptSuppliceContractSonList(xfptSuppliceContractSon);

        XfptSuppliceContract xfptSuppliceContract = iXfptSuppliceContractService.getById(xfptSuppliceContractSon.getSuppliceContractId());
        for (XfptSuppliceContractSon suppliceContractSon : list) {
            suppliceContractSon.setFzrdh(mpUtil.decrypt(suppliceContractSon.getFzrdh(), mpUtil.getToken()));
            //查询子表数据
            List<TeamMeetingSon> sonDataList = iTeamMeetingSonService.selectTeamMeetingSonList(TeamMeetingSon.builder().teamMeetingId(Long.parseLong(xfptSuppliceContract.getTeamMeetingId())).build());
            suppliceContractSon.setTeamMeetingSonList(sonDataList);
            if(StrUtil.isNotEmpty(suppliceContractSon.getIntegrity())){
                String mac = mpUtil.mac(suppliceContractSon.getUrl());
                if(!suppliceContractSon.getIntegrity().equals(mac)){
                    throw new ServiceException("完整性校验失败，数据被篡改");
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 导出学校与供应商采购合同从列表
     */
    @Operation(summary = "导出学校与供应商采购合同从列表")
//    @RequiresPermissions("system:son:export")
    @Log(title = "学校与供应商采购合同从", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfptSuppliceContractSon xfptSuppliceContractSon) {
        List<XfptSuppliceContractSon> list = xfptSuppliceContractSonService.selectXfptSuppliceContractSonList(xfptSuppliceContractSon);
        ExcelUtil<XfptSuppliceContractSon> util = new ExcelUtil<XfptSuppliceContractSon>(XfptSuppliceContractSon.class);
        util.exportExcel(response, list, "学校与供应商采购合同从数据");
    }

    /**
     * 获取学校与供应商采购合同从详细信息
     */
    @Operation(summary = "获取学校与供应商采购合同从详细信息")
//    @RequiresPermissions("system:son:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        XfptSuppliceContractSon byId = xfptSuppliceContractSonService.getById(id);
        return success(xfptSuppliceContractSonService.getById(id));
    }
    @Autowired
    private RemoteDeptService remoteDeptService;
    @Autowired
    private RemoteNoticService remoteNoticService;


    /**
     * 新增学校与供应商采购合同从
     */
    @Operation(summary = "新增学校与供应商采购合同从")
//    @RequiresPermissions("system:son:add")
    @Log(title = "学校与供应商采购合同从", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfptSuppliceContractSon xfptSuppliceContractSon) {
        SysDept dept = SecurityUtils.getLoginUser().getSysUser().getDept();
        XfptSuppliceContract xfptSuppliceContract = iXfptSuppliceContractService.getById(xfptSuppliceContractSon.getSuppliceContractId());
        if(xfptSuppliceContract==null){
            return AjaxResult.warn("合同信息不存在");
        }
        //填充采购信息报备数据
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(Long.valueOf(xfptSuppliceContract.getTeamMeetingId()));
        xfptProcSummary.setCount(Long.valueOf(xfptSuppliceContractSon.getCount()));
        xfptProcSummary.setDeptId( dept.getDeptId());
        xfptProcSummary.setAncestors(dept.getAncestors());
        xfptProcSummary.setMoney( Math.round(xfptSuppliceContractSon.getZj()));
        xfptProcSummary.setDj(xfptSuppliceContractSon.getDj());
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptSuppliceContractSon.toString());
            throw new ServiceException("系统错误");
        }
        //增加公告发布
        XfptNotice xfptNotice = new XfptNotice();
        xfptNotice.setTitle(xfptSuppliceContractSon.getContractName());
        xfptNotice.setGgFile(xfptSuppliceContractSon.getUrl());
        xfptNotice.setType(NoticeTypeStatus.CJGG.getCode());
        xfptNotice.setStatus(NoticeStatus.FB.getCode());
        //生成30到100随机数
        int i = RandomUtil.randomInt(30, 100);
        xfptNotice.setReadCount(Long.valueOf(i));
        //xfptNotice.setFileType(xfptAnnoResults.getFileType());
        xfptNotice.setCatagory(Constants.CGGG);
        xfptNotice.setEnteId(Long.valueOf(xfptSuppliceContractSon.getSuppliceContractId()));
        xfptNotice.setRemark(String.valueOf(xfptSuppliceContract.getDeptId()));
        Long parentId = dept.getParentId();
        R<List<SysDept>> deptDataList = remoteDeptService.getDeptListByDeptId(parentId, SecurityConstants.INNER);
        if (deptDataList == null || deptDataList.getCode() != 200) {
            log.error("远程调用获取部门数据列表失败：{}", deptDataList.getMsg());
            throw new ServiceException("远程调用获取部门数据列表失败");
        }
        SysDept sysDept = deptDataList.getData().get(0);
        xfptNotice.setArea(String.valueOf(sysDept.getDeptId()));
        xfptNotice.setAreaname(sysDept.getDeptName());
        xfptNotice.setDeptname(dept.getDeptName());
                R<Boolean> save = remoteNoticService.save(xfptNotice, SecurityConstants.INNER);
        if (save == null || save.getCode() != 200) {
            log.error("远程调用保存公告失败：{}", save.getMsg());
            throw new ServiceException("远程调用保存公告失败");
        }
        xfptSuppliceContractSon.setFzrdh(mpUtil.encrypt(xfptSuppliceContractSon.getFzrdh(),mpUtil.getToken()));
        xfptSuppliceContractSon.setIntegrity(mpUtil.mac(xfptSuppliceContractSon.getUrl()));
        return toAjax(xfptSuppliceContractSonService.save(xfptSuppliceContractSon));
    }

    /**
     * 修改学校与供应商采购合同从
     */
    @Operation(summary = "修改学校与供应商采购合同从")
//    @RequiresPermissions("system:son:edit")
    @Log(title = "学校与供应商采购合同从", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfptSuppliceContractSon xfptSuppliceContractSon) {
        XfptSuppliceContract xfptSuppliceContract = iXfptSuppliceContractService.getById(xfptSuppliceContractSon.getSuppliceContractId());
        if(xfptSuppliceContract==null){
            return AjaxResult.warn("合同信息不存在");
        }
        //填充采购信息报备数据
        XfptProcSummary xfptProcSummary=new XfptProcSummary();
        xfptProcSummary.setTeamId(Long.valueOf(xfptSuppliceContract.getTeamMeetingId()));
        xfptProcSummary.setCount(Long.valueOf(xfptSuppliceContractSon.getCount()));
        xfptProcSummary.setMoney( Math.round(xfptSuppliceContractSon.getZj()));
        xfptProcSummary.setDj(xfptSuppliceContractSon.getDj());
        Boolean b2 = iXfptProcSummaryService.setDate(xfptProcSummary);
        if(!b2){
            log.error("保存采购信息报备失败：{}", xfptSuppliceContractSon.toString());
            throw new ServiceException("系统错误");
        }
        if(StrUtil.isNotEmpty(xfptSuppliceContractSon.getFzrdh())){
            xfptSuppliceContractSon.setFzrdh(mpUtil.encrypt(xfptSuppliceContractSon.getFzrdh(),mpUtil.getToken()));
        }
        if(StrUtil.isNotEmpty(xfptSuppliceContractSon.getUrl())){
            xfptSuppliceContractSon.setIntegrity(mpUtil.mac(xfptSuppliceContractSon.getUrl()));
        }

        return toAjax(xfptSuppliceContractSonService.updateById(xfptSuppliceContractSon));
    }


    /**
     * 删除学校与供应商采购合同从
     */
    @Operation(summary = "删除学校与供应商采购合同从")
//    @RequiresPermissions("system:son:remove")
    @Log(title = "学校与供应商采购合同从", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(xfptSuppliceContractSonService.removeByIds(Arrays.asList(ids)));
    }



    @PostMapping("/downloadZip")
    public void downloadZip( XfptSuppliceContractSon xfptSuppliceContractSon, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        System.err.println("xfptSuppliceContractSon:"+xfptSuppliceContractSon);
        String deptName = SecurityUtils.getLoginUser().getSysUser().getDept().getDeptName();
        String[] urls = xfptSuppliceContractSon.getUrl().split("&&&");
        if(StrUtil.isEmpty(xfptSuppliceContractSon.getFileName())){
            //xfptSuppliceContractSon.getUrl()截取最后一个/的内容
            xfptSuppliceContractSon.setFileName(urls[urls.length-1].substring(urls[urls.length-1].lastIndexOf("/")+1));
        }

        String[] fileNames = xfptSuppliceContractSon.getFileName().split("&&&");
        String zipFilePath =deptName+"_"+xfptSuppliceContractSon.getContractName()+ ".zip";
        // 设置响应头
        response.setContentType("application/zip; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(zipFilePath,"UTF-8"));

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; i < urls.length; i++) {
                try {
                    String URL = fileUrl + urls[i];
                    //截取fileName3的文件名
                    String fileName2 = URL.substring(URL.lastIndexOf("/")+1);
                    //截取fileName3最后一个/前面的内容
                    String filePath= URL.substring(0,URL.lastIndexOf("/"))+"/";
                    // 对文件名进行URL编码
                    String encodedFileName = java.net.URLEncoder.encode(fileName2, "UTF-8");
                    // 将空格编码从+替换为%20（更标准的URL编码）
                    encodedFileName = encodedFileName.replace("+", "%20");
                    URL url = new URL(filePath+encodedFileName);
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


    public static void main(String[] args) throws UnsupportedEncodingException {
        String fileName2 = "http://192.168.1.88:9300/filestatics/2025/09/24/";

        String fileurl = "http://192.168.1.88:9300/filestatics/2025/09/24/学校三重一大事项会议记录（公开）_20250924143729A007.pdf";
        //截取fileName3的文件名
        String fileName = fileurl.substring(fileurl.lastIndexOf("/")+1);
        //截取fileName3最后一个/前面的内容
        String filePath= fileurl.substring(0,fileurl.lastIndexOf("/"))+"/";
        // 对文件名进行URL编码
        String encodedFileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        // 将空格编码从+替换为%20（更标准的URL编码）
        encodedFileName = encodedFileName.replace("+", "%20");
        try {
            URL url = new URL(filePath+encodedFileName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @GetMapping("/dateEnc")
    public AjaxResult dataEnc(){
        List<XfptSuppliceContractSon> list = xfptSuppliceContractSonService.lambdaQuery().list();
        for (XfptSuppliceContractSon xfptSuppliceContractSon : list) {
            if(StrUtil.isNotEmpty(xfptSuppliceContractSon.getFzrdh())){
                xfptSuppliceContractSon.setFzrdh(mpUtil.encrypt(xfptSuppliceContractSon.getFzrdh(),mpUtil.getToken()));
            }
        }
        boolean b = xfptSuppliceContractSonService.updateBatchById(list);
        if(!b){
            return AjaxResult.error("更新失败");
        }
        return AjaxResult.success("成功");
    }

}
