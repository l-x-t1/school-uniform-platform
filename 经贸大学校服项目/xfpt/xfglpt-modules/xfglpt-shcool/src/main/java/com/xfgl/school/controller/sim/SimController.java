package com.xfgl.school.controller.sim;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xfgl.common.core.constant.CacheConstants;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.redis.service.RedisService;
import com.xfgl.common.security.utils.SecurityUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 对接石i民与家长端移动登录。
 */
@RestController
@RequestMapping({"/simOpen", "/mobile"})
@Slf4j
public class SimController {
    private static final String PARENT_APP_ROLE_KEY = "parent_app";
    private static final String PARENT_APP_SMS_CACHE_PREFIX = CacheConstants.SMS + "parent_app:";
    private static final String LOCAL_DEBUG_SMS_CODE = "123456";
    private static final String REGISTER_SOURCE_PREFIX = "LOCAL_PARENT_";
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
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
        add("school:comment:list");
        add("school:comment:query");
        add("school:comment:add");
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
    @Autowired
    private RedisService redisService;

    /**
     * 本地开发登录。仅用于绕过石i民真实授权，方便调试 H5。
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
            return AjaxResult.error("本地库没有可用的家长账号");
        }
        return buildLoginResult(list);
    }

    /**
     * 石i民回调登录。
     */
    @RequestMapping("/redirecturi")
    public AjaxResult redirecturi(String code) {
        String body = simRequestUtil.getToken(code);
        if (StrUtil.isEmpty(body)) {
            return AjaxResult.error("登录失败");
        }
        JSONObject jsonObject = JSON.parseObject(body);
        String accessToken = jsonObject.getString("access_token");
        String user = jsonObject.getString("user");
        if (StrUtil.isEmpty(accessToken) || StrUtil.isEmpty(user)) {
            log.error("获取Token失败");
            return AjaxResult.error("登录失败");
        }

        String userResponse = simRequestUtil.getUser(accessToken, user);
        if (StrUtil.isEmpty(userResponse)) {
            log.error("获取用户信息失败");
            return AjaxResult.error("登录失败");
        }

        JSONObject jsonUserObject = JSON.parseObject(userResponse);
        String data = jsonUserObject.getString("data");
        JSONObject dataObject = JSON.parseObject(data);

        String name = dataObject.getString("name");
        String mobile = dataObject.getString("mobile");
        String userName = dataObject.getString("userName");
        String userId = dataObject.getString("userId");
        if (StrUtil.isEmpty(userId)) {
            return AjaxResult.error("获取信息失败");
        }

        List<XfptSimUser> list = iXfptSimUserService.lambdaQuery()
                .eq(XfptSimUser::getIdCard, userId)
                .list();
        if (CollUtil.isEmpty(list)) {
            XfptSimUser xfptSimUser = new XfptSimUser();
            xfptSimUser.setName(name);
            xfptSimUser.setIdCard(userId);
            xfptSimUser.setParm1(mobile);
            xfptSimUser.setParm_2(userName);
            xfptSimUser.setDelFlag("0");
            iXfptSimUserService.save(xfptSimUser);
            list = Collections.singletonList(xfptSimUser);
        }
        return buildLoginResult(list);
    }

    @PostMapping("/sendSmsCode")
    public AjaxResult sendMobileSmsCode(@RequestBody(required = false) Map<String, Object> body) {
        String phone = normalizePhone(body == null ? null : body.get("phone"));
        if (StrUtil.isBlank(phone)) {
            return AjaxResult.error("手机号不能为空");
        }

        XfptSimUser simUser = findSimUserByPhone(phone);
        if (simUser == null) {
            return AjaxResult.error("该手机号尚未注册，请先注册");
        }

        String cacheKey = buildParentSmsCacheKey(phone);
        redisService.setCacheObject(cacheKey, LOCAL_DEBUG_SMS_CODE, 10L, TimeUnit.MINUTES);
        log.info("家长端手机号验证码已生成, phone={}, code={}", phone, LOCAL_DEBUG_SMS_CODE);
        return AjaxResult.success("验证码已发送", Collections.singletonMap("devCode", LOCAL_DEBUG_SMS_CODE));
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody(required = false) Map<String, Object> body) {
        String phone = normalizePhone(body == null ? null : body.get("phone"));
        String code = body == null ? null : StrUtil.trimToEmpty(String.valueOf(body.get("code")));
        String password = body == null ? null : StrUtil.trimToEmpty(String.valueOf(body.get("password")));

        if (!isValidPhone(phone)) {
            return AjaxResult.error("请输入正确的手机号");
        }
        if (!StrUtil.equals(LOCAL_DEBUG_SMS_CODE, code)) {
            return AjaxResult.error("验证码不正确，请使用123456");
        }
        if (!isValidRegisterPassword(password)) {
            return AjaxResult.error("密码需为8-20位，且包含字母、数字、符号");
        }
        if (findSimUserByPhone(phone) != null) {
            return AjaxResult.error("该手机号已注册");
        }

        XfptSimUser simUser = new XfptSimUser();
        simUser.setName(buildDefaultParentName(phone));
        simUser.setIdCard(REGISTER_SOURCE_PREFIX + phone);
        simUser.setParm1(phone);
        simUser.setParm_2(phone);
        simUser.setParm_3(SecurityUtils.encryptPassword(password));
        simUser.setDelFlag("0");
        simUser.setCreateBy("parent_register");

        if (!iXfptSimUserService.save(simUser)) {
            return AjaxResult.error("注册失败，请稍后再试");
        }
        return AjaxResult.success("注册成功", Collections.singletonMap("userId", simUser.getId()));
    }

