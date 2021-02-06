package com.mojang.launcher;

import java.util.*;
import java.net.*;
import org.apache.commons.io.*;
import java.io.*;
import org.apache.logging.log4j.*;

public class Http
{
    private static final Logger LOGGER;
    
    private Http() {
    }
    
    public static String buildQuery(final Map<String, Object> query) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, Object> entry : query.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                Http.LOGGER.error("Unexpected exception building query", e);
            }
            if (entry.getValue() != null) {
                builder.append('=');
                try {
                    builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    Http.LOGGER.error("Unexpected exception building query", e);
                }
            }
        }
        return builder.toString();
    }
    
    public static String performGet(final URL url, final Proxy proxy) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection(proxy);
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(60000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Hydra/0");
        final InputStream inputStream = connection.getInputStream();
        try {
            return IOUtils.toString(inputStream);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
