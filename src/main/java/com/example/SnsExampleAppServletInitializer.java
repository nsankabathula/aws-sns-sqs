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

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.GetSubscriptionAttributesRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListTopicsRequest;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import javax.servlet.Servlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * Spring boot application initializer.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication
public class SnsExampleAppServletInitializer extends SpringBootServletInitializer {

    /**
     * ElasticBeanstalk endpoint
     */
    private static final String ENDPOINT = "http://snsmessageprocessor-env.us-west-2.elasticbeanstalk.com";

    /**
     * Name of SNS topic used in this example.
     */
    private static final String TOPIC_NAME = "SnsMessageProcessorExampleTopic";

    @SuppressWarnings("serial")
    @Bean
    public Servlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SnsExampleAppServletInitializer.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SnsExampleAppServletInitializer.class, args);
/*
        // Create and subscribe to topic.
        AmazonSNS sns = AmazonSNSClient.builder().build();
        String topicArn = sns.createTopic(TOPIC_NAME).getTopicArn();
        if (!doesSubscriptionExist(sns, topicArn)) {
            sns.subscribe(new SubscribeRequest()
                              .withEndpoint(ENDPOINT)
                              .withProtocol("http")
                              .withTopicArn(topicArn));
        }
        */
    }

    /**
     * createTopic is idempotent but subscribe is not so make sure we aren't already subscribed first.
     *
     * @param sns SNS client.
     * @param topicArn ARN of SNS topic.
     * @return True if the {@link #ENDPOINT} is already subscribed to the topic, false otherwise.
     */
    private static boolean doesSubscriptionExist(AmazonSNS sns, String topicArn) {
        String nextToken;
        do {
            ListSubscriptionsByTopicResult result = sns.listSubscriptionsByTopic(
                new ListSubscriptionsByTopicRequest().withTopicArn(topicArn));
            nextToken = result.getNextToken();
            if (result.getSubscriptions().stream().anyMatch(s -> s.getEndpoint().equals(ENDPOINT))) {
                return true;
            }
        } while (nextToken != null);
        return false;
    }

}
