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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for processing an HTTP request to a servlet.
 */
public interface HttpServletProcessor {

    /**
     * Process the request and write any output to the response servlet.
     *
     * @param httpRequest Request servlet object.
     * @param httpResponse Response servlet object.
     * @throws ServletException
     * @throws IOException
     */
    void process(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
        throws ServletException, IOException;
}
