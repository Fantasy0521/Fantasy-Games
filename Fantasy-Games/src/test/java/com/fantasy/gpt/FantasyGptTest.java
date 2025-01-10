package com.fantasy.gpt;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fantasy.util.TFIDFUtil;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.junit.jupiter.api.Test;
import org.apdplat.word.segmentation.SegmentationAlgorithm;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


public class FantasyGptTest {

    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a game recommendation assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("能推荐给我一款国产角色扮演3a游戏吗？")
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .apiKey("")
                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen2.5-coder-3b-instruct")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }
    public static void main(String[] args) {
        try {
            long l = System.currentTimeMillis();
            GenerationResult result = callWithMessage();
            System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
            System.out.println("耗时："+(System.currentTimeMillis()-l));
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.err.println("错误信息："+e.getMessage());
            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        }
        System.exit(0);
    }

    @Test
    public void test(){
        List<Word> words = WordSegmenter.seg("杨尚川是APDPlat应用级产品开发平台的作者");
        List<Word> words3 = WordSegmenter.seg("能给我推荐一个动作冒险类型的游戏吗");
        List<Word> words2 = WordSegmenter.segWithStopWords("能给我推荐一个动作冒险类型的游戏吗");
        System.out.println(words3);

    }

}
