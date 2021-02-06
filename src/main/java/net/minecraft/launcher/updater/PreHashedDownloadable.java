package net.minecraft.launcher.updater;

import org.apache.commons.io.*;
import com.mojang.launcher.updater.download.*;
import java.net.*;
import java.io.*;

public class PreHashedDownloadable extends Downloadable
{
    private final String expectedHash;
    
    public PreHashedDownloadable(final Proxy proxy, final URL remoteFile, final File localFile, final boolean forceDownload, final String expectedHash) {
        super(proxy, remoteFile, localFile, forceDownload);
        this.expectedHash = expectedHash;
    }
    
    @Override
    public String download() throws IOException {
        ++this.numAttempts;
        this.ensureFileWritable(this.getTarget());
        final File target = this.getTarget();
        String localHash = null;
        if (target.isFile()) {
            localHash = Downloadable.getDigest(target, "SHA-1", 40);
            if (this.expectedHash.equalsIgnoreCase(localHash)) {
                return "Local file matches hash, using that";
            }
            FileUtils.deleteQuietly(target);
        }
        try {
            final HttpURLConnection connection = this.makeConnection(this.getUrl());
            connection.setRequestProperty("User-Agent", "Hydra/0");
            final int status = connection.getResponseCode();
            if (status / 100 == 2) {
                this.updateExpectedSize(connection);
                final InputStream inputStream = new MonitoringInputStream(connection.getInputStream(), this.getMonitor());
                final FileOutputStream outputStream = new FileOutputStream(this.getTarget());
                final String digest = Downloadable.copyAndDigest(inputStream, outputStream, "SHA", 40);
                if (this.expectedHash.equalsIgnoreCase(digest)) {
                    return "Downloaded successfully and hash matched";
                }
                throw new RuntimeException(String.format("Hash did not match downloaded file (Expected %s, downloaded %s)", this.expectedHash, digest));
            }
            else {
                if (this.getTarget().isFile()) {
                    return "Couldn't connect to server (responded with " + status + ") but have local file, assuming it's good";
                }
                throw new RuntimeException("Server responded with " + status);
            }
        }
        catch (IOException e) {
            if (this.getTarget().isFile()) {
                return "Couldn't connect to server (" + e.getClass().getSimpleName() + ": '" + e.getMessage() + "') but have local file, assuming it's good";
            }
            throw e;
        }
    }
}
