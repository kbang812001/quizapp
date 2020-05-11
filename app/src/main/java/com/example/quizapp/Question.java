package com.example.quizapp;

public class Question {

    String Question;
    String optionA;
    String optionB;
    String optionC;
    String OptionD;
    int correctAns;

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return OptionD;
    }

    public void setOptionD(String optionD) {
        OptionD = optionD;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public Question(String question, String optionA, String optionB, String optionC, String optionD, int correctAns) {
        Question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        OptionD = optionD;
        this.correctAns = correctAns;
    }
}
