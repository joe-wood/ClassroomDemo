package com.handcraftedapps.classroom.server.jsonservices;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.handcraftedapps.classroom.shared.ObjectRepository;

// Extend HttpServlet class
public class UniqueItemFetcherServlet extends HttpServlet
{
  private static final long serialVersionUID = 1L;
 
   public void init() throws ServletException {
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
     long objectId = Long.valueOf(request.getParameter("id"));
     
      String json = new Gson().toJson(ObjectRepository.getInstance().getItem(objectId));
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(json);
   }

   public void destroy() {
      // do nothing.
   }
}