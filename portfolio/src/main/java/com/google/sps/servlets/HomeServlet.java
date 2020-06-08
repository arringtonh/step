package com.google.sps.servlets;

import com.google.sps.data.Comment;
import java.util.Date;
import com.google.gson.Gson;
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
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        UserService userService = UserServiceFactory.getUserService();

        response.setContentType("text/html");
        if (!userService.isUserLoggedIn()) {
            String loginUrl = userService.createLoginURL("/home");
            out.println("Not logged in.");
            out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
        } else {
            String logoutUrl = userService.createLogoutURL("/home");
            out.println("Logged in!");
            out.println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
        }
  }
}
