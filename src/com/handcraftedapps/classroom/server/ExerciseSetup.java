package com.handcraftedapps.classroom.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.handcraftedapps.classroom.shared.NamedItem;
import com.handcraftedapps.classroom.shared.ObjectRepository;
import com.handcraftedapps.classroom.shared.Quiz;
import com.handcraftedapps.classroom.shared.QuizAnswer;
import com.handcraftedapps.classroom.shared.QuizPair;
import com.handcraftedapps.classroom.shared.QuizQuestion;
import com.handcraftedapps.classroom.shared.QuizStudentAnswers;
import com.handcraftedapps.classroom.shared.Classroom;
import com.handcraftedapps.classroom.shared.SchoolPerson;
import com.handcraftedapps.classroom.shared.Student;
import com.handcraftedapps.classroom.shared.Subject;
import com.handcraftedapps.classroom.shared.SubjectClass;
import com.handcraftedapps.classroom.shared.Teacher;
import com.handcraftedapps.classroom.shared.UniqueItem;

/**
 * Stores everything; source of all knowledge. This is the class that
 * generates and stores the fake data for our exercise.
 *
 */
public class ExerciseSetup
{
  public static void main(String[] pArgs)
  {
    getInstance();
  }
  
  private static ExerciseSetup s_instance = new ExerciseSetup();
  public static ExerciseSetup getInstance() { return s_instance; }
  
  private boolean            _initialized = false;
  private List<Classroom>    _rooms       = new ArrayList<>();
  private List<SubjectClass> _classes     = new ArrayList<>();
  private List<Subject>      _subjects    = new ArrayList<>();
  private List<Student>      _students    = new ArrayList<>();
  private List<Teacher>      _teachers    = new ArrayList<>();
  
  private HashMap<Student, HashMap<SubjectClass, List<QuizStudentAnswers>>> _studentAnswerMap = new HashMap<>();
  private HashMap<SubjectClass, HashMap<Student, List<QuizStudentAnswers>>> _classAnswerMap   = new HashMap<>();
  
  public List<Classroom>    getRooms         () { return _rooms   ; }
  public List<SubjectClass> getSubjectClasses() { return _classes ; }
  public List<Subject>      getSubjects      () { return _subjects; }
  public List<Student>      getStudents      () { return _students; }
  public List<Teacher>      getTeachers      () { return _teachers; }
  
  public HashMap<Student, HashMap<SubjectClass, List<QuizStudentAnswers>>> getStudentAnswerMap() { return _studentAnswerMap; }
  public HashMap<SubjectClass, HashMap<Student, List<QuizStudentAnswers>>> getClassAnswerMap  () { return _classAnswerMap  ; }
  
  public ExerciseSetup()
  {
    initPeopleAndPlaces(false);
  }
  
