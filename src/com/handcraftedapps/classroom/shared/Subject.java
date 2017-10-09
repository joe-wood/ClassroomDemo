package com.handcraftedapps.classroom.shared;

import java.io.Serializable;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

/**
 * For our purposes, a subject is just a named item with a list of associated classes
 */
public class Subject extends NamedItem implements Serializable, IComplexSerialized
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
  public SubjectClass[] getSubjectClasses() { return _classes; }
  
  /**
   * These are the IDs for classes to allow for rehydration
   */
  private Long[]  _classIds;  
  public Long[] getSubjectClassIds() { return _classIds; }
  

  public void addSubjectClass(SubjectClass pClass)
  {
    // Fine, if no array or it's too small, make one big enough
    if (_classes == null)
    {
      _classes = new SubjectClass[1];  
    }
    else
    {
      SubjectClass[] newClasses = new SubjectClass[_classes.length + 1];
      
      for (int loop = 0; loop < _classes.length; ++loop)
      {
        newClasses[loop] = _classes[loop];
      }
      
      _classes = newClasses;
    }
    
    _classes[_classes.length - 1] = pClass;
    
    addSubjectClassId((pClass == null) ? null : pClass.getId());  
  }
  
  private void addSubjectClassId(Long pClassId) 
  { 
    // Fine, if no array or it's too small, make one big enough
    if (_classIds == null)
    {
      _classIds = new Long[1];  
    }
    else
    {
      Long[] newClasses = new Long[_classIds.length + 1];
      
      for (int loop = 0; loop < _classIds.length; ++loop)
      {
        newClasses[loop] = _classIds[loop];
      }
      
      _classIds = newClasses;
    }
    
    _classIds[_classIds.length - 1] = pClassId;
  }
  
  public Subject()
  {
    
  }
  
  public int getMinLengthPerWord() { return 4; }
  public int getMaxLengthPerWord() { return 10; }
  
  public String toString()
  {
    return super.toString();
  }
}
