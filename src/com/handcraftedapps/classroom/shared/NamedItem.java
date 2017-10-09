package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * A unique item that also has a name (which can be
 * composed of numerous smaller words). 
 * 
 * Since this, for now, exists solely within the exercise,
 * and since, for now, we're auto-genning names, this also
 * has overrideable functions to allow some parameters for
 * that random generation (number and length of names)
 */
public class NamedItem extends UniqueItem implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private String _name;
  
  public String getName()            { return _name; }
  public void   setName(String pVal) { _name = pVal; }  
  
  public NamedItem()
  {
    
  }
  
  public String toString()
  {
    return getName();
  }
  
  public int getMinWordsInName  () { return 1; }
  public int getMaxWordsInName  () { return 1; }
  public int getMinLengthPerWord() { return 1; }
  public int getMaxLengthPerWord() { return 1; }
}
