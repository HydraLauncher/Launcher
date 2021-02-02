package net.minecraft.hopper;

import java.nio.charset.*;
import org.apache.commons.io.*;
import java.io.*;
import java.net.*;

public class Util
{
    public static String performPost(final URL url, final String parameters, final Proxy proxy, final String contentType, final boolean returnErrorPage) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        final byte[] paramAsBytes = parameters.getBytes(Charset.forName("UTF-8"));
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + paramAsBytes.length);
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        final DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(paramAsBytes);
        writer.flush();
        writer.close();
        InputStream stream = null;
        try {
            stream = connection.getInputStream();
        }
        catch (IOException e) {
            if (!returnErrorPage) {
                throw e;
            }
            stream = connection.getErrorStream();
            if (stream == null) {
                throw e;
            }
        }
        return IOUtils.toString(stream);
    }
    
    public static URL constantURL(final String input) {
        try {
            return new URL(input);
        }
        catch (MalformedURLException e) {
            throw new Error(e);
        }
    }
}
