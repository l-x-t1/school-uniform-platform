package com.xfgl.common.core.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 时间工具类
 *
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format)
    {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date)
    {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts)
    {
        try
        {
            return new SimpleDateFormat(format).parse(ts);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime()
    {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate()
    {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算时间差
     *
     * @param endDate 最后时间
     * @param startTime 开始时间
     * @return 时间差（天/小时/分钟）
     */
    public static String timeDistance(Date endDate, Date startTime)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 增加 LocalDateTime ==> Date
     */
    public static Date toDate(LocalDateTime temporalAccessor)
    {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 增加 LocalDate ==> Date
     */
    public static Date toDate(LocalDate temporalAccessor)
    {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 判断当前系统日期是否在给定的日期区间内
     * 需传入对应日期格式的数据
     * @param beforeDateStr
     * @param afterDateStr
     * @param timeUnit
     * @return
     */
    public static boolean dateBetween(String beforeDateStr, String afterDateStr, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "timeUnit can not be null!");
        if (StringUtils.isBlank(beforeDateStr)) {
            Objects.requireNonNull(null, "beforeDateStr can not be null!");
        }
        if (StringUtils.isBlank(afterDateStr)) {
            Objects.requireNonNull(null, "afterDateStr can not be null!");
        }
        if (timeUnit == TimeUnit.SECONDS) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
            //日期需要精确到秒来比较
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = LocalDateTime.parse(beforeDateStr, dateTimeFormatter);
            LocalDateTime after = LocalDateTime.parse(afterDateStr, dateTimeFormatter);
            return (now.isAfter(before) && now.isBefore(after)) || (now.equals(before) || now.equals(after));
        } else if (timeUnit == TimeUnit.MINUTES) {
            //日期精确到分钟
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = LocalDateTime.parse(beforeDateStr, dateTimeFormatter);
            LocalDateTime after = LocalDateTime.parse(afterDateStr, dateTimeFormatter);
            return (now.isAfter(before) && now.isBefore(after)) || (now.equals(before) || now.equals(after));
        }
        return false;
    }

    /**
     * 判断当前日期是否在给定日期之前，不包含
     * @param beforeDateStr
     * @param timeUnit
     * @return
     */
    public static boolean dateBefore(String beforeDateStr, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "timeUnit can not be null!");
        if (StringUtils.isBlank(beforeDateStr)) {
            Objects.requireNonNull(null, "beforeDateStr can not be null!");
        }
        if (timeUnit == TimeUnit.SECONDS) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
            //日期需要精确到秒来比较
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = LocalDateTime.parse(beforeDateStr, dateTimeFormatter);
            return now.isBefore(before);
        } else if (timeUnit == TimeUnit.MINUTES) {
            //日期精确到分钟
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = LocalDateTime.parse(beforeDateStr, dateTimeFormatter);
            return now.isBefore(before);
        }
        return false;
    }

    /**
     * 判断当前日期是否在给定日期之后，不包含
     * @param afterDateStr
     * @param timeUnit
     * @return
     */
    public static boolean dateAfter(String afterDateStr, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "timeUnit can not be null!");
        if (StringUtils.isBlank(afterDateStr)) {
            Objects.requireNonNull(null, "afterDateStr can not be null!");
        }
        if (timeUnit == TimeUnit.SECONDS) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
            //日期需要精确到秒来比较
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime after = LocalDateTime.parse(afterDateStr, dateTimeFormatter);
            return now.isAfter(after);
        } else if (timeUnit == TimeUnit.MINUTES) {
            //日期精确到分钟
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime after = LocalDateTime.parse(afterDateStr, dateTimeFormatter);
            return now.isAfter(after);
        }
        return false;
    }
}
