/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.http;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Http client interface.
 * @author Tony Washer
 */
public abstract class MetisHTTPDataClient
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
     * Found return code.
     */
    private static final int HTTP_FOUND = 302;

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
     * Constructor.
     * @param pBaseAddress the base address for the client.
     */
    protected MetisHTTPDataClient(final String pBaseAddress) {
        this(pBaseAddress, MetisHTTPAuthType.NONE, null);
    }

    /**
     * Constructor.
     * @param pBaseAddress the base address for the client.
     * @param pAuthType the authorisation type
     * @param pAuth the authorisation string
     */
    protected MetisHTTPDataClient(final String pBaseAddress,
                                  final MetisHTTPAuthType pAuthType,
                                  final String pAuth) {
        theBaseAddress = pBaseAddress;
        theClient = HttpClients.createDefault();

        /* Determine the authorisation string */
        theAuth = pAuthType.getAuthString(pAuth);
    }

    /**
     * Obtain query results from explicit object as JSON object.
     * @param pURL the URL
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject getAbsoluteJSONObject(final String pURL) throws OceanusException {
        final JSONTokener myTokener = performJSONQuery(pURL);
        return new JSONObject(myTokener);
    }

    /**
     * Obtain query results from explicit object as JSON array.
     * @param pURL the URL
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray getAbsoluteJSONArray(final String pURL) throws OceanusException {
        final JSONTokener myTokener = performJSONQuery(pURL);
        return new JSONArray(myTokener);
    }

    /**
     * Post a request.
     * @param pHeader the header string
     * @param pRequest the request
     * @return the resulting object
     * @throws OceanusException on error
     */
    protected JSONObject postRequest(final String pHeader,
                                     final JSONObject pRequest) throws OceanusException {
        /* Build the correct URL */
        final String myURL = buildURL(pHeader, null, null);

        /* Post the request */
        final JSONTokener myTokener = performJSONPost(myURL, pRequest);
        return new JSONObject(myTokener);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the header string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject getJSONObject(final String pHeader) throws OceanusException {
        return queryJSONObjectWithHeaderAndTrailer(pHeader, null, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pQuery the query string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject queryJSONObject(final String pQuery) throws OceanusException {
        return queryJSONObjectWithHeaderAndTrailer(null, pQuery, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject queryJSONObjectWithHeader(final String pHeader,
                                                   final String pQuery) throws OceanusException {
        return queryJSONObjectWithHeaderAndTrailer(pHeader, pQuery, null);
    }

    /**
     * Obtain query results as JSON object.
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject queryJSONObjectWithTrailer(final String pQuery,
                                                    final String pTrailer) throws OceanusException {
        return queryJSONObjectWithHeaderAndTrailer(null, pQuery, pTrailer);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONObject queryJSONObjectWithHeaderAndTrailer(final String pHeader,
                                                             final String pQuery,
                                                             final String pTrailer) throws OceanusException {
        /* Parse result as object */
        final JSONTokener myTokener = performJSONQuery(pHeader, pQuery, pTrailer);
        return new JSONObject(myTokener);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the header string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray getJSONArray(final String pHeader) throws OceanusException {
        return queryJSONArrayWithHeaderAndTrailer(pHeader, null, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pQuery the query string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray queryJSONArray(final String pQuery) throws OceanusException {
        return queryJSONArrayWithHeaderAndTrailer(null, pQuery, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray queryJSONArrayWithHeader(final String pHeader,
                                                 final String pQuery) throws OceanusException {
        return queryJSONArrayWithHeaderAndTrailer(pHeader, pQuery, null);
    }

    /**
     * Obtain query results as JSON array.
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray queryJSONArrayWithTrailer(final String pQuery,
                                                  final String pTrailer) throws OceanusException {
        return queryJSONArrayWithHeaderAndTrailer(null, pQuery, pTrailer);
    }

    /**
     * Obtain query results as JSON array.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws OceanusException on error
     */
    protected JSONArray queryJSONArrayWithHeaderAndTrailer(final String pHeader,
                                                           final String pQuery,
                                                           final String pTrailer) throws OceanusException {
        /* Parse result as array */
        final JSONTokener myTokener = performJSONQuery(pHeader, pQuery, pTrailer);
        return new JSONArray(myTokener);
    }

    /**
     * Obtain query results as JSON object.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the query results
     * @throws OceanusException on error
     */
    private JSONTokener performJSONQuery(final String pHeader,
                                         final String pQuery,
                                         final String pTrailer) throws OceanusException {
        /* Build the correct URL */
        final String myURL = buildURL(pHeader, pQuery, pTrailer);

        /* Perform the query */
        return performJSONQuery(myURL);
    }

    /**
     * Obtain query results as JSON object.
     * @param pURL the location to be queried
     * @return the query results
     * @throws OceanusException on error
     */
    private JSONTokener performJSONQuery(final String pURL) throws OceanusException {
        /* Create the get request */
        final HttpGet myGet = new HttpGet(pURL);
        if (theAuth != null) {
            /* Build header */
            myGet.addHeader(HEADER_AUTH, theAuth);
            myGet.addHeader(HEADER_ACCEPT, HEADER_ACCEPT_DTL);
        }

        /* Protect against exceptions */
        try (CloseableHttpResponse myResponse = theClient.execute(myGet)) {
            /* Access the entity */
            final StatusLine myStatusLine = myResponse.getStatusLine();

            /* If we were successful */
            if (myStatusLine.getStatusCode() == HTTP_OK) {
                /* Access the response as a JSON Object */
                final HttpEntity myEntity = myResponse.getEntity();

                /* Parse into string to prevent timeouts */
                final String myRes = EntityUtils.toString(myEntity);
                return new JSONTokener(myRes);
            }

            /* Notify of failure */
            throw new MetisDataException(myStatusLine, HTTPERROR_QUERY);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new MetisIOException(HTTPERROR_QUERY, e);
        }
    }

    /**
     * Build the requested URL.
     * @param pHeader the leading details
     * @param pQuery the query string
     * @param pTrailer the trailing details
     * @return the requested URL
     * @throws OceanusException on error
     */
    private String buildURL(final String pHeader,
                            final String pQuery,
                            final String pTrailer) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build up the URL */
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theBaseAddress);
            if (pHeader != null) {
                myBuilder.append(pHeader);
            }
            if (pQuery != null) {
                final String myQuery = URLEncoder.encode(pQuery, ENCODING);
                myBuilder.append(myQuery);
            }
            if (pTrailer != null) {
                myBuilder.append(pTrailer);
            }

            /* return the URL */
            return myBuilder.toString();

            /* Catch exceptions */
        } catch (UnsupportedEncodingException e) {
            throw new MetisIOException(URLERROR_BUILD, e);
        }
    }

    /**
     * Obtain query results as JSON object.
     * @param pURL the location to be posted to
     * @param pRequest the details of the post
     * @return the resulting object
     * @throws OceanusException on error
     */
    private JSONTokener performJSONPost(final String pURL,
                                        final JSONObject pRequest) throws OceanusException {
        /* Create the post request */
        final HttpPost myPost = new HttpPost(pURL);

        /* Build header */
        myPost.addHeader(HEADER_AUTH, theAuth);

        /* Convert JSONObject to Name/Value Pairs */
        final List<NameValuePair> myRequest = new ArrayList<>();
        for (String myKey : pRequest.keySet()) {
            myRequest.add(new BasicNameValuePair(myKey, pRequest.getString(myKey)));
        }

        /* Build the content */
        final EntityBuilder myBuilder = EntityBuilder.create();
        myBuilder.setParameters(myRequest);
        myPost.setEntity(myBuilder.build());

        /* Protect against exceptions */
        try (CloseableHttpResponse myResponse = theClient.execute(myPost)) {
            /* Access the entity */
            final StatusLine myStatusLine = myResponse.getStatusLine();

            /* If we were successful */
            if (myStatusLine.getStatusCode() == HTTP_FOUND) {
                /* Access the response as a JSON Object */
                final HttpEntity myEntity = myResponse.getEntity();

                /* Parse into string to prevent timeouts */
                final String myRes = EntityUtils.toString(myEntity);

                /* Extract URL of new object and query it */
                final int myBase = myRes.indexOf(theBaseAddress);
                final int myEnd = myRes.indexOf("/;");
                return performJSONQuery(myRes.substring(myBase, myEnd));
            }

            /* Notify of failure */
            throw new MetisDataException(myStatusLine, HTTPERROR_QUERY);

            /* Catch exceptions */
        } catch (IOException e) {
            throw new MetisIOException(HTTPERROR_QUERY, e);
        }
    }

    @Override
    public void close() throws IOException {
        theClient.close();
    }

    /**
     * AuthorizationType.
     */
    public enum MetisHTTPAuthType {
        /**
         * None.
         */
        NONE,

        /**
         * Basic.
         */
        BASIC,

        /**
         * Bearer.
         */
        BEARER;

        /**
         * Build authString.
         * @param pSecret the secret
         * @return the authString
         */
        String getAuthString(final String pSecret) {
            switch (this) {
                case BASIC:
                    final byte[] myBytes = TethysDataConverter.stringToByteArray(pSecret);
                    return "Basic " + TethysDataConverter.byteArrayToBase64(myBytes);
                case BEARER:
                    return "Bearer " + pSecret;
                case NONE:
                default:
                    return null;
            }
        }
    }
}
