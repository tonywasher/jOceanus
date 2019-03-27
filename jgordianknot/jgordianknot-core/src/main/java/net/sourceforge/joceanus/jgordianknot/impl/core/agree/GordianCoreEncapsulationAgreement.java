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

import java.io.IOException;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @throws OceanusException on error
     */
    protected byte[] createMessage(final byte[] pBase) throws OceanusException {
        /* Build the sequence */
        try {
            final ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(new DEROctetString(newInitVector()));
            v.add(new DEROctetString(pBase));
            return new DERSequence(v).getEncoded();

        } catch (IOException e) {
            throw new GordianIOException("Unable to build ASN1 sequence", e);
        }
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
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pMessage);
            final Enumeration en = mySequence.getObjects();

            /* Store the initVector */
            storeInitVector(ASN1OctetString.getInstance(en.nextElement()).getOctets());

            /* Return the encoded message */
            return ASN1OctetString.getInstance(en.nextElement()).getOctets();

        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }
}
