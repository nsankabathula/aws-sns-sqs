package com.example;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;


import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class SqsClient implements HttpServletProcessor {

    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    final String QUEUE_NAME = "sqs_test_queue";
    final String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

    final SetQueueAttributesRequest setQueueAttributesRequest = new SetQueueAttributesRequest()
            .withQueueUrl(queueUrl)
            .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");

    @PostConstruct
    public void init() {
        sqs.setQueueAttributes(setQueueAttributesRequest);
    }




    /**
     * Process the request and write any output to the response servlet.
     *
     * @param httpRequest  Request servlet object.
     * @param httpResponse Response servlet object.
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {

        final ReceiveMessageRequest receive_request = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withWaitTimeSeconds(20);

        List<Message> messages = sqs.receiveMessage(receive_request).getMessages();

        String messageBody = messages.stream().map(Message::getBody).reduce("", String::concat);

        displayMessage(httpResponse, messageBody);

    }

    private void displayMessage(HttpServletResponse httpServletResponse, String messageBody) {

        try {

            System.out.println(messageBody);
            httpServletResponse.getWriter()
                    //.append(m.getClass().getSimpleName())

                    //.append(DateUtils.formatISO8601Date(m.getTimestamp()))

                    .append(messageBody)
                    ;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
