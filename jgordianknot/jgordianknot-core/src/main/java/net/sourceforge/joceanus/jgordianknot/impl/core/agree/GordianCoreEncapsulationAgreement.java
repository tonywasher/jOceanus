/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Encapsulation Agreement.
 */
public abstract class GordianCoreEncapsulationAgreement
        extends GordianCoreAgreement
        implements GordianEncapsulationAgreement {
    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreEncapsulationAgreement(final GordianCoreFactory pFactory,
                                                final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Create the message.
     * @param pBase the base message
     * @return the composite message
     */
    protected byte[] createMessage(final byte[] pBase) {
        /* Create buffer for message */
        final int myLen = pBase.length;
        final byte[] myMessage = new byte[myLen + INITLEN];

        /* Create the message */
        System.arraycopy(newInitVector(), 0, myMessage, 0, INITLEN);
        System.arraycopy(pBase, 0, myMessage, INITLEN, myLen);
        return myMessage;
    }

    /**
     * Parse the incoming message.
     * @param pMessage the incoming message
     * @return the base message
     */
    protected byte[] parseMessage(final byte[] pMessage) {
        /* Obtain initVector */
        final byte[] myInitVector = new byte[INITLEN];
        System.arraycopy(pMessage, 0, myInitVector, 0, INITLEN);
        storeInitVector(myInitVector);

        /* Obtain base message */
        final int myBaseLen = pMessage.length - INITLEN;
        final byte[] myBase = new byte[myBaseLen];
        System.arraycopy(pMessage, INITLEN, myBase, 0, myBaseLen);
        return myBase;
    }
}
