package org.camunda.bpm.micro;

import org.camunda.bpm.client.ExternalTaskClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class StartPaymentEx10 {
    private final static Logger LOGGER = Logger.getLogger(DeductExistingCreditEx7.class.getName());

    public static void main(String[] args) {
        System.out.println("Hello start payment worker !");

        // bootstrap the client
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(20000) // long polling timeout
                .lockDuration(10000)
                .maxTasks(1)
                .build();

        // subscribe to an external task topic as specified in the process
        client.subscribe("start-payment")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    String businessKey = externalTask.getBusinessKey();

                    try {
                        LOGGER.info("Handling process instance id : " + externalTask.getProcessInstanceId()
                                + " with businessKey : " + externalTask.getBusinessKey());
                        // URL des REST-Endpunkts
                        URL url = new URL("http://localhost:8080/engine-rest/message");

                        // Verbindung herstellen
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        // POST-Methode festlegen
                        connection.setRequestMethod("POST");

                        // Request-Header festlegen
                        connection.setRequestProperty("Content-Type", "application/json");

                        // Request-Body erstellen
                        String requestBody = "{\"messageName\":\"messagePaymentRequest\", \"businessKey\":" + businessKey
                                + ", \"processVariables\": {\"amount\": { \"value\": " + 123 + ", \"type\": \"Integer\"} } }";

                        // Request-Body senden
                        connection.setDoOutput(true);
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(requestBody);
                        outputStream.flush();
                        outputStream.close();

                        // Response-Code überprüfen
                        int responseCode = connection.getResponseCode();
                        System.out.println("Response Code: " + responseCode);

                        // Response lesen
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Response ausgeben
                        System.out.println("Response: " + response.toString());

                        // Verbindung schließen
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}