package com.xfgl.school.service.impl;

import com.xfgl.common.core.utils.StringUtils;
import com.xfgl.school.domain.vo.XfptJYNoticeSchoolStatisticsVO;
import com.xfgl.school.domain.vo.XfptJYNoticeStatisticsVO;
import com.xfgl.school.mapper.XfptSchoolStatisticsMapper;
import com.xfgl.school.service.IXfptSchoolStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class XfptSchoolStatisticsServiceImpl implements IXfptSchoolStatisticsService {

    @Autowired
    private XfptSchoolStatisticsMapper schoolStatisticsMapper;

    @Override
    public List<XfptJYNoticeStatisticsVO> getJYNoticeStatisticsList(XfptJYNoticeStatisticsVO jYNoticeStatisticsVO) {
        return schoolStatisticsMapper.getJYNoticeStatisticsList(jYNoticeStatisticsVO);
    }

    @Override
    public List<XfptJYNoticeSchoolStatisticsVO> getJYNoticeSchoolStatisticsList(XfptJYNoticeSchoolStatisticsVO jYNoticeSchoolStatisticsVO) {
        List<XfptJYNoticeSchoolStatisticsVO> jyNoticeSchoolStatisticsList = schoolStatisticsMapper.getJYNoticeSchoolStatisticsList(jYNoticeSchoolStatisticsVO);
        for (XfptJYNoticeSchoolStatisticsVO xfptJYNoticeSchoolStatisticsVO : jyNoticeSchoolStatisticsList) {
            xfptJYNoticeSchoolStatisticsVO.setHasFirstZjCount(xfptJYNoticeSchoolStatisticsVO.getHasZjCount());
            //处理占比
            String hasFirstZjCountStr = xfptJYNoticeSchoolStatisticsVO.getHasFirstZjCount();
            String ggCount = xfptJYNoticeSchoolStatisticsVO.getGgCount();
            if (StringUtils.isBlank(hasFirstZjCountStr) || hasFirstZjCountStr.equals("0")) {
                xfptJYNoticeSchoolStatisticsVO.setHasZjRate("0%");
                xfptJYNoticeSchoolStatisticsVO.setHasFirstZjRate("0%");
            } else {
                //首次质检和有质检数值是等价的
                String hasFirstZjCount = xfptJYNoticeSchoolStatisticsVO.getHasFirstZjCount();
                String hasZjRate = bigDecimalToString(hasFirstZjCount, ggCount);
                xfptJYNoticeSchoolStatisticsVO.setHasZjRate(hasZjRate + "%");
                xfptJYNoticeSchoolStatisticsVO.setHasFirstZjRate(hasZjRate + "%");
            }
            //二次质检
            String hasSecondZjCount = xfptJYNoticeSchoolStatisticsVO.getHasSecondZjCount();
            String hasSecondZjRate = bigDecimalToString(hasSecondZjCount, ggCount);
            xfptJYNoticeSchoolStatisticsVO.setHasSecondZjRate(hasSecondZjRate + "%");
            //无质检
            String noZjCount = xfptJYNoticeSchoolStatisticsVO.getNoZjCount();
            String noZjRate = bigDecimalToString(noZjCount, ggCount);
            xfptJYNoticeSchoolStatisticsVO.setNoZjRate(noZjRate + "%");
        }
        return jyNoticeSchoolStatisticsList;
    }

    public static String bigDecimalToString(String first, String second) {
        if (first == null || second == null || first.equals("0") || second.equals("0")) {
            return "0";
        }
        BigDecimal one = new BigDecimal(first);
        BigDecimal two = new BigDecimal(second);
        BigDecimal result = one.divide(two, 1, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        return result.toString();
    }
}
