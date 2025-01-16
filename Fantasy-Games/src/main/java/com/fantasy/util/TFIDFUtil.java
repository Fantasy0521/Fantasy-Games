package com.fantasy.util;

import com.fantasy.entity.Question;
import com.fantasy.mapper.QuestionMapper;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TFIDFUtil {

    //语料库中所有词的TF-IDF缓存值 每天更新一次(todo Redis优化)
    static Map<String, Double> wordTFIDFAll = new ConcurrentHashMap<>();


    /**
     * 计算TF-IDF 由于语料库更新频繁，这里使用缓存
     * 问题长短不一 导致结果不准 这里适当对TF的值进行修改 TF = 某个词在语料库中出现的次数/语料库的总词数
     * @param content
     * @return 实时数据的关键词
     */
    public static List<String> calculateTFIDF(String content, List<List<String>> docs) {
        //对问题进行分词
        List<String> words = splitContent(content);
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : words) {
            Integer count = wordCountMap.get(word);
            if (count == null){
                wordCountMap.put(word, 1);
            }else {
                wordCountMap.put(word, count + 1);
            }
        }
        //1 计算词频TF
        //语库（语料库）这里就是所有问题的分词结果
//        List<List<String>> docs = questionService.list().stream().map(Question::getContent).map(TFIDFUtil::splitContent).collect(Collectors.toList());
        docs.add(words);


        //用于统计词频的数组
        List<String> wordList = new ArrayList<>();

        //将docs中的所有词加入到wordList中
        docs.stream().forEach(wordList::addAll);
        //groupby <词语,词频>
        Map<String, Long> wordCount = wordList.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        //统计词频
//        Map<String, Long> wordCountDoc = words.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));
        Map<String, Double> wordTF = new HashMap<>();
        //公式（1）
        wordCount.keySet().stream().forEach(key -> {
            wordTF.put(key, wordCount.get(key) / Integer.valueOf(wordList.size()).doubleValue());
        });
        //2 计算逆文档频率IDF

        //用来缓存所有文档中去除重复后的词。
        Set<String> uniqueWords = new HashSet<>();
        //用来缓存词在文档中出现的个数。
        Map<String, Long> wordDocCount = new HashMap<>();
        //统计唯一词
        docs.stream().forEach(tokens -> {
            uniqueWords.addAll(tokens);
        });
        //统计不同词在多少个文档中出现
        docs.stream().forEach(doc -> {
            uniqueWords.stream().forEach(token -> {
                if (doc.contains(token)) {
                    if (!wordDocCount.containsKey(token)) {
                        wordDocCount.put(token, 0l);
                    }
                    wordDocCount.put(token, wordDocCount.get(token) + 1);
                }
            });
        });
        //计算IDF
        Map<String, Double> wordIDF = new HashMap<>();
        //公式（2）
        wordDocCount.keySet().stream().forEach(key -> {
            wordIDF.put(key, Math.log(Float.valueOf(Integer.valueOf(docs.size()).floatValue() / (wordDocCount.get(key)) + 1).doubleValue()));
        });

//        计算出 TF与IDF之后，利用公式（3）计算，不同词的 TF-IDF

        Map<String, Double> wordTFIDF = new HashMap<>();


        uniqueWords.stream().forEach(token -> {
            if (content != null && wordTF.get(token) != null && wordCountMap.get(token) != null) {
                wordTFIDF.put(token,wordTF.get(token)  * wordIDF.get(token));
            }
            wordTFIDFAll.put(token,wordTF.get(token)  * wordIDF.get(token));
        });

        //获取TF-IDF排行前半的
        List<String> result;
        if (content != null){
            result = wordTFIDF.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(wordCountMap.size()/2).map(Map.Entry::getKey).collect(Collectors.toList());
        }else {
            result = wordTFIDFAll.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(wordTFIDFAll.size()/2).map(Map.Entry::getKey).collect(Collectors.toList());
        }
        return result;
    }

    /**
     * 计算TF-IDF 由于语料库更新频繁，这里使用缓存
     * 严格遵守TF-IDF的公式 TF = 某个词在文档出现的次数/文档的总词数
     * @param content
     * @param questionMapper
     * @return 实时数据的关键词
     */
    public static List<String> calculateTFIDFReal(String content, QuestionMapper questionMapper) {
        //对问题进行分词
        List<String> wordList = splitContent(content);
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : wordList) {
            Integer count = wordCountMap.get(word);
            if (count == null){
                wordCountMap.put(word, 1);
            }else {
                wordCountMap.put(word, count + 1);
            }
        }
        //1 计算词频TF

        //groupby <词语,词频>
        Map<String, Long> wordCount = wordList.stream().collect(Collectors.groupingBy(String::toString, Collectors.counting()));

        Map<String, Double> wordTF = new HashMap<>();
        //公式（1）
        wordCount.keySet().stream().forEach(key -> {
            wordTF.put(key, wordCount.get(key) / Integer.valueOf(wordList.size()).doubleValue());
        });
        //2 计算逆文档频率IDF
        //语库（语料库）这里就是所有问题的分词结果
        List<List<String>> docs = new ArrayList<>();
//        List<List<String>> docs = questionService.list().stream().map(Question::getContent).map(TFIDFUtil::splitContent).collect(Collectors.toList());
        docs.add(wordList);
