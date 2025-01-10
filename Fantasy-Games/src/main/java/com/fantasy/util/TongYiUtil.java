package com.fantasy.util;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fantasy.model.bean.TongYiConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author: fantasy
 * 通义语大模型调用工具
 */
@Component
public class TongYiUtil {

    @Autowired
    private TongYiConf tongYiConf;

    public String questAI(String question) {
        try {
            GenerationResult generationResult = callWithMessage(question);
            String result = generationResult.getOutput().getChoices().get(0).getMessage().getContent();
            return result;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.err.println("错误信息："+e.getMessage());
//            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
            throw new RuntimeException(e);
        }
    }


    public GenerationResult callWithMessage(String question) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(tongYiConf.getRole())
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(question)
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey(tongYiConf.getApiKey())
                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(tongYiConf.getModel())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

}
