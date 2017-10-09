package com.handcraftedapps.classroom.shared;


import java.io.Serializable;

import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * Grade on a quiz.Average of answer scores; assumes
 * all answer scores weigh equally.
 */
public class QuizStudentGrade extends UniqueItem implements Serializable, IComplexSerialized
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Rehydrate all the Long-ID-based items into real items from the repository
   */
  private transient boolean _rehydrateCalled = false;
  private transient boolean _rehydrated      = false;
  
  @Override
  public boolean isRehydrated() { return _rehydrated; }
  
  @Override
  public synchronized void rehydrate(final IRehydrateCallback pCallback)
  {
    if (_rehydrateCalled) return;
 
    final UniqueItem parent = this;
    
    ObjectRepository.getInstance().fetchItem(getAnswersId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          setQuizStudentAnswers((QuizStudentAnswers) pItem);
          _rehydrated = true;
          if (pCallback != null)
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "QuizStudentAnswers");
        }
      }});

    _rehydrateCalled = true;
  }

  /**
   * Classroom object/id - object for local use, ID for serialization
   */
  private Long _answersId;
  public  Long getAnswersId() { return _answersId; }

  private transient QuizStudentAnswers _answers;
  
  public QuizStudentAnswers getAnswers()  { return _answers;  }
  
  public void setQuizStudentAnswers(QuizStudentAnswers pAnswers) 
  { 
    _answers = pAnswers; 
    
    setScore(new QuizAnswerScore());
    
    if ((_answers != null) && !_answers.getAnswers().isEmpty())
    {
      double totalScore = 0;
      
      for (QuizAnswer answer : _answers.getAnswers())
      {
        totalScore += answer.getScore().getScoreValue();
      }
      
      totalScore /= _answers.getAnswers().size(); 
      
      getScore().setScoreValue((int)Math.rint(totalScore));
    }
    
    _answersId = (pAnswers == null) ? null : pAnswers.getId();
  }

  private QuizAnswerScore _score;
  
  public void setScore(QuizAnswerScore pVal)
  { 
    _score = pVal; 
  }

  public QuizAnswerScore getScore()
  { 
    return _score; 
  }

  public QuizStudentGrade()
  {
  }

  public QuizStudentGrade(QuizStudentAnswers pAnswers)
  {
    setQuizStudentAnswers(pAnswers);
  }
}
