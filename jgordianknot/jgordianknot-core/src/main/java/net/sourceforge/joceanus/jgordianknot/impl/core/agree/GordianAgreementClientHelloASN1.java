/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of Agreement ClientHello.
 * <pre>
 * GordianAgreementClientHelloASN1 ::= SEQUENCE  {
 *      id OCTET STRING
 *      agreeId AlgorithmIdentifier
 *      result AlgorithmIdentifier
 *      initVector OCTET STRING
 *      data OCTET STRING OPTIONAL
 * }
 * </pre>
 */
public class GordianAgreementClientHelloASN1
        extends GordianASN1Object {
    /**
     * The MessageId.
     */
    static final byte[] MSG_ID = new byte[] { 'C', 'H' };

    /**
     * The AgreementSpec.
     */
    private final AlgorithmIdentifier theAgreement;

    /**
     * The ResultType.
     */
    private final AlgorithmIdentifier theResultType;

    /**
     * The initVector.
     */
    private final byte[] theInitVector;

    /**
     * The Associated Data.
     */
    private final byte[] theData;

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreementId
     * @param pResult the resultId
     * @param pInitVector theInitVector
     * @param pData the associated data
     */
    public GordianAgreementClientHelloASN1(final AlgorithmIdentifier pAgreement,
                                           final AlgorithmIdentifier pResult,
                                           final byte[] pInitVector,
                                           final byte[] pData) {
        /* Store the Details */
        theAgreement = pAgreement;
        theResultType = pResult;
        theInitVector = pInitVector;
        theData = pData;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianAgreementClientHelloASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Check MessageId */
            final byte[] myId = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            if (!Arrays.equals(myId, MSG_ID)) {
                throw new GordianDataException("Incorrect message type");
            }

            /* Access message parts */
            theAgreement = AlgorithmIdentifier.getInstance(en.nextElement());
            theResultType = AlgorithmIdentifier.getInstance(en.nextElement());
            theInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theData = en.hasMoreElements()
                      ? ASN1OctetString.getInstance(en.nextElement()).getOctets()
                      : null;

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws OceanusException on error
     */
    public static GordianAgreementClientHelloASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianAgreementClientHelloASN1) {
            return (GordianAgreementClientHelloASN1) pObject;
        } else if (pObject != null) {
            return new GordianAgreementClientHelloASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public AlgorithmIdentifier getAgreementId() {
        return theAgreement;
    }

    /**
     * Obtain the resultId.
     * @return the resultId
     */
    public AlgorithmIdentifier getResultId() {
        return theResultType;
    }

    /**
     * Obtain the initVector.
     * @return the initVector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public byte[] getData() {
        return theData;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new BEROctetString(MSG_ID));
        v.add(theAgreement);
        v.add(theResultType);
        v.add(new BEROctetString(theInitVector));
        if (theData != null) {
            v.add(new BEROctetString(theData));
        }

        return new DERSequence(v);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the classes are the same */
        if (!(pThat instanceof GordianAgreementClientHelloASN1)) {
            return false;
        }
        final GordianAgreementClientHelloASN1 myThat = (GordianAgreementClientHelloASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theAgreement, myThat.getAgreementId())
                && Objects.equals(getResultId(), myThat.getResultId())
                && Arrays.equals(getData(), myThat.getData())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreementId(), getResultId())
                + Arrays.hashCode(getData())
                + Arrays.hashCode(getInitVector());
    }
}
