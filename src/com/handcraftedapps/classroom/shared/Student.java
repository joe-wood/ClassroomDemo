package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * A student - has classes, takes quizzes
 */
public class Student extends SchoolPerson implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public Student()
  {
    
  }
  
  public Student(int pTotalPeriodsInDay)
  {
    super(pTotalPeriodsInDay);
  }
}
