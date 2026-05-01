package com.xfgl.system.controller;

import java.util.Arrays;
import java.util.Date;

import cn.hutool.core.util.StrUtil;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.RsaUtils;
import com.xfgl.system.mp.MpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.file.FileTypeUtils;
import com.xfgl.common.core.utils.file.MimeTypeUtils;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.service.TokenService;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.RemoteFileService;
import com.xfgl.system.api.domain.SysFile;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import com.xfgl.system.service.ISysUserService;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RemoteFileService remoteFileService;
    @Autowired
    private MpUtil mpUtil;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        if(StrUtil.isNotEmpty(user.getPhone())){
            user.setPhone(mpUtil.decrypt(user.getPhone(), mpUtil.getToken()));
        }

        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(username));
        ajax.put("postGroup", userService.selectUserPostGroup(username));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        String token = mpUtil.getToken();
        if(StrUtil.isNotEmpty(user.getPhone())){
            //判断手机号是否存在
            Long count = userService.lambdaQuery().eq(SysUser::getPhone, mpUtil.encrypt(user.getPhone(), token)).count();
            if(count>0){
                return AjaxResult.warn("手机号已存在");
            }
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getSysUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if(StrUtil.isNotEmpty(user.getPhone())){
            //加密
            String encrypt = mpUtil.encrypt(user.getPhone(),token);
            currentUser.setPhone(encrypt);
        }

        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser))
        {
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {


        try {
            oldPassword= RsaUtils.decryptByPrivateKey(oldPassword);
            newPassword= RsaUtils.decryptByPrivateKey(newPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 密码复杂度验证：必须包含数字、字母和特殊符号
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{5,20}$";
        if (!newPassword.matches(passwordPattern)) {
            throw new ServiceException("密码必须包含数字、字母和特殊符号");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        String password = user.getPassword();
        //解密
        password = mpUtil.decrypt(password, mpUtil.getToken());
        if(StrUtil.isEmpty(password)){
            return AjaxResult.error("解密失败");
        }
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        //if (SecurityUtils.matchesPassword(newPassword, password))
        //{
        //    return error("新密码不能与旧密码相同");
        //}
        newPassword = SecurityUtils.encryptPassword(newPassword);

        newPassword = mpUtil.encrypt(newPassword, mpUtil.getToken());
        if(StrUtil.isEmpty(newPassword)){
            return AjaxResult.error("加密失败");
        }
        if (userService.resetUserPwd(username, newPassword) > 0)
        {
            // 更新缓存用户密码
            LoginUser loginUser = SecurityUtils.getLoginUser();
            loginUser.getSysUser().setPassword(newPassword);
            loginUser.getSysUser().setPwdtime(new Date());
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file)
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String extension = FileTypeUtils.getExtension(file);
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION))
            {
                return error("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            R<SysFile> fileResult = remoteFileService.upload(file);
            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData()))
            {
                return error("文件服务异常，请联系管理员");
            }
            String url = fileResult.getData().getUrl();
            if (userService.updateUserAvatar(loginUser.getUsername(), url))
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", url);
                // 更新缓存用户头像
                loginUser.getSysUser().setAvatar(url);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
