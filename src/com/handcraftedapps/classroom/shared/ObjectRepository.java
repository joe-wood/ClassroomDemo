package com.handcraftedapps.classroom.shared;

import java.util.HashMap;
import java.util.List;

import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;

public class ObjectRepository
{
  static private ObjectRepository s_instance = new ObjectRepository();
  
  static public ObjectRepository getInstance() { return s_instance; }
  static public void             setInstance(ObjectRepository pVal) { s_instance = pVal; }
  
  private HashMap<Long, UniqueItem> _itemMap = new HashMap<>();
  
  public synchronized void addItem(UniqueItem pItem)
  {
    if (pItem != null)
    {
      _itemMap.put(pItem.getId(), pItem);
    }
  }
  
  public synchronized UniqueItem getItem(Long pItemId)
  {
    UniqueItem item = _itemMap.get(pItemId);
    
    if ((item != null) && (item.getId().equals(pItemId)))
    {
      return item;
    }

    return null;
  }
  
  public synchronized void fetchItem(Long            pItemId,
                                     IItemRehydrator pHydrator)
  {
  // No-op by default except on client  
  }
  
  public synchronized void fetchItems(Long[]           pItemIds,
                                      IArrayRehydrator pHydrator)
  {
  }
  
  public synchronized void fetchItems(List<Long>       pItemIds,
                                      IArrayRehydrator pHydrator)
  {
    Long[] array = new Long[pItemIds.size()];
    
    fetchItems(pItemIds.toArray(array), pHydrator);
  }
  
  public void indicateRehydrationError(UniqueItem pCaster,
                                       UniqueItem pCasted,
                                       String     pCastIntent)
  {
  }
}
