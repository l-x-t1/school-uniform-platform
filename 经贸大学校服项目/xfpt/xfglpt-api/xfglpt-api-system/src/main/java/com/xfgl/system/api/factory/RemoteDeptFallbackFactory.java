package com.xfgl.system.api.factory;

import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.RemoteDeptService;
import com.xfgl.system.api.RemoteUserService;
import com.xfgl.system.api.domain.SysDept;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteDeptFallbackFactory.class);

    @Override
    public RemoteDeptService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteDeptService()
        {


            @Override
            public R<AjaxResult> updateDeptInfo(SysDept dept, String source) {
                return null;
            }
            @Override
            public R<List<SysDept>> getDeptDataList(Long parentDeptId, String source) {
                return R.fail("获取部门数据失败:" + throwable.getMessage());
            }

            @Override
            public R<List<SysDept>> getDeptListByDeptId(Long parentDeptId, String source) {
                return R.fail("获取部门数据失败:" + throwable.getMessage());
            }
        };
    }
}
