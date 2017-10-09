package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * A generated sentence that is an answer and has a score.
 */
public class QuizAnswer extends QuizItem implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private Long _questionId;
  
  public Long getQuestionId()          { return _questionId; }
  public void setQuestionId(Long pVal) { _questionId = pVal; }  
  
  private QuizAnswerScore _score = new QuizAnswerScore();
  
  public QuizAnswerScore getScore()                     { return _score; }
  public void            setScore(QuizAnswerScore pVal) { _score = pVal; }  
  
  public QuizAnswer()
  {
    
  }

  public QuizAnswer(String pValue)
  {
    this(pValue, null);
  }

  public QuizAnswer(String pValue,
                    Long   pQuestionId)
  {
    super(pValue);
    setQuestionId(pQuestionId);
  }
  
  @Override
  public String getValue() { return super.getValue() + "."; }
}
