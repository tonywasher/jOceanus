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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import java.util.Enumeration;

/**
 * ASN1 Encoding of CertificateAck.
 * <pre>
 * GordianCertAckASN1 ::= SEQUENCE {
 *      certRespId      INTEGER
 *      digestValue     OCTET STRING
 * }
 * </pre>
 */
public class GordianCertAckASN1
        extends GordianASN1Object {
    /**
     * The responseId.
     */
    private final int theRespId;

    /**
     * The digestValue.
     */
    private final byte[] theDigestValue;

    /**
     * Create the ASN1 sequence.
     * @param pRespId the responseId
     * @param pDigestValue the digestValue
     */
    GordianCertAckASN1(final int pRespId,
                       final byte[] pDigestValue) {
        /* Store the Details */
        theRespId = pRespId;
        theDigestValue = pDigestValue;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertAckASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the responseId from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theRespId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();

            /* Extract the digestValue from the sequence */
            theDigestValue = ASN1OctetString.getInstance(en.nextElement()).getOctets();

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertAckASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertAckASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertAckASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the responseId.
     * @return the id
     */
    public int getRespId() {
        return theRespId;
    }

    /**
     * Obtain the digestValue.
     * @return the digestValue
     */
    public byte[] getDigestValue() {
        return theDigestValue;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(theRespId));
        v.add(new DEROctetString(theDigestValue));
        return new DERSequence(v);
    }
 }
