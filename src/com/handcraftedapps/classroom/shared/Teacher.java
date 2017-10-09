package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * A teacher, obviously. Has a prefix ("Ms/Mr/Mx").
 */
public class Teacher extends SchoolPerson implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private String _prefix;
  
  public String getPrefix()            { return _prefix; }
  public void   setPrefix(String pVal) { _prefix = pVal; }  
  
  public Teacher()
  {
  }
  
  public Teacher(int pTotalPeriodsInDay)
  {
    super(pTotalPeriodsInDay);
  }
  
  public String toString()
  {
    return getPrefix() + " " + getName();
  }
}
