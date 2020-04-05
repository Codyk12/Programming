package handlers;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import dataAccessObject.databaseException;
import json.json;
import requests.fillRequest;
import requests.loadRequest;
import results.fillResult;
import services.fillService;

/**
 * Created by Admin on 2/13/17.
 */

public class fillHandler implements HttpHandler {

    public fillHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        json jsn = new json();
        OutputStream os = null;
        String seperator = System.getProperty("file.separator");
        int generations = 4;
        String response;
        String userName;
        fillRequest request;


        try{

            String uri = exchange.getRequestURI().toString();
            System.out.println(uri);
            userName = uri.substring(6);
            if (userName.contains(seperator)) {
                generations = Integer.parseInt(userName.substring(userName.length() - 1));
                userName = userName.substring(0, userName.length() - 2);
            }

            request = new fillRequest(userName, generations);

            fillService service = new fillService();
            fillResult result = service.fill(request);

            if(result != null) {
                response = jsn.encode(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else
            {
                String resultS = "Fill request error";
                response = jsn.encode(resultS);

                exchange.sendResponseHeaders(500, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }


        }
        catch(IOException e)
        {
            e.printStackTrace();
        } catch (databaseException e) {
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
