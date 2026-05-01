package com.xfgl.school.controller;

import com.xfgl.common.core.web.domain.AjaxResult;
import com.xfgl.school.domain.XfptParentsFeedback;
import com.xfgl.school.service.IXfptParentsFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school/open")
@Slf4j
public class SchoolOpenController {

    @Autowired
    private IXfptParentsFeedbackService iXfptParentsFeedbackService;

    @PostMapping("/addYjjy")
    public AjaxResult addYjjy(@RequestBody XfptParentsFeedback xfptParentsFeedback){
        boolean save = iXfptParentsFeedbackService.save(xfptParentsFeedback);
        if(!save){
            return AjaxResult.error("系统错误");
        }
        return AjaxResult.success("提交成功");
    }
}
