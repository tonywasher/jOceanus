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
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStore;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.function.Function;

/**
 * Certificate Gateway.
 */
public interface GordianCertGateway {
    /**
     * Obtain the keyStore.
     *
     * @return the keyStore
     */
    GordianKeyStore getKeyStore();

    /**
     * Obtain the keyStoreManager.
     *
     * @return the keyStoreMgr
     */
    GordianKeyStoreManager getKeyStoreManager();

    /**
     * create certificate request.
     *
     * @param pAlias the alias
     * @return the request message
     * @throws GordianException on error
     */
    GordianCertGatewayRequest createCertificateRequest(String pAlias) throws GordianException;

    /**
     * process certificate request.
     *
     * @param pRequest the request message
     * @return the response message
     * @throws GordianException on error
     */
    GordianCertGatewayResponse processCertificateRequest(GordianCertGatewayRequest pRequest) throws GordianException;

    /**
     * process certificate response.
     *
     * @param pResponse response message
     * @return the confirmation message (or null)
     * @throws GordianException on error
     */
    GordianCertGatewayConfirm processCertificateResponse(GordianCertGatewayResponse pResponse) throws GordianException;

    /**
     * process certificate confirm.
     *
     * @param pConfirm the confirmation message
     * @throws GordianException on error
     */
    void processCertificateConfirm(GordianCertGatewayConfirm pConfirm) throws GordianException;

    /**
     * set the Certifier.
     *
     * @param pAlias the alias
     * @throws GordianException on error
     */
    void setCertifier(String pAlias) throws GordianException;

    /**
     * set the passwordResolver.
     *
     * @param pResolver the resolver
     */
    void setPasswordResolver(Function<String, char[]> pResolver);

    /**
     * Set the MAC secret resolver.
     *
     * @param pResolver the resolver
     */
    void setMACSecretResolver(Function<X500Name, String> pResolver);
}
