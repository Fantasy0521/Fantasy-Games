package com.fantasy.service;

import com.fantasy.entity.Answer;

public interface IFantasyGptService {
    Answer questAI(String question);

    Answer retry(Long answerId,String question);

    void pick(Long answerId);
}
