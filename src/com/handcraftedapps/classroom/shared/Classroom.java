package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * Pretty complex stuff: this is a classroom.
 */
public class Classroom extends ItemWithScheduledClasses implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public Classroom()
  {
    super();
  }
  
  public Classroom(int pTotalPeriodsInDay)
  {
    super(pTotalPeriodsInDay);
  }
  
  public String toString()
  {
    return "Room " + getName();
  }
}
