package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import dataAccessObject.databaseException;
import json.json;
import requests.registerRequest;
import results.registerResult;
import services.registerService;

import static javax.imageio.ImageIO.read;

/**
 * Created by Admin on 2/13/17.
 */

public class registerHandler implements HttpHandler {

    public registerHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        json jsn = new json();
        OutputStream os = null;
        String response;

        try{
            System.out.println(exchange.getRequestBody());
            registerRequest request = (registerRequest)jsn.decodeExchange(exchange, registerRequest.class);
            registerService service = new registerService();
            System.out.println(request);
            registerResult result = service.register(request);

            if(result != null) {
                response = jsn.encode(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
            }
            else
            {
                String resultS = "Register request error";
                response = jsn.encode(resultS);
                exchange.sendResponseHeaders(500, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            os.close();
        }
    }
}
