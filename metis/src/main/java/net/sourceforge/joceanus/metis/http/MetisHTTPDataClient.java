/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import net.sourceforge.joceanus.metis.exc.MetisDataException;
import net.sourceforge.joceanus.metis.exc.MetisIOException;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;

/**
 * Http client interface.
 * @author Tony Washer
 */
public abstract class MetisHTTPDataClient {
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
    private final HttpClient theClient;

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
        theClient = HttpClient.newHttpClient();

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
        final HttpRequest.Builder myBuilder = HttpRequest.newBuilder()
                .uri(URI.create(pURL));
        if (theAuth != null) {
            /* Build header */
            myBuilder.setHeader(HEADER_AUTH, theAuth);
            myBuilder.setHeader(HEADER_ACCEPT, HEADER_ACCEPT_DTL);
        }
        final HttpRequest myGet = myBuilder.build();

        /* Protect against exceptions */
        try {
            final HttpResponse<String> myResponse = theClient.send(myGet, BodyHandlers.ofString());

            /* If we were successful */
            if (myResponse.statusCode() == HTTP_OK) {
                /* Parse into string to prevent timeouts */
                final String myRes = myResponse.body();
                return new JSONTokener(myRes);
            }

            /* Notify of failure */
            throw new MetisDataException(myResponse.toString(), HTTPERROR_QUERY);

            /* Catch exceptions */
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return performJSONQuery(pURL);
        } catch (IOException  e) {
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
        final HttpRequest.Builder myBuilder = HttpRequest.newBuilder()
                .uri(URI.create(pURL));
        if (theAuth != null) {
            /* Build header */
            myBuilder.setHeader(HEADER_AUTH, theAuth);
        }
        myBuilder.POST(BodyPublishers.ofString(pRequest.toString()));
        final HttpRequest myPost = myBuilder.build();

        /* Protect against exceptions */
        try {
            final HttpResponse<String> myResponse = theClient.send(myPost, BodyHandlers.ofString());

            /* If we were successful */
            if (myResponse.statusCode() == HTTP_FOUND) {
                /* Parse into string to prevent timeouts */
                final String myRes = myResponse.body();

                /* Extract URL of new object and query it */
                final int myBase = myRes.indexOf(theBaseAddress);
                final int myEnd = myRes.indexOf("/;");
                return performJSONQuery(myRes.substring(myBase, myEnd));
            }

            /* Notify of failure */
            throw new MetisDataException(myResponse.toString(), HTTPERROR_QUERY);

            /* Catch exceptions */
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return performJSONPost(pURL, pRequest);
        } catch (IOException  e) {
            throw new MetisIOException(HTTPERROR_QUERY, e);
        }
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
                    final byte[] myBytes = OceanusDataConverter.stringToByteArray(pSecret);
                    return "Basic " + OceanusDataConverter.byteArrayToBase64(myBytes);
                case BEARER:
                    return "Bearer " + pSecret;
                case NONE:
                default:
                    return null;
            }
        }
    }
}
