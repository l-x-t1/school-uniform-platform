package com.xfgl.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;
import com.xfgl.common.core.constant.CacheConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.redis.service.RedisService;
import com.xfgl.common.security.service.TokenService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import com.xfgl.system.mapper.SysUserMapper;
import com.xfgl.system.mp.MpUtil;
import com.xfgl.system.service.ISysConfigService;
import com.xfgl.system.service.ISysPermissionService;
import com.xfgl.system.service.ISysUserService;
import com.xfgl.system.tool.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {
    @Autowired
    private RedisService redisService;

    @Autowired
    private ISysConfigService iSysConfigService;
    @Autowired
    private ISysUserService iSysUserService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysPermissionService permissionService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SendSms sendSms;
    @Autowired
    private MpUtil mpUtil;

    /**
     * 获取短信状态
     *
     * @return
     */
    @GetMapping("/getSmsStatus")
    public AjaxResult getSmsStatus() {
        String string = iSysConfigService.selectConfigByKey("smsStatus");
        return AjaxResult.success(string);
    }



    /**
     * 发送短信
     * @param phone
     * @return
     */
    @PostMapping("/sendSms")
    public AjaxResult sendSms(String phone) {
        Boolean b = redisService.hasKey(CacheConstants.SMS + phone);
        if(b){
            return AjaxResult.warn("请勿重复发送");
        }
        SysUser sysUser=new SysUser();
        //加密
        String phoneEnc=mpUtil.encrypt(phone, mpUtil.getToken());
        sysUser.setPhone(phoneEnc);
        List<SysUser> sysUsers = iSysUserService.selectUserList(sysUser);
        if(CollUtil.isEmpty(sysUsers)){
            return AjaxResult.error("输入的手机号不存在");
        }
        //随机生成6位数验证码，存入redis，有效期10分钟
        String randomCode = String.valueOf((int)((Math.random() + 1) * 100000));
        //发送验证码
        Boolean send = sendSms.send(phone, randomCode);
        if(!send){
            return AjaxResult.error("发送失败");
        }
        redisService.setCacheObject(CacheConstants.SMS + phone, randomCode, 10l, TimeUnit.MINUTES);
        return AjaxResult.success("发送成功");
    }

    @GetMapping("/smsLogin")
    public R<?> smsLogin(String phone, String code){
        //验证验证码是否正确
        String redisCode = redisService.getCacheObject(CacheConstants.SMS + phone);
        if(redisCode == null){
            return R.fail("验证码已过期");
        }
        if(!redisCode.equals(code)){
            return R.fail("验证码错误");
        }

        //根据手机号查询用户，生成token
        SysUser sysUserParm=new SysUser();
        String phoneEnc=mpUtil.encrypt(phone, mpUtil.getToken());
        sysUserParm.setPhone(phoneEnc);
        List<SysUser> sysUsers = iSysUserService.selectUserList(sysUserParm);
        SysUser sysUser = userService.selectUserByUserName(sysUsers.get(0).getUserName());

        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        Map<String, Object> token = tokenService.createToken(sysUserVo);
        //删除redis中的验证码
        boolean b = redisService.deleteObject(CacheConstants.SMS + phone);
        if(!b){
            log.error("删除redis验证码错误");
            return R.fail("系统错误");
        }
        return R.ok(token);

    }

    /**
     * 收集用户手机号
     * @param phone
     * @param userId
     * @return
     */
    @GetMapping("/addPhone")
    public AjaxResult addPhone(String phone,Long userId){
        //判断手机号是否存在
        Long count = userService.lambdaQuery().eq(SysUser::getPhone, phone).count();
        if(count>0){
            return AjaxResult.warn("手机号已存在");
        }
        if(StrUtil.isEmpty(phone) || userId == null){
            return AjaxResult.warn("参数不能为空");
        }
        SysUser sysUser=new SysUser();
        sysUser.setUserId(userId);
        String encrypt = mpUtil.encrypt(phone, mpUtil.getToken());
        sysUser.setPhone(encrypt);
        int i = sysUserMapper.updateUser(sysUser);

        if(i==0){
            log.error("修改用户信息失败：{}",sysUser);
            return AjaxResult.error("系统错误");
        }
        return AjaxResult.success("添加成功");
    }

    /**
     * 完整性校验参数
     * @param user
     * @return
     */
    private String getMac(SysUser user) {
        String text="";
        //信息完整性user.getUserName()+user.getNickName()+user.getPhone()+user.getDeptId()+
        if(StrUtil.isNotEmpty(user.getUserName())){
            text=text+ user.getUserName();
        }
        if(StrUtil.isNotEmpty(user.getNickName())){
            text=text+ user.getNickName();
        }
        if(StrUtil.isNotEmpty(user.getPhone())){
            text=text+ user.getPhone();
        }
        if(user.getDeptId()!=null){
            text=text+ user.getDeptId();
        }
        log.info("完整性校验：{}",text);
        String mac = mpUtil.mac(text, mpUtil.getToken());
        return mac;
    }
    /**
     * 密码和手机号进行加密
     */
    @GetMapping("/userEncrypt")
    public AjaxResult userEncrypt(){
        String token = mpUtil.getToken();
        SysUser sysUser1=new SysUser();
        List<SysUser> list = sysUserMapper.selectUserAll();
        for (SysUser sysUser : list) {
            if(StrUtil.isNotEmpty(sysUser.getPassword())){
                String encrypt = mpUtil.encrypt(sysUser.getPassword(), token);
                sysUser.setPassword(encrypt);
            }
            if(StrUtil.isNotEmpty(sysUser.getPhone())){
                String encrypt = mpUtil.encrypt(sysUser.getPhone(), token);
                sysUser.setPhone(encrypt);
            }
            int i = sysUserMapper.updateUser(sysUser);
            if(i==0){
                log.error("用户密码加密失败");
                return AjaxResult.error("系统错误");
            }
        }


        return AjaxResult.success("用户密码加密成功");
    }

}
