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

import java.util.Objects;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Encapsulation Agreement.
 */
public abstract class GordianCoreAnonymousAgreement
        extends GordianCoreAgreement
        implements GordianAnonymousAgreement {
    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSpec the agreementSpec
     */
    protected GordianCoreAnonymousAgreement(final GordianCoreFactory pFactory,
                                            final GordianAgreementSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Create the message.
     * @param pBase the base message
     * @return the composite message
     * @throws OceanusException on error
     */
    protected byte[] createMessage(final byte[] pBase) throws OceanusException {
        /* Create the request */
        final GordianCoreAgreementFactory myFactory = getAgreementFactory();
        final AlgorithmIdentifier myAlgId = myFactory.getIdentifierForSpec(getAgreementSpec());
        final AlgorithmIdentifier myResId = getIdentifierForResult();
        final GordianAgreementRequestASN1 myRequest = new GordianAgreementRequestASN1(myAlgId, myResId, newInitVector(), pBase);
        return myRequest.getEncodedBytes();
    }

    /**
     * Parse the incoming message.
     * @param pMessage the incoming message
     * @return the base message
     * @throws OceanusException on error
     */
    protected byte[] parseMessage(final byte[] pMessage) throws OceanusException {
        /* Parse the sequence */
        try {
            /* Access the sequence */
            final GordianAgreementRequestASN1 myRequest = GordianAgreementRequestASN1.getInstance(pMessage);

            /* Access message parts */
            final AlgorithmIdentifier myAlgId = myRequest.getAgreementId();
            final AlgorithmIdentifier myResId = myRequest.getResultId();
            final byte[] myInitVector = myRequest.getInitVector();
            final byte[] myMessage = myRequest.getData();

            /* Check agreementSpec */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
            if (!Objects.equals(mySpec, getAgreementSpec())) {
                throw new GordianDataException(ERROR_INVSPEC);
            }

            /* Process result identifier */
            processResultIdentifier(myResId);

            /* Store initVector */
            storeInitVector(myInitVector);

            /* Return the encoded message */
            return myMessage;

        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }
}
