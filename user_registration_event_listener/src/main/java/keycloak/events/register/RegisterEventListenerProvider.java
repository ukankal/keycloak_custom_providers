package keycloak.events.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Set;

public class RegisterEventListenerProvider implements EventListenerProvider {

    private Set<EventType> excludedEvents;
    private String eventsUrl;
    private Set<OperationType> excludedAdminOperations;
    private String operationsUrl;
    private CloseableHttpClient httpClient;

    public RegisterEventListenerProvider(Set<EventType> excludedEvents, String eventsUrl, Set<OperationType> excludedAdminOperations, String operationsUrl, CloseableHttpClient httpClient) {
        this.excludedEvents = excludedEvents;
        this.eventsUrl = eventsUrl;
        this.excludedAdminOperations = excludedAdminOperations;
        this.operationsUrl = operationsUrl;
        this.httpClient = httpClient;
    }

    public void onEvent(Event event) {
        if (excludedEvents != null && excludedEvents.contains(event.getType())) {
            return;
        } else if(eventsUrl != null && event.getType().equals(EventType.REGISTER)) {
            try {
                // CompletableFuture
                //     .runAsync(() -> sendEvent(event));
                sendEvent(event);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // this.httpClient.getConnectionManager().shutdown();
            }
        }
    }

    public void onEvent(AdminEvent adminEvent, boolean b) {
        if(operationsUrl==null && excludedAdminOperations!=null && excludedAdminOperations.contains(adminEvent.getOperationType())){
            return;
        }else if(operationsUrl!=null){
            try {
                sendAdminEvent(adminEvent);
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                // this.httpClient.getConnectionManager().shutdown();
            }
        }
    }

    private void sendEvent(Event event) throws IOException {
        // HttpPost httpPost = new HttpPost(this.eventsUrl);

        // HttpResponse response = this.httpClient.execute(httpPost);

        HttpPost httpPost = new HttpPost(this.eventsUrl);
        httpPost.setEntity(new StringEntity(this.getEventJson(event)));
        httpPost.setHeader("Content-Type", "application/json");
        CloseableHttpResponse response2 = this.httpClient.execute(httpPost);
        response2.close();
    }

    private String getEventJson(Event event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(event);
    }

    private void sendAdminEvent(AdminEvent adminEvent) throws IOException {
        // HttpPost httpPost = new HttpPost(this.operationsUrl);
        // httpPost.setEntity(new StringEntity(this.getAdminEventJson(adminEvent)));
        // httpPost.setHeader("Content-Type", "application/json");
        // HttpResponse response = this.httpClient.execute(httpPost);

        HttpPost httpPost = new HttpPost(this.operationsUrl);
        httpPost.setEntity(new StringEntity(this.getAdminEventJson(adminEvent)));
        httpPost.setHeader("Content-Type", "application/json");
        CloseableHttpResponse response2 = this.httpClient.execute(httpPost);
        response2.close();

    }

    private String getAdminEventJson(AdminEvent adminEvent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(adminEvent);
    }

    public void close() {

    }
}