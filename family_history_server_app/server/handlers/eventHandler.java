package handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import dataAccessObject.databaseException;
import json.json;
import requests.eventRequest;
import requests.fillRequest;
import requests.personRequest;
import results.eventResult;
import results.fillResult;
import results.personsResult;
import services.eventService;
import services.fillService;
import services.personService;

/**
 * Created by Admin on 2/14/17.
 */

public class eventHandler implements HttpHandler{

    public eventHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        json jsn = new json();
        OutputStream os = null;
        String eventID = "";
        String response;

        try{

            Headers reqHeaders = exchange.getRequestHeaders();
            String authToken = reqHeaders.getFirst("Authorization");

            String uri = exchange.getRequestURI().toString();
            if(uri.length() > 7) {
                eventID = uri.substring(7);
            }

            eventRequest request = new eventRequest(eventID, authToken);
            eventService service = new eventService();
            eventResult result = service.getEvents(request);


            if(result != null) {
                response = jsn.encode(result);
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                os = exchange.getResponseBody();
                os.write(response.getBytes());
            }
            else
            {
                String resultS = "Event request error";
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
