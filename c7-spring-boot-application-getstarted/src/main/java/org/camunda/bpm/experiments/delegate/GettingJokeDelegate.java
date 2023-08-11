package org.camunda.bpm.experiments.delegate;

import org.json.JSONObject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component("gettingJoke")
public class GettingJokeDelegate implements JavaDelegate {

    private final Logger LOGGER = Logger.getLogger(LoggerDelegate.class.getName());

    public void execute(DelegateExecution execution) throws Exception {

        HttpResponse<String> response = get("https://api.chucknorris.io/jokes/random");


        if (response.statusCode() != 200) {

            // create incidence if Status code is not 200
            throw new Exception("Error from REST call, Response code: " + response.statusCode());

        } else {

            // getStatusText
            String body = response.body();
            JSONObject jsonObject = new JSONObject(body);
            String joke = jsonObject.getString("value");

            execution.setVariable("body", body);
            execution.setVariable("joke", joke);
        }

        LOGGER.info("\n... GettingJokeDelegate invoked by \n"
                + "  body=" + execution.getVariable("body")
                + "\n  joke=" + execution.getVariable("joke")
                + " \n\n");
    }

    public HttpResponse<String> get(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        return response;
    }

}