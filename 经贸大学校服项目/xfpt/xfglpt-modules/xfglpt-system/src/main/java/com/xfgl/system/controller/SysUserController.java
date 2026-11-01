package com.xfgl.system.controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.hebca.cloud.sdk.api.request.CertAndSignVerifyRequest;
import com.hebca.cloud.sdk.api.response.BasicResponse;
import com.hebca.cloud.sdk.tools.HebcaCloudAPITool;
import com.hebca.pki.Cert;
import com.hebca.pki.CertParse;
import com.xfgl.common.core.constant.CacheConstants;
import com.xfgl.common.core.exception.ServiceException;
import com.xfgl.common.core.utils.RsaUtils;
import com.xfgl.common.redis.service.RedisService;
import com.xfgl.system.mp.MpUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.common.core.utils.poi.ExcelUtil;
import com.xfgl.common.core.web.controller.BaseController;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.common.core.web.page.TableDataInfo;
import com.xfgl.common.log.annotation.Log;
import com.xfgl.common.log.enums.BusinessType;
import com.xfgl.common.security.annotation.InnerAuth;
import com.xfgl.common.security.annotation.RequiresPermissions;
import com.xfgl.common.security.service.TokenService;
import com.xfgl.common.security.utils.SecurityUtils;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysRole;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import com.xfgl.system.service.ISysConfigService;
import com.xfgl.system.service.ISysDeptService;
import com.xfgl.system.service.ISysPermissionService;
import com.xfgl.system.service.ISysPostService;
import com.xfgl.system.service.ISysRoleService;
import com.xfgl.system.service.ISysUserService;

