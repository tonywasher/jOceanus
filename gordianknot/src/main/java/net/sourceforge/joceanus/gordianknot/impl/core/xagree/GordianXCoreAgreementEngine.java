/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;

/**
 * Implementation engine for Agreements.
 */
public abstract class GordianXCoreAgreementEngine {
    /**
     * The supplier.
     */
    private final GordianXCoreAgreementSupplier theSupplier;

    /**
     * The builder.
     */
    private final GordianXCoreAgreementBuilder theBuilder;

    /**
     * Constructor.
     * @param pSupplier the supplier
     * @param pSpec the agreementSpec
     * @throws GordianException on error
     */
    GordianXCoreAgreementEngine(final GordianXCoreAgreementSupplier pSupplier,
                                final GordianAgreementSpec pSpec) throws GordianException {
        theSupplier = pSupplier;
        theBuilder = new GordianXCoreAgreementBuilder(pSupplier, pSpec);
    }

    /**
     * Obtain the supplier.
     * @return the supplier
     */
    GordianXCoreAgreementSupplier getSupplier() {
        return theSupplier;
    }

    /**
     * Obtain the builder.
     * @return the builder
     */
    protected GordianXCoreAgreementBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Build the clientHello.
     * @throws GordianException on error
     */
    public abstract void buildClientHello() throws GordianException;

    /**
     * Process the clientHello.
     * @throws GordianException on error
     */
    public abstract void processClientHello() throws GordianException;

    /**
     * Process the serverHello.
     * @throws GordianException on error
     */
    public void processServerHello() throws GordianException {
        /* NoOp */
    }

    /**
     * Process the clientConfirm.
     * @throws GordianException on error
     */
    public void processClientConfirm() throws GordianException {
        /* NoOp */
    }
}
