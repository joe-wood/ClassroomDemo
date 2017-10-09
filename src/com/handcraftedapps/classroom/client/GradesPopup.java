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
import com.handcraftedapps.classroom.shared.QuizAnswer;
import com.handcraftedapps.classroom.shared.QuizStudentAnswers;
import com.handcraftedapps.classroom.shared.interfaces.IRehydrateCallback;

public class GradesPopup extends PopupPanel 
{
  private HTML  _gradesList = new HTML("...pending");
  private final Long _studentId;
  private final Long _subjectClassId;
  private final int  _weekIndex;
  public GradesPopup(final Long pStudentId, final Long pSubjectClassId, final int pWeekIndex) 
  {
    super(true);
    _studentId = pStudentId;
    _subjectClassId = pSubjectClassId;
    _weekIndex = pWeekIndex;
    VerticalPanel panel = GuiHelper.newVerticalPanel("", /*new Label("(click outside of popup to close)"), GuiHelper.getSpacer(5),*/  _gradesList);
    setWidget(panel);
    
    final GradesPopup thisPopup = this;
    addDomHandler(new MouseOutHandler() { public void onMouseOut(MouseOutEvent event) { thisPopup.hide(); } }, MouseOutEvent.getType());
    addDomHandler(new ClickHandler() { public void onClick(ClickEvent event) { thisPopup.hide(); } }, ClickEvent.getType());
    fetchGrades();
  }
  
  void fetchGrades()
  {
    ClassroomService.Util.getInstance().getQuizAnswers(_studentId, _subjectClassId, _weekIndex, _weekIndex, new AsyncCallback<List<QuizStudentAnswers>>()
    {
      public void onFailure(Throwable caught)
      {
        _gradesList.setText("Not found.");
      }

      public void onSuccess(List<QuizStudentAnswers> pAnswers)
      {
        if ((pAnswers == null) || (pAnswers.isEmpty()))
        {
          _gradesList.setText("Not found.");
          return;
        }
        final QuizStudentAnswers answers = pAnswers.get(0);
        
        answers.rehydrate(new IRehydrateCallback(){
         @Override
          public void rehydrateDone()
          {
             String text = "<table border='0'><tr><th><u>Q#</u></th><th><u>Score</u></th></tr>";
             int question = 1;
             for (QuizAnswer quizAnswer : answers.getAnswers())
             {
               text += "<tf><td align='right'>" + (question++) + "</td><td>"+Classroom.getSvgForScore(false, false, 50, quizAnswer.getScore().getScoreValue())+"</td></tr>";
             }
             text += "</table>";
             _gradesList.setHTML(text);
          }});
      }
    }); 
  }
}