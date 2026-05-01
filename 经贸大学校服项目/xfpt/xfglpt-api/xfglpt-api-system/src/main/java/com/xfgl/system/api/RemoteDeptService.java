package com.xfgl.system.api;

import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.constant.ServiceNameConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.factory.RemoteDeptFallbackFactory;
import com.xfgl.system.api.factory.RemoteUserFallbackFactory;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteDeptService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/dept/update")
    public R<AjaxResult> updateDeptInfo(@RequestBody SysDept dept, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/dept/deptList/{parentDeptId}")
    R<List<SysDept>> getDeptDataList(@PathVariable("parentDeptId") Long parentDeptId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/dept/getDeptListByDeptId/{deptId}")
    public R<List<SysDept>> getDeptListByDeptId(@PathVariable(value = "deptId") Long deptId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
