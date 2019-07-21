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

import com.amazonaws.util.ImmutableMapParameter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dispatches servlet requests to the appropriate {@link HttpServletProcessor} based on the HTTP method and resource path.
 */
public class DispatcherServlet extends GenericServlet {

    /**
     * Default processor that just returns a 404
     */
    private static final HttpServletProcessor DEFAULT_PROCESSOR = (httpRequest, httpResponse) -> {
        httpResponse.setStatus(404);
        httpResponse.getWriter().append("404 Not Found");
    };

    private final Map<String, Map<String, HttpServletProcessor>> processors = createProcessors();

    /**
     * Bind the processors to the right resource path and HTTP method.
     *
     * @return Map of processors.
     */
    private Map<String, Map<String, HttpServletProcessor>> createProcessors() {
        Map<String, Map<String, HttpServletProcessor>> processors = new HashMap<>();
        processors.put("/", ImmutableMapParameter.of("POST", new SnsServletProcessor(),
                                                     "GET", new WebServletProcessor()
                                                    ));
        processors.put("/sqs", ImmutableMapParameter.of(
                                        "GET", new SqsClient()));
        return processors;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        processHttp((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
    }

    private void processHttp(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        System.out.printf("Processing request for %s %s %n", req.getMethod(), req.getServletPath());
        Map<String, HttpServletProcessor> processorMap = processors.get(req.getServletPath());
        if (processorMap == null) {
            DEFAULT_PROCESSOR.process(req, res);
        } else {
            processorMap.getOrDefault(req.getMethod(), DEFAULT_PROCESSOR).process(req, res);
        }
    }
}
