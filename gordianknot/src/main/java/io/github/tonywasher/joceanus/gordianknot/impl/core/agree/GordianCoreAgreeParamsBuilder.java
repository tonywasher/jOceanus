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

package io.github.tonywasher.joceanus.gordianknot.impl.core.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianNewAgreeParams;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianNewAgreeParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;

public class GordianCoreAgreeParamsBuilder
        implements GordianNewAgreeParamsBuilder {
    /**
     * The current parameters.
     */
    private final GordianCoreAgreementSupplier theSupplier;

    /**
     * The current parameters.
     */
    private GordianCoreAgreeParams theParams;

    /**
     * Constructor.
     *
     * @param pSupplier the supplier
     */
    private GordianCoreAgreeParamsBuilder(final GordianCoreAgreementSupplier pSupplier) {
        theSupplier = pSupplier;
    }

    /**
     * Create new AgreeParamsBuilder.
     *
     * @param pSupplier the supplier
     * @return the Builder
     */
    public static GordianNewAgreeParamsBuilder newInstance(final GordianCoreAgreementSupplier pSupplier) {
        return new GordianCoreAgreeParamsBuilder(pSupplier);
    }

    @Override
    public GordianNewAgreeParamsBuilder withSpecAndResultType(final GordianAgreementSpec pSpec,
                                                              final Object pResultType) throws GordianException {
        theParams = new GordianCoreAgreeParams(theSupplier, pSpec, pResultType);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder withBaseParams(final GordianNewAgreeParams pBase) {
        theParams = new GordianCoreAgreeParams((GordianCoreAgreeParams) pBase);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder setClientCertificate(final GordianCertificate pClient) throws GordianException {
        checkParameters();
        theParams.setClientCertificate(pClient);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder setServerCertificate(final GordianCertificate pServer) throws GordianException {
        checkParameters();
        theParams.setServerCertificate(pServer);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder setSigner(final GordianCertificate pSigner) throws GordianException {
        checkParameters();
        theParams.setSigner(pSigner);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder setSigner(final GordianCertificate pSigner, GordianSignatureSpec pSignSpec) throws GordianException {
        checkParameters();
        theParams.setSigner(pSigner, pSignSpec);
        return this;
    }

    @Override
    public GordianNewAgreeParamsBuilder setAdditionalData(final byte[] pData) throws GordianException {
        checkParameters();
        theParams.setAdditionalData(pData);
        return this;
    }

    @Override
    public GordianNewAgreeParams build() throws GordianException {
        checkParameters();
        GordianNewAgreeParams myParams = theParams;
        theParams = null;
        return myParams;
    }

    /**
     * Check parameters exist.
     *
     * @throws GordianException on error
     */
    private void checkParameters() throws GordianException {
        if (theParams == null) {
            throw new GordianLogicException("No base parameters set");
        }
    }
}
