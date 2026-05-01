package com.xfgl.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.enums.NoticeStatus;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.mp.MpUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.domain.XfptCarouselImage;
import com.xfgl.system.domain.XfptCategoryNotice;
import com.xfgl.system.domain.vo.FeedBackVo;
import com.xfgl.system.domain.vo.SchoolInfoVo;
import com.xfgl.system.domain.vo.SchoolListDto;
import com.xfgl.system.domain.vo.SchoolNameVo;
import com.xfgl.system.mapper.OpenMapper;
import com.xfgl.system.mapper.SysDeptMapper;
import com.xfgl.system.service.IXfptCarouselImageService;
import com.xfgl.system.service.IXfptCategoryNoticeService;
import com.xfgl.system.service.IXfptNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/open/link")
public class OpenController extends BaseController {
    @Autowired
    private IXfptCategoryNoticeService iXfptCategoryNoticeService;
    @Autowired
    private IXfptNoticeService iXfptNoticeService;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private OpenMapper openMapper;

    /**
     * 查询首页分栏目
     * @return
     */
    @GetMapping("/noticeCategory")
    public AjaxResult noticeCategory(){
        List<XfptCategoryNotice> list = iXfptCategoryNoticeService.lambdaQuery().eq(XfptCategoryNotice::getStatus, 1).list();
        if(CollUtil.isEmpty(list)){
            return AjaxResult.error("未配置栏目");
        }


        //父类集合
        List<XfptCategoryNotice> filteredList = list.stream().filter(item -> item.getParentId() == 0).collect(Collectors.toList());
        Map<Long, List<XfptCategoryNotice>> resultMap = list.stream()
                .collect(Collectors.groupingBy(
                        XfptCategoryNotice::getParentId // 根据 parentId 分组
                ));
        for (XfptCategoryNotice xfptCategoryNotice : filteredList) {
            List<XfptCategoryNotice> xfptCategoryNotices = resultMap.get(xfptCategoryNotice.getId());
            if(CollUtil.isNotEmpty(xfptCategoryNotices)){
                xfptCategoryNotice.setChildren(xfptCategoryNotices);
            }
        }
        return AjaxResult.success(filteredList);
    }

    /**
     * 查询文章列表
     * @return
     */
    @GetMapping("/noticeList")
    public TableDataInfo noticeList(String bs,String type,String area,String deptName,String title,String createBy){

        if(!"CGGG".equals(bs)){
            type=null;
        }
        startPage();
        List<XfptNotice> list = iXfptNoticeService.lambdaQuery()
                .eq(XfptNotice::getCatagory, bs)
                .eq(StrUtil.isNotEmpty(area),XfptNotice::getArea, area)
                .like(StrUtil.isNotEmpty(deptName),XfptNotice::getCreateBy, deptName)
                .eq(StrUtil.isNotEmpty(title),XfptNotice::getTitle, title)
                .eq(XfptNotice::getStatus, NoticeStatus.FB.getCode())
                .like(StrUtil.isNotEmpty(createBy),XfptNotice::getCreateBy, createBy)
                .eq(StrUtil.isNotEmpty(type), XfptNotice::getType, type)
                .orderByDesc(XfptNotice::getCreateTime)
                .list();

        return getDataTable(list);
    }

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/noticeDetail")
    public AjaxResult noticeDetail(Long id){
        XfptNotice notice = iXfptNoticeService.getById(id);
        if(notice == null){
            return AjaxResult.error("未找到该文章");
        }
        return AjaxResult.success(notice);
    }

    /**
     * 增加阅读量
     */
    @GetMapping("/readCount")
    public AjaxResult readCount(Long id){
        XfptNotice notice = iXfptNoticeService.getById(id);
        if(notice == null){
            return AjaxResult.error("未找到该文章");
        }
        notice.setReadCount(notice.getReadCount() + 1);
        if(StrUtil.isEmpty(notice.getUpdateBy())){
            notice.setUpdateBy("系统");
        }
        iXfptNoticeService.updateById(notice);
        return AjaxResult.success();
    }

