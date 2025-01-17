package com.fantasy.model.vo;

import com.fantasy.entity.Answer;
import com.fantasy.entity.Keyword;
import com.fantasy.entity.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionVo extends Question {

    private Answer answer;

    private List<Keyword> keywords;

}
