package wtf.squish.minecraft.loggers;

import wtf.squish.minecraft.SquishLogs;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.CRC32;

/**
 * Handles error reporting to SquishLogs
 * @author Livaco
 */
public class ErrorLogger extends Handler {
    @Override
    public void publish(LogRecord record) {
        if(record.getThrown() != null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("token", SquishLogs.getConfigInstance().getString("token"));
            String error = record.getThrown().toString();
            data.put("error", error);
            StringBuilder stack = new StringBuilder();
            int stackCount = 1;
            for(StackTraceElement el : record.getThrown().getStackTrace()) {
                stack.append(stackCount);
                stack.append(": ");
                stack.append(el.toString());
                stack.append("\n");
                stackCount++;
            }
            if(record.getThrown().getCause() != null) {
                stack.append("\nCaused by: ");
                stack.append(record.getThrown().getCause().getMessage());
                stack.append("\n");
                stackCount = 1;
                for(StackTraceElement el : record.getThrown().getCause().getStackTrace()) {
                    stack.append(stackCount);
                    stack.append(": ");
                    stack.append(el.toString());
                    stack.append("\n");
                    stackCount++;
                }
            }
            data.put("stack", stack.toString());
            CRC32 crc = new CRC32();
            crc.update((error + stack.toString()).getBytes());
            data.put("hash", String.valueOf(crc.getValue()));
            data.put("gmv", SquishLogs.getInstance().getServer().getVersion());
            data.put("os", System.getProperty("os.name"));
            data.put("realm", "N/A");

            StringBuilder url = new StringBuilder(SquishLogs.getConfigInstance().getString("domain") + "/api/v1/error?");
            for(Map.Entry<String, String> map : data.entrySet()) {
                url.append(URLEncoder.encode(map.getKey(), StandardCharsets.UTF_8));
                url.append("=");
                url.append(URLEncoder.encode(map.getValue(), StandardCharsets.UTF_8));
                url.append("&");
            }
            String uri = url.substring(0, url.toString().length() - 1);

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(uri))
                    .setHeader("User-Agent", "SquishLogsMinecraft/1.0.0 (+https://squish.wtf/)")
                    .setHeader("X-Game-Server-Token", SquishLogs.getConfigInstance().getString("token"))
                    .build();


            CompletableFuture<HttpResponse<String>> response = SquishLogs.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());

//            String responseBody;
            int statusCode = 0;
            try {
//                responseBody = response.thenApply(HttpResponse::body).get();
                statusCode = response.thenApply(HttpResponse::statusCode).get();
//                SquishLogs.print("Response body: " + responseBody);
//                SquishLogs.print("Response code " + statusCode);
            } catch(InterruptedException | ExecutionException e) {
                SquishLogs.print("Failed to get valid response from error reporting URI.");
            }

            if(statusCode != 200) {
                SquishLogs.print("Failed to get valid response from URI " + request.uri().toString());
                SquishLogs.print("Response code " + statusCode);
            }
        }
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}
