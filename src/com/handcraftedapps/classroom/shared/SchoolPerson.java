package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * Basically, a student or a teacher, although could be an admin if we add those later.
 */
public class SchoolPerson extends ItemWithScheduledClasses implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public SchoolPerson()
  {
    
  }
  
  public SchoolPerson(int pTotalPeriodsInDay)
  {
    super(pTotalPeriodsInDay);
  }
  
  @Override
  public int getMinWordsInName  () { return 2; }
  
  @Override
  public int getMaxWordsInName  () { return 3; }
  
  @Override
  public int getMinLengthPerWord() { return 2; }
  
  @Override
  public int getMaxLengthPerWord() { return 10; }
}
