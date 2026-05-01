package com.xfgl.system.controller;
import com.hebca.cloud.sdk.api.request.CertAndSignVerifyRequest;
import com.hebca.cloud.sdk.api.response.BasicResponse;
import com.hebca.cloud.sdk.tools.HebcaCloudAPITool;
import com.hebca.pki.Cert;
import com.hebca.pki.CertParse;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.security.service.TokenService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import com.xfgl.system.mp.MpUtil;
import com.xfgl.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org2.bouncycastle.jce.provider.BouncyCastleProvider;


import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class LoginAction {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private MpUtil mpUtil;
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @GetMapping("/getRandom")
    @CrossOrigin
    public String getRandom() throws Exception {
        SecureRandom random = new SecureRandom();
        StringBuilder getRandom = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            getRandom.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return getRandom.toString();
    }
    @PostMapping("/verifyCertAndSign")
    @CrossOrigin
    public R<?> verifyCertAndSign2(String random,String cert, String sign) throws Exception {
        String certUnique;
        if (random == null) {
            throw new ServiceException("随机数为空，无法进行证书验证");

        } else if (cert == null) {
            throw new ServiceException("签名证书为空，无法进行证书验证");
        } else if (sign == null) {
            throw new ServiceException("签名值为空，无法进行证书验证");
        }
        System.out.println("random:" + random);
        System.out.println("cert:" + cert);
        System.out.println("sign:" + sign);
        HebcaCloudAPITool apiTool = new HebcaCloudAPITool("http://127.0.0.1:8089", "sjzsxfglpt", "sjzsxfglpt@123456", "all");
        BasicResponse resp1 = apiTool.execute(new CertAndSignVerifyRequest(Base64.getEncoder().encodeToString(random.getBytes()), cert, sign));
        if(resp1.getCode() != 0){
            throw new ServiceException("验证签名失败："+resp1.getCode());
        }else{
            Security.addProvider(new BouncyCastleProvider());
            CertParse cp = new CertParse(new Cert(cert),"BC2");
            certUnique = new String(cp.getSubject("G"));
            System.out.println("证书唯一项:"+certUnique);
        }
        //根据唯一性查询用户
        SysUser user = new SysUser();
        user.setCaid(certUnique);
        List<SysUser> sysUsers = userService.selectUserList(user);
        if(sysUsers.size()==0){
            SysUser adminUser = userService.selectUserById(1l);
            adminUser.setCaid(certUnique);
            boolean b = userService.updateById(adminUser);
            if(!b){
                throw new ServiceException("登录失败");
            }
            sysUsers.add(adminUser);
        }
        //生成token
        SysUser sysUser = sysUsers.get(0);
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        Map<String, Object> token = tokenService.createToken(sysUserVo);
        return R.ok(token);
    }
}