    @PostMapping("/login")
    public AjaxResult mobileLogin(@RequestBody(required = false) Map<String, Object> body) {
        String phone = normalizePhone(body == null ? null : body.get("phone"));
        String code = body == null ? null : StrUtil.trimToEmpty(String.valueOf(body.get("code")));
        if (StrUtil.isBlank(phone)) {
            return AjaxResult.error("手机号不能为空");
        }
        if (StrUtil.isBlank(code)) {
            return AjaxResult.error("验证码不能为空");
        }

        String cacheKey = buildParentSmsCacheKey(phone);
        String cacheCode = redisService.getCacheObject(cacheKey);
        if (StrUtil.isBlank(cacheCode)) {
            return AjaxResult.error("验证码已过期");
        }
        if (!StrUtil.equals(cacheCode, code)) {
            return AjaxResult.error("验证码错误");
        }

        XfptSimUser simUser = findSimUserByPhone(phone);
        if (simUser == null) {
            return AjaxResult.error("该手机号尚未注册，请先注册");
        }

        redisService.deleteObject(cacheKey);
        return buildLoginResult(Collections.singletonList(simUser));
    }

    @PostMapping("/wechat-phone-login")
    public AjaxResult wechatPhoneLogin(@RequestBody(required = false) Map<String, Object> body) {
        return AjaxResult.error("当前环境暂未开通微信一键登录，请使用手机号验证码登录");
    }

    private AjaxResult buildLoginResult(List<XfptSimUser> list) {
        if (CollUtil.isEmpty(list)) {
            return AjaxResult.error("登录失败");
        }

        XfptSimUser simUser = list.get(0);
        XfptSchoolMessage schoolMessage = resolveSchoolForSimUser(simUser);

        LoginUser loginUser = new LoginUser();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(simUser.getId());
        if (schoolMessage != null) {
            sysUser.setDeptId(schoolMessage.getDeptId());
        }
        sysUser.setUserName(StrUtil.blankToDefault(simUser.getParm_2(), "sim_user_" + simUser.getId()));
        sysUser.setNickName(StrUtil.blankToDefault(simUser.getName(), "家长用户"));
        sysUser.setPhonenumber(simUser.getParm1());

        loginUser.setSysUser(sysUser);
        loginUser.setUserid(simUser.getId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setRoles(Collections.singleton(PARENT_APP_ROLE_KEY));
        loginUser.setPermissions(new LinkedHashSet<>(PARENT_APP_PERMISSIONS));

        Map<String, Object> token = tokenService.createToken(loginUser);
        List<XfptStudentInfo> studentList = iXfptStudentInfoService.lambdaQuery()
                .eq(XfptStudentInfo::getSimId, String.valueOf(simUser.getId()))
                .list();
        if (CollUtil.isNotEmpty(studentList)) {
            for (XfptStudentInfo xfptStudentInfo : studentList) {
                xfptStudentInfo.setPhone(simUser.getParm1());
            }
            token.put("is_improve", "1");
        } else {
            token.put("is_improve", "0");
        }

        token.put("user", simUser);
        token.put("school", schoolMessage);
        token.put("roles", loginUser.getRoles());
        token.put("permissions", loginUser.getPermissions());
        return AjaxResult.success(token);
    }

    private XfptSimUser findSimUserByPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        List<XfptSimUser> list = iXfptSimUserService.lambdaQuery()
                .eq(XfptSimUser::getParm1, phone)
                .eq(XfptSimUser::getDelFlag, "0")
                .orderByAsc(XfptSimUser::getId)
                .list();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        for (XfptSimUser simUser : list) {
            if (resolveSchoolForSimUser(simUser) != null) {
                return simUser;
            }
        }
        return list.get(0);
    }

    private String buildParentSmsCacheKey(String phone) {
        return PARENT_APP_SMS_CACHE_PREFIX + phone;
    }

    private String normalizePhone(Object phoneValue) {
        if (phoneValue == null) {
            return "";
        }
        return StrUtil.trimToEmpty(String.valueOf(phoneValue)).replace(" ", "");
    }

    private boolean isValidPhone(String phone) {
        return StrUtil.isNotBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    private boolean isValidRegisterPassword(String password) {
        if (StrUtil.isBlank(password) || password.length() < 8 || password.length() > 20) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSymbol = false;
        for (char current : password.toCharArray()) {
            if (Character.isLetter(current)) {
                hasLetter = true;
            } else if (Character.isDigit(current)) {
                hasNumber = true;
            } else {
                hasSymbol = true;
            }
        }
        return hasLetter && hasNumber && hasSymbol;
    }

    private String buildDefaultParentName(String phone) {
        if (StrUtil.length(phone) >= 4) {
            return "家长" + StrUtil.subSuf(phone, phone.length() - 4);
        }
        return "家长用户";
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
