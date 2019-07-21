/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.example;

import com.amazonaws.services.sns.message.DefaultSnsMessageHandler;
import com.amazonaws.services.sns.message.SnsMessage;
import com.amazonaws.services.sns.message.SnsMessageManager;
import com.amazonaws.services.sns.message.SnsNotification;
import com.amazonaws.services.sns.message.SnsSubscriptionConfirmation;
import com.amazonaws.services.sns.message.SnsUnsubscribeConfirmation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * Processes message received from SNS.
 */
public class SnsServletProcessor implements HttpServletProcessor {

    private final SnsMessageManager messageParser = new SnsMessageManager();

    /**
     * Keeps track of the last 10 messages received to display in {@link WebServletProcessor}.
     */
    private static final CircularFifoBuffer receivedMessages = new CircularFifoBuffer(10);

    @Override
    public void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        messageParser.handleMessage(httpRequest.getInputStream(), new DefaultSnsMessageHandler() {
            @Override
            public void handle(SnsNotification snsNotification) {
                addMessage(snsNotification);
                // If the subject is "unsubscribe" then unsubscribe from this topic
                if (snsNotification.getSubject().equalsIgnoreCase("unsubscribe")) {
                    snsNotification.unsubscribeFromTopic();
                } else {
                    // Otherwise process the message
                    System.out.printf("Received message %n"
                                      + "Subject=%s %n"
                                      + "Message = %s %n",
                                      snsNotification.getSubject(), snsNotification.getMessage());
                }
            }

            @Override
            public void handle(SnsUnsubscribeConfirmation message) {
                addMessage(message);
                System.out.println("Received unsubscribe confirmation.");
            }

            @Override
            public void handle(SnsSubscriptionConfirmation message) {
                super.handle(message);
                System.out.println("Received subscription confirmation.");
            }
        });
    }

    /**
     * Add a message to the circular buffer.
     *
     * @param message Message to add.
     */
    private static void addMessage(SnsMessage message) {
        synchronized (receivedMessages) {
            receivedMessages.add(message);
        }
    }

    /**
     * @return All recently received messages in the buffer.
     */
    public static List<SnsMessage> getRecentMessages() {
        synchronized (receivedMessages) {
            List<SnsMessage> messages = new ArrayList<>(receivedMessages.size());
            for (Object o : receivedMessages) {
                messages.add((SnsMessage) o);
            }
            return messages;
        }
    }
}
