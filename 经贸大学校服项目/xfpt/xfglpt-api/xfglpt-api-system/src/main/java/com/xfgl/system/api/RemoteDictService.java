package com.xfgl.system.api;

import com.xfgl.common.core.constant.SecurityConstants;
import com.xfgl.common.core.constant.ServiceNameConstants;
import com.xfgl.common.core.domain.R;
import com.xfgl.system.api.factory.RemoteDictFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 字典数据服务
 *
 * @author wxm
 */
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteDictFallbackFactory.class)
public interface RemoteDictService {

    /**
     * 根据dict_type 获取字典map数据
     *
     * @param dictType
     * @param keyOrder 1：key为label，否则key为value
     * @param source
     * @return
     */
    @GetMapping(value = "/dict/data/getDictDataMap/{dictType}/{keyOrder}")
    public R<Map<String, String>> getDictDataMap(@PathVariable(value = "dictType") String dictType,
                                                 @PathVariable("keyOrder") Integer keyOrder,
                                                 @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
