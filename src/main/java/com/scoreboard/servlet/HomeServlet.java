package com.scoreboard.servlet;

import com.scoreboard.util.JspPaths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet ("/home")
public class HomeServlet extends HttpServlet {
    private static final String HOME_JSP = JspPaths.HOME_JSP;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher(HOME_JSP).forward(req, resp);
    }
}

