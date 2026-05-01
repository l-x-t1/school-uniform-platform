package com.xfgl.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.xfgl.common.core.utils.RsaUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xfgl.auth.form.LoginBody;
import com.xfgl.auth.form.RegisterBody;
import com.xfgl.auth.service.SysLoginService;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.JwtUtils;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.security.auth.AuthUtil;
import com.xfgl.common.security.service.TokenService;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.model.LoginUser;

/**
 * TokenUtil 控制
 *
 * @author ruoyi
 */
@RestController
public class TokenController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @SneakyThrows
    @PostMapping("login")
    public R<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = null;

        userInfo = sysLoginService.login(form.getUsername(), RsaUtils.decryptByPrivateKey(form.getPassword()));

        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return R.ok();
    }
}