    @GetMapping("/getSchoolList")
    public TableDataInfo getSchoolList(SchoolListDto schoolListDto){
        startPage();
        List<SysDept> list=sysDeptMapper.getSchoolList(schoolListDto);
        return getDataTable(list);
    }

    @GetMapping("/getSchoolInfo")
    public AjaxResult getSchoolInfo(Long id){
        SchoolInfoVo schoolInfo=sysDeptMapper.getSchoolInfo(id);
        return AjaxResult.success(schoolInfo);
    }

    @Autowired
    private IXfptCarouselImageService iXfptCarouselImageService;
    @GetMapping("/carouselList")
    public AjaxResult carouselList(String type){
        List<XfptCarouselImage> list = iXfptCarouselImageService.lambdaQuery().eq(XfptCarouselImage::getType, type)
                .orderByDesc(XfptCarouselImage::getSort).list();
        return AjaxResult.success(list);
    }

    @GetMapping("/getSchoolByName")
    public AjaxResult getSchoolByName(String name){
        List<SchoolNameVo> schoolName=sysDeptMapper.getSchoolByName(name);
        return AjaxResult.success(schoolName);
    }

    @GetMapping("/getDeptList")
    public AjaxResult getDeptList(){
        SysDept sysDept=new SysDept();
        sysDept.setParentId(0l);
        SysDept sysDept1 = sysDeptMapper.selectDeptList(sysDept).get(0);

        SysDept sysDept2=new SysDept();
        sysDept2.setParentId(sysDept1.getDeptId());
        List<SysDept> list = sysDeptMapper.selectDeptList(sysDept2);
        return AjaxResult.success(list);
    }

    @GetMapping("/getfeednackList")
    public TableDataInfo getfeednackList(){
        startPage();
        List<FeedBackVo> list=openMapper.getfeednackList();

        return getDataTable(list);
    }

    @GetMapping("/getfeednackInfo")
    public AjaxResult getfeednackInfo(Long id){
        FeedBackVo feedBackVo=openMapper.getfeednackInfo(id);

        return AjaxResult.success(feedBackVo);
    }

    @PostMapping("/search")
    public AjaxResult search(String name){
    	List<XfptNotice> list = iXfptNoticeService.lambdaQuery()
                .like(XfptNotice::getTitle, name)
                .or()
                .like(XfptNotice::getContent, name)
                .list();
        return AjaxResult.success(list);
    }

    /**
     * 验证用户是否存在
     * @param userName
     * @param phone
     * @return
     */
    @GetMapping("/checkUserAndPhone")
    public AjaxResult checkUserAndPhone(String userName,String phone,Long id){
        Long count=openMapper.checkUserAndPhone(userName+"-"+phone,id);
        if(count>0){
            return AjaxResult.success(1);
        }

        return AjaxResult.warn("姓名手机号不匹配");
    }

    @Autowired
    private MpUtil mpUtil;
    @GetMapping("/encrypt")
    public AjaxResult encrypt(String text){
        String string = SecurityUtils.encryptPassword("admin123");
        String token = mpUtil.encrypt(text, mpUtil.getToken());
        return AjaxResult.success(token);
    }

    @GetMapping("/decrypt")
    public AjaxResult decrypt(String decrypt){
        decrypt = decrypt
                .replaceAll(" ", "+")  // 空格还原为 +
                .replace("-", "+")     // 兼容 - 替代 + 的情况
                .replace("_", "/");    // 兼容 _ 替代 / 的情况
        String token = mpUtil.decrypt(decrypt, mpUtil.getToken());
        return AjaxResult.success(token);
    }

    @GetMapping("/mac")
    public AjaxResult mac(String text){
        String token = mpUtil.mac(text, mpUtil.getToken());
        return AjaxResult.success(token);
    }
}
