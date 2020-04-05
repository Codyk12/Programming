package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Created by Admin on 2/15/17.
 */

public class indexHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String seperator = System.getProperty("file.separator");
        URI uri = httpExchange.getRequestURI();

        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

        String filePathStr = String.valueOf(uri);

        if(filePathStr.equals(seperator))
        {
            filePathStr = "www"+seperator+"index.html";

        }
        else if(filePathStr.equals(seperator+"css"+seperator+"main.css"))
        {
            filePathStr = "www"+seperator+"css"+seperator+"main.css";

        }
        else if(filePathStr.equals(seperator+"favicon.ico"))
        {
            filePathStr = "www"+seperator+"favicon.ico";

        }
            System.out.println(filePathStr);
        try
        {
            Path filePath = FileSystems.getDefault().getPath(filePathStr);
            Files.copy(filePath, httpExchange.getResponseBody());
        }
        catch (NoSuchFileException e)
        {
            filePathStr = "www"+seperator+"404.html";
            Path filePath = FileSystems.getDefault().getPath(filePathStr);
            Files.copy(filePath, httpExchange.getResponseBody());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            httpExchange.getResponseBody().close();
        }

    }

}
