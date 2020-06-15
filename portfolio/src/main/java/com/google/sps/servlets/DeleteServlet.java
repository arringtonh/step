package com.google.sps.servlets;

import com.google.sps.data.Comment;
import java.util.Date;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;

/** Servlet that deletes some data. */
@WebServlet("/delete-data")
public class DeleteServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      long commentId = Long.parseLong(request.getParameter("comment-id"));

      Key key = KeyFactory.createKey("Comment", commentId);
      String currentUserId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
      String commentUserId = null;
      try {
          commentUserId = (String) datastore.get(key).getProperty("userId");
      } catch (EntityNotFoundException e) {
          commentUserId = null;
      }

      if (commentUserId.equals(currentUserId))
        datastore.delete(key);

      response.sendRedirect("/index.html");
  }
}