/**
 * 用户信息
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController {
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

    /**
     * 获取用户列表
     */
    @RequiresPermissions("system:user:list")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {

        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        String filename = file.getOriginalFilename();
        if (!filename.contains(".xls") || !filename.contains(".xlsx")) {
            return AjaxResult.warn("请上传xls或者xlsx格式文件");
        }
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        for (SysUser sysUser : userList) {
            sysUser.setCreateTime(new Date());
        }
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<LoginUser> info(@PathVariable("username") String username) {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        LoginUser sysUserVo = new LoginUser();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 注册用户信息
     */
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return R.fail("当前系统没有开启注册功能！");
        }
        if (!userService.checkUserNameUnique(sysUser)) {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(userService.registerUser(sysUser));
    }

    /**
     * 内部新增用户（跳过注册开关，供供应商入驻审核等内部流程使用）
     */
    @InnerAuth
    @PostMapping("/addInner")
    public R<Boolean> addInner(@RequestBody SysUser sysUser) {
        String username = sysUser.getUserName();
        if (!userService.checkUserNameUnique(sysUser)) {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(userService.registerUser(sysUser));
    }

    /**
     * 记录用户登录IP地址和登录时间
     */
    @InnerAuth
    @PutMapping("/recordlogin")
    public R<Boolean> recordlogin(@RequestBody SysUser sysUser) {
        return R.ok(userService.updateUserProfile(sysUser));
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getSysUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();

        SysUser sysUser = userService.selectUserById(user.getUserId());
        String mac = getMac(sysUser);
        if(StrUtil.isNotEmpty(sysUser.getIntegrity())){
            if(StrUtil.isEmpty(mac) || !StrUtil.equals(mac, sysUser.getIntegrity())){
                //验证完整性
                return ajax.error("数据被篡改，完整性校验失败");
            }
        }


        //查询用户手机号是否填写
        if (StrUtil.isNotEmpty(sysUser.getPhone())) {
            ajax.put("isPhone", 1);
            //解密
            user.setPhone(mpUtil.decrypt(sysUser.getPhone(), mpUtil.getToken()));
        } else {
            ajax.put("isPhone", 0);
        }

        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @RequiresPermissions("system:user:query")
    @GetMapping(value = {"/", "/{userId}"})
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        AjaxResult ajax = AjaxResult.success();
        if (StringUtils.isNotNull(userId)) {
            userService.checkUserDataScope(userId);
            SysUser sysUser = userService.selectUserById(userId);
            String mac = getMac(sysUser);
            if(StrUtil.isNotEmpty(sysUser.getIntegrity())){
                if(StrUtil.isEmpty(mac) || !StrUtil.equals(mac, sysUser.getIntegrity())){
                    //验证完整性
                    return ajax.error("数据被篡改，完整性校验失败");
                }
            }

            if (StrUtil.isNotEmpty(sysUser.getPhone())) {
                String phoneDec = mpUtil.decrypt(sysUser.getPhone(), mpUtil.getToken());
                if(StrUtil.isEmpty(phoneDec)){
                    return AjaxResult.error("解密失败");
                }
                sysUser.setPhone(phoneDec);
            }

            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        return ajax;
    }

    /**
     * 新增用户
     */
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user) {
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        //加密
        String token = mpUtil.getToken();
        String passwrodEnc = mpUtil.encrypt(SecurityUtils.encryptPassword(user.getPassword()), token);
        if (StrUtil.isEmpty(passwrodEnc)) {
            return AjaxResult.error("加密失败");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(passwrodEnc);
        if (StrUtil.isNotEmpty(user.getPhone())) {
            String phoneEnc = mpUtil.encrypt(user.getPhone(), token);
            if (StrUtil.isEmpty(phoneEnc)) {
                return AjaxResult.error("加密失败");
            }
            user.setPhone(phoneEnc);
        }
        //增加完整性
        String mac = getMac(user);
        user.setIntegrity(mac);
        return toAjax(userService.insertUser(user));
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
        //if(StrUtil.isNotEmpty(user.getPhone())){
        //    text=text+ user.getPhone();
        //}
        if(user.getDeptId()!=null){
            text=text+ user.getDeptId();
        }
        System.out.println("userInfo：完整性验证:============"+text);
        String mac = mpUtil.mac(text, mpUtil.getToken());
        return mac;
    }

    /**
     * 修改用户
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        if (StrUtil.isNotEmpty(user.getPhone())) {
            String phoneEnc = mpUtil.encrypt(user.getPhone(), mpUtil.getToken());
            if (StrUtil.isEmpty(phoneEnc)) {
                return AjaxResult.error("加密失败");
            }
            user.setPhone(phoneEnc);
        }
        //完整性
        String mac = getMac(user);
        user.setIntegrity(mac);
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, SecurityUtils.getUserId())) {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        String pwd = SecurityUtils.encryptPassword(user.getPassword());
        String encrypt = mpUtil.encrypt(pwd, mpUtil.getToken());
        if (StrUtil.isEmpty(encrypt)) {
            return AjaxResult.error("加密失败");
        }
        user.setPassword(encrypt);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @RequiresPermissions("system:user:query")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId) {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @RequiresPermissions("system:user:list")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept) {
        return success(deptService.selectDeptTreeList(dept));
    }


    /**
     * 登录方法
     *
     * @param
     * @return 结果
     */
    @GetMapping("/isPwdExpire")
    public AjaxResult isPwdExpire(String username) {
        boolean pwdExpire = userService.isPwdExpire(username);
        if (pwdExpire) {
            return AjaxResult.success(0);
        }
        return AjaxResult.success(1);
    }

    @Autowired
    private RedisService redisService;

    /**
     * 重置密码
     */
    @PostMapping("/resetPwdSendSms")
    public AjaxResult resetPwdSendSms(@RequestBody SysUser user) throws Exception {
        String token = mpUtil.getToken();
        //判断是否存在用户
        Long count = userService.lambdaQuery().eq(SysUser::getPhone, mpUtil.encrypt(user.getPhone(), token)).count();
        if (count == 0) {
            return AjaxResult.warn("手机号不存在");
        }
        //验证验证码是否正确
        String redisCode = redisService.getCacheObject(CacheConstants.SMS + user.getPhone());
        if (redisCode == null) {
            return AjaxResult.warn("验证码已过期");
        }
        if (!redisCode.equals(user.getCode())) {
            return AjaxResult.warn("验证码错误");
        }
        //删除redis中的验证码
        boolean b = redisService.deleteObject(CacheConstants.SMS + user.getPhone());
        if (!b) {
            return AjaxResult.error("系统错误");
        }


        boolean update = userService.lambdaUpdate().eq(SysUser::getPhone, mpUtil.encrypt(user.getPhone(), token))
                .set(SysUser::getPassword, mpUtil.encrypt(SecurityUtils.encryptPassword(RsaUtils.decryptByPrivateKey(user.getPassword())), token))
                .update();
        return toAjax(update);
    }

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //@GetMapping("/getRandom")@CrossOrigin
    public String getRandom() throws Exception {
        SecureRandom random = new SecureRandom();
        StringBuilder getRandom = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            getRandom.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return getRandom.toString();
    }
    //@PostMapping("/verifyCertAndSign")@CrossOrigin
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
        HebcaCloudAPITool apiTool = new HebcaCloudAPITool("https://cloud.tecshield.com", "hebca_test", "hebcatest@2025", "all");
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
            SysUser adminUser = userService.getById(1l);
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
