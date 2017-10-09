package com.handcraftedapps.classroom.shared.interfaces;

public interface IComplexSerialized
{
  void rehydrate(IRehydrateCallback pCallback);
  boolean isRehydrated();
}
