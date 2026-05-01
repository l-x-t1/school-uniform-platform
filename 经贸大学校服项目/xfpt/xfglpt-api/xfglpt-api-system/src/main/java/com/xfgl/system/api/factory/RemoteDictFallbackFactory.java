package com.xfgl.system.api.factory;

import com.xfgl.common.core.domain.R;
import com.xfgl.system.api.RemoteDictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 字典降级处理
 *
 * @author wxm
 */
@Component
public class RemoteDictFallbackFactory implements FallbackFactory<RemoteDictService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteDictFallbackFactory.class);

    @Override
    public RemoteDictService create(Throwable throwable) {
        log.error("字典服务调用失败:{}", throwable.getMessage());
        return new RemoteDictService() {
            @Override
            public R<Map<String, String>> getDictDataMap(String dictType, Integer keyOrder,String source) {
                return R.fail("获取字典数据失败:" + throwable.getMessage());
            }
        };
    }
}
