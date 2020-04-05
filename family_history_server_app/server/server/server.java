package server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import java.nio.file.*;

import dataAccessObject.database;
import dataAccessObject.databaseException;
import handlers.clearHandler;
import handlers.eventHandler;
import handlers.fillHandler;
import handlers.indexHandler;
import handlers.loadHandler;
import handlers.loginHandler;
import handlers.personHandler;
import handlers.registerHandler;


/**
 * Created by Admin on 3/2/17.
 */

    public class server {


        private static final int MAX_WAITING_CONNECTIONS = 12;

        private HttpServer server;

    public static void main(String[] args)
    {
        String portNumber = "8080";
        new server().run(portNumber);
    }

        private void run(String portNumber)
        {

            String seperator = System.getProperty("file.separator");

            System.out.println("Initializing HTTP Server");
            try
            {
                database db = new database();

                try {
                    db.openConnection();
                    db.createTables();
                    db.closeConnection(true);
                } catch (databaseException e) {
                    e.printStackTrace();
                    try {
                        db.closeConnection(false);
                    } catch (databaseException e1) {
                        e1.printStackTrace();
                    }
                }

                server = HttpServer.create(new InetSocketAddress(Integer.parseInt(portNumber)), MAX_WAITING_CONNECTIONS);
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }

            server.setExecutor(null); // use the default executor

            System.out.println("Creating contexts");
            server.createContext(seperator, new indexHandler());
            server.createContext(seperator+"favicon.ico", new indexHandler());

            server.createContext(seperator+"main.css", new indexHandler());

            server.createContext(seperator+"user"+seperator+"register", new registerHandler());
            server.createContext(seperator+"user"+seperator+"login", new loginHandler());
            server.createContext(seperator+"clear",  new clearHandler());
            server.createContext(seperator+"fill", new fillHandler());
            server.createContext(seperator+"event", new eventHandler());
            server.createContext(seperator+"person", new personHandler());
            server.createContext(seperator+"load", new loadHandler());


            System.out.println("Starting server");
            server.start();
        }
    }
