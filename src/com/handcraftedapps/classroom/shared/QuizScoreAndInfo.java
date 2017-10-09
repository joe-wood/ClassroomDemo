package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

public class QuizScoreAndInfo implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private QuizAnswerScore _quizScore;
  private int             _week;
  private int             _index;  

  public QuizAnswerScore getQuizScore() { return _quizScore; }
  public int             getWeek     () { return _week;      }
  public int             getIndex    () { return _index;     }  

  public QuizScoreAndInfo()
  {
    
  }

  public QuizScoreAndInfo(QuizAnswerScore pQuizScore,
                          int             pWeek,
                          int             pIndex)
  {
    _quizScore = pQuizScore;
    _week      = pWeek;
    _index     = pIndex;  
  }
}
