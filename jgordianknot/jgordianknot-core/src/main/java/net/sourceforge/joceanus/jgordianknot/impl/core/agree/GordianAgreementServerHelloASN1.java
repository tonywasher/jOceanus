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
 * ASN1 Encoding of Agreement ServerHello.
 * <pre>
 * GordianAgreementServerHelloASN1 ::= SEQUENCE  {
 *      id OCTET STRING
 *      agreeId AlgorithmIdentifier
 *      initVector OCTET STRING
 *      data OCTET STRING OPTIONAL
 * }
 * </pre>
 */
public class GordianAgreementServerHelloASN1
        extends GordianASN1Object {
    /**
     * The MessageId.
     */
    static final byte[] MSG_ID = new byte[] { 'S', 'H' };

    /**
     * The AgreementSpec.
     */
    private final AlgorithmIdentifier theAgreement;

    /**
     * The InitVector.
     */
    private final byte[] theInitVector;

    /**
     * The Associated Data.
     */
    private final byte[] theData;

    /**
     * The Confirmation.
     */
    private final byte[] theConfirmation;

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreementId
     * @param pInitVector theInitVector
     * @param pData the associated data
     * @param pConfirmation the confirmation
     */
    public GordianAgreementServerHelloASN1(final AlgorithmIdentifier pAgreement,
                                           final byte[] pInitVector,
                                           final byte[] pData,
                                           final byte[] pConfirmation) {
        /* Store the Details */
        theAgreement = pAgreement;
        theInitVector = pInitVector;
        theData = pData;
        theConfirmation = pConfirmation;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianAgreementServerHelloASN1(final ASN1Sequence pSequence) throws OceanusException {
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
            theInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theData = en.hasMoreElements()
                      ? ASN1OctetString.getInstance(en.nextElement()).getOctets()
                      : null;
            theConfirmation = en.hasMoreElements()
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
    public static GordianAgreementServerHelloASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianAgreementServerHelloASN1) {
            return (GordianAgreementServerHelloASN1) pObject;
        } else if (pObject != null) {
            return new GordianAgreementServerHelloASN1(ASN1Sequence.getInstance(pObject));
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
     * Obtain the initVector.
     * @return the initVector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain the confirmation.
     * @return the data
     */
    public byte[] getConfirmation() {
        return theConfirmation;
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
        v.add(new BEROctetString(theInitVector));
        if (theData != null) {
            v.add(new BEROctetString(theData));
        }
        if (theConfirmation != null) {
            v.add(new BEROctetString(theConfirmation));
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
        if (!(pThat instanceof GordianAgreementServerHelloASN1)) {
            return false;
        }
        final GordianAgreementServerHelloASN1 myThat = (GordianAgreementServerHelloASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theAgreement, myThat.getAgreementId())
                && Arrays.equals(getData(), myThat.getData())
                && Arrays.equals(getConfirmation(), myThat.getConfirmation())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreementId())
                + Arrays.hashCode(getData())
                + Arrays.hashCode(getConfirmation())
                + Arrays.hashCode(getInitVector());
    }
}
