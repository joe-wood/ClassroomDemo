package com.handcraftedapps.classroom.client;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.handcraftedapps.classroom.client.services.ClassroomService;
import com.handcraftedapps.classroom.shared.QuizScoreAndInfo;
import com.handcraftedapps.classroom.shared.SubjectClassQuizSummaryInfo;

public class StudentOverviewPopop extends PopupPanel 
{
  private HTML  _summaryView = new HTML("...pending");
  
  private final Long   _studentId;
  private final String _currentSubjectClassName;
  private final int    _startWeekIndex;
  private final int    _endWeekIndex;
  
  public StudentOverviewPopop(final Long pStudentId, final String pCurrentSubjectClassName, final int pStartWeekIndex, final int pEndWeekIndex) 
  {
    super(true);
    _studentId = pStudentId;
    _currentSubjectClassName = pCurrentSubjectClassName;
    _startWeekIndex = pStartWeekIndex;
    _endWeekIndex = pEndWeekIndex;
    VerticalPanel panel = GuiHelper.newVerticalPanel("", _summaryView);
    setWidget(panel);
    
    final StudentOverviewPopop thisPopup = this;
    addDomHandler(new MouseOutHandler() { public void onMouseOut(MouseOutEvent event) { thisPopup.hide(); } }, MouseOutEvent.getType());
    addDomHandler(new ClickHandler() { public void onClick(ClickEvent event) { thisPopup.hide(); } }, ClickEvent.getType());
    fetchOverview();
  }
  
  void fetchOverview()
  {
    ClassroomService.Util.getInstance().getStudentSummary(_studentId, _startWeekIndex, _endWeekIndex, new AsyncCallback<List<SubjectClassQuizSummaryInfo>>()
    {
      public void onFailure(Throwable caught)
      {
        _summaryView.setText("Not found.");
      }

      public void onSuccess(List<SubjectClassQuizSummaryInfo> pSummaryList)
      {
        if ((pSummaryList == null) || (pSummaryList.isEmpty()))
        {
          _summaryView.setText("Not found.");
          return;
        }
        
        String text = "<div style=\"background-color:#eeeeff;margin-left:100px\"><table border='0'><tr><th><u>Subject</u></th><th><u>Average</u></th>";

        for (int week = _startWeekIndex; week <= _endWeekIndex; ++week)
        {
          text += "<th>Week "+(week + 1)+"</th>";
        }
        
        text += "</tr>";

        for (SubjectClassQuizSummaryInfo info : pSummaryList)
        {
          text += "<tr><td>" + Classroom.getSvgForStudentButtonSubjectClass(info.getClassName(), info.getClassName().equals(_currentSubjectClassName)) + "</td>";

          double score = 0.0;
          for (QuizScoreAndInfo quizLInfo : info.getInfoList())
          {
            score += quizLInfo.getQuizScore().getScoreValue();
          }
          text += "<td>" + Classroom.getSvgForScore(true, false, null, score/info.getInfoList().size())+"</td>";
          
          for (QuizScoreAndInfo quizLInfo : info.getInfoList())
          {
            text += "<td>" + Classroom.getSvgForScore(false, false, null, quizLInfo.getQuizScore().getScoreValue())+"</td>";
          }
          text += "</tr>";
        }
        text += "</table><div>";
        _summaryView.setHTML(text);
      }
    }); 
  }
}