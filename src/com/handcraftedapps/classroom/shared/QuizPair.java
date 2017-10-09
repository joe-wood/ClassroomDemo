package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * Question/answer pair
 */
public class QuizPair extends UniqueItem implements Serializable, IComplexSerialized
{
  private static final long serialVersionUID = 1L;

  /**
   * Rehydrate all the Long-ID-based items into real items from the repository
   */
  private transient boolean _rehydrateCalled = false;
  private transient int     _rehydrated      = 0;
  
  @Override
  public boolean isRehydrated() { return _rehydrated >= 2; }
  
  @Override
  public synchronized void rehydrate(final IRehydrateCallback pCallback)
  {
    if (_rehydrateCalled) return;
 
    final UniqueItem parent = this;
    ObjectRepository.getInstance().fetchItem(getQuestionId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _question = (QuizQuestion) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "QuizQuestion");
        }
      }});
    
    ObjectRepository.getInstance().fetchItem(getAnswerId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _answer = (QuizAnswer) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "QuizAnswer");
        }
      }});

      _rehydrateCalled = true;
  }

  /**
   * Question object/id - object for local use, ID for serialization
   */
  private Long _questionId;
  public  Long getQuestionId() { return _questionId; }

  private transient QuizQuestion _question;
  
  public QuizQuestion getQuestion() { return _question; }
  public void         setQuestion(QuizQuestion pVal) 
  { 
    _question = pVal; 
    _questionId = (_question == null) ? null : _question.getId();
  }
  
  /**
   * Answer object/id - object for local use, ID for serialization
   */
  private Long _answerId;
  public  Long getAnswerId() { return _answerId; }

  private transient QuizAnswer _answer;
  
  public QuizAnswer getAnswer() { return _answer; }
  public void setAnswer(QuizAnswer pVal) 
  { 
    _answer = pVal; 
    _answerId = (_answer == null) ? null : _answer.getId();
  }
  
  public QuizPair()
  {
    
  }
}
