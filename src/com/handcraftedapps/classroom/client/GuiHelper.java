package com.handcraftedapps.classroom.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GuiHelper
{
  static public HorizontalPanel newHorizontalPanel(String pStyleName)
  {
    HorizontalPanel panel = new HorizontalPanel();
    if ((pStyleName != null) && !pStyleName.isEmpty()) panel.setStylePrimaryName(pStyleName);
    return panel;
  }
  
  static public HorizontalPanel newHorizontalPanel(Widget... pWidgets)
  {
    return newHorizontalPanel(null, pWidgets);
  }
  
  static public HorizontalPanel newHorizontalPanel(String    pStyleName,
                                                   Widget... pWidgets)
  {
    HorizontalPanel panel = newHorizontalPanel(pStyleName);
    
    for (Widget widget : pWidgets) panel.add(widget);
    
    return panel;
  }
  
  static public VerticalPanel newVerticalPanel(String pStyleName)
  {
    VerticalPanel panel = new VerticalPanel();
    if ((pStyleName != null) && !pStyleName.isEmpty()) panel.setStylePrimaryName(pStyleName);
    return panel;
  }
  
  static public VerticalPanel newVerticalPanel(Widget... pWidgets)
  {
    return newVerticalPanel(null, pWidgets);
  }
  
  static public VerticalPanel newVerticalPanel(String    pStyleName,
                                               Widget... pWidgets)
  {
    VerticalPanel panel = newVerticalPanel(pStyleName);
    
    for (Widget widget : pWidgets) panel.add(widget);
    
    return panel;
  }
  
  static public synchronized Label getSpacer(int pSpacingInPixels)
  {
    Label spacer = new Label();
    spacer.setWidth (pSpacingInPixels + "px");
    spacer.setHeight(pSpacingInPixels + "px");
    return spacer;
  }
}
