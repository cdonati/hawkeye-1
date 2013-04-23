package com.appscale.hawkeye.cron;

import com.appscale.hawkeye.JSONUtils;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CronHandlerServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String query = request.getParameter("query");        
        if(query == null || query.equals(""))
        {
            Entity cronObject = new Entity("CronObject", "cronKey");
            Date lastUpdate = new Date();
            cronObject.setProperty("lastUpdate", lastUpdate);
            datastore.put(cronObject);
        }
        else
        {
            Key key = KeyFactory.createKey("CronObject", "cronKey");
            Entity cronObject;
            try
            {
                cronObject = datastore.get(key);
            }
            catch(EntityNotFoundException e)
            {
                try
                {
                    Thread.sleep(57000);
                }
                catch(InterruptedException e1)
                {
                    System.out.println("Thread interrupted exception");
                }
                try
                {
                    cronObject = datastore.get(key);
                }
                catch(EntityNotFoundException e2)
                {
                    throw new ServletException("CronObject entity was not found");
                }
            }
            Date now = new Date();
            Date lastUpdate = (Date)cronObject.getProperty("lastUpdate");
            long nowMillis = now.getTime();
            long lastUpdateMillis = lastUpdate.getTime();
            long delta = nowMillis - lastUpdateMillis;
            if(delta > 61000)
            {
                throw new ServletException("Delta was greater than 1 minute");
            }
            else
            {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("success", "true");
                JSONUtils.serialize(map, response);
            }
        }
    }
}
