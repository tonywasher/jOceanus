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
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * <pre>
     * GordianBasicRequest ::= SEQUENCE  {
     *      id AlgorithmIdentifier
     *      initVector OCTET STRING
     * }
     * </pre>
     * @return the message
     * @throws OceanusException on error
     */
    protected byte[] createMessage() throws OceanusException {
        /* Build the sequence */
        try {
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(myFactory.getIdentifierForSpec(getAgreementSpec()));
            v.add(new DEROctetString(newInitVector()));
            return new DERSequence(v).getEncoded();

        } catch (IOException e) {
            throw new GordianIOException("Unable to build ASN1 sequence", e);
        }
    }

    /**
     * Parse the incoming message.
     * @param pMessage the incoming message
     * @throws OceanusException on error
     */
    protected void parseMessage(final byte[] pMessage) throws OceanusException {
        /* Parse the sequence */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pMessage);
            final Enumeration en = mySequence.getObjects();

            /* Access message parts */
            final AlgorithmIdentifier myAlgId = AlgorithmIdentifier.getInstance(en.nextElement());
            final byte[] myInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();

            /* Check agreementSpec */
            final GordianCoreAgreementFactory myFactory = getAgreementFactory();
            final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myAlgId);
            if (!Objects.equals(mySpec, getAgreementSpec())) {
                throw new GordianDataException(ERROR_INVSPEC);
            }

            /* Store initVector */
            storeInitVector(myInitVector);

        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }
}

