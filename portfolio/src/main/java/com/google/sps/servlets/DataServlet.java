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
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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

  UserService userService = UserServiceFactory.getUserService();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);

      int size = results.countEntities(FetchOptions.Builder.withDefaults());
    
      ArrayList<Comment> comments = new ArrayList<>();
      for (Entity entity : results.asIterable()) {
          String name = (String) entity.getProperty("name");
          String content = (String) entity.getProperty("content");
          Date timestamp = (Date) entity.getProperty("timestamp");
          String email = (String) entity.getProperty("email");

          Comment comment = new Comment(name, content, timestamp, email);
          comments.add(comment);
      }

      String json = convertToJsonUsingGson(comments, size);
      response.setContentType("application/json;");
      response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Comment comment = makeComment(request);
      
      
      if (comment != null) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("name", comment.getName());
        commentEntity.setProperty("content", comment.getContent());
        commentEntity.setProperty("timestamp", comment.getDate());
        commentEntity.setProperty("email", comment.getEmail());

        datastore.put(commentEntity);
      }
      response.sendRedirect("/index.html");
  }

  private String convertToJsonUsingGson(ArrayList<Comment> comments, int numComments) {
    Gson gson = new Gson();

    JsonObject obj = new JsonObject();
    JsonArray jsonComments = new JsonArray();
    for (int i = 0; i < comments.size(); i++) {
        jsonComments.add(comments.get(i).getJsonObject());
    }
    obj.add("comments", jsonComments);
    obj.addProperty("numComments", numComments);
    String json = gson.toJson(obj);
    return json;
  }

  private Comment makeComment(HttpServletRequest request) {
      String name = request.getParameter("name");
      String comment = request.getParameter("comment");
      if (name == null && comment == null)
        return null;
      else
        return new Comment(name, comment, userService.getCurrentUser().getEmail());
  }
}