package com.fantasy.service;

import com.fantasy.entity.Answer;

import java.util.List;

public interface IFantasyGptService {
    Answer questAI(String question,List<Answer> excludeAnswers);

    Answer retry(Long answerId,List<Answer> excludeAnswers, String question);

    void pick(Long answerId);
}
