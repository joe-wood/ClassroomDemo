package com.handcraftedapps.classroom.server.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.handcraftedapps.classroom.client.services.ClassroomService;
import com.handcraftedapps.classroom.server.ExerciseConstraints;
import com.handcraftedapps.classroom.server.ExerciseSetup;
import com.handcraftedapps.classroom.shared.ObjectRepository;
import com.handcraftedapps.classroom.shared.Quiz;
import com.handcraftedapps.classroom.shared.QuizScoreAndInfo;
import com.handcraftedapps.classroom.shared.QuizStudentAnswers;
import com.handcraftedapps.classroom.shared.QuizStudentGrade;
import com.handcraftedapps.classroom.shared.Student;
import com.handcraftedapps.classroom.shared.SubjectClass;
import com.handcraftedapps.classroom.shared.SubjectClassQuizSummaryInfo;
import com.handcraftedapps.classroom.shared.Teacher;
import com.handcraftedapps.classroom.shared.UniqueItem;

/**
 * GWT service implementation; useful for testing with GWT before moving onto React.
 * See ClassroomService.jav on the client for documentation.
 */
public class ClassroomServiceImpl
  extends RemoteServiceServlet
  implements ClassroomService
{
  private static final long serialVersionUID = 1L;
  
  public ClassroomServiceImpl()
  {
    
  }

  @Override
  public List<Student> getStudents()
  {
    List<Student> list = ExerciseSetup.getInstance().getStudents();
    
    Collections.sort(list, new Comparator<Student>() { public int compare(Student pOne, Student pTwo) { return pOne.getName().compareTo(pTwo.getName()); }});
    return list;
  }

  @Override
  public List<SubjectClass> getSubjectClasses()
  {
    List<SubjectClass> list = ExerciseSetup.getInstance().getSubjectClasses();
    
    Collections.sort(list, new Comparator<SubjectClass>() { public int compare(SubjectClass pOne, SubjectClass pTwo) { return pOne.getSubject().getName().compareTo(pTwo.getSubject().getName()); }});
    return list;
   }

  @Override
  public List<Teacher> getTeachers()
  {
    List<Teacher> list = ExerciseSetup.getInstance().getTeachers();
    
    Collections.sort(list, new Comparator<Teacher>() { public int compare(Teacher pOne, Teacher pTwo) { return pOne.getName().compareTo(pTwo.getName()); }});
    
    return list;
  }

  @Override
  public Quiz getQuiz(SubjectClass pClass,
                      int          pWeekInSchool,
                      int          pIndexInWeek)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getTotalNumberOfWeeks()
  {
    // TODO Auto-generated method stub
    return ExerciseConstraints.getTotalWeeks();
  }

  @Override
  public UniqueItem getItem(Long pItemId)
  {
    return ObjectRepository.getInstance().getItem(pItemId);
  }

  @Override
  public synchronized UniqueItem[] getItems(Long[] pItemIds)
  {
//    System.out.println("=> " + pItemIds.length);
    UniqueItem[] returnArray = new UniqueItem[pItemIds.length];
    
    for (int loop = 0; loop < pItemIds.length; ++loop)
    {
      returnArray[loop] = ObjectRepository.getInstance().getItem(pItemIds[loop]);
      
//      if (returnArray[loop] != null)
//      {
//        System.out.println("--> ["+returnArray[loop].getClass().getSimpleName()+"] " + returnArray[loop]);
//      }
//      else
//      {
//        System.out.println("--> ??? Cannot find " + pItemIds[loop] + ".");
//        System.exit(-1);
//      }
    }
    
    return returnArray;
  }
  

  @Override
  public void log(String pMessage)
  {
    System.out.println(pMessage);
  }

  @Override
  public int getWeeksPerGradingBlock()
  {
    return ExerciseConstraints.getViewWindowInWeeks();
  }

  @Override
  public List<QuizStudentAnswers> getQuizAnswers(Long pStudentId,
                                                 Long pSubjectClassId,
                                                 int  pStartWeekIndex,
                                                 int  pStopWeekIndex)
  {
    List<QuizStudentAnswers> answersInRange = new ArrayList<>();
    
    Student student           = (Student) ObjectRepository.getInstance().getItem(pStudentId);
    SubjectClass subjectClass = (SubjectClass) ObjectRepository.getInstance().getItem(pSubjectClassId);
    
    List<QuizStudentAnswers> answerList = ExerciseSetup.getInstance().getStudentAnswerMap().get(student).get(subjectClass);
    
    for (QuizStudentAnswers answer : answerList)
    {
      if ((answer.getQuiz().getClassWeek() >= pStartWeekIndex) && (answer.getQuiz().getClassWeek() <= pStopWeekIndex))
      {
        answersInRange.add(answer);
      }
    }
    
    return answersInRange;
  }

  @Override
  public List<QuizScoreAndInfo> getQuizScores(Long pStudentId,
                                              Long pSubjectClassId,
                                              int pStartWeekIndex,
                                              int pStopWeekIndex)
  {
    List<QuizStudentAnswers> answers = getQuizAnswers(pStudentId, pSubjectClassId, pStartWeekIndex, pStopWeekIndex);
    
    List<QuizScoreAndInfo> infoList = new ArrayList<>();

    for (QuizStudentAnswers answer : answers)
    {
      infoList.add(new QuizScoreAndInfo(new QuizStudentGrade(answer).getScore(), answer.getQuiz().getClassWeek(), answer.getQuiz().getIndexInWeek()));
    }
    
    return infoList;
  }

  @Override
  public List<SubjectClassQuizSummaryInfo> getStudentSummary(Long pStudentId,
                                                             int  pStartWeekIndex,
                                                             int  pStopWeekIndex)
  {
    List<SubjectClassQuizSummaryInfo> summaryList = new ArrayList<>();
    
    Student student = (Student) ObjectRepository.getInstance().getItem(pStudentId);
    
    for (int period = 0; period < ExerciseConstraints.getTotalPeriods(); ++period)
    {
      SubjectClass subjectClass = student.getClassForPeriod(period);
      
      List<QuizScoreAndInfo> scoreList = getQuizScores(pStudentId, subjectClass.getId(), pStartWeekIndex, pStopWeekIndex);
      
      summaryList.add(new SubjectClassQuizSummaryInfo(subjectClass.getName(), scoreList));
    }

    return summaryList;
  }

}
