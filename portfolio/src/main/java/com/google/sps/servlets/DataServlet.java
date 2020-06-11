// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private int numComments = 10;
  private int page = 0;
  UserService userService = UserServiceFactory.getUserService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      int offset = page * numComments;
      FetchOptions options = FetchOptions.Builder.withLimit(numComments).offset(offset);


      ArrayList<Comment> comments = new ArrayList<>();
      
      for (Entity entity : results.asList(options)) {
          String name = getNickname(userService.getCurrentUser().getUserId());
          String content = (String) entity.getProperty("content");
          Date timestamp = (Date) entity.getProperty("timestamp");

          Comment comment = new Comment(name, content, timestamp);
          comments.add(comment);
      }

      String json = convertToJsonUsingGson(comments);
      response.setContentType("application/json;");
      response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      changePage(request);
      Comment comment = makeComment(request);
      numComments = getCommentNumber(request);
      
      
      if (comment != null) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("content", comment.getContent());
        commentEntity.setProperty("timestamp", comment.getDate());

        datastore.put(commentEntity);
      }
      response.sendRedirect("/index.html");
  }

  private String convertToJsonUsingGson(ArrayList<Comment> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  private Comment makeComment(HttpServletRequest request) {
      String name = userService.getCurrentUser().getUserId();
      String comment = request.getParameter("comment");
      if (name == null && comment == null)
        return null;
      else
        return new Comment(name, comment);
  }

  private int getCommentNumber(HttpServletRequest request) {
      String commentNumberString = request.getParameter("num-comments");
      if (commentNumberString == null)
        return numComments;
      return Integer.parseInt(commentNumberString);
  }

  private void changePage(HttpServletRequest request) {
      String pageString = request.getParameter("pag");
      page = Math.max(page + Integer.parseInt(pageString), 0);
  }

    public String getNickname(String id) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
        PreparedQuery results = datastore.prepare(query);
        Entity entity = results.asSingleEntity();
        if (entity == null) {
            return "";
        }
        String nickname = (String) entity.getProperty("nickname");
        return nickname;
  }
}