package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * Super rudimentary for now - just a numeric score, 0-100
 */
public class QuizAnswerScore implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private double _scoreValue;       // 0-100
  
  public double  getScoreValue()            { return _scoreValue; }
  public void    setScoreValue(double pVal) { _scoreValue = pVal; }
  
  public QuizAnswerScore()
  {
    
  }

  public QuizAnswerScore(int pGrade)
  {
    _scoreValue = pGrade;
  }
}
