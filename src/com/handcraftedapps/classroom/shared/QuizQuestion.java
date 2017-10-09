package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * A generated sentence that is a question.
 */
public class QuizQuestion extends QuizItem implements Serializable
{
  private static final long serialVersionUID = 1L;

  public QuizQuestion()
  {
    
  }

  public QuizQuestion(String pValue)
  {
    super(pValue);
  }
  
  @Override
  public String getValue() { return super.getValue() + "?"; }
}
