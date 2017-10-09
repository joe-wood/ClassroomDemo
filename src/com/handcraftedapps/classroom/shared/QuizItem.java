package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * Question or answer, this is basically a generated sentence of sorts.
 */
public class QuizItem extends UniqueItem implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private String _value = "";
  
  public String getValue() { return _value; }
  public void   setValue(String pVal) { _value = pVal; }
  
  public QuizItem()
  {
    
  }

  public QuizItem(String pValue)
  {
    setValue(pValue);
  }
  
  public String toString()
  {
    return getValue();
  }
}