  /**
   * Generate all the data
   */
  private void initPeopleAndPlaces(boolean pShowResults)
  {
    if (_initialized) return;
    
    // Create the rooms
    
    for (int subjectId = 0; subjectId < ExerciseConstraints.getTotalRooms(); ++subjectId)
    {
      getRooms().add(initRoom(new Classroom(ExerciseConstraints.getTotalPeriods())));
    }
    
    // Create the subjects and classes; each subject will be in 'GeneralConstraints.getClassesPerSubject()' classes
    
    for (int subjectId = 0; subjectId < ExerciseConstraints.getTotalSubjects(); ++subjectId)
    {
      getSubjects().add(initSubject(new Subject()));
    }
    
    // Create teachers
    
    for (int teacherId = 0; teacherId < ExerciseConstraints.getTotalTeachers(); ++teacherId)
    {
      getTeachers().add(initTeacher(new Teacher(ExerciseConstraints.getTotalPeriods())));
    }

    // Create students
    
    for (int studentId = 0; studentId < ExerciseConstraints.getTotalStudents(); ++studentId)
    {
      getStudents().add(initStudent(new Student(ExerciseConstraints.getTotalPeriods())));
    }

    // Assign classes and teachers to rooms and periods randomly
    
    List<SubjectClass> randomClasses = new ArrayList<>(getSubjectClasses());
    
    Collections.shuffle(randomClasses);
    
    Iterator<SubjectClass> classIter = randomClasses.iterator();

    for (int period = 0; period < ExerciseConstraints.getTotalPeriods(); ++period)
    {
      List<Teacher> randomTeachers = new ArrayList<>(getTeachers());
      Collections.shuffle(randomTeachers);
      Iterator<Teacher> teacherIter = randomTeachers.iterator();

      for (Classroom room : getRooms())
      {
        SubjectClass klass = classIter.next();
        Teacher      teach = teacherIter.next();
        klass.setTeacher(teach);
        teach.setClassForPeriod(period, klass);
        room.setClassForPeriod(period, klass);
        
        klass.setPeriod(period);
        klass.setRoom(room);
      }
    }
    
  // Now assign students randomly. For now, don't worry about a student having the same
  // subject more than once.
    
    for (int period = 0; period < ExerciseConstraints.getTotalPeriods(); ++period)
    {
      List<Student> randomStudents = new ArrayList<>(getStudents());
      
      Collections.shuffle(randomStudents);
      
      Iterator<Student> studentIter = randomStudents.iterator();
      
      while (studentIter.hasNext())
      {
        for (Classroom room : getRooms())
        {
          Student student = studentIter.next();
          
          SubjectClass klass = room.getClassForPeriod(period);
          
          student.setClassForPeriod(period, klass);
          
          klass.addStudent(student);
          if (!studentIter.hasNext()) break;
        }
      }
    }
        
    // Create and "take" quizzes
    
    int answered = 0;
    
    for (int classWeek = 0; classWeek < ExerciseConstraints.getTotalWeeks(); ++classWeek)
    {
      for (int indexInWeek = 0; indexInWeek < ExerciseConstraints.getQuizzesPerWeek(); ++indexInWeek)
      {
        for (SubjectClass subjectClass : getSubjectClasses())
        {
          Quiz quiz = createQuiz(subjectClass, classWeek, indexInWeek);
          
          subjectClass.addQuiz(quiz);
           
          for (Student student : subjectClass.getStudents())
          {
            QuizStudentAnswers answers = gradeQuiz(takeQuiz(student, quiz));
            
            answered += answers.getAnswers().size();
            
            addAnswers(answers);
          }
        }
      }
    }
    
    // Show what we've created
    
    if (pShowResults)
    {
      for (Classroom room : getRooms())
      {
        System.out.println(room.getName());
        for (int period = 0; period < ExerciseConstraints.getTotalPeriods(); ++period)
        {
          System.out.println("\t" + room.getClassForPeriod(period).toShortString());
        }
      }
      System.out.println("Answers: " + answered);
    }
    
    // Mark as done
    _initialized = true;
  }
  
  /**
   * Fill in the appropriate hashmaps with the answers.
   * @param pAnswers
   */
  private void addAnswers(QuizStudentAnswers pAnswers)
  {
    Student      student   = pAnswers.getStudent();
    SubjectClass thisClass = pAnswers.getQuiz().getSubjectClass();

    if (_studentAnswerMap.get(student) == null)
    {
      _studentAnswerMap.put(student, new HashMap<>());
    }
    if (_studentAnswerMap.get(student).get(thisClass) == null)
    {
      _studentAnswerMap.get(student).put(thisClass, new ArrayList<>());
    }
    
    _studentAnswerMap.get(student).get(thisClass).add(pAnswers);
    

    if (_classAnswerMap.get(thisClass) == null)
    {
      _classAnswerMap.put(thisClass, new HashMap<>());
    }
    if (_classAnswerMap.get(thisClass).get(student) == null)
    {
      _classAnswerMap.get(thisClass).put(student, new ArrayList<>());
    }
    
    _classAnswerMap.get(thisClass).get(student).add(pAnswers);
    
  }
  
  /**
   * Fake-grade a quiz.
   * @param pAnswers The answers, ungraded.
   * @return The answers, graded.
   */
  private QuizStudentAnswers gradeQuiz(QuizStudentAnswers pAnswers)
  {
    for (QuizAnswer answer : pAnswers.getAnswers())
    {
      double gradeCheck = Math.random();
      
      if (gradeCheck > 0.8)
      {
        answer.getScore().setScoreValue(ThreadLocalRandom.current().nextInt(91, 101));
      }
      else if (gradeCheck > 0.5)
      {
        answer.getScore().setScoreValue(ThreadLocalRandom.current().nextInt(80, 101));
      }
      else if (gradeCheck > 0.05)
      {
        answer.getScore().setScoreValue(ThreadLocalRandom.current().nextInt(60, 81));
      }
      else
      {
        answer.getScore().setScoreValue(ThreadLocalRandom.current().nextInt(0, 61));
      }
    }
    return pAnswers;
  }
  