//        List<Question> questions = questionMapper.selectList(null);
//        for (Question question : questions) {
//            docs.add(splitContent(question.getContent()));
//        }
        //模拟语料库
        docs.add(splitContent("能给我推荐一个你喜欢的游戏吗"));
        docs.add(splitContent("有没有一款好玩的动作冒险游戏"));
        docs.add(splitContent("请介绍一款角色扮演游戏"));
        docs.add(splitContent("请介绍一款剧情冒险游戏"));
        //用来缓存所有文档中去除重复后的词。
        Set<String> uniqueWords = new HashSet<>();
        //用来缓存词在文档中出现的个数。
        Map<String, Long> wordDocCount = new HashMap<>();
        //统计唯一词
        docs.stream().forEach(tokens -> {
            uniqueWords.addAll(tokens);
        });
        //统计不同词在多少个文档中出现
        docs.stream().forEach(doc -> {
            uniqueWords.stream().forEach(token -> {
                if (doc.contains(token)) {
                    if (!wordDocCount.containsKey(token)) {
                        wordDocCount.put(token, 0l);
                    }
                    wordDocCount.put(token, wordDocCount.get(token) + 1);
                }
            });
        });
        //计算IDF
        Map<String, Double> wordIDF = new HashMap<>();
        //公式（2）
        wordDocCount.keySet().stream().forEach(key -> {
            wordIDF.put(key, Math.log(Float.valueOf(Integer.valueOf(docs.size()).floatValue() / (wordDocCount.get(key)) + 1).doubleValue()));
        });

//        计算出 TF与IDF之后，利用公式（3）计算，不同词的 TF-IDF

        Map<String, Double> wordTFIDF = new HashMap<>();


        uniqueWords.stream().forEach(token -> {
            if (content != null && wordTF.get(token) != null && wordCountMap.get(token) != null) {
                wordTFIDF.put(token,wordTF.get(token)  * wordIDF.get(token));
            }
        });

        //获取TF-IDF排行前半的
        List<String> result = wordTFIDF.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(wordCountMap.size()/2).map(Map.Entry::getKey).collect(Collectors.toList());
        return result;
    }

    /**
     * 将文本内容分词 会移除停用词
     * @param content
     * @return
     */
    public static List<String> splitContent(String content) {
        if (content == null) {
            return new ArrayList<>();
        }
        List<Word> seg = WordSegmenter.seg(content);
        List<String> words = seg.stream().map(Word::getText).collect(Collectors.toList());
        return words;
    }

    /**
     * 将文本内容分词 会移除停用词 以及 自定义停用词
     * @param content
     * @param noKeywordMap 自定义的停用词
     * @return
     */
    public static List<String> splitContentRemoveNoKeyWord(String content,Map<String,Integer> noKeywordMap) {
        if (content == null) {
            return new ArrayList<>();
        }
        List<Word> seg = WordSegmenter.seg(content);
        List<String> words = seg.stream().filter(word -> noKeywordMap.get(word.getText()) == null).map(Word::getText).collect(Collectors.toList());
        return words;
    }

    /**
     * 将文本内容分词 会移除停用词
     * @param content
     * @param noKeywordMap 自定义的停用词
     * @return
     */
    public static Map<String,Integer> splitUniqueContent(String content,Map<String,Integer> noKeywordMap) {
        List<String> wordList = splitContent(content);
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String word : wordList) {
            if (noKeywordMap.get(word) != null) {
                continue;
            }
            Integer count = wordCountMap.get(word);
            if (count == null){
                wordCountMap.put(word, 1);
            }else {
                wordCountMap.put(word, count + 1);
            }
        }
        return wordCountMap;
    }

    /**
     * 通过TF-IDF算法提取关键词 这里取缓存值
     * @param content
     * @param topN
     * @return 缓存中的关键词
     */
    public static List<String> getKeyWords(String content, Integer topN) {
        List<String> tokens = splitContent(content);
        if (topN == null) {
            topN = tokens.size()/2;
        }
        List<List<String>> tokensArr = tokens.stream().filter(token -> wordTFIDFAll.containsKey(token))
                .map(token -> Arrays.asList(token, String.valueOf(wordTFIDFAll.get(token))))
                .sorted(Comparator.comparing(t -> Double.valueOf(t.get(1)))).collect(Collectors.toList());
        Collections.reverse(tokensArr);
        if (tokensArr.size() < topN) {
            topN = tokensArr.size();
        }
        List<String> keywords = tokensArr.subList(0, topN - 1).stream().map(tokenValue -> tokenValue.get(0)).collect(Collectors.toList());
        return keywords;
    }

    /**
     * 通过TF-IDF算法提取关键词的权重 这里取缓存值
     * @param content
     * @return 缓存中的关键词的权重
     */
    public static Double getKeyWordWeight(String content) {
        return wordTFIDFAll.get(content);
    }

    /**
     * 通过TF-IDF算法提取关键词和TFIDF值 这里取缓存值
     * @param content
     * @param topN
     * @return 缓存中的关键词
     */
    public static Map<String,Double> getKeyWordsAndTFIDF(String content, Integer topN) {
        List<String> tokens = splitContent(content);
        if (topN == null) {
            topN = tokens.size()/2;
        }
        List<List<String>> tokensArr = tokens.stream().filter(token -> wordTFIDFAll.containsKey(token))
                .map(token -> Arrays.asList(token, String.valueOf(wordTFIDFAll.get(token))))
                .sorted(Comparator.comparing(t -> Double.valueOf(t.get(1)))).collect(Collectors.toList());
        Collections.reverse(tokensArr);
        if (tokensArr.size() < topN) {
            topN = tokensArr.size();
        }
        List<String> keywords = tokensArr.subList(0, topN - 1).stream().map(tokenValue -> tokenValue.get(0)).collect(Collectors.toList());
        Map<String,Double> map = new HashMap<>();
        for (String key : keywords) {
            Double value = map.get(key);
            if (value != null) {
                //如果存在，则累加
                map.put(key, value + wordTFIDFAll.get(key));
            }else {
                map.put(key,wordTFIDFAll.get(key));
            }
        }
        return map;
    }

}
