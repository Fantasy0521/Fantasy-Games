package com.fantasy.service;

import com.fantasy.entity.Answer;

public interface IFantasyGptService {
    Answer questAI(String question,Long answerId);

    Answer retry(Long answerId,String question);

    void pick(Long answerId);
}
