package com.handcraftedapps.classroom.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * A quiz and its answers - one set per quiz per student
 */
public class QuizStudentAnswers extends UniqueItem implements Serializable, IComplexSerialized
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Rehydrate all the Long-ID-based items into real items from the repository
   */
  private transient boolean _rehydrateCalled = false;
  private transient int     _rehydrated      = 0;
  
  @Override
  public boolean isRehydrated() { return _rehydrated >= 3; }
  
  @Override
  public synchronized void rehydrate(final IRehydrateCallback pCallback)
  {
    if (_rehydrateCalled) return;
 
    final UniqueItem parent = this;
    
    _answers = new ArrayList<>();
    if ((getAnswerIdList() != null) && (getAnswerIdList().size() > 0))
    {
      ObjectRepository.getInstance().fetchItems(getAnswerIdList(), new IArrayRehydrator() { 
        @Override public void rehydrateArray(UniqueItem[] pItems)
        {
          for (int loop = 0; loop < ((pItems == null) ? 0 : pItems.length); ++loop)
          {
            try
            {
              _answers.add((QuizAnswer) pItems[loop]);
            }
            catch (Exception e)
            {
              ObjectRepository.getInstance().indicateRehydrationError(parent, pItems[loop], "QuizAnswer");
            }
          }
          _rehydrated++;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }});
    }

    ObjectRepository.getInstance().fetchItem(getQuizId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _quiz = (Quiz) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "Quiz");
        }
      }});
    
    ObjectRepository.getInstance().fetchItem(getStudentId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _student = (Student) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "Student");
        }
      }});

    _rehydrateCalled = true;
  }

  /**
   * Student object/id - object for local use, ID for serialization
   */
  private Long _studentId;
  public  Long getStudentId() { return _studentId; }

  private transient Student _student;
  
  public Student getStudent() { return _student;  }
  public void    setStudent(Student pStudent) 
  { 
    _student = pStudent;
    _studentId = (_student == null) ? null : _student.getId();
  }

  /**
   * Quiz object/id - object for local use, ID for serialization
   */
  private Long _quizId;
  public  Long getQuizId() { return _quizId; }

  private transient Quiz _quiz;

  public Quiz getQuiz()           { return _quiz;  }
  public void setQuiz(Quiz pQuiz) 
  { 
    _quiz = pQuiz; 
    _quizId = (_quiz == null) ? null : _quiz.getId();
  }
  
  /**
   * QuizAnswer object/id list - object for local use, ID for serialization
   * 
   * Yes, I know the returned list should be immutable, but I'm not
   * worried about it for this exercise
   */
  private List<Long> _answerIdList = new ArrayList<>();  ;
  public  List<Long> getAnswerIdList() { return _answerIdList; }

  private transient List<QuizAnswer> _answers = new ArrayList<>();

  public List<QuizAnswer> getAnswers() { return _answers;  }

  public void addAnswer(QuizAnswer pAnswer)
  {
    _answers.add(pAnswer);
    _answerIdList.add((pAnswer == null) ? null : pAnswer.getId());
  }
  
  public QuizStudentAnswers()
  {
    
  }
}
