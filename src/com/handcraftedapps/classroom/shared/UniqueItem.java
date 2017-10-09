package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

/**
 * Basically, just an item with a unique ID. Obviously,
 * this isn't necessarily going to be globally unique as 
 * implemented, but it doesn't matter now.
 */
public class UniqueItem implements Serializable
{
  private static final long serialVersionUID = 1L;

  private static long s_nextId = 0;
  
  /**
   * The unique ID, and its getter/setter. Pretty simple stuff.
   */
  private Long _id; 
  
  public Long getId() { return _id; }
  
  private String _objectType = this.getClass().getSimpleName();
  
  public String getObjectType() { return _objectType; }
  
  public UniqueItem()
  {
    _id = ++s_nextId;
  }
  
  public String toString()
  {
    return "" + getId();
  }
  
  public int hashCode()
  {
    return (int) ((long)_id);
  }
  
  public boolean equals(Object pOther)
  {
    if (!(pOther instanceof UniqueItem)) return false;
    
    return ((long)_id) == ((long)(((UniqueItem)pOther)._id));
  }
}
