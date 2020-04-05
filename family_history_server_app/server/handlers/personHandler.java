package handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import dataAccessObject.databaseException;
import json.json;
import requests.personRequest;
import results.personsResult;
import services.personService;

/**
 * Handler for looking up people
 * Created by Admin on 3/6/17.
 */

public class personHandler implements HttpHandler {

    public personHandler() {
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        json jsn = new json();
        OutputStream os = null;
        String personID = "";
        String seperator = System.getProperty("file.separator");
        String response;

        try {

            Headers reqHeaders = exchange.getRequestHeaders();
            String authToken = reqHeaders.getFirst("Authorization");

            String uri = exchange.getRequestURI().toString();
            if(uri.length() > 8) {
                personID = uri.substring(8);
            }

            personRequest request = new personRequest(personID, authToken);
            personService service = new personService();
            personsResult result = service.getPersons(request);

            if(result != null) {
                response = jsn.encode(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else
            {
                String resultS = "Person request error";
                response = jsn.encode(resultS);
                exchange.sendResponseHeaders(500, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }


        } catch (IOException e) {
            exchange.sendResponseHeaders(400, 5);
            os = exchange.getResponseBody();
            os.write(Integer.parseInt(e.getLocalizedMessage()));

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
