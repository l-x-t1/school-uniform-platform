package com.xfgl.system.api.factory;

import com.xfgl.common.core.domain.R;
import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.system.api.RemoteNoticService;
import com.xfgl.system.api.RemoteUserService;
import com.xfgl.system.api.domain.SysUser;
import com.xfgl.system.api.domain.XfptNotice;
import com.xfgl.system.api.domain.NoticePublisherVo;
import com.xfgl.system.api.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author ruoyi
 */
@Component
public class RemoteNoticFallbackFactory implements FallbackFactory<RemoteNoticService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteNoticFallbackFactory.class);

    @Override
    public RemoteNoticService create(Throwable throwable)
    {
        log.error("公告服务调用失败:{}", throwable.getMessage());
        return new RemoteNoticService()
        {
            @Override
            public R<Boolean> save(XfptNotice xfptNotice, String source) {
                return R.fail("保存公告失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> updateStatus(Long id, String status, String source) {
                return R.fail("修改公告状态失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> removeNotice(Long id, String source) {
                return R.fail("删除公告失败:" + throwable.getMessage());
            }

            @Override
            public R<XfptNotice> getNoticeInfo(Long id, String source) {
                return R.fail("查询公告详情失败:" + throwable.getMessage());
            }

            @Override
            public R<NoticePublisherVo> getNoticePublisher(Long id, String source) {
                return R.fail("查询公告发布人失败:" + throwable.getMessage());
            }
        };
    }
}
