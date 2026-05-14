package com.wehear.model;

import java.time.LocalDateTime;

public class QuizQuestion {
    private Long id;
    private Long quizId;
    private String questionText;
    private String questionType; // MULTIPLE_CHOICE
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Long relatedSignId; // Associated with a sign for showing video
    private LocalDateTime createdAt;
    
    // Virtual field for SignMedia (to show video in quiz)
    private SignMedia signMedia;

    public QuizQuestion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public Long getRelatedSignId() { return relatedSignId; }
    public void setRelatedSignId(Long relatedSignId) { this.relatedSignId = relatedSignId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public SignMedia getSignMedia() { return signMedia; }
    public void setSignMedia(SignMedia signMedia) { this.signMedia = signMedia; }
}
