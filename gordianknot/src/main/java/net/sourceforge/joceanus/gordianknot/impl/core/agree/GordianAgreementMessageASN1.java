/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * ASN1 Encoding of Agreement Messages.
 * <pre>
 * GordianAgreementMessageASN1 ::= SEQUENCE  {
 *      Id Identification
 *      Algs [1] Specifications
 *      initVector [2] OCTET STRING OPTIONAL
 *      encapsulated [3] OCTET STRING OPTIONAL
 *      ephemeral [4] SubjectPublicKeyInfo OPTIONAL
 *      confirmation [5] OCTET STRING OPTIONAL
 *      signature [5] SignDetails OPTIONAL
 * }
 *
 * Identification ::= SEQUENCE  {
 *      messageType AlgorithmIdentifier
 *      clientId INTEGER OPTIONAL
 *      serverId INTEGER OPTIONAL
 * }
 *
 * Specification ::= SEQUENCE  {
 *      agreeId AlgorithmIdentifier
 *      resultId AlgorithmIdentifier OPTIONAL
 * }
 *
 * SignDetails ::= SEQUENCE  {
 *      signId AlgorithmIdentifier
 *      signature OCTET STRING
 * }
 * </pre>
 */
public final class GordianAgreementMessageASN1
        extends GordianASN1Object {
    /**
     * The algorithms tag.
     */
    private static final int TAG_ALGORITHMS = 1;

    /**
     * The intVector tag.
     */
    private static final int TAG_INITVECTOR = 2;

    /**
     * The encapsulated tag.
     */
    private static final int TAG_ENCAPSULATED = 3;

    /**
     * The ephemeral tag.
     */
    private static final int TAG_EPHEMERAL = 4;

    /**
     * The confirmation tag.
     */
    private static final int TAG_CONFIRMATION = 5;

    /**
     * The signature tag.
     */
    private static final int TAG_SIGNATURE = 6;

    /**
     * The MessageType.
     */
    private final GordianMessageType theMessageType;

    /**
     * The ClientId.
     */
    private final Integer theClientId;

    /**
     * The ServerId.
     */
    private final Integer theServerId;

    /**
     * The Agreement AlgorithmId.
     */
    private AlgorithmIdentifier theAgreementId;

    /**
     * The ResultId.
     */
    private AlgorithmIdentifier theResultId;

     /**
     * The InitVector.
     */
    private byte[] theInitVector;

    /**
     * The Encapsulated.
     */
    private byte[] theEncapsulated;

    /**
     * The Ephemeral.
     */
    private X509EncodedKeySpec theEphemeral;

    /**
     * The SignatureId.
     */
    private AlgorithmIdentifier theSignatureId;

    /**
     * The Signature.
     */
    private byte[] theSignature;

    /**
     * The Confirmation.
     */
    private byte[] theConfirmation;

    /**
     * Constructor.
     * @param pType the messageType
     * @param pClientId the clientId
     * @param pServerId the serverId
     */
    private GordianAgreementMessageASN1(final GordianMessageType pType,
                                        final Integer pClientId,
                                        final Integer pServerId) {
        theMessageType = pType;
        theClientId = pClientId;
        theServerId = pServerId;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianAgreementMessageASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            final ASN1Sequence myId = ASN1Sequence.getInstance(en.nextElement());
            final Enumeration<?> enIds = myId.getObjects();
            theMessageType = GordianMessageType.determineType(ASN1Integer.getInstance(enIds.nextElement()).intValueExact());
            theClientId = enIds.hasMoreElements() && theMessageType != GordianMessageType.CLIENTCONFIRM
                            ? ASN1Integer.getInstance(enIds.nextElement()).intValueExact()
                            : null;
            theServerId = enIds.hasMoreElements()
                            ? ASN1Integer.getInstance(enIds.nextElement()).intValueExact()
                            : null;

            /* Loop through the optional elements */
            while (en.hasMoreElements()) {
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_ALGORITHMS:
                        final ASN1Sequence myAlgs = ASN1Sequence.getInstance(myTagged, false);
                        final Enumeration<?> enAlgs = myAlgs.getObjects();
                        theAgreementId = AlgorithmIdentifier.getInstance(enAlgs.nextElement());
                        if (enAlgs.hasMoreElements()) {
                            theResultId = AlgorithmIdentifier.getInstance(enAlgs.nextElement());
                        }
                        break;
                    case TAG_INITVECTOR:
                        theInitVector = ASN1OctetString.getInstance(myTagged, false).getOctets();
                        break;
                    case TAG_ENCAPSULATED:
                        theEncapsulated = ASN1OctetString.getInstance(myTagged, false).getOctets();
                        break;
                    case TAG_EPHEMERAL:
                        theEphemeral = new X509EncodedKeySpec(SubjectPublicKeyInfo.getInstance(myTagged, false).getEncoded());
                        break;
                    case TAG_CONFIRMATION:
                        theConfirmation = ASN1OctetString.getInstance(myTagged, false).getOctets();
                        break;
                    case TAG_SIGNATURE:
                        final ASN1Sequence mySign = ASN1Sequence.getInstance(myTagged, false);
                        final Enumeration<?> enSign = mySign.getObjects();
                        theSignatureId = AlgorithmIdentifier.getInstance(enSign.nextElement());
                        theSignature = ASN1OctetString.getInstance(enSign.nextElement()).getOctets();
                        break;
                    default:
                        throw new GordianDataException("Unexpected tag");
                }
            }

            /* handle exceptions */
        } catch (IllegalArgumentException
                | IOException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Create a client hello.
     * @param pClientId the clientId
     * @return the clientHello
     */
    public static GordianAgreementMessageASN1 newClientHello(final Integer pClientId)  {
        return new GordianAgreementMessageASN1(GordianMessageType.CLIENTHELLO, pClientId, null);
    }

    /**
     * Create a server hello.
     * @param pClientId the clientId
     * @param pServerId the serverId
     * @return the serverHello
     */
    public static GordianAgreementMessageASN1 newServerHello(final Integer pClientId,
                                                             final Integer pServerId)  {
        return new GordianAgreementMessageASN1(GordianMessageType.SERVERHELLO, pClientId, pServerId);
    }
    /**
     * Create a client confirm.
     * @param pServerId the serverId
     * @return the clientConfirm
     */
    public static GordianAgreementMessageASN1 newClientConfirm(final Integer pServerId)  {
        return new GordianAgreementMessageASN1(GordianMessageType.CLIENTCONFIRM, null, pServerId);
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws OceanusException on error
     */
    public static GordianAgreementMessageASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianAgreementMessageASN1) {
            return (GordianAgreementMessageASN1) pObject;
        } else if (pObject != null) {
            return new GordianAgreementMessageASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Get the messageType.
     * @return the messageType
     */
    GordianMessageType getMessageType() {
        return theMessageType;
    }

    /**
     * Check the message type.
     * @param pMessageType the message type
     * @throws OceanusException on error
     */
    public void checkMessageType(final GordianMessageType pMessageType) throws OceanusException {
        if (!theMessageType.equals(pMessageType)) {
            throw new GordianDataException("Unexpected Message type: " + pMessageType);
        }
    }

    /**
     * Get the clientId.
     * @return the clientId
     */
    Integer getClientId() {
        return theClientId;
    }

    /**
     * Get the clientId.
     * @return the clientId
     */
    Integer getServerId() {
        return theServerId;
    }

    /**
     * Get the agreement algorithmId.
     * @return the agreement algorithmId (or null)
     */
    AlgorithmIdentifier getAgreementId() {
        return theAgreementId;
    }

    /**
     * Set the agreement algorithmId.
     * @param pAgreeId the agreementId
     * @return this object
     */
    GordianAgreementMessageASN1 setAgreementId(final AlgorithmIdentifier pAgreeId) {
        theAgreementId = pAgreeId;
        return this;
    }

    /**
     * Get the result algorithmId.
     * @return the result algorithmId (or null)
     */
    AlgorithmIdentifier getResultId() {
        return theResultId;
    }

    /**
     * Set the result algorithmId.
     * @param pResultId the resultId
     * @return this object
     */
    GordianAgreementMessageASN1 setResultId(final AlgorithmIdentifier pResultId) {
        theResultId = pResultId;
        return this;
    }

    /**
     * Set the initVector.
     * @return the initVector (or null)
     */
    byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Set the initVector.
     * @param pInitVector the initVector Tag
     * @return this object
     */
    GordianAgreementMessageASN1 setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
        return this;
    }

    /**
     * Get the encapsulated.
     * @return the encapsulated (or null)
     */
    public byte[] getEncapsulated() {
        return theEncapsulated;
    }

    /**
     * Set the encapsulated.
     * @param pEncapsulated the encapsulated
     * @return this object
     */
    GordianAgreementMessageASN1 setEncapsulated(final byte[] pEncapsulated) {
        theEncapsulated = pEncapsulated;
        return this;
    }

    /**
     * Get the ephemeral.
     * @return the ephemeral (or null)
     */
    public X509EncodedKeySpec getEphemeral() {
        return theEphemeral;
    }

    /**
     * Set the ephemeral.
     * @param pEphemeral the ephemeral
     * @return this object
     */
    GordianAgreementMessageASN1 setEphemeral(final X509EncodedKeySpec pEphemeral) {
        theEphemeral = pEphemeral;
        return this;
    }

    /**
     * Get the signature algorithmId.
     * @return the signature algorithmId (or null)
     */
    AlgorithmIdentifier getSignatureId() {
        return theSignatureId;
    }

    /**
     * Get the signature.
     * @return the signature (or null)
     */
    byte[] getSignature() {
        return theSignature;
    }

    /**
     * Set the signature and algorithmId.
     * @param pSignatureId the signature algorithmId
     * @param pSignature the signature
     * @return this object
     */
    GordianAgreementMessageASN1 setSignature(final AlgorithmIdentifier pSignatureId,
                                             final byte[] pSignature) {
        theSignatureId = pSignatureId;
        theSignature = pSignature;
        return this;
    }

    /**
     * Get the confirmation.
     * @return the confirmation Tag (or null)
     */
    byte[] getConfirmation() {
        return theConfirmation;
    }

    /**
     * Set the confirmation.
     * @param pConfirmation the confirmation Tag
     * @return this object
     */
    GordianAgreementMessageASN1 setConfirmation(final byte[] pConfirmation) {
        theConfirmation = pConfirmation;
        return this;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        final ASN1EncodableVector vId = new ASN1EncodableVector();
        vId.add(new ASN1Integer(theMessageType.getId()));
        if (theClientId != null) {
            vId.add(new ASN1Integer(theClientId));
        }
        if (theServerId != null) {
            vId.add(new ASN1Integer(theServerId));
        }
        v.add(new DERSequence(vId));

        if (theAgreementId != null) {
            final ASN1EncodableVector vAlg = new ASN1EncodableVector();
            vAlg.add(theAgreementId);
            if (theResultId != null) {
                vAlg.add(theResultId);
            }
            v.add(new DERTaggedObject(false, TAG_ALGORITHMS, new DERSequence(vAlg)));
        }
        if (theInitVector != null) {
            v.add(new DERTaggedObject(false, TAG_INITVECTOR, new BEROctetString(theInitVector)));
        }
        if (theEncapsulated != null) {
            v.add(new DERTaggedObject(false, TAG_ENCAPSULATED, new BEROctetString(theEncapsulated)));
        }
        if (theEphemeral != null) {
            v.add(new DERTaggedObject(false, TAG_EPHEMERAL, SubjectPublicKeyInfo.getInstance(theEphemeral.getEncoded())));
        }
        if (theConfirmation != null) {
            v.add(new DERTaggedObject(false, TAG_CONFIRMATION, new BEROctetString(theConfirmation)));
        }
        if (theSignatureId != null && theSignature != null) {
            final ASN1EncodableVector vSign = new ASN1EncodableVector();
            vSign.add(theSignatureId);
            vSign.add(new BEROctetString(theSignature));
            v.add(new DERTaggedObject(false, TAG_SIGNATURE, new DERSequence(vSign)));
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
        if (!(pThat instanceof GordianAgreementMessageASN1)) {
            return false;
        }
        final GordianAgreementMessageASN1 myThat = (GordianAgreementMessageASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(getMessageType(), myThat.getMessageType())
                && Objects.equals(getClientId(), myThat.getClientId())
                && Objects.equals(getServerId(), myThat.getServerId())
                && Objects.equals(getAgreementId(), myThat.getAgreementId())
                && Objects.equals(getResultId(), myThat.getResultId())
                && Objects.equals(getSignatureId(), myThat.getSignatureId())
                && Objects.equals(getEphemeral(), myThat.getEphemeral())
                && Arrays.equals(getSignature(), myThat.getSignature())
                && Arrays.equals(getEncapsulated(), myThat.getEncapsulated())
                && Arrays.equals(getConfirmation(), myThat.getConfirmation())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageType(), getClientId(), getServerId())
                ^ Objects.hash(getAgreementId(), getResultId(), getSignatureId(), getEphemeral())
                ^ Arrays.hashCode(getSignature())
                ^ Arrays.hashCode(getEncapsulated())
                ^ Arrays.hashCode(getConfirmation())
                ^ Arrays.hashCode(getInitVector());
    }

    /**
     * The messageType.
     */
    public enum GordianMessageType {
        /**
         * ClientHello.
         */
        CLIENTHELLO(1),

        /**
         * ServerHello.
         */
        SERVERHELLO(2),

        /**
         * ClientConfirm.
         */
        CLIENTCONFIRM(3);

        /**
         * The messageId.
         */
        private final int theId;

        /**
         * Constructor.
         * @param pId the id
         */
        GordianMessageType(final int pId) {
            theId = pId;
        }

        /**
         * Obtain id for messageType.
         * @return the id
         */
        int getId() {
            return theId;
        }

        /**
         * Determine the MessageType from the id.
         * @param pId the id
         * @return the messageType
         * @throws OceanusException on error
         */
        private static GordianMessageType determineType(final int pId) throws OceanusException {
            for (GordianMessageType myType : values()) {
                if (pId == myType.getId()) {
                    return myType;
                }
            }
            throw new GordianDataException("Unexpected messageType: " + pId);
        }
    }
}
