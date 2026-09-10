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

package io.github.tonywasher.joceanus.gordianknot.api.cert;

/**
 * KeyPair Purpose.
 */
public enum GordianKeyPairPurpose {
    /**
     * Server Authorization.
     */
    SERVERAUTH,

    /**
     * Client Authorization.
     */
    CLIENTAUTH,

    /**
     * Code Signing.
     */
    CODESIGN,

    /**
     * EMail Protection.
     */
    EMAILPROTECT,

    /**
     * TimeStamping.
     */
    TIMESTAMP,

    /**
     * OCSP Signing.
     */
    OCSPSIGN,

    /**
     * DVCS.
     */
    DVCS,

    /**
     * SBGP Cert AA Server Auth.
     */
    SBGPCERT,

    /**
     * SCVP Responder.
     */
    SCVPRESPONDER,

    /**
     * EAP Over PPP.
     */
    EAPOVERPPP,

    /**
     * EAP Over LAN.
     */
    EAPOVERLAN,

    /**
     * SCVP Server.
     */
    SCVPSERVER,

    /**
     * SCVPClient.
     */
    SCVPCLIENT,

    /**
     * ipsecIKE.
     */
    IPSECIKE,

    /**
     * Secure Shell Client.
     */
    SECURESHELLCLIENT,

    /**
     * Secure Shell Server.
     */
    SECURESHELLSERVER,

    /**
     * capwap AC.
     */
    CAPWAPAC,

    /**
     * capwap WTP.
     */
    CAPWAPWTP,

    /**
     * cmc CA.
     */
    CMCCA,

    /**
     * cmc RA.
     */
    CMCRA,

    /**
     * cmc Archive.
     */
    CMCARCHIVE,

    /**
     * cm KGA.
     */
    CMKGA,

    /**
     * Bundle Security.
     */
    BUNDLESECURITY,

    /**
     * document Signing.
     */
    DOCSIGN,

    /**
     * JWT.
     */
    JWT,

    /**
     * httpContent Encrypt.
     */
    HTTPCONTENT,

    /**
     * oauth Access Token.
     */
    OAUTHACCESSTOKEN,

    /**
     * im Uri.
     */
    IMURI,

    /**
     * config Signing.
     */
    CONFIGSIGN,

    /**
     * trustAnchor Config Signing.
     */
    TRUSTANCHORCONFIGSIGN,

    /**
     * update Package Signing.
     */
    UPDATEPACKAGE,

    /**
     * safety Communications.
     */
    SAFETYCOMMS,
}