  /**
   * Fake-take a quiz.
   * @param pStudent
   * @param pQuiz
   * @return The answers to the quiz,
   */
  public QuizStudentAnswers takeQuiz(Student pStudent,
                                     Quiz    pQuiz)
  {
    QuizStudentAnswers myQuiz = new QuizStudentAnswers();
    initUniqueItem(myQuiz);

    
    myQuiz.setQuiz(pQuiz);
    myQuiz.setStudent(pStudent);
    
    for (QuizPair pair : pQuiz.getCorrectPairs())
    {
      QuizQuestion question = pair.getQuestion();
      
      QuizAnswer answer = new QuizAnswer(getRandomPseudoName(0, 20, 1, 8, false, "."), question.getId());
      initUniqueItem(answer);
      
      myQuiz.addAnswer(answer);
    }  
    
    return myQuiz;
  }

  /**
   * Initialize a classroom.
   * @param pRoom Classroom, uninitialized
   * @return pRoom, initialized
   */
  private Classroom initRoom(Classroom pRoom)
  {
    if (pRoom != null)
    {
      initNamedItem(pRoom);
      String name = "" + (getRooms().size()+1);
      
      while (name.length() < 2) name = "0" + name;
      while (name.length() < 3) name = "" + ThreadLocalRandom.current().nextInt(1, 4)  + name;
      pRoom.setName(name);
    }
    
    return pRoom;
  }
  
  /**
   * Initialize a subject and create classes for it.
   * @param pSubject Subject, uninitialized
   * @return pSubject, initialized
   */
  private Subject initSubject(Subject pSubject)
  {
    if (pSubject != null)
    {
      initNamedItem(pSubject);
      pSubject.setName(ThreadLocalRandom.current().nextInt(1, 5) + "0" + ThreadLocalRandom.current().nextInt(1, 5) + " " + pSubject.getName());
      
      for (int loop = 0; loop < ExerciseConstraints.getClassesPerSubject(); ++loop)
      {      
        SubjectClass subjectClass = initSubjectClass(new SubjectClass());
      
        getSubjectClasses().add(subjectClass);
      
        pSubject.addSubjectClass(subjectClass);
       
        subjectClass.setSubject(pSubject);
      }
    }
    
    return pSubject;
  }

  /**
   * Initialize a SubjectClass.
   * @param pSubjectClass SubjectClass, uninitialized
   * @return pSubjectClass, initialized
   */
  private SubjectClass initSubjectClass(SubjectClass pSubjectClass)
  {
    if (pSubjectClass != null)
    {
      initUniqueItem(pSubjectClass);
    }
    
    return pSubjectClass;
  }
  
  /**
   * Initialize a Student.
   * @param pStudent Student, uninitialized
   * @return pStudent, initialized
   */
  private Student initStudent(Student pStudent)
  {
    if (pStudent != null)
    {
      initPerson(pStudent);
    }
    
    return pStudent;
  }
  
  /**
   * Initialize a Teacher.
   * @param pTeacher Teacher, uninitialized
   * @return pTeacher, initialized
   */
  private Teacher initTeacher(Teacher pTeacher)
  {
    if (pTeacher != null)
    {
      initPerson(pTeacher);
      
      double prefixGen = Math.random();
      
      pTeacher.setPrefix((prefixGen < 0.45) ? "Mr." : (prefixGen < 0.9) ? "Ms." : "Mx.");      
    }
    
    return pTeacher;
  }
  
  /**
   * Initialize a SchoolPerson.
   * @param pPerson SchoolPerson, uninitialized
   * @return pPerson, initialized
   */
  private SchoolPerson initPerson(SchoolPerson pPerson)
  {
    if (pPerson != null)
    {
      initNamedItem(pPerson);
    }
    
    return pPerson;
  }
  
