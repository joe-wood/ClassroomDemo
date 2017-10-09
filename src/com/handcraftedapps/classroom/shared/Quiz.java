package com.handcraftedapps.classroom.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * A quiz is a set of questions and answers (QuizPair) in q given class
 * on a given week at a given index in the week (1st, 2nd, 3rd... indicated
 * by 0, 1, 2,...)
 */
public class Quiz extends UniqueItem implements Serializable, IComplexSerialized
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

    _quizPairList = new ArrayList<>();  
 
    final UniqueItem parent = this;
    if ((getQuizPairIdList() != null) && (getQuizPairIdList().size() > 0))
    {
      ObjectRepository.getInstance().fetchItems(getQuizPairIdList(), new IArrayRehydrator() { 
        @Override public void rehydrateArray(UniqueItem[] pItems)
        {
          for (int loop = 0; loop < ((pItems == null) ? 0 : pItems.length); ++loop)
          {
            try
            {
              _quizPairList.add((QuizPair) pItems[loop]);
            }
            catch (Exception e)
            {
              ObjectRepository.getInstance().indicateRehydrationError(parent, pItems[loop], "QuizPair");
            }
          }
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }});
    }

    ObjectRepository.getInstance().fetchItem(getSubjectClassId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        _subjectClass = (SubjectClass) pItem;
        ++_rehydrated;
        if (isRehydrated() && (pCallback != null))
        {
          pCallback.rehydrateDone();
        }
      }});

      _rehydrateCalled = true;
  }
  
  private int _classWeek;
  
  public int  getClassWeek()         { return _classWeek; }
  public void setClassWeek(int pVal) { _classWeek = pVal; }
  
  private int _indexInWeek;
   
  public int  getIndexInWeek()         { return _indexInWeek; }
  public void setIndexInWeek(int pVal) { _indexInWeek = pVal; }
  
  /**
   * SubjectClass object/id - object for local use, ID for serialization
   */
  private Long _subjectClassId;
  public  Long getSubjectClassId() { return _subjectClassId; }

  private transient SubjectClass _subjectClass;
  
  public SubjectClass getSubjectClass()                 { return _subjectClass; }
  public void  setSubjectClass(SubjectClass pVal) 
  { 
    _subjectClass = pVal; 
    _subjectClassId = (_subjectClass == null) ? null : _subjectClass.getId();
  }
  
  /**
   * QuizPair object/id list - object for local use, ID for serialization
   * 
   * Yes, I know the returned list should be immutable, but I'm not
   * worried about it for this exercise
   */
  private List<Long> _quizPairIdList = new ArrayList<>();  ;
  public  List<Long> getQuizPairIdList() { return _quizPairIdList; }

  private transient List<QuizPair>  _quizPairList = new ArrayList<>();  
  public List<QuizPair> getCorrectPairs() { return _quizPairList; }
  
  public void addCorrectPair(QuizPair pQuizPair)
  {
    _quizPairList.add(pQuizPair);
    _quizPairIdList.add((pQuizPair == null) ? null : pQuizPair.getId());
  }

  public Quiz()
  {
    
  }

  public Quiz(SubjectClass pSubjectClass,
              int          pClassWeek,
              int          pIndexInWeek)
  {
    this();
    _subjectClass = pSubjectClass;
    _classWeek   = pClassWeek;
    _indexInWeek = pIndexInWeek;
  }
}
