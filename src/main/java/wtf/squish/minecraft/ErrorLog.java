package wtf.squish.minecraft;

import wtf.squish.minecraft.exceptions.HttpFailedException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.CRC32;

public class ErrorLog {
    private final String error;
    private final String stack;

    public ErrorLog(Throwable e) {
        this.error = e.toString();

        // Stack string
        StringBuilder stackBuilder = new StringBuilder();
        int stackDepth = 1;
        for(StackTraceElement el : e.getStackTrace()) {
            stackBuilder.append(stackDepth);
            stackBuilder.append(": ");
            stackBuilder.append(el.toString());
            stackBuilder.append('\n');
            stackDepth++;
        }

        if(e.getCause() != null) {
            stackBuilder.append("\nCaused by:");
            stackBuilder.append(e.getCause().getMessage());
            stackBuilder.append('\n');
            stackDepth = 1;
            for(StackTraceElement el : e.getCause().getStackTrace()) {
                stackBuilder.append(stackDepth);
                stackBuilder.append(": ");
                stackBuilder.append(el.toString());
                stackBuilder.append('\n');
                stackDepth++;
            }
        }

        this.stack = stackBuilder.toString();
    }

    public void send() {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(SquishLogs.config.getDomain() + "/api/v1/error?" + this.formatGetParameters(SquishLogs.config.getToken())))
                .setHeader("User-Agent", SquishLogs.getInstance().getHttpUserAgent())
                .setHeader("X-Game-Server-Token", SquishLogs.getSquishConfig().getToken())
                .build();

        CompletableFuture<HttpResponse<String>> response = SquishLogs.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());

        int statusCode;
        // no exceptions on failure cus that'll cause an infinite spiral
        try {
            statusCode = response.thenApply(HttpResponse::statusCode).get();
        } catch(InterruptedException | ExecutionException e) {
            SquishLogs.log("Error: Failed to send error log request.");
            return;
        }
        if(statusCode != 200) {
            SquishLogs.log("Status code was not successful on error log request.");
            SquishLogs.log("Response code " + statusCode);
        }
    }

    private String formatGetParameters(String communityToken) {
        StringBuilder params = new StringBuilder();

        params.append(formatSingleGetParamater("token", communityToken));
        params.append("&");
        params.append(formatSingleGetParamater("error", this.error));
        params.append("&");
        params.append(formatSingleGetParamater("stack", this.stack));
        params.append("&");

        CRC32 crc = new CRC32();
        crc.update((error + stack).getBytes());
        params.append(formatSingleGetParamater("hash", String.valueOf(crc.getValue())));
        SquishLogs.log("Error hashed as " + crc.getValue());
        params.append("&");

        params.append(formatSingleGetParamater("gmv", SquishLogs.getInstance().getServer().getVersion()
                + " (Bukkit "
                + SquishLogs.getInstance().getServer().getBukkitVersion() + ")"));
        params.append("&");
        params.append(formatSingleGetParamater("os", System.getProperty("os.name")));
        params.append("&");
        params.append(formatSingleGetParamater("realm", "N/A"));

        return params.toString();
    }
    private String formatSingleGetParamater(String key, String value) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
