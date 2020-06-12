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
import java.util.Enumeration;

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
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


  // sends the appropriate amount of comments to the client along with the number of comments
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
      PreparedQuery results = datastore.prepare(query);
      
      int limit = getLimit(request);  // the number of comments to send
      int offset = getOffset(request);  // comment to start from
      FetchOptions options = FetchOptions.Builder.withLimit(limit).offset(offset);

      int size = results.countEntities(FetchOptions.Builder.withDefaults());
    
      ArrayList<Comment> comments = new ArrayList<>();
      for (Entity entity : results.asList(options)) {
          String content = (String) entity.getProperty("content");
          long timestamp = (long) entity.getProperty("timestamp");
          String userId = (String) entity.getProperty("userId");
          String name = (String) getNickname(userId);
          long commentId = entity.getKey().getId();

          Comment comment = new Comment(name, content, timestamp, userId, commentId);
          comments.add(comment);
      }

      String json = convertToJsonUsingGson(comments, size);
      response.setContentType("application/json;");
      response.getWriter().println(json);
  }

    // collects comment data from the form
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String userId = userService.getCurrentUser().getUserId();
      Comment comment = makeComment(request); // this comment is basically to get help extract the
                                              // values more cleanly to make an entity
      
      if (comment != null) {
        Entity commentEntity = new Entity("Comment");
        commentEntity.setProperty("content", comment.getContent());
        commentEntity.setProperty("timestamp", comment.getTimestamp());
        commentEntity.setProperty("userId", userId);

        datastore.put(commentEntity);
      }
      response.sendRedirect("/index.html");
  }

  // transforms the comments objects and the number of comments into JSON  
  private String convertToJsonUsingGson(ArrayList<Comment> comments, int numComments) {
      String userId = "";
      if (userService.isUserLoggedIn()) {
          userId = userService.getCurrentUser().getUserId();
      } else {
          userId = null;
      }
    Gson gson = new Gson();

    JsonObject obj = new JsonObject();
    JsonArray jsonComments = new JsonArray();
    for (int i = 0; i < comments.size(); i++) {
        Comment current = comments.get(i);
        JsonObject commentObj = current.getJsonObject();
        commentObj.addProperty("isOwnComment", current.isSameUserId(userId));
        jsonComments.add(commentObj);
    }
    obj.add("comments", jsonComments);
    obj.addProperty("numComments", numComments);
    String json = gson.toJson(obj);
    return json;
  }

  // creates a comment object from the form data
  private Comment makeComment(HttpServletRequest request) {
      String userId = userService.getCurrentUser().getUserId();
      String name = getNickname(userService.getCurrentUser().getUserId());
      String comment = request.getParameter("comment");
      if (name == null && comment == null)
        return null;
      else
        return new Comment(name, comment, userId);
  }

  // calculates the number of comments to send (which is just comments-to-show)
  private int getLimit(HttpServletRequest request) {
      String commentsToShowString = request.getParameter("comments-to-show");
      int limit = Integer.parseInt(commentsToShowString);
      return limit;
  }

  // calculate what comment to begin at
  private int getOffset(HttpServletRequest request) {
      String numberString = request.getParameter("comments-to-show");
      String pageString = request.getParameter("page-number");

      int commentsToShow = Integer.parseInt(numberString);
      int currPage = Integer.parseInt(pageString);

      int offset = (currPage - 1) * commentsToShow;
      return offset;
  }
    
    // gets the nickname of the user from their ID
    public String getNickname(String id) {
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
