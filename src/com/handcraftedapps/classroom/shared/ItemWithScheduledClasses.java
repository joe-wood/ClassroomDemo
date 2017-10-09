package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * Used in the exercise, this is any item that has classes for a number
 * of periods. It just consolidates the storage and logic around getting
 * to a class in a particular period.
 */
public class ItemWithScheduledClasses extends NamedItem implements Serializable, IComplexSerialized
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Rehydrate all the Long-ID-based items into real items from the repository
   */
  private transient boolean _rehydrateCalled = false;
  private transient boolean _rehydrated      = false;
  
  @Override
  public boolean isRehydrated() { return _rehydrated; }
  
  @Override
  public synchronized void rehydrate(final IRehydrateCallback pCallback)
  {
    if (_rehydrateCalled) return;
    
    _classes = null;
    
    final UniqueItem parent = this;
    if ((getSubjectClassIds() != null) && (getSubjectClassIds().length > 0))
    {
      ObjectRepository.getInstance().fetchItems(getSubjectClassIds(), new IArrayRehydrator() { 
        @Override public void rehydrateArray(UniqueItem[] pItems)
        {
          _classes = (pItems == null) ? null : new SubjectClass[pItems.length];
          for (int loop = 0; loop < ((pItems == null) ? 0 : pItems.length); ++loop)
          {
            try
            {
              _classes[loop] = (SubjectClass) pItems[loop];
            }
            catch (Exception e)
            {
              _classes[loop] = null;
              ObjectRepository.getInstance().indicateRehydrationError(parent, pItems[loop], "SubjectClass");
            }
          }
          _rehydrated = true;
          
          if (pCallback != null)
          {
            pCallback.rehydrateDone();
          }
        }});
    }
    _rehydrateCalled = true;
  }
  
  /**
   * These are the actual classes - not stored during transmission.
   */
  private transient SubjectClass[]  _classes;  
  public synchronized SubjectClass[] getSubjectClasses() { return _classes; }
  
  /**
   * These are the IDs for classes to allow for rehydration
   */
  private Long[]  _classIds;  
  public Long[] getSubjectClassIds() { return _classIds; }
  
  public SubjectClass getClassForPeriod(int pPeriod) 
  { 
    if ((_classes == null) || (pPeriod < 0) || (pPeriod >= _classes.length))
    {
      return null;
    }
    return _classes[pPeriod]; 
  }

  public Long getSubjectClassIdForPeriod(int pPeriod) 
  { 
    if ((_classIds == null) || (pPeriod < 0) || (pPeriod >= _classIds.length))
    {
      return null;
    }
    return _classIds[pPeriod]; 
  }
  
  static public final int MAX_REASONABLE_PERIOD = 12; // Won't allow more than this many in a day
    
  /**
   * Same as with SubjectClass instances, but only storing the ID for non-transient purposes
   * 
   * @param pPeriod
   * @param pClassId
   */
  private void setClassIdForPeriod(int  pPeriod, 
                                   Long pClassId) 
  { 
    // Just cannot have a negative index.
    if (pPeriod < 0) 
    {
      return;
    }
    
    // Also can't have very large number of them - makes no sense
    if (pPeriod > MAX_REASONABLE_PERIOD) 
    {
      return;
    }
    
    // Fine, if no array or it's too small, make one big enough
    if (_classIds == null)
    {
      _classIds = new Long[pPeriod + 1];  
    }
    else if (_classIds.length <= pPeriod)
    {
      Long[] newClasses = new Long[pPeriod + 1];
      
      for (int loop = 0; loop < _classIds.length; ++loop)
      {
        newClasses[loop] = _classIds[loop];
      }
      
      _classIds = newClasses;
    }
    
    _classIds[pPeriod] = pClassId;
  }

  /**
   * We're going to be nice and, if the period is outside the range
   * of the class array (or if that array is null), we'll do what we
   * need to do within reason.
   * 
   * @param pPeriod
   * @param pClass
   */
  public void setClassForPeriod(int          pPeriod, 
                                SubjectClass pClass) 
  { 
    // Just cannot have a negative index.
    if (pPeriod < 0) 
    {
      return;
    }
    
    // Also can't have very large number of them - makes no sense
    if (pPeriod > MAX_REASONABLE_PERIOD) 
    {
      return;
    }
    
    // Fine, if no array or it's too small, make one big enough
    if (_classes == null)
    {
      _classes = new SubjectClass[pPeriod + 1];  
    }
    else if (_classes.length <= pPeriod)
    {
      SubjectClass[] newClasses = new SubjectClass[pPeriod + 1];
      
      for (int loop = 0; loop < _classes.length; ++loop)
      {
        newClasses[loop] = _classes[loop];
      }
      
      _classes = newClasses;
    }
    
    _classes[pPeriod] = pClass;
    
    setClassIdForPeriod(pPeriod, (pClass == null) ? null : pClass.getId());
  }

  public ItemWithScheduledClasses()
  {
  }
  
  public ItemWithScheduledClasses(int pTotalPeriodsInDay)
  {
    _classes = new SubjectClass[pTotalPeriodsInDay];  
  }
  
  public String toString()
  {
    String result = super.toString() + " { ";

    for (SubjectClass subjectClass : getSubjectClasses()) result += subjectClass.getSubject().getName() + ", ";
    result += "}";
    
    return result.replace(", }", " }");
  }
}
