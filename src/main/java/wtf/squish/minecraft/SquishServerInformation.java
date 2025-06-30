package wtf.squish.minecraft;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wtf.squish.minecraft.exceptions.HttpFailedException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SquishServerInformation {
    private int id;
    private String name;
    private String regionId;
    private String ipAddress;
    private String serverType;
    private String color;
    private boolean logErrors; // Either a 1 or a 0 - fuck you GSON for not being able to parse one between the other ffs
    private String discordWebhook;
    private SquishWebsocketInformation socket;

    private static final FixGsonBoolean gsonBooleanAdapter = new FixGsonBoolean();
    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Boolean.class, gsonBooleanAdapter)
            .registerTypeAdapter(boolean.class, gsonBooleanAdapter)
            .create();

    protected static SquishServerInformation getFromRemote() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(SquishLogs.getSquishConfig().getDomain() + "/api/v1/server"))
                .setHeader("User-Agent", "SquishLogsMinecraft/2.0.0 (+https://squish.wtf/)")
                .setHeader("X-Game-Server-Token", SquishLogs.getSquishConfig().getToken())
                .build();

        CompletableFuture<HttpResponse<String>> response = SquishLogs.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String responseBody;
        int statusCode;
        try {
            responseBody = response.thenApply(HttpResponse::body).get();
            statusCode = response.thenApply(HttpResponse::statusCode).get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(statusCode != 200) {
            throw new HttpFailedException("Status code was not successful.");
        }

        // Parse the JSON it gave us
        return gson.fromJson(responseBody, SquishServerInformation.class);
    }

    protected int getId() {
        return id;
    }
    protected String getName() {
        return name;
    }
    protected String getRegionId() {
        return regionId;
    }
    protected String getIpAddress() {
        return ipAddress;
    }
    protected String getServerType() {
        return serverType;
    }
    protected String getColor() {
        return color;
    }
    protected boolean getLogErrors() {
        return logErrors;
    }
    protected String getDiscordWebhook() {
        return discordWebhook;
    }
    protected SquishWebsocketInformation getSocket() {
        return socket;
    }

    protected static class SquishWebsocketInformation {
        private String uid;
        private String name;
        private String ipAddress;
        private String continent;

        protected String getUid() {
            return uid;
        }
        protected String getName() {
            return name;
        }
        protected String getIpAddress() {
            return ipAddress;
        }
        protected String getContinent() {
            return continent;
        }
    }
}