  /**
   * Initialize a NamedItem.
   * @param pItem NamedItem, uninitialized
   * @return pItem, initialized
   */
  private NamedItem initNamedItem(NamedItem pItem)
  {
    if (pItem != null)
    {
      initUniqueItem(pItem);
      pItem.setName(getRandomPseudoName(pItem.getMinWordsInName(), pItem.getMaxWordsInName(), pItem.getMinLengthPerWord(), pItem.getMaxLengthPerWord(), true, ""));
    }
    
    return pItem;
  }
  
  /**
   * Initialize a UniqueItem (right now, a no-op).
   * @param pItem UniqueItem, uninitialized
   * @return pItem, initialized
   */
  private UniqueItem initUniqueItem(UniqueItem pItem)
  {
    ObjectRepository.getInstance().addItem(pItem);
    return pItem;
  }
  
  /**
   * Create a dummy quiz.
   * @param pSubjectClass
   * @param pClassWeek
   * @param pIndexInWeek
   * @return The quiz
   */
  private Quiz createQuiz(SubjectClass pSubjectClass,
                          int         pClassWeek,
                          int         pIndexInWeek)
  { 
     Quiz quiz = new Quiz(pSubjectClass, pClassWeek, pIndexInWeek);
     initUniqueItem(quiz);
     
     for (int loop = 0; loop < ExerciseConstraints.getQuestionsPerQuiz(); ++loop)
     {
       QuizPair pair = new QuizPair();
       initUniqueItem(pair);
       
       QuizQuestion question = new QuizQuestion(getRandomPseudoName(3, 10, 1, 10, false, null));
       initUniqueItem(question);

       pair.setQuestion(question);
       QuizAnswer answer = new QuizAnswer(getRandomPseudoName(1, 20, 1, 10, false, null), question.getId());
       initUniqueItem(answer);

       pair.setAnswer(answer);
       
       quiz.addCorrectPair(pair);


     }
     
     return quiz;
  }
  
  
  private static final String s_vowels     = "aeiouy";
  private static final String s_consanants = "bcdfghjklmnpqrstvwxz";
  private static final String s_letters    = s_vowels + s_consanants;
  
  /**
   * Random name/sentence generator
   * @param pMinWords               Minimum number of words to generate.
   * @param pMaxWords               Maximum number of words to generate.
   * @param pMinWordLength          Minimum word length
   * @param pMaxWordLength          Maximum word length
   * @param pAllWordsCapitalized    Capitalize every work (as opposed to just the first)?
   * @param pEnding                 If there's final punctuation, what is it?
   * @return
   */
  private String getRandomPseudoName(int     pMinWords,
                                     int     pMaxWords,
                                     int     pMinWordLength,
                                     int     pMaxWordLength,
                                     boolean pAllWordsCapitalized,
                                     String  pEnding)
  {
    String result = "";
    
    if (pMinWords < 1) pMinWords = 1;
    if (pMaxWords < pMinWords) pMaxWords = pMinWords;
    
    if (pMinWordLength < 1) pMinWordLength = 1;
    if (pMaxWordLength < pMinWordLength) pMaxWordLength = pMinWordLength;
    
    int totalWords = ThreadLocalRandom.current().nextInt(pMinWords, pMaxWords + 1);
    if (totalWords == 0) return "-";
    
    for (int word = 0; word < totalWords; ++word)
    {
      int wordLength = ThreadLocalRandom.current().nextInt(pMinWordLength, pMaxWordLength + 1);
      
      char firstChar = (wordLength == 1) ? s_vowels.charAt(ThreadLocalRandom.current().nextInt(0, s_vowels.length())) :
                                           s_letters.charAt(ThreadLocalRandom.current().nextInt(0, s_letters.length()));
      
      String thisWord = "" + ((pAllWordsCapitalized || (word == 0)) ? Character.toUpperCase(firstChar) : firstChar);
      
      for (int letterNumber = 1; letterNumber < wordLength; ++letterNumber)
      {
        char nextChar = s_vowels.contains(""+Character.toLowerCase(thisWord.charAt(letterNumber - 1))) ?
                          s_consanants.charAt(ThreadLocalRandom.current().nextInt(0, s_consanants.length())) :
                          s_vowels    .charAt(ThreadLocalRandom.current().nextInt(0, s_vowels    .length()));
                        
        thisWord += nextChar;
      }
      
      result += thisWord + " ";
    }
    
    return result.trim() + ((pEnding == null) ? "" : pEnding);
  }
}