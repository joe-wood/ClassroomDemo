
package com.handcraftedapps.classroom.client.services;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.handcraftedapps.classroom.shared.Quiz;
import com.handcraftedapps.classroom.shared.QuizScoreAndInfo;
import com.handcraftedapps.classroom.shared.QuizStudentAnswers;
import com.handcraftedapps.classroom.shared.Student;
import com.handcraftedapps.classroom.shared.SubjectClass;
import com.handcraftedapps.classroom.shared.SubjectClassQuizSummaryInfo;
import com.handcraftedapps.classroom.shared.Teacher;
import com.handcraftedapps.classroom.shared.UniqueItem;

/**
 * This class provides the services related to the student quiz exercise.
 */
@RemoteServiceRelativePath("ClassroomService")
public interface ClassroomService
  extends RemoteService
{
  /**
   * Utility class for simplifying access to the instance of async service.
   */
  public static class Util
  {
    private static ClassroomServiceAsync instance;

    public static ClassroomServiceAsync getInstance()
    {
      if (instance == null)
      {
        instance = GWT.create(ClassroomService.class);
      }
      return instance;
    }
  }
  
  /**
   * Get all the students
   * @returnList of students
   */
  List<Student> getStudents();
  
  /**
   * Get all the subject classes
   * @return Every class; could be multiples per subject
   */
  List<SubjectClass> getSubjectClasses();
  
  /**
   * Get all the teachers
   * @return List of all teachers
   */
  List<Teacher> getTeachers();

  /**
   * Get the number of weeks in a grading block
   * @return Number of weeks
   */
  int getWeeksPerGradingBlock();
  
  /**
   * Get the total number of weeks in the semester
   * @return Number of weeks
   */
  int getTotalNumberOfWeeks();
  
  /**
   * Given a class, school week, and index into that week (e.g., "second quiz
   * of the week") return that quiz.
   * @param pClass
   * @param pWeekInSchool
   * @param pIndexInWeek
   * @return Quiz matching params
   */
  Quiz getQuiz(SubjectClass pClass, int pWeekInSchool, int pIndexInWeek);
  
  /**
   * Given an item id, get the corresponding item
   * @param pItemId
   * @return
   */
  UniqueItem getItem(Long pItemId);

  /**
   * Given a list of item ids, get the corresponding items
   * @param pItemId
   * @return
   */
  UniqueItem[] getItems(Long[] pItemIds);
  
  List<SubjectClassQuizSummaryInfo> getStudentSummary(Long pStudentId, int pStartWeekIndex, int pStopWeekIndex);
  
  List<QuizStudentAnswers> getQuizAnswers(Long pStudentId, Long pSubjectClassId, int pStartWeekIndex, int pStopWeekIndex);
  
  List<QuizScoreAndInfo> getQuizScores(Long pStudentId, Long pSubjectClassId, int pStartWeekIndex, int pStopWeekIndex);
  
  void log(String pMessage);
}