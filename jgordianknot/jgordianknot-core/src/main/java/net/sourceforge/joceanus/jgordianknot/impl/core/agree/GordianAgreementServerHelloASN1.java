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
 * ASN1 Encoding of Agreement ServerHello.
 * <pre>
 * GordianAgreementServerHelloASN1 ::= SEQUENCE  {
 *      id OCTET STRING
 *      agreeId AlgorithmIdentifier
 *      initVector OCTET STRING
 *      data OCTET STRING OPTIONAL
 *      extra CHOICE {
 *          confirmation OCTET STRING
 *          signature signatureASN1
 *      } OPTIONAL
 * }
 *
 * signatureASN1 ::- SEQUENCE {
 *      signId AlgorithmIdentifier
 *      signature OCTET STRING signature
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
     * The AgreementId.
     */
    private final AlgorithmIdentifier theAgreementId;

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
     * The signatureId.
     */
    private final AlgorithmIdentifier theSignId;

    /**
     * The Signature.
     */
    private final byte[] theSignature;

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
        theAgreementId = pAgreement;
        theInitVector = pInitVector;
        theData = pData;
        theConfirmation = pConfirmation;
        theSignId = null;
        theSignature = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreementId
     * @param pInitVector theInitVector
     * @param pData the associated data
     * @param pSignId the signatureId
     * @param pSignature the signature
     */
    public GordianAgreementServerHelloASN1(final AlgorithmIdentifier pAgreement,
                                           final byte[] pInitVector,
                                           final byte[] pData,
                                           final AlgorithmIdentifier pSignId,
                                           final byte[] pSignature) {
        /* Store the Details */
        theAgreementId = pAgreement;
        theInitVector = pInitVector;
        theData = pData;
        theConfirmation = null;
        theSignId = pSignId;
        theSignature = pSignature;
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

            /* Access standard message parts */
            theAgreementId = AlgorithmIdentifier.getInstance(en.nextElement());
            theInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theData = en.hasMoreElements()
                      ? ASN1OctetString.getInstance(en.nextElement()).getOctets()
                      : null;

            /* If there are more elements */
            if (en.hasMoreElements()) {
                /* Access next item */
                final Object myItem = en.nextElement();

                /* Look for confirmation */
                if (myItem instanceof ASN1OctetString) {
                    theConfirmation = ASN1OctetString.getInstance(myItem).getOctets();
                    theSignId = null;
                    theSignature = null;

                    /* Look for signature */
                } else if (myItem instanceof ASN1Sequence) {
                    final ASN1Sequence mySignature = ASN1Sequence.getInstance(myItem);
                    theConfirmation = null;
                    final Enumeration<?> es = mySignature.getObjects();
                    theSignId = AlgorithmIdentifier.getInstance(es.nextElement());
                    theSignature = ASN1OctetString.getInstance(es.nextElement()).getOctets();

                    /* Unknown */
                } else {
                    throw new GordianDataException("Unexpected value in ASN1 sequence");
                }

                /* No extra */
            } else {
                theConfirmation = null;
                theSignId = null;
                theSignature = null;
            }

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
        return theAgreementId;
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

    /**
     * Obtain the confirmation.
     * @return the confirmation
     */
    public byte[] getConfirmation() {
        return theConfirmation;
    }

    /**
     * Obtain the signatureId.
     * @return the signatureId
     */
    public AlgorithmIdentifier getSignatureId() {
        return theSignId;
    }

    /**
     * Obtain the signature.
     * @return the signature
     */
    public byte[] getSignature() {
        return theSignature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build basic part */
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new BEROctetString(MSG_ID));
        v.add(theAgreementId);
        v.add(new BEROctetString(theInitVector));

        /* Add data if present */
        if (theData != null) {
            v.add(new BEROctetString(theData));
        }

        /* Add confirmation if present */
        if (theConfirmation != null) {
            v.add(new BEROctetString(theConfirmation));

            /* else add signature if present */
        } else if (theSignId != null) {
            final ASN1EncodableVector sv = new ASN1EncodableVector();
            sv.add(theSignId);
            sv.add(new BEROctetString(theSignature));
            v.add(new DERSequence(sv).toASN1Primitive());
        }

        /* return the sequence */
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
        return Objects.equals(getAgreementId(), myThat.getAgreementId())
                && Objects.equals(getSignatureId(), myThat.getSignatureId())
                && Arrays.equals(getData(), myThat.getData())
                && Arrays.equals(getConfirmation(), myThat.getConfirmation())
                && Arrays.equals(getSignature(), myThat.getSignature())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreementId(), getSignatureId())
                + Arrays.hashCode(getData())
                + Arrays.hashCode(getConfirmation())
                + Arrays.hashCode(getSignature())
                + Arrays.hashCode(getInitVector());
    }
}
