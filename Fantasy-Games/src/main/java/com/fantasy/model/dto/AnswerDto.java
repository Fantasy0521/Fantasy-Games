package com.fantasy.model.dto;

import com.fantasy.entity.Answer;
import com.fantasy.entity.Keyword;
import lombok.Data;

import java.util.List;

@Data
public class AnswerDto extends Answer {

    private Long keywordId;

    private List<Keyword> keywords;

}
