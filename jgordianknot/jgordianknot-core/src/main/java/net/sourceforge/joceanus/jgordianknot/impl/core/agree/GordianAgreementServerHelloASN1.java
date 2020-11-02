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

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of Agreement ServerHello.
 * <pre>
 * GordianAgreementServerHelloASN1 ::= SEQUENCE  {
 *      agreeId AlgorithmIdentifier
 *      initVector OCTET STRING
 *      ephemeral SubjectPublicKeyInfo OPTIONAL
 *      body CHOICE {
 *          confirmation    [1] OCTET STRING
 *          signature       [2] SEQUENCE {
 *              signId AlgorithmIdentifier
 *              signature OCTET STRING
 *          }
 *      } OPTIONAL
 * }
 * </pre>
 */
public class GordianAgreementServerHelloASN1
        extends GordianASN1Object {
    /**
     * The confirmation tag.
     */
    private static final int TAG_CONFIRMATION = 1;

    /**
     * The signature tag.
     */
    private static final int TAG_SIGNATURE = 2;

    /**
     * The AgreementId.
     */
    private final AlgorithmIdentifier theAgreementId;

    /**
     * The InitVector.
     */
    private final byte[] theInitVector;

    /**
     * The Ephemeral PublicKey.
     */
    private final X509EncodedKeySpec theEphemeral;

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
     * @param pEphemeral the ephemeral PublicKey
     * @param pConfirmation the confirmation
     */
    public GordianAgreementServerHelloASN1(final AlgorithmIdentifier pAgreement,
                                           final byte[] pInitVector,
                                           final X509EncodedKeySpec pEphemeral,
                                           final byte[] pConfirmation) {
        /* Store the Details */
        theAgreementId = pAgreement;
        theInitVector = pInitVector;
        theEphemeral = pEphemeral;
        theConfirmation = pConfirmation;
        theSignId = null;
        theSignature = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreementId
     * @param pInitVector theInitVector
     * @param pEphemeral the ephemeral publicKey
     * @param pSignId the signatureId
     * @param pSignature the signature
     */
    public GordianAgreementServerHelloASN1(final AlgorithmIdentifier pAgreement,
                                           final byte[] pInitVector,
                                           final X509EncodedKeySpec pEphemeral,
                                           final AlgorithmIdentifier pSignId,
                                           final byte[] pSignature) {
        /* Store the Details */
        theAgreementId = pAgreement;
        theInitVector = pInitVector;
        theEphemeral = pEphemeral;
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

            /* Access standard message parts */
            theAgreementId = AlgorithmIdentifier.getInstance(en.nextElement());
            theInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theEphemeral = en.hasMoreElements()
                    ? new X509EncodedKeySpec(SubjectPublicKeyInfo.getInstance(en.nextElement()).getEncoded())
                    : null;

            /* If there are more elements */
            if (en.hasMoreElements()) {
                /* Access next item */
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_CONFIRMATION:
                        theConfirmation = ASN1OctetString.getInstance(myTagged.getObject()).getOctets();
                        theSignId = null;
                        theSignature = null;
                        break;
                    case TAG_SIGNATURE:
                        final ASN1Sequence mySignature = ASN1Sequence.getInstance(myTagged.getObject());
                        final Enumeration<?> es = mySignature.getObjects();
                        theSignId = AlgorithmIdentifier.getInstance(es.nextElement());
                        theSignature = ASN1OctetString.getInstance(es.nextElement()).getOctets();
                        theConfirmation = null;
                        break;
                        /* Unknown */
                    default:
                        throw new GordianDataException("Unexpected value in ASN1 sequence");
                }

                /* No confirmation or signature */
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
        } catch (IllegalArgumentException
                | IOException e) {
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
     * Obtain the ephemeral publicKey.
     * @return the data
     */
    public X509EncodedKeySpec getEphemeral() {
        return theEphemeral;
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
        v.add(theAgreementId);
        v.add(new BEROctetString(theInitVector));

        /* Add ephemeral if present */
        if (theEphemeral != null) {
            v.add(SubjectPublicKeyInfo.getInstance(theEphemeral.getEncoded()));
        }

        /* Add confirmation if present */
        if (theConfirmation != null) {
            final BEROctetString myObject = new BEROctetString(theConfirmation);
            v.add(new DERTaggedObject(false, TAG_CONFIRMATION, myObject));

            /* else add signature if present */
        } else if (theSignId != null) {
            final ASN1EncodableVector sv = new ASN1EncodableVector();
            sv.add(theSignId);
            sv.add(new BEROctetString(theSignature));
            v.add(new DERTaggedObject(false, TAG_SIGNATURE, new DERSequence(sv)));
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
                && Objects.equals(getEphemeral(), myThat.getEphemeral())
                && Arrays.equals(getConfirmation(), myThat.getConfirmation())
                && Arrays.equals(getSignature(), myThat.getSignature())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreementId(), getSignatureId(), getEphemeral())
                ^ Arrays.hashCode(getConfirmation())
                ^ Arrays.hashCode(getSignature())
                ^ Arrays.hashCode(getInitVector());
    }
}
