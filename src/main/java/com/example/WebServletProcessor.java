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

import com.amazonaws.services.sns.message.SnsMessage;
import com.amazonaws.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays up to 10 of the most recently received messages. Bound to 'GET /'.
 */
public class WebServletProcessor implements HttpServletProcessor {

    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        List<SnsMessage> recentMessages = SnsServletProcessor.getRecentMessages();
        if (recentMessages.isEmpty()) {
            httpResponse.getWriter().append("No SNS messages received since application startup.");
        } else {
            recentMessages.forEach(m -> displayMessage(httpResponse, m));
        }
    }

    private void displayMessage(HttpServletResponse httpServletResponse, SnsMessage m) {
        try {
            httpServletResponse.getWriter()
                               .append(m.getClass().getSimpleName())
                               .append(" received on ")
                               .append(DateUtils.formatISO8601Date(m.getTimestamp()))
                               .append(":\n")
                               .append(mapper.writeValueAsString(m))
                               .append("\n************************************************************************************************************************\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
