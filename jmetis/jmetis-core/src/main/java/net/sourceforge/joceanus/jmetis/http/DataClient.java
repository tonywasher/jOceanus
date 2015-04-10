/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/field/JFieldModel.java $
 * $Revision: 587 $
 * $Author: Tony $
 * $Date: 2015-03-31 14:44:28 +0100 (Tue, 31 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.JMetisIOException;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Http client interface.
 * @author Tony Washer
 */
public abstract class DataClient
        implements Closeable {
    /**
     * Byte encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * HTTP parse error.
     */
    private static final String HTTPERROR_QUERY = "Failed to perform query";

    /**
     * URL build error.
     */
    private static final String URLERROR_BUILD = "Failed to build URL";

    /**
     * Authorisation header.
     */
    private static final String HEADER_AUTH = "Authorization";

    /**
     * Basic Authorisation header.
     */
    private static final String HEADER_AUTH_BASIC = "Basic ";

    /**
     * Accept header.
     */
    private static final String HEADER_ACCEPT = "Accept";

    /**
     * Accept header detail.
     */
    private static final String HEADER_ACCEPT_DTL = "application/json";

    /**
     * OK return code.
     */
    private static final int HTTP_OK = 200;

    /**
     * The Base address for the client.
     */
    private final String theBaseAddress;

    /**
     * The HTTPClient.
     */
    private final CloseableHttpClient theClient;

    /**
     * The Authorisation string.
     */
    private final String theAuth;

    /**
     * Prepare for large responses.
     */
    private boolean doLargeResponses = false;

    /**
     * Constructor.
     * @param pBaseAddress the base address for the client.
     * @throws JOceanusException on error
     */
    protected DataClient(final String pBaseAddress) throws JOceanusException {
        this(pBaseAddress, null);
    }

    /**
     * Constructor.
     * @param pBaseAddress the base address for the client.
     * @param pAuth the auth string
     * @throws JOceanusException on error
     */
    protected DataClient(final String pBaseAddress,
                         final String pAuth) throws JOceanusException {
        theBaseAddress = pBaseAddress;
        theClient = HttpClients.createDefault();

        /* If we have an authorisation string */
        if (pAuth != null) {
            byte[] myBytes = DataConverter.stringToByteArray(pAuth);
            theAuth = HEADER_AUTH_BASIC + DataConverter.byteArrayToBase64(myBytes);
        } else {
            theAuth = null;
        }
    }

    /**
     * Set whether we expect large responses.
     * @param pLargeResponses true/false
     * @return the previous setting
     */
    protected boolean expectLargeResponses(final boolean pLargeResponses) {
        boolean myCurr = doLargeResponses;
        doLargeResponses = pLargeResponses;
        return myCurr;
    }

    /**
     * Obtain query results from explicit object as JSON object.
     * @param pURL the URL
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject getAbsoluteJSONObject(final String pURL) throws JOceanusException {
        JSONTokener myTokener = performJSONQuery(pURL);
        return new JSONObject(myTokener);
    }

    /**
     * Obtain query results from explicit object as JSON array.
     * @param pURL the URL
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray getAbsoluteJSONArray(final String pURL) throws JOceanusException {
        JSONTokener myTokener = performJSONQuery(pURL);
        return new JSONArray(myTokener);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the header string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject getJSONObject(final String pHeader) throws JOceanusException {
        return queryJSONObjectWithHeaderAndTrailer(pHeader, null, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pQuery the query string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject queryJSONObject(final String pQuery) throws JOceanusException {
        return queryJSONObjectWithHeaderAndTrailer(null, pQuery, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject queryJSONObjectWithHeader(final String pHeader,
                                                   final String pQuery) throws JOceanusException {
        return queryJSONObjectWithHeaderAndTrailer(pHeader, pQuery, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject queryJSONObjectWithTrailer(final String pQuery,
                                                    final String pTrailer) throws JOceanusException {
        return queryJSONObjectWithHeaderAndTrailer(null, pQuery, pTrailer);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONObject queryJSONObjectWithHeaderAndTrailer(final String pHeader,
                                                             final String pQuery,
                                                             final String pTrailer) throws JOceanusException {
        /* Parse result as object */
        JSONTokener myTokener = performJSONQuery(pHeader, pQuery, pTrailer);
        return new JSONObject(myTokener);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the header string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray getJSONArray(final String pHeader) throws JOceanusException {
        return queryJSONArrayWithHeaderAndTrailer(pHeader, null, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pQuery the query string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray queryJSONArray(final String pQuery) throws JOceanusException {
        return queryJSONArrayWithHeaderAndTrailer(null, pQuery, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray queryJSONArrayWithHeader(final String pHeader,
                                                 final String pQuery) throws JOceanusException {
        return queryJSONArrayWithHeaderAndTrailer(pHeader, pQuery, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray queryJSONArrayWithTrailer(final String pQuery,
                                                  final String pTrailer) throws JOceanusException {
        return queryJSONArrayWithHeaderAndTrailer(null, pQuery, pTrailer);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws JOceanusException on error
     */
    protected JSONArray queryJSONArrayWithHeaderAndTrailer(final String pHeader,
                                                           final String pQuery,
                                                           final String pTrailer) throws JOceanusException {
        /* Parse result as array */
        JSONTokener myTokener = performJSONQuery(pHeader, pQuery, pTrailer);
        return new JSONArray(myTokener);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws JOceanusException on error
     */
    private JSONTokener performJSONQuery(final String pHeader,
                                         final String pQuery,
                                         final String pTrailer) throws JOceanusException {
        /* Build the correct URL */
        String myURL = buildURL(pHeader, pQuery, pTrailer);

        /* Perform the query */
        return performJSONQuery(myURL);
    }

    /**
     * Obtain query results as JSON object.
     * @param pURL the location to be queried
     * @return the query results
     * @throws JOceanusException on error
     */
    private JSONTokener performJSONQuery(final String pURL) throws JOceanusException {
        /* Create the get request */
        HttpGet myGet = new HttpGet(pURL);
        if (theAuth != null) {
            /* Build header */
            myGet.addHeader(HEADER_AUTH, theAuth);
            myGet.addHeader(HEADER_ACCEPT, HEADER_ACCEPT_DTL);
        }

        /* Protect against exceptions */
        try (CloseableHttpResponse myResponse = theClient.execute(myGet)) {
            /* Access the entity */
            StatusLine myStatusLine = myResponse.getStatusLine();

            /* If we were successful */
            if (myStatusLine.getStatusCode() == HTTP_OK) {
                /* Access the response as a JSON Object */
                HttpEntity myEntity = myResponse.getEntity();

                /* If we are expecting large responses */
                if (doLargeResponses) {
                    /* Parse into string to prevent timeouts */
                    String myRes = EntityUtils.toString(myEntity);
                    return new JSONTokener(myRes);

                    /* else parse directly from stream */
                } else {
                    InputStream myStream = myEntity.getContent();
                    return new JSONTokener(myStream);
                }
            }

            /* Notify of failure */
            throw new JMetisDataException(myStatusLine, HTTPERROR_QUERY);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new JMetisIOException(HTTPERROR_QUERY, e);
        }
    }

    /**
     * Build the requested URL.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the requested URL
     * @throws JOceanusException on error
     */
    private String buildURL(final String pHeader,
                            final String pQuery,
                            final String pTrailer) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Build up the URL */
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theBaseAddress);
            if (pHeader != null) {
                myBuilder.append(pHeader);
            }
            if (pQuery != null) {
                String myQuery = URLEncoder.encode(pQuery, ENCODING);
                myBuilder.append(myQuery);
            }
            if (pTrailer != null) {
                myBuilder.append(pTrailer);
            }

            /* return the URL */
            return myBuilder.toString();

            /* Catch exceptions */
        } catch (UnsupportedEncodingException e) {
            throw new JMetisIOException(URLERROR_BUILD, e);
        }
    }

    @Override
    public void close() throws IOException {
        theClient.close();
    }
}
