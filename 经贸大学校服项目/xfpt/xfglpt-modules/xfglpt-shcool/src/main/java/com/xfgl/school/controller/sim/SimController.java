package com.xfgl.school.controller.sim;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.school.controller.util.SimRequestUtil;
import com.xfgl.school.domain.XfptSchoolMessage;
import com.xfgl.school.domain.XfptSimUser;
import com.xfgl.school.domain.XfptStudentInfo;
import com.xfgl.school.service.IXfptSchoolMessageService;
import com.xfgl.school.service.IXfptSimUserService;
import com.xfgl.school.service.IXfptStudentInfoService;
import com.xfgl.school.service.Token2Service;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对接石i民登录
 */
@RestController
@RequestMapping("/simOpen")
@Slf4j
public class SimController {
    private static final String PARENT_APP_ROLE_KEY = "parent_app";
    private static final Set<String> PARENT_APP_PERMISSIONS = Collections.unmodifiableSet(new LinkedHashSet<String>() {{
        add("system:xfptnotice:list");
        add("system:xfptnotice:query");
        add("school:groupNotice:list");
        add("school:groupNotice:query");
        add("school:goods:list");
        add("school:goods:query");
        add("school:cartItem:list");
        add("school:cartItem:query");
        add("school:cartItem:add");
        add("school:cartItem:edit");
        add("school:cartItem:remove");
        add("school:userAddress:list");
        add("school:userAddress:query");
        add("school:userAddress:add");
        add("school:userAddress:edit");
        add("school:userAddress:remove");
        add("school:orderInfo:list");
        add("school:orderInfo:query");
        add("school:orderInfo:add");
        add("school:orderInfo:edit");
        add("school:orderInfo:remove");
        add("school:afterSale:list");
        add("school:afterSale:query");
        add("school:afterSale:add");
        add("school:afterSale:edit");
    }});

    @Value("${sim.ip:}")
    private String simIp;

    @Value("${sim.getCode:}")
    private String simGetCodeUri;

    @Value("${sim.getToken:}")
    private String simGetTokenUri;

    @Value("${sim.redirecturi:}")
    private String simRedirectUri;

    @Value("${sim.getUser:}")
    private String simGetUserUri;

    @Autowired
    private SimRequestUtil simRequestUtil;
    @Autowired
    private IXfptSimUserService iXfptSimUserService;
    @Autowired
    private Token2Service tokenService;
    @Autowired
    private IXfptStudentInfoService iXfptStudentInfoService;
    @Autowired
    private IXfptSchoolMessageService iXfptSchoolMessageService;

    /**
     * 本地开发登录。只用于绕过石i民真实授权，方便本机调试 H5。
     */
    @RequestMapping("/devLogin")
    public AjaxResult devLogin() {
        List<XfptSimUser> list = iXfptSimUserService.lambdaQuery()
                .eq(XfptSimUser::getIdCard, "LOCAL_DEV_USER")
                .list();
        if (CollUtil.isEmpty(list)) {
            XfptSimUser devLoginUser = findFirstAvailableParentUser();
            if (devLoginUser != null) {
                list = Collections.singletonList(devLoginUser);
            }
        }
        if (CollUtil.isEmpty(list)) {
            return AjaxResult.error("本地库没有可用的石i民用户");
        }
        return buildLoginResult(list);
    }

    ///**
    // * 获取code，石i民调取回调接口
    // */
    //@RequestMapping("/getCode")
    //public void getCode(){
    //    simRequestUtil.getCode();
    //
    //}

