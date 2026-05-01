package com.xfgl.school.domain.vo;

import com.xfgl.school.domain.XfptQuestionnaireResult;
import lombok.Data;

import java.util.List;

@Data
public class QuestionVo {
   private List<XfptQuestionnaireResult> xfptQuestionnaireResult;
    private String signature;
}
