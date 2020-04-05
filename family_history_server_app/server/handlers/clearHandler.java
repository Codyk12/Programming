package handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import dataAccessObject.databaseException;
import json.json;
import requests.clearRequest;
import results.clearResult;
import services.clearService;

/**
 * Created by Admin on 2/13/17.
 */

public class clearHandler implements HttpHandler{

    public clearHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        json jsn = new json();
        OutputStream os = null;
        String response;

        try {

            Headers reqHeaders = exchange.getRequestHeaders();
            if (reqHeaders.containsKey("Authorization")) {

                String authToken = reqHeaders.getFirst("Authorization");
                clearRequest request = new clearRequest(authToken);

                clearService service = new clearService();
                clearResult result = service.clear(request);

                if(result != null) {
                    response = jsn.encode(result);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                    os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                else
                {
                    String resultS = "Clear request error";
                    response = jsn.encode(resultS);
                    exchange.sendResponseHeaders(500, response.length());
                    os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } catch (databaseException e) {
            e.printStackTrace();
        } catch(Exception e)
        {
            e.printStackTrace();
        }

        finally {
            os.close();
        }


    }
}
