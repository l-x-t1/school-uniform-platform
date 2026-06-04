package com.xfgl.system.api;

import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.constant.ServiceNameConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.api.domain.NoticePublisherVo;
import com.xfgl.system.api.factory.RemoteNoticFallbackFactory;
import com.xfgl.system.api.factory.RemoteUserFallbackFactory;
import com.xfgl.system.api.model.LoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "remoteNoticService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteNoticFallbackFactory.class)
public interface RemoteNoticService
{


    /**
     * 保存公告
     * @param xfptNotice
     * @param source
     * @return
     */
    @PostMapping("/xfptnotice/save")
    public R<Boolean> save(@RequestBody XfptNotice xfptNotice, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) ;

    /**
     * 修改公告状态
     * @param id 关联公告id
     * @param status 状态
     * @param source
     * @return
     */
    @PostMapping("/xfptnotice/updateStatus/{id}/{status}")
    public R<Boolean> updateStatus(@PathVariable("id") Long id,@PathVariable("status") String status, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) ;

    @PostMapping("/xfptnotice/removeNotice/{id}")
    public R<Boolean> removeNotice(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/xfptnotice/inner/{id}")
    public R<XfptNotice> getNoticeInfo(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/xfptnotice/inner/publisher/{id}")
    public R<NoticePublisherVo> getNoticePublisher(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
