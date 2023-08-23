package org.camunda.bpm.developers.delegate;

import org.camunda.bpm.developers.service.TwitterService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTweetDelegate implements JavaDelegate {
    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());
    TwitterService twitter = new TwitterService();

    public void execute(DelegateExecution execution) throws Exception {
        // Input mapping
        String content = (String) execution.getVariable("content");

        if (content.equals("Network error")) {
            throw new RuntimeException("simulated network error");
        }

        // Usually you would use try and catch
        if (content.equals("Trigger error event")) {
            throw new BpmnError("error_tweet_problem","There's a problem with the tweet!");
        }

        // Service call
        String tweetId = twitter.updateStatus(content);

        // Output mapping
        execution.setVariable("tweetId", tweetId);
    }
}