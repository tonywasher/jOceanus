/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.api.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;

/**
 *
 */
public interface GordianCertGatewayWrappedMsg {
    /**
     * Obtain Message Type.
     *
     * @return the message type
     */
    GordianCertGatewayMessageType getMessageType();

    /**
     * Obtain Request Message.
     *
     * @return the request message
     */
    GordianCertGatewayRequest getRequest();

    /**
     * Obtain Response Message.
     *
     * @return the response message
     */
    GordianCertGatewayResponse getResponse();

    /**
     * Obtain Confirm Message.
     *
     * @return the confirm message
     */
    GordianCertGatewayConfirm getConfirm();

    /**
     * Obtain bytes for certGateway message.
     *
     * @return the bytes representation
     * @throws GordianException on error
     */
    byte[] getEncodedBytes() throws GordianException;

    /**
     * Message Type.
     */
    enum GordianCertGatewayMessageType {
        /**
         * Certificate Request.
         */
        CERTREQUEST,

        /**
         * Certificate Response.
         */
        CERTRESPONSE,

        /**
         * Certificate Confirm.
         */
        CERTCONFIRM,
    }
}
