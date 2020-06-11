package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {

    UserService userService = UserServiceFactory.getUserService();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }
        String nickname = request.getParameter("name");
        String id = userService.getCurrentUser().getUserId();

        Entity userEntity = new Entity("UserInfo", id);
        userEntity.setProperty("id", id);
        userEntity.setProperty("nickname", nickname);
        datastore.put(userEntity);

        response.sendRedirect("/index.html");
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