package com.xfgl.system.mapper;

import com.xfgl.system.domain.vo.FeedBackVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OpenMapper {

    @Select("SELECT * FROM XFPT_PARENTS_FEEDBACK WHERE IS_PUBLIC='1' AND REPLY_CONTENT is NOT NULL order by create_time desc")
    List<FeedBackVo> getfeednackList();


    @Select("SELECT * FROM XFPT_PARENTS_FEEDBACK WHERE id=#{id}")
    FeedBackVo getfeednackInfo(Long id);

    @Select("SELECT count(*) FROM XFPT_QUESTIONNAIRE_CONFIG WHERE id=#{id}  and names LIKE CONCAT('%', #{check}, '%')")
    Long checkUserAndPhone(@Param("check") String check,@Param("id") Long id);
}
