package com.iyuba.ieltslistening.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

// 文章的每个句子
@Data
@AllArgsConstructor
@ToString
public class Sentence {
    private int voaId;
    private int paraId;
    private int idIndex;
    private String sentence;
    private String sentenceCn;
    private String timing;
    private String endTiming;
    private boolean isPlaying; // 当前句子是否在播放
}
