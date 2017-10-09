package com.handcraftedapps.classroom.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.handcraftedapps.classroom.client.services.ClassroomService;
import com.handcraftedapps.classroom.shared.ObjectRepository;
import com.handcraftedapps.classroom.shared.QuizScoreAndInfo;
import com.handcraftedapps.classroom.shared.Student;
import com.handcraftedapps.classroom.shared.SubjectClass;
import com.handcraftedapps.classroom.shared.Teacher;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Classroom
  implements EntryPoint
{
  private List<Teacher> _teachers = new ArrayList<>();

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
    ClientObjectRepository.init(); // Just to set this up as the repository of
                                   // choice
    setup();
  } 
  
  private int _totalWeeks  = 1;
  private int _sizeOfBlock = 1;
  private int _currentStartWeekIndex = 1;
  private int _currentEndWeekIndex   = 1;
  private int _currentTeacherSelectedPeriod = 1;

  final private ListBox   _teacherListBox    = new ListBox();
  final private ListBox   _classListBox      = new ListBox();
  final private ListBox   _studentListBox    = new ListBox();
  final private ListBox   _gradeBlockListBox = new ListBox();
  private HorizontalPanel _teacherPanel      = null;
  private HorizontalPanel _classPanel        = null;
  //private HorizontalPanel _studentPanel      = null;
  private HorizontalPanel _gradeBlockPanel   = null;
  private HorizontalPanel _selectionPanel    = null;
  
  private List<List<Double>> _scores = new ArrayList<>();
  
  private Grid _studentGrid = new Grid(1,1);
  
  private void setup()
  {
    _teacherListBox.setVisibleItemCount(1);
    _teacherListBox.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        loginTeacher(_teacherListBox.getSelectedItemText());
      }
    });
    _teacherPanel = GuiHelper.newHorizontalPanel("", new Label("Teacher: "), GuiHelper.getSpacer(5), _teacherListBox);

    _classListBox.setVisibleItemCount(1);
    _classListBox.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        selectTeacherClass(_classListBox.getSelectedItemText());
      }
    });
    _classPanel = GuiHelper.newHorizontalPanel("", new Label("Period: "), GuiHelper.getSpacer(5), _classListBox);


    _gradeBlockListBox.setVisibleItemCount(1);
    _gradeBlockListBox.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        selectBlock(_gradeBlockListBox.getSelectedItemText());
      }
    });
    _gradeBlockPanel = GuiHelper.newHorizontalPanel("", new Label("Block: "), GuiHelper.getSpacer(5), _gradeBlockListBox);

    _studentListBox.setVisibleItemCount(1);
    _studentListBox.addChangeHandler(new ChangeHandler()
    {
      @Override
      public void onChange(ChangeEvent event)
      {
        selectTeacherClassStudent(_studentListBox.getSelectedItemText());
      }
    });
    //_studentPanel = GuiHelper.newHorizontalPanel("", new Label("Student: "), GuiHelper.getSpacer(5), _studentListBox);

    _selectionPanel = GuiHelper.newHorizontalPanel("", _teacherPanel, GuiHelper.getSpacer(20), _classPanel, GuiHelper.getSpacer(20), _gradeBlockPanel);//, GuiHelper.getSpacer(20), _studentPanel);

    setRootPanel("selections", _selectionPanel);
    
    getBlockInfo();
  }
  
  private void resetStudentGrid()
  {
    setRootPanel("studentdata", null);
    _scores = new ArrayList<>();

    if ((_currentTeacher == null) || (_currentClass == null))
    {
      return;
    }
    
    try
    {
      int weeks = _currentEndWeekIndex - _currentStartWeekIndex + 1;
      
      _studentGrid = new Grid(_currentClass.getStudents().size() + 3, 2 + weeks);
  
      _studentGrid.setWidget(2, 0, new HTML("<center><b><u>Student</u></b></center>"));
      
      for (int loop = 0; loop < weeks; ++loop)
      {
        _studentGrid.setWidget(2, loop + 2, new HTML("<center><b>Week "+(_currentStartWeekIndex + loop + 1)+"</b></center>"));
      }
      _studentGrid.setWidget(2, 1, new HTML("<center><b><u>Average</u></b></center>"));
      
      _studentGrid.setWidget(0, 0, new HTML("<p align='right'>Class Average:&nbsp;</p>"));

      for (int row = 1; row <= _currentClass.getStudents().size(); ++row) 
      {
        List<Double> weekScores = new ArrayList<>();
        
        for (int week = _currentStartWeekIndex; week <= _currentEndWeekIndex; ++week)
        {
          weekScores.add(new Double(0.0));
        }
        weekScores.add(new Double(0.0));
        _scores.add(weekScores);

        final Student student = _currentClass.getStudents().get(row - 1);
        
        final Button button = new Button(getSvgForStudentButton(student));
        button.setStyleName("studentButton");

        try
        {
          final String currentClassName = _classListBox.getSelectedItemText().substring(_classListBox.getSelectedItemText().indexOf(":") + 1).trim();
          
          button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              final StudentOverviewPopop popup = new StudentOverviewPopop(student.getId(), currentClassName, _currentStartWeekIndex, _currentEndWeekIndex);
              final Button buttonToBeNear = button;
              popup.setPopupPositionAndShow(new PositionCallback() { public void setPosition(int offsetWidth, int offsetHeight) { popup.showRelativeTo(buttonToBeNear); } });
              popup.show();
            }
          });
        }
        catch (Exception e)
        {
          
        }
        
        _studentGrid.setWidget(row + 2, 0, button);
        fillInStudentAnswerGrid(row + 2, row - 1, 0, student.getId(),_currentClass.getId(), _currentStartWeekIndex, _currentEndWeekIndex);
      }
  
      setRootPanel("studentdata", _studentGrid);
    }
    catch (Exception e)
    {
      Window.alert(e.getMessage());
    }
  }
  
  private void fillInStudentAnswerGrid(final int pGridRow,
                                       final int pStudentIndex,
                                       final int pAveragesRow,
                                       final Long pStudentId, final Long pSubjectClassId, final int pStartWeekIndex, final int pStopWeekIndex)
  {
    ClassroomService.Util.getInstance().getQuizScores(pStudentId, pSubjectClassId, pStartWeekIndex, pStopWeekIndex, new AsyncCallback<List<QuizScoreAndInfo>>()
    {
      public void onFailure(Throwable caught)
      {
        setRootPanel("studentdata", new Label(caught.getLocalizedMessage()));
      }

      public void onSuccess(List<QuizScoreAndInfo> pAnswers)
      {
        double total = 0;
        for (QuizScoreAndInfo answer : pAnswers)
        {
          final QuizScoreAndInfo finalAnswer = answer;
          
          int column = answer.getWeek() - pStartWeekIndex + 2;
 
          _scores.get(pStudentIndex).set(column - 1, answer.getQuizScore().getScoreValue());

          total += answer.getQuizScore().getScoreValue();
          final Button button = new Button(getSvgForScore(false, false, null, answer.getQuizScore().getScoreValue()));
          button.setStyleName("gradeButton");
          button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              final GradesPopup popup = new GradesPopup(pStudentId, pSubjectClassId, finalAnswer.getWeek());
              final Button buttonToBeNear = button;
              popup.setPopupPositionAndShow(new PositionCallback() { public void setPosition(int offsetWidth, int offsetHeight) { popup.showRelativeTo(buttonToBeNear); } });
              popup.show();
            }
          });
          _studentGrid.setWidget(pGridRow, column, button);
        }
        _studentGrid.setWidget(pGridRow, 1, new HTML(getSvgForScore(true, false, null, total/pAnswers.size())));
        _scores.get(pStudentIndex).set(0, total/pAnswers.size());
        
        for (int column = 0; column < _scores.get(0).size(); ++column)
        {
          double average = 0;
          for (int student = 0; student < _scores.get(0).size(); ++student)
          {
            average += _scores.get(student).get(column);
          }
          
          average /= _scores.get(0).size();

          _studentGrid.setWidget(pAveragesRow, column + 1, new HTML(getSvgForScore(true, (column > 0), null, average)));
        }
      }
    });
  }
  
  public static String getSvgForStudentButton(Student pStudent)
  {
    double red   = 200;
    double green = 200;    
    double blue  = 240;
    
    return getSvgForLabel(300, pStudent.getName(), true, red, green, blue, false); 
  }
  
  public static String getSvgForStudentButtonSubjectClass(String pSubjectClassName, boolean pSpecial)
  {
    double red   = 255;
    double green = 230;    
    double blue  = 255;
    
    return getSvgForLabel(200, pSubjectClassName, false, red, green, blue, pSpecial); 
  }
  
  public static String getSvgForLabel(int pWidth, String pName, boolean pBorder, double pRed, double pGreen, double pBlue, boolean pSpecial)
  {
    double opacity  = 0.5;
       
    double red   = pRed;
    double green = pRed;    
    double blue  = pBlue;
    
    int width  = pWidth;
    int height = 25;
    
    String svgText = "<svg viewBox=\"0 0 "+width+" "+height+"\" xmlns=\"http://www.w3.org/2000/svg\" width=\""+width+"\" height=\""+height+"\">";
    
    if (pBorder)
    {
      svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:"+opacity+";fill:rgb("+red+","+green+","+blue+");stroke-width:2;stroke-opacity:1.0;stroke:rgb(60,60,60)\" />";
    }
    else
    {
      svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:"+opacity+";fill:rgb("+red+","+green+","+blue+");stroke:none\" />";
    }
    
    svgText += "<text x=\""+(width*0.05)+"\" text-anchor=\"start\" font-size=\"14\" y=\""+(height*0.75)+"\" font-style=\""+(pSpecial ? "italic" : "normal")+"\" fill=\"black\" opacity=\"1\">"+ 
               (pName + (pSpecial ? " (current)" : ""))+"</text>";
    svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:0.0;fill:rgb(255,255,255);stroke:none\" />";
    svgText += "</svg>";
    
    return svgText; 
  }
    
  public static String getSvgForScore(boolean pAverage, boolean pHideBorder, Integer pWidth, double pScore)
  {
    double scoreValue = pScore/100.0;

    if (scoreValue < 0) scoreValue = 0;
    if (scoreValue > 1) scoreValue = 1;
    
    double redVal   = 0.5;
    double greenVal = 0.5;
    double blueVal  = 0.0;
    double opacity  = 0.5;
    
    
    if (scoreValue < 0.6)      // Fail
    {
      redVal   = 1.0;
      greenVal = 0.0;
      opacity  = 0.6;
    }
    else if (scoreValue < 0.7)  // D
    {
      redVal   = 1.0 - (scoreValue - 0.7) * 0.25;
      greenVal = 0.5 + (scoreValue - 0.7) * 0.25;
      opacity  = 0.5;
    }
    else if (scoreValue < 0.8)  // C
    {
      redVal   = 0.75 - (scoreValue - 0.8) * 0.25;
      greenVal = 0.75 + (scoreValue - 0.8) * 0.25;
      opacity  = 0.4;
    }
    else if (scoreValue < 0.9)  // B
    {
      redVal   = 0.5 - (scoreValue - 0.9) * 0.5;
      greenVal = 1.0;
      opacity  = 0.3;
    }
    else
    {
      redVal   = 0.0;
      greenVal = 1.0;
      opacity  = 0.5;
    }
    
    double maxVal = Math.max(redVal, greenVal);
    double mult   = (maxVal <= 0) ? 1.0 : 1/maxVal;
    
    double red   = Math.round(mult * redVal   * 255);
    double blue  = Math.round(mult * blueVal  * 255);
    double green = Math.round(mult * greenVal * 255);    
    
    int width  = (pWidth == null) ? 100 : pWidth;
    int height = 25;
    
    String scoreText = "" + ((int)(pScore*10.0))/10.0;
    
    String svgText = "<svg viewBox=\"0 0 "+width+" "+height+"\" xmlns=\"http://www.w3.org/2000/svg\" width=\""+width+"\" height=\""+height+"\">";
    
    if (pAverage)
    {
      if (pHideBorder)
      {
        svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:0.25;fill:rgb("+red+","+green+","+blue+");stroke:none\" />";
      }
      else
      {
        svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:0.25;fill:rgb("+red+","+green+","+blue+");stroke-width:3;stroke:rgb(0,0,255)\" />";
      }
    }
    else
    {
      svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:"+opacity+";fill:rgb("+red+","+green+","+blue+");stroke-opacity:0.5;stroke-width:1;stroke:rgb(0,0,0)\" />";
    }
    svgText += "<text x=\""+(width/2)+"\" text-anchor=\"middle\" font-size=\"16\" y=\""+(height*0.8)+"\" fill=\"black\" opacity=\"1\">"+ scoreText+"</text>";
    svgText += "<rect width=\""+width+"\" height=\""+height+"\" style=\"fill-opacity:0.0;fill:rgb(255,255,255);stroke:none\" />";
    svgText += "</svg>";
    
    return svgText;
  }
  
  private void getBlockInfo()
  {
    setRootPanel("selections", new Label("Initializing..."));
    
    getTotalWeeks();
  }
  
  private void getTotalWeeks()
  {
    ClassroomService.Util.getInstance().getTotalNumberOfWeeks(new AsyncCallback<Integer>()
    {
      public void onFailure(Throwable caught)
      {
        setRootPanel("selections", new Label(caught.getLocalizedMessage()));
      }

      public void onSuccess(Integer pValue)
      {
        _totalWeeks = pValue;
        if (_totalWeeks < 1) _totalWeeks = 1;
        getSizeOfBlock();
      }
    });
  }
  
  private void getSizeOfBlock()
  {
    ClassroomService.Util.getInstance().getWeeksPerGradingBlock(new AsyncCallback<Integer>()
    {
      public void onFailure(Throwable caught)
      {
        setRootPanel("selections", new Label(caught.getLocalizedMessage()));
      }

      public void onSuccess(Integer pValue)
      {
        _sizeOfBlock = pValue;
        if (_totalWeeks < _sizeOfBlock) _sizeOfBlock = _totalWeeks;
        
        int numberOfBlocks = (int) Math.ceil((1.0 *_totalWeeks)/_sizeOfBlock);

        _gradeBlockListBox.clear();
        for (int loop = 0; loop < numberOfBlocks; ++loop)
        {
          int start = loop * _sizeOfBlock + 1;
          int end   = start + (_sizeOfBlock - 1);
          
          if (end > _totalWeeks) end = _totalWeeks;
          
          _gradeBlockListBox.addItem("Weeks " + start + " - " + end);
        }
        
        _currentStartWeekIndex = 0;
        _currentEndWeekIndex   = _sizeOfBlock - 1;
        
        getTeachers();
      }
    });
  }

  private void getTeachers()
  {
    setRootPanel("selections", new Label("Initializing..."));
    ClassroomService.Util.getInstance().getTeachers(new AsyncCallback<List<Teacher>>()
    {
      public void onFailure(Throwable caught)
      {
        setRootPanel("selections", new Label(caught.getLocalizedMessage()));
      }

      public void onSuccess(List<Teacher> pList)
      {
        _teachers = pList;
        _teacherListBox.clear();
        _teacherListBox.addItem("--");
        for (Teacher teacher : _teachers)
        {
          ObjectRepository.getInstance().addItem(teacher);
          _teacherListBox.addItem(teacher.getPrefix() + " " + teacher.getName());
        }

        setRootPanel("selections", _selectionPanel);
     }
    });
  }

  private Teacher _currentTeacher = null;

  public void loginTeacher(String pTeacherName)
  {
    _currentTeacher = null;
    for (Teacher teacher : _teachers)
    {
      String fullName = teacher.getPrefix() + " " + teacher.getName();
      if (fullName.equals(pTeacherName))
      {
        _currentTeacher = teacher;
        break;
      }
    }
    resetStudentGrid();

    getClassesForCurrentTeacher();
    _classListBox.setSelectedIndex(_currentTeacherSelectedPeriod - 1);
    selectTeacherClass(_classListBox.getSelectedItemText());
  }

  private void getClassesForCurrentTeacher()
  {
    _classListBox.clear();
    _studentListBox.clear();

    if (_currentTeacher == null) return;

    int period = 1;

    if (_currentTeacher.getSubjectClasses() != null)
    {
      for (SubjectClass klass : _currentTeacher.getSubjectClasses())
      {
        _classListBox.addItem("" + (period++) + ": " + klass.getName());
      }
    }
  }

  public void selectBlock(String pBlockName)
  {
    try
    {
      int hyphenIndex = pBlockName.indexOf("-");
      
      String start = pBlockName.substring(0, hyphenIndex).trim();
      
      start = start.substring(start.lastIndexOf(" ")).trim();
      
      String end   = pBlockName.substring(hyphenIndex + 1).trim();
      
      _currentStartWeekIndex = Integer.valueOf(start) - 1;
      _currentEndWeekIndex   = Integer.valueOf(end  ) - 1;
      
      resetStudentGrid();
    }
    catch (Exception e)
    {
      Window.alert(pBlockName + ": " + e.getLocalizedMessage());
    }
  }
  
  private SubjectClass _currentClass = null;

  public void selectTeacherClass(String pClassName)
  {
    _currentClass = null;
    int colonIndex = pClassName.indexOf(":");
    
    if ((_currentTeacher != null) && (colonIndex >= 0))
    {
      String actualClassName = pClassName.substring(colonIndex + 1).trim();
      for (SubjectClass klass : _currentTeacher.getSubjectClasses())
      {
        if (actualClassName.equals(klass.getName()))
        {
          _currentClass = klass;
          String temp = pClassName.substring(0, colonIndex).trim();
          try
          {
             _currentTeacherSelectedPeriod = Integer.valueOf(temp);
          }
          catch (Exception e)
          {
            _currentTeacherSelectedPeriod = 1;
          }
          break;
        }
      }
    }
    
    resetStudentGrid();

    getStudentsForCurrentClass();
  }

  private void getStudentsForCurrentClass()
  {
    _studentListBox.clear();
    _studentListBox.addItem("All");

    if ((_currentClass != null) && (_currentClass.getStudents() != null) && !_currentClass.getStudents().isEmpty())
    {
      Collections.sort(_currentClass.getStudents(), new Comparator<Student>()
      {
        public int compare(Student pOne,
                           Student pTwo)
        {
          return pOne.getName().compareTo(pTwo.getName());
        }
      });

      for (Student student : _currentClass.getStudents())
      {
        _studentListBox.addItem(student.getName());
      }
    }
  }

  // private Student _currentStudent = null;

  public void selectTeacherClassStudent(String pStudentName)
  {
    // _currentStudent = null;
    if ((_currentTeacher != null) && (_currentClass != null))
    {
      for (Student student : _currentClass.getStudents())
      {
        if (pStudentName.equals(student.getName()))
        {
          // _currentStudent = student;
          break;
        }
      }
    }
  }

  private void setRootPanel(String pName,
                            Widget pElement)
  {
    RootPanel panel = (pName == null) ? RootPanel.get() : RootPanel.get(pName);
    
    if (panel == null) return;
    
    panel.clear();
    
    if (pElement != null)
    {
      panel.add(pElement);
    }
  }
}
  