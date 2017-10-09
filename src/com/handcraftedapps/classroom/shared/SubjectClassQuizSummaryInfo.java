package com.handcraftedapps.classroom.shared;

import java.io.Serializable;
import java.util.List;

public class SubjectClassQuizSummaryInfo implements Serializable
{
  private static final long serialVersionUID = 1L;

  private String _className;
  public String getClassName() { return _className; }
  
  private List<QuizScoreAndInfo> _infoList;
  public List<QuizScoreAndInfo> getInfoList() { return _infoList; }

  public SubjectClassQuizSummaryInfo()
  {
    
  }

  public SubjectClassQuizSummaryInfo(String                 pClassName,
                                     List<QuizScoreAndInfo> pInfoList)
  {
    _className = pClassName;
    _infoList = pInfoList;
  }
}
