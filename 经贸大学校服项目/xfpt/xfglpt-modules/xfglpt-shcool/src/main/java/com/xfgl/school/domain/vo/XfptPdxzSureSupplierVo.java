package com.xfgl.school.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xfgl.common.core.annotation.Excel;
import com.xfgl.school.domain.XfptPdxzSureSupplier;
import com.xfgl.school.domain.XfptQuestionnaireConfig;
import com.xfgl.school.domain.XfptQuestionnaireSurvey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class XfptPdxzSureSupplierVo  {


    private XfptPdxzSureSupplier xfptPdxzSureSupplier;

    private XfptQuestionnaireConfig xfptQuestionnaireConfig;

}
