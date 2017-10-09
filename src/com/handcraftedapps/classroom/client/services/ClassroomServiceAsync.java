package com.handcraftedapps.classroom.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.handcraftedapps.classroom.shared.Quiz;
import com.handcraftedapps.classroom.shared.QuizScoreAndInfo;
import com.handcraftedapps.classroom.shared.QuizStudentAnswers;
import com.handcraftedapps.classroom.shared.SubjectClass;
import com.handcraftedapps.classroom.shared.SubjectClassQuizSummaryInfo;
import com.handcraftedapps.classroom.shared.Student;
import com.handcraftedapps.classroom.shared.Teacher;
import com.handcraftedapps.classroom.shared.UniqueItem;

/**
 * Async interface for ClassroomService
 * @author joewood
 *
 */
public interface ClassroomServiceAsync
{
  void getStudents(AsyncCallback<List<Student>> callback);

  void getSubjectClasses(AsyncCallback<List<SubjectClass>> callback);

  void getTeachers(AsyncCallback<List<Teacher>> callback);

  void getQuiz(SubjectClass pClass,
               int pWeekInSchool,
               int pIndexInWeek,
               AsyncCallback<Quiz> callback);

  void getTotalNumberOfWeeks(AsyncCallback<Integer> callback);

  void getItem(Long pItemId,
               AsyncCallback<UniqueItem> callback);

  void getItems(Long[] pItemIds,
                AsyncCallback<UniqueItem[]> callback);

  void log(String pMessage,
           AsyncCallback<Void> callback);

  void getWeeksPerGradingBlock(AsyncCallback<Integer> callback);

  void getQuizAnswers(Long pStudentId,
                      Long pSubjectClassId,
                      int pStartWeekIndex,
                      int pStopWeekIndex,
                      AsyncCallback<List<QuizStudentAnswers>> callback);

  void getQuizScores(Long pStudentId,
                     Long pSubjectClassId,
                     int pStartWeekIndex,
                     int pStopWeekIndex,
                     AsyncCallback<List<QuizScoreAndInfo>> callback);

  void getStudentSummary(Long pStudentId,
                         int pStartWeekIndex,
                         int pStopWeekIndex,
                         AsyncCallback<List<SubjectClassQuizSummaryInfo>> callback);
}
