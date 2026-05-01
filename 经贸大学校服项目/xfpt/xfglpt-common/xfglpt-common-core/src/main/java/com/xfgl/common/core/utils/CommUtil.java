package com.xfgl.common.core.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommUtil {
    /**
     * 列表对象去重
     */
    public static  <K, T> Predicate<K> distinctPredicate(Function<K, T> function) {
        // 因为stream流是多线程操作所以需要使用线程安全的ConcurrentHashMap
        ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();
        return t -> null == map.putIfAbsent(function.apply(t), true);
    }
}
