/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Basic Agreement.
 */
public abstract class GordianCoreBasicAgreement
        extends GordianCoreAgreement
        implements GordianBasicAgreement {
    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreBasicAgreement(final GordianCoreFactory pFactory,
                                        final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Create the message.
     * @return the message
     */
    protected byte[] createMessage() {
        /* Create the message */
        return newInitVector();
    }

    /**
     * Parse the incoming message.
     * @param pMessage the incoming message
     */
    protected void parseMessage(final byte[] pMessage) {
        /* Obtain initVector */
        final byte[] myInitVector = new byte[INITLEN];
        System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
        storeInitVector(myInitVector);
    }
}

