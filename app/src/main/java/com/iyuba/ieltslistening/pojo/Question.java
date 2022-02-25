package com.iyuba.ieltslistening.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Question {
    private String titleNum;
    private String quesText;
    private String answerText;
    private String quesImage;
    private String answer;
}