    /**
     * 登录
     * @param code
     * @return
     */
    @RequestMapping("/redirecturi")
    public AjaxResult redirecturi(String code){
        //获取accessToken
        String body = simRequestUtil.getToken(code);
        if(StrUtil.isEmpty(body)){
            return AjaxResult.error("登录失败");
        }
        JSONObject jsonObject = JSON.parseObject(body);
        String accessToken = jsonObject.getString("access_token");
        String user = jsonObject.getString("user");
        if(StrUtil.isEmpty(accessToken) || StrUtil.isEmpty(user)){
            log.error("获取Token失败");
            return AjaxResult.error("登录失败");
        }
        //根据token获取用户信息
        String user1 = simRequestUtil.getUser(accessToken, user);
        if(StrUtil.isEmpty(user)){
            log.error("获取Token失败");
            return AjaxResult.error("登录失败");
        }
        JSONObject jsonUserObject = JSON.parseObject(user1);
        String data = jsonUserObject.getString("data");
        JSONObject dataObject = JSON.parseObject(data);

        String name = dataObject.getString("name");
        String mobile = dataObject.getString("mobile");
        String userName = dataObject.getString("userName");
        String userId = dataObject.getString("userId");
        if(StrUtil.isEmpty(userId)){
            return AjaxResult.error("获取信息失败");
        }
        //查询是否登陆过石I民，如果没有把石I民的信息存储自己系统
        List<XfptSimUser> list = iXfptSimUserService.lambdaQuery().eq(XfptSimUser::getIdCard, userId).list();
        if(CollUtil.isEmpty(list)){
            XfptSimUser xfptSimUser=new XfptSimUser();
            xfptSimUser.setName(name);
            xfptSimUser.setIdCard(userId);
            xfptSimUser.setParm1(mobile);
            xfptSimUser.setParm_2(userName);
            xfptSimUser.setDelFlag("0");
            list.add(xfptSimUser);
            boolean save = iXfptSimUserService.save(xfptSimUser);
        }
        return buildLoginResult(list);
    }

    private AjaxResult buildLoginResult(List<XfptSimUser> list) {
        if (CollUtil.isEmpty(list)) {
            return AjaxResult.error("登录失败");
        }
        XfptSimUser simUser = list.get(0);
        XfptSchoolMessage schoolMessage = resolveSchoolForSimUser(simUser);
        if (schoolMessage == null || schoolMessage.getDeptId() == null) {
            return AjaxResult.error("当前家长未关联学校信息，请先绑定学生后再登录");
        }

        LoginUser loginUser = new LoginUser();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(simUser.getId());
        sysUser.setDeptId(schoolMessage.getDeptId());
        sysUser.setUserName(StrUtil.blankToDefault(simUser.getParm_2(), "sim_user_" + simUser.getId()));
        sysUser.setNickName(StrUtil.blankToDefault(simUser.getName(), "家长用户"));
        sysUser.setPhonenumber(simUser.getParm1());
        loginUser.setSysUser(sysUser);
        loginUser.setUserid(simUser.getId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setRoles(Collections.singleton(PARENT_APP_ROLE_KEY));
        loginUser.setPermissions(new LinkedHashSet<>(PARENT_APP_PERMISSIONS));
        Map<String, Object> token = tokenService.createToken(loginUser);
        //判断是否完善过学生信息
        Long id = simUser.getId();
        List<XfptStudentInfo> list1 = iXfptStudentInfoService.lambdaQuery()
                .eq(XfptStudentInfo::getSimId, String.valueOf(id))
                .list();
        if(CollUtil.isNotEmpty(list1)){
            for (XfptStudentInfo xfptStudentInfo : list1) {
                xfptStudentInfo.setPhone(simUser.getParm1());
            }
            token.put("is_improve", "1");
        }else {
            token.put("is_improve", "0");
        }
        //返回自己系统token
        token.put("user", simUser);
        token.put("school", schoolMessage);
        token.put("roles", loginUser.getRoles());
        token.put("permissions", loginUser.getPermissions());
        return AjaxResult.success(token);
    }

    private XfptSimUser findFirstAvailableParentUser() {
        List<XfptSimUser> simUsers = iXfptSimUserService.lambdaQuery()
                .eq(XfptSimUser::getDelFlag, "0")
                .orderByAsc(XfptSimUser::getId)
                .list();
        for (XfptSimUser simUser : simUsers) {
            if (resolveSchoolForSimUser(simUser) != null) {
                return simUser;
            }
        }
        return CollUtil.isEmpty(simUsers) ? null : simUsers.get(0);
    }

    private XfptSchoolMessage resolveSchoolForSimUser(XfptSimUser simUser) {
        if (simUser == null || simUser.getId() == null) {
            return null;
        }
        XfptStudentInfo studentInfo = iXfptStudentInfoService.lambdaQuery()
                .eq(XfptStudentInfo::getSimId, String.valueOf(simUser.getId()))
                .last("limit 1")
                .one();
        if (studentInfo == null || StrUtil.isBlank(studentInfo.getShcoolId())) {
            return null;
        }
        Long schoolId;
        try {
            schoolId = Long.valueOf(studentInfo.getShcoolId());
        } catch (NumberFormatException e) {
            log.warn("家长端登录解析学校ID失败, simUserId={}, schoolId={}", simUser.getId(), studentInfo.getShcoolId());
            return null;
        }
        return iXfptSchoolMessageService.lambdaQuery()
                .eq(XfptSchoolMessage::getId, schoolId)
                .one();
    }
}




