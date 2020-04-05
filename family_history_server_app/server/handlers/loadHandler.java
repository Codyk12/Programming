package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;

import dataAccessObject.databaseException;
import json.json;
import requests.loadRequest;
import results.loadResult;
import services.loadService;

/**
 * Created by Admin on 2/14/17.
 */

public class loadHandler implements HttpHandler{

    public loadHandler() {

    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        json jsn = new json();
        OutputStream os = null;
        String response;


        try{

            loadRequest request = (loadRequest)jsn.decodeExchange(exchange, loadRequest.class);
            loadService service = new loadService();
            loadResult result = service.load(request);

            if(result != null) {
                response = jsn.encode(result);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            else
            {
                String resultS = "Load request error";
                response = jsn.encode(resultS);
                exchange.sendResponseHeaders(500, response.length());
                os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }

        }
        catch(IOException e)
        {

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

