package com.handcraftedapps.classroom.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * A class - has students, has a teacher, has a subject, is scheduled, has quizzes.
 * This is the main object we track and show.
 */
public class SubjectClass extends NamedItem implements Serializable, IComplexSerialized
{
  private static final long serialVersionUID = 1L;
  private static final String s_letters = "ABCDEFGHIJKLMENOPQRSTUVWXYZ";
  
  /**
   * Rehydrate all the Long-ID-based items into real items from the repository
   */
  private transient boolean _rehydrateCalled = false;
  private transient int     _rehydrated      = 0;
  
  @Override
  public boolean isRehydrated() { return _rehydrated >= 4; }
  
  @Override
  public synchronized void rehydrate(final IRehydrateCallback pCallback)
  {
    if (_rehydrateCalled) return;
  
//    _quizzes = new ArrayList<>();
//    if ((getQuizIdList() != null) && (getQuizIdList() .size() > 0))
//    {
//      ObjectRepository.getInstance().fetchItems(getQuizIdList(), new IArrayRehydrator() { 
//        @Override public void rehydrateArray(UniqueItem[] pItems)
//        {
//          for (int loop = 0; loop < ((pItems == null) ? 0 : pItems.length); ++loop)
//          {
//            addQuiz((Quiz) pItems[loop], false);
//          }
//          _rehydrated++;
//        }});
//    }

    final UniqueItem parent = this;
    ObjectRepository.getInstance().fetchItem(getSubjectId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _subject = (Subject) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "Subject");
        }
      }});
    
    ObjectRepository.getInstance().fetchItem(getTeacherId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _teacher = (Teacher) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "Teacher");
        }
      }});
 
    ObjectRepository.getInstance().fetchItem(getRoomId(), new IItemRehydrator() { 
      @Override public void rehydrateItem(UniqueItem pItem)
      {
        try
        {
          _room = (Classroom) pItem;
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }
        catch (Exception e)
        {
          ObjectRepository.getInstance().indicateRehydrationError(parent, pItem, "Classroom");
        }
      }});

    _students = new ArrayList<>();  
    
    if ((getStudentIdList() != null) && (getStudentIdList().size() > 0))
    {
      ObjectRepository.getInstance().fetchItems(getStudentIdList(), new IArrayRehydrator() { 
        @Override public void rehydrateArray(UniqueItem[] pItems)
        {
          for (int loop = 0; loop < ((pItems == null) ? 0 : pItems.length); ++loop)
          {
             try
             {
               _students.add((Student) pItems[loop]);
             }
             catch (Exception e)
             {
               ObjectRepository.getInstance().indicateRehydrationError(parent, pItems[loop], "Student");
             }
          }
          ++_rehydrated;
          if (isRehydrated() && (pCallback != null))
          {
            pCallback.rehydrateDone();
          }
        }});
    }

    _rehydrateCalled = true;
  }

  /**
   * Period of this class
   */
  private int _period;

  public int  getPeriod()         { return _period; }
  public void setPeriod(int pVal) { _period = pVal; }

  /**
   * Subject object/id - object for local use, ID for serialization
   */
  private Long _subjectId;
  public  Long getSubjectId() { return _subjectId; }

  private transient Subject _subject;
  
  public Subject getSubject() { return _subject; }
  public void    setSubject(Subject pVal) 
  { 
    _subject = pVal; 
    _subjectId = (_subject == null) ? null : _subject.getId();
    
  // Name this after the subject, but add a letter at the end indicating the index
    
    if ((_subject == null) || (_subject.getName() == null))
    {
      setName("");
      return;
    }

    String name = _subject.getName();
    int index = 0;
    for (SubjectClass klass : getSubject().getSubjectClasses())
    {
      if (klass.getId() == getId())
      {
        break;
      }
      ++index;
    }
    
    name += " " + s_letters.charAt(index);
  
    setName(name);
  }
  
  /**
   * Teacher object/id - object for local use, ID for serialization
   */
  private Long _teacherId;
  public  Long getTeacherId() { return _teacherId; }

  private transient Teacher _teacher;
  
  public Teacher getTeacher() { return _teacher; }
  public void    setTeacher(Teacher pVal) 
  { 
    _teacher = pVal; 
    _teacherId = (_teacher == null) ? null : _teacher.getId();
  }
  
  /**
   * Classroom object/id - object for local use, ID for serialization
   */
  private Long _roomId;
  public  Long getRoomId() { return _roomId; }

  private transient Classroom _room;
  
  public Classroom getRoom() { return _room; }
  public void      setRoom(Classroom pVal) 
  { 
    _room = pVal; 
    _roomId = (_room == null) ? null : _room.getId();
  }
  
  /**
   * Student object/id list - object for local use, ID for serialization
   * 
   * Yes, I know the returned list should be immutable, but I'm not
   * worried about it for this exercise
   */
  private List<Long> _studentIdList = new ArrayList<>();  ;
  public  List<Long> getStudentIdList() { return _studentIdList; }

  private transient List<Student>  _students = new ArrayList<>();  
  public List<Student> getStudents() { return _students; }
  
  public void addStudent(Student pStudent)
  {
    _students.add(pStudent);
    _studentIdList.add((pStudent == null) ? null : pStudent.getId());
  }
  
  /**
   * Quiz object/id list - object for local use, ID for serialization
   * 
   * Yes, I know the returned list should be immutable, but I'm not
   * worried about it for this exercise
   */
  private List<Long> _quizIdList = new ArrayList<>();  ;
  public  List<Long> getQuizIdList() { return _quizIdList; }

  private transient List<Quiz>  _quizzes = new ArrayList<>();  
  public List<Quiz> getQuizzes() { return _quizzes; }
  
  private transient HashMap<Integer, List<Quiz>> _quizMap = new HashMap<>();
  
  public void addQuiz(Quiz pQuiz)
  {
    addQuiz(pQuiz, true);
  }
  
  public void addQuiz(Quiz    pQuiz,
                      boolean pUpdateIds)
  {
    if (pQuiz == null) return;
    
    getQuizzes().add(pQuiz);
    
    if (pUpdateIds)
    {
      _quizIdList.add(pQuiz.getId());
    }
    
    if (_quizMap.get(pQuiz.getClassWeek()) == null)
    {
      _quizMap.put(pQuiz.getClassWeek(), new ArrayList<Quiz>());
    }
    
    _quizMap.get(pQuiz.getClassWeek()).add(pQuiz);
  }
  
  public Quiz getQuiz(int pClassWeek,
                      int pIndexInWeek)
  {
    List<Quiz> quizzesForWeek = _quizMap.get(pClassWeek);
    
    if (quizzesForWeek != null)
    {
      for (Quiz quiz : quizzesForWeek)
      {
        if (quiz.getIndexInWeek() == pIndexInWeek)
        {
          return quiz;
        }
      }
    }
    
    return null;
  }
  
  public SubjectClass()
  {
    
  }

  public String toShortString()
  {
    String result = getName();
    
    while (result.length() < 20) result += " ";
    
    result += ":\t" + getRoom() + " [" + (getPeriod() + 1) + "]\t" + ((getTeacher() == null) ? "?" : getTeacher());
    
    while (result.length() < 70) result += " ";

    result += "| " + ((getSubject() == null) ? "?" : getSubject().getName());

    while (result.length() < 90) result += " ";

    result += "\t["+getStudents().size()+"] { ";

    for (Student student : getStudents()) result += student.getName() + ", ";
    result += "}";;
                    
    return result.replace(", }", " }");
  }
  
  public String toString()
  {
    String result = "Subject Class:\n\tSubject: " + ((getSubject() == null) ? "?" : getSubject().getName()) + 
                    "\n\tTeacher: " + ((getTeacher() == null) ? "?" : getTeacher().getName()) + 
                    "\n\tStudents: ["+getStudents().size()+"] { ";

    for (Student student : getStudents()) result += student.getName() + ", ";
    result += "}";
    
    return result.replace(", }", " }");
  }
}
