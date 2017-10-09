package com.handcraftedapps.classroom.server;

public class ExerciseConstraints
{
/*
Here's the react app exercise and you can use json mocks to emulate the data so no need for backend:

Here are high level requirements and you are free to implement in the best possible way you think that expresses your skills and discipline.

1. Create a dashboard for student performance
2. There are 384 students.  Each student takes 6 subjects each and there are 32 subjects.
3. There are 1 class per subject and 8 teachers that teach 4 subjects each so each teacher has 4 classes they manage with 4 different subjects. 
4. In each class they take a quiz weekly for 24 weeks a year and each quiz has 10 questions
5. Each teacher can login to see the performance of their students in each class
   5a. Be able to visualize each students performance in their class
   5b. Be able to visualize each classes total performance
   5c. Be able to compare performance of each class (across subjects)
   5d. For each student you can see their 6 subjects and how they are doing over the 10 week period.  
   5e. Be able to generate data for 10 weeks, then have another button to generate data for 10 weeks, and then final 4 weeks.
6. You can randomly generate right/wrong for each question.

One should be able to run `docker-compose up` to run the app on localhost:8000
*/
  public final static int DEFAULT_TOTAL_STUDENTS          = 384;
  public final static int DEFAULT_TOTAL_PERIODS           = 6;
  public final static int DEFAULT_SUBJECTS_PER_STUDENT    = 6;
  public final static int DEFAULT_TOTAL_SUBJECTS          = 32;
  public final static int DEFAULT_CLASSES_PER_SUBJECT     = 3;      //   1
  public final static int DEFAULT_TOTAL_TEACHERS          = 16;     //   8
  public final static int DEFAULT_QUESTIONS_PER_QUIZ      = 10;
  public final static int DEFAULT_QUIZZES_PER_WEEK        = 1;
  public final static int DEFAULT_TOTAL_WEEKS             = 24;
  public final static int DEFAULT_VIEW_WINDOW_IN_WEEKS    = 10;
  public final static int DEFAULT_UNIQUE_SUBJ_PER_TEACHER = 4;
  
  public static int calculateClassesPerTeacher()
  {
    int totalClasses = getClassesPerSubject() * getTotalSubjects();
    
    return (int) Math.ceil((1.0*totalClasses)/getTotalTeachers());
  }
  
  // getTotalRooms() * getTotalPeriods()  must equal getTotalSubjects() * getClassesPerSubject()!

  public static int getTotalRooms()
  {
     return getTotalTeachers();
  }

  private static int s_totalStudents = DEFAULT_TOTAL_STUDENTS;
  
  public static int  getTotalStudents()         { return s_totalStudents; }
  public static void setTotalStudents(int pVal) { s_totalStudents = pVal; }

  private static int s_totalPeriods = DEFAULT_TOTAL_PERIODS;
  
  public static int  getTotalPeriods()         { return s_totalPeriods; }
  public static void setTotalPeriods(int pVal) { s_totalPeriods = pVal; }

  private static int s_subjectsPerStudent = DEFAULT_SUBJECTS_PER_STUDENT;
  
  public static int  getSubjectsPerStudent()         { return s_subjectsPerStudent; }
  public static void setSubjectsPerStudent(int pVal) { s_subjectsPerStudent = pVal; }

  private static int s_totalSubjects = DEFAULT_TOTAL_SUBJECTS;
  
  public static int  getTotalSubjects()         { return s_totalSubjects; }
  public static void setTotalSubjects(int pVal) { s_totalSubjects = pVal; }

  private static int s_classesPerSubject = DEFAULT_CLASSES_PER_SUBJECT;
  
  public static int  getClassesPerSubject()         { return s_classesPerSubject; }
  public static void setClassesPerSubject(int pVal) { s_classesPerSubject = pVal; }

  private static int s_totalTeachers = DEFAULT_TOTAL_TEACHERS;
  
  public static int  getTotalTeachers()         { return s_totalTeachers; }
  public static void setTotalTeachers(int pVal) { s_totalTeachers = pVal; }

  private static int s_questionsPerQuiz = DEFAULT_QUESTIONS_PER_QUIZ;
  
  public static int  getQuestionsPerQuiz()         { return s_questionsPerQuiz; }
  public static void setQuestionsPerQuiz(int pVal) { s_questionsPerQuiz = pVal; }

  private static int s_quizzesPerWeek = DEFAULT_QUIZZES_PER_WEEK;
  
  public static int  getQuizzesPerWeek()         { return s_quizzesPerWeek; }
  public static void setQuizzesPerWeek(int pVal) { s_quizzesPerWeek = pVal; }

  private static int s_viewWindowInWeeks = DEFAULT_VIEW_WINDOW_IN_WEEKS;
  
  public static int  getViewWindowInWeeks()         { return s_viewWindowInWeeks; }
  public static void setViewWindowInWeeks(int pVal) { s_viewWindowInWeeks = pVal; }

  private static int s_totalWeeks = DEFAULT_TOTAL_WEEKS;
  public static int  getTotalWeeks()         { return s_totalWeeks; }
  public static void setTotalWeeks(int pVal) { s_totalWeeks = pVal; }

  private static int s_uniqueSubjectsPerTeacher = DEFAULT_UNIQUE_SUBJ_PER_TEACHER;
  public static int  getUniqueSubjectsPerTeacher()         { return s_uniqueSubjectsPerTeacher; }
  public static void setUniqueSubjectsPerTeacher(int pVal) { s_uniqueSubjectsPerTeacher = pVal; }
}
