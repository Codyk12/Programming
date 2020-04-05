package handlers;

import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import dataAccessObject.databaseException;
import json.json;
import requests.loginRequest;
import results.loginResult;
import services.loginService;

/**
 * Created by Admin on 2/13/17.
 */

public class loginHandler implements HttpHandler{

    public loginHandler() {
    }

    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws IOException {
        json jsn = new json();
        String response;
        OutputStream os = null;

        try{
            loginRequest request = (loginRequest)jsn.decodeExchange(exchange, loginRequest.class);
            loginService service = new loginService();
            loginResult result = service.login(request);

            if(result != null) {
                response = jsn.encode(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
            }
            else
            {
                String resultS = "Login request error";
                response = jsn.encode(resultS);
                exchange.sendResponseHeaders(500, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

        }
        catch(IOException e)
        {
            exchange.sendResponseHeaders(400, 5);
            os = exchange.getResponseBody();
            os.write(Integer.parseInt("Error"));

        }  catch (databaseException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }finally {
            os.close();
        }
    }
}
