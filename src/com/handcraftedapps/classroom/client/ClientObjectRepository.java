package com.handcraftedapps.classroom.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.handcraftedapps.classroom.client.services.ClassroomService;
import com.handcraftedapps.classroom.shared.ObjectRepository;
import com.handcraftedapps.classroom.shared.UniqueItem;
import com.handcraftedapps.classroom.shared.interfaces.IArrayRehydrator;
import com.handcraftedapps.classroom.shared.interfaces.IComplexSerialized;
import com.handcraftedapps.classroom.shared.interfaces.IItemRehydrator;

public class ClientObjectRepository extends ObjectRepository
{
  public static void init()
  {
    ObjectRepository.setInstance(new ClientObjectRepository());
  }
  
  @Override
  public synchronized void addItem(UniqueItem pItem)
  {
    super.addItem(pItem);
    if (pItem instanceof IComplexSerialized)
    {
      ((IComplexSerialized) pItem).rehydrate(null);
    }
  }
  
  @Override
  public synchronized UniqueItem getItem(Long pItemId)
  {
    UniqueItem item = super.getItem(pItemId);
    
    if (item != null)
    {
      return item;
    }
    
    fetchItem(pItemId, null);
    
    return null;
  }
  
  
  @Override
  public synchronized void fetchItem(Long                  pItemId,
                                     final IItemRehydrator pHydrator)
  {
    UniqueItem storedItem = super.getItem(pItemId);
    if (storedItem == null)
    {
      ClassroomService.Util.getInstance().getItem(pItemId, new AsyncCallback<UniqueItem>()
      {
        public void onFailure(Throwable caught)
        {
          log(caught.getMessage());
        }

        public void onSuccess(UniqueItem pItem)
        {
          getInstance().addItem(pItem);
          pHydrator.rehydrateItem(pItem);
        }
      });
    }
    else
    {
      pHydrator.rehydrateItem(storedItem);
    }
  }
  
  @Override
  public synchronized void fetchItems(final Long[]           pItemIds,
                                      final IArrayRehydrator pHydrator)
  {
    List<Long> unfound = new ArrayList<>();
    
    for (Long id : pItemIds)
    {
      UniqueItem storedItem = super.getItem(id);
      if (storedItem == null)
      {
        unfound.add(id);
      }
    }
    
    if (!unfound.isEmpty())
    {
      Long[] itemsToGet = new Long[unfound.size()];
    
      unfound.toArray(itemsToGet);
      
      ClassroomService.Util.getInstance().getItems(itemsToGet, new AsyncCallback<UniqueItem[]>()
      {
        public void onFailure(Throwable caught)
        {
        }

        public void onSuccess(UniqueItem[] pItems)
        {
          for (UniqueItem item : pItems)
          {
            getInstance().addItem(item);
          }
          
          callArrayRehydrator(pHydrator, pItemIds);
        }
      });
    }
    else
    {
      callArrayRehydrator(pHydrator, pItemIds);
    }
  }
  
  private void callArrayRehydrator(final IArrayRehydrator pHydrator,
                                   final Long[]           pItemIds)
  {
    List<UniqueItem> itemList = new ArrayList<>();
    
    for (Long itemId : pItemIds)
    {
      UniqueItem item = getItem(itemId);
      
     if (item.getId().equals(itemId))
     {
        itemList.add(item);
      }
      else
      {
        log("Item '"+item+"' is rogue ["+item.getId()+" is not id "+itemId+"]");
      }
    }
    
    UniqueItem[] items = new UniqueItem[itemList.size()];

    pHydrator.rehydrateArray(itemList.toArray(items));
  }
  
  @Override
  public void indicateRehydrationError(UniqueItem pCaster,
                                       UniqueItem pCasted,
                                       String     pCastIntent)
  {
    final String message = pCaster.getClass().getSimpleName() + "["+ pCaster.getId()+"]: Cannot cast '" + pCasted.getClass().getSimpleName() + "' ["+pCasted.getId()+"] as '"+pCastIntent+"'";

    log(message);
  }
  
  private void log(String pMessage)
  {
    ClassroomService.Util.getInstance().log(pMessage, new AsyncCallback<Void>()
    {
      @Override public void onFailure(Throwable caught) { }
      @Override public void onSuccess(Void result) { }
    });
  }
}
