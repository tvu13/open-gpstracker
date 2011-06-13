/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.ogt.http.impl.io;

import java.io.IOException;

import org.apache.ogt.http.HttpException;
import org.apache.ogt.http.HttpMessage;
import org.apache.ogt.http.HttpResponse;
import org.apache.ogt.http.HttpResponseFactory;
import org.apache.ogt.http.NoHttpResponseException;
import org.apache.ogt.http.ParseException;
import org.apache.ogt.http.StatusLine;
import org.apache.ogt.http.io.SessionInputBuffer;
import org.apache.ogt.http.message.LineParser;
import org.apache.ogt.http.message.ParserCursor;
import org.apache.ogt.http.params.HttpParams;
import org.apache.ogt.http.util.CharArrayBuffer;

/**
 * HTTP response parser that obtain its input from an instance
 * of {@link SessionInputBuffer}.
 * <p>
 * The following parameters can be used to customize the behavior of this
 * class:
 * <ul>
 *  <li>{@link org.apache.ogt.http.params.CoreConnectionPNames#MAX_HEADER_COUNT}</li>
 *  <li>{@link org.apache.ogt.http.params.CoreConnectionPNames#MAX_LINE_LENGTH}</li>
 * </ul>
 *
 * @since 4.0
 */
public class HttpResponseParser extends AbstractMessageParser {

    private final HttpResponseFactory responseFactory;
    private final CharArrayBuffer lineBuf;

    /**
     * Creates an instance of this class.
     *
     * @param buffer the session input buffer.
     * @param parser the line parser.
     * @param responseFactory the factory to use to create
     *    {@link HttpResponse}s.
     * @param params HTTP parameters.
     */
    public HttpResponseParser(
            final SessionInputBuffer buffer,
            final LineParser parser,
            final HttpResponseFactory responseFactory,
            final HttpParams params) {
        super(buffer, parser, params);
        if (responseFactory == null) {
            throw new IllegalArgumentException("Response factory may not be null");
        }
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
    }

    protected HttpMessage parseHead(
            final SessionInputBuffer sessionBuffer)
        throws IOException, HttpException, ParseException {

        this.lineBuf.clear();
        int i = sessionBuffer.readLine(this.lineBuf);
        if (i == -1) {
            throw new NoHttpResponseException("The target server failed to respond");
        }
        //create the status line from the status string
        ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
        StatusLine statusline = lineParser.parseStatusLine(this.lineBuf, cursor);
        return this.responseFactory.newHttpResponse(statusline, null);
    }

}