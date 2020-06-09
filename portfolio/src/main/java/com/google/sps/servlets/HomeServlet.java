package com.google.sps.servlets;

import com.google.sps.data.Comment;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    UserService userService = UserServiceFactory.getUserService();
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        response.setContentType("application/json");
        if (!userService.isUserLoggedIn()) {
            String loginUrl = userService.createLoginURL("/index.html");
            out.println(convertJson(false, loginUrl));
        } else {
            String logoutUrl = userService.createLogoutURL("/index.html");
            out.println(convertJson(true, logoutUrl));
        }
  }

  public String convertJson(Boolean isLoggedIn, String url) {
      Gson gson = new Gson();
      JsonObject obj = new JsonObject();
      obj.addProperty("isLoggedIn", isLoggedIn);
      obj.addProperty("url", url);
      return gson.toJson(obj);
  }
}
