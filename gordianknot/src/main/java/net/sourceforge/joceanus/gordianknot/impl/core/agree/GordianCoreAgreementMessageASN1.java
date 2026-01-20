/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.gordianknot.impl.core.agree;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCertificateASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
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

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of Agreement Messages.
 * <pre>
 * GordianAgreementMessageASN1 ::= SEQUENCE  {
 *      Id Identification
 *      Algs Specifications
 *      clientCertificate [1] GordianCertificateASN1 OPTIONAL
 *      serverCertificate [2] GordianCertificateASN1 OPTIONAL
 *      signerCertificate [3] GordianCertificateASN1 OPTIONAL
 *      initVector [4] OCTET STRING OPTIONAL
 *      encapsulated [5] OCTET STRING OPTIONAL
 *      ephemeral [6] SubjectPublicKeyInfo OPTIONAL
 *      confirmation [7] OCTET STRING OPTIONAL
 *      signature [8] OCTET STRING OPTIONAL
 * }
 *
 * Identification ::= SEQUENCE  {
 *      messageType INTEGER
 *      clientId [1] INTEGER OPTIONAL
 *      serverId [2] INTEGER OPTIONAL
 * }
 *
 * Specification ::= SEQUENCE  {
 *      agreeId AlgorithmIdentifier
 *      resultId [1] AlgorithmIdentifier OPTIONAL
 *      signId [2] lgorithmIdentifier OPTIONAL
 *  }
 * </pre>
 */
public final class GordianCoreAgreementMessageASN1
        extends GordianASN1Object {
    /**
     * The clientCertificate tag.
     */
    private static final int TAG_CLIENTCERTIFICATE = 1;

    /**
     * The serverCertificate tag.
     */
    private static final int TAG_SERVERCERTIFICATE = 2;

    /**
     * The signingCertificate tag.
     */
    private static final int TAG_SIGNERCERTIFICATE = 3;

    /**
     * The initVector tag.
     */
    private static final int TAG_INITVECTOR = 4;

    /**
     * The encapsulated tag.
     */
    private static final int TAG_ENCAPSULATED = 5;

    /**
     * The ephemeral tag.
     */
    private static final int TAG_EPHEMERAL = 6;

    /**
     * The confirmation tag.
     */
    private static final int TAG_CONFIRMATION = 7;

    /**
     * The signature tag.
     */
    private static final int TAG_SIGNATURE = 8;

    /**
     * The clientId tag.
     */
    private static final int TAG_CLIENTID = 1;

    /**
     * The serverId tag.
     */
    private static final int TAG_SERVERID = 2;


    /**
     * The resultAlgId tag.
     */
    private static final int TAG_RESULTALGID = 1;

    /**
     * The signAlgId tag.
     */
    private static final int TAG_SIGNALGID = 2;

    /**
     * The MessageType.
     */
    private final GordianAgreementMessageType theMessageType;

    /**
     * The ClientId.
     */
    private Long theClientId;

    /**
     * The ServerId.
     */
    private Long theServerId;

    /**
     * The Agreement AlgorithmId.
     */
    private AlgorithmIdentifier theAgreementId;

    /**
     * The ResultId.
     */
    private AlgorithmIdentifier theResultId;

    /**
     * The SignatureId.
     */
    private AlgorithmIdentifier theSignatureId;

    /**
     * The Client Certificate.
     */
    private GordianCertificateASN1 theClientCertificate;

    /**
     * The Server Certificate.
     */
    private GordianCertificateASN1 theServerCertificate;

    /**
     * The Signer Certificate.
     */
    private GordianCertificateASN1 theSignerCertificate;

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
     * The Confirmation.
     */
    private byte[] theConfirmation;

    /**
     * The Signature.
     */
    private byte[] theSignature;

    /**
     * Constructor.
     *
     * @param pType the messageType
     */
    private GordianCoreAgreementMessageASN1(final GordianAgreementMessageType pType) {
        theMessageType = pType;
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCoreAgreementMessageASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access id element */
            final ASN1Sequence myId = ASN1Sequence.getInstance(en.nextElement());
            final Enumeration<?> enIds = myId.getObjects();
            theMessageType = GordianAgreementMessageType.determineType(ASN1Integer.getInstance(enIds.nextElement()).intValueExact());

            /* Loop through the optional id elements */
            while (enIds.hasMoreElements()) {
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(enIds.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_CLIENTID:
                        theClientId = ASN1Integer.getInstance(myTagged, false).longValueExact();
                        break;
                    case TAG_SERVERID:
                        theServerId = ASN1Integer.getInstance(myTagged, false).longValueExact();
                        break;
                    default:
                        throw new GordianDataException("Unexpected id tag");
                }
            }

            /* Access algorithms element */
            final ASN1Sequence myAlgs = ASN1Sequence.getInstance(en.nextElement());
            final Enumeration<?> enAlgs = myAlgs.getObjects();
            theAgreementId = AlgorithmIdentifier.getInstance(enAlgs.nextElement());

            /* Loop through the optional algorithm elements */
            while (enAlgs.hasMoreElements()) {
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(enAlgs.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_RESULTALGID:
                        theResultId = AlgorithmIdentifier.getInstance(myTagged, false);
                        break;
                    case TAG_SIGNALGID:
                        theSignatureId = AlgorithmIdentifier.getInstance(myTagged, false);
                        break;
                    default:
                        throw new GordianDataException("Unexpected algorithm tag");
                }
            }

            /* Loop through the optional elements */
            while (en.hasMoreElements()) {
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_CLIENTCERTIFICATE:
                        theClientCertificate = GordianCertificateASN1.getInstance(myTagged, false);
                        break;
                    case TAG_SERVERCERTIFICATE:
                        theServerCertificate = GordianCertificateASN1.getInstance(myTagged, false);
                        break;
                    case TAG_SIGNERCERTIFICATE:
                        theSignerCertificate = GordianCertificateASN1.getInstance(myTagged, false);
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
                        theSignature = ASN1OctetString.getInstance(myTagged, false).getOctets();
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
     *
     * @return the clientHello
     */
    public static GordianCoreAgreementMessageASN1 newClientHello() {
        return new GordianCoreAgreementMessageASN1(GordianAgreementMessageType.CLIENTHELLO);
    }

    /**
     * Create a server hello.
     *
     * @return the serverHello
     */
    public static GordianCoreAgreementMessageASN1 newServerHello() {
        return new GordianCoreAgreementMessageASN1(GordianAgreementMessageType.SERVERHELLO);
    }

    /**
     * Create a client confirm.
     *
     * @return the clientConfirm
     */
    public static GordianCoreAgreementMessageASN1 newClientConfirm() {
        return new GordianCoreAgreementMessageASN1(GordianAgreementMessageType.CLIENTCONFIRM);
    }

    /**
     * Parse the ASN1 object.
     *
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCoreAgreementMessageASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCoreAgreementMessageASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCoreAgreementMessageASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Get the messageType.
     *
     * @return the messageType
     */
    GordianAgreementMessageType getMessageType() {
        return theMessageType;
    }

    /**
     * Check the message type.
     *
     * @param pMessageType the message type
     * @throws GordianException on error
     */
    public void checkMessageType(final GordianAgreementMessageType pMessageType) throws GordianException {
        if (!theMessageType.equals(pMessageType)) {
            throw new GordianDataException("Unexpected Message type: " + pMessageType);
        }
    }

    /**
     * Get the clientId.
     *
     * @return the clientId
     */
    Long getClientId() {
        return theClientId;
    }

    /**
     * Set the clientId.
     *
     * @param pClientId the clientId
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setClientId(final Long pClientId) {
        theClientId = pClientId;
        return this;
    }

    /**
     * Get the clientId.
     *
     * @return the clientId
     */
    Long getServerId() {
        return theServerId;
    }

    /**
     * Set the serverId.
     *
     * @param pServerId the serverId
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setServerId(final Long pServerId) {
        theServerId = pServerId;
        return this;
    }

    /**
     * Get the agreement algorithmId.
     *
     * @return the agreement algorithmId (or null)
     */
    AlgorithmIdentifier getAgreementId() {
        return theAgreementId;
    }

    /**
     * Set the agreement algorithmId.
     *
     * @param pAgreeId the agreementId
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setAgreementId(final AlgorithmIdentifier pAgreeId) {
        theAgreementId = pAgreeId;
        return this;
    }

    /**
     * Get the result algorithmId.
     *
     * @return the result algorithmId (or null)
     */
    AlgorithmIdentifier getResultId() {
        return theResultId;
    }

    /**
     * Set the result algorithmId.
     *
     * @param pResultId the resultId
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setResultId(final AlgorithmIdentifier pResultId) {
        theResultId = pResultId;
        return this;
    }

    /**
     * Get the client certificate.
     *
     * @param pFactory the factory
     * @return the certificate (or null)
     * @throws GordianException on error
     */
    GordianCertificate getClientCertificate(final GordianFactory pFactory) throws GordianException {
        return theClientCertificate == null ? null : theClientCertificate.getCertificate(pFactory);
    }

    /**
     * Set the client certificate.
     *
     * @param pCertificate the certificate
     * @return this object
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 setClientCertificate(final GordianCertificate pCertificate) throws GordianException {
        theClientCertificate = pCertificate == null ? null : new GordianCertificateASN1(pCertificate);
        return this;
    }

    /**
     * Get the server certificate.
     *
     * @param pFactory the factory
     * @return the certificate (or null)
     * @throws GordianException on error
     */
    GordianCertificate getServerCertificate(final GordianFactory pFactory) throws GordianException {
        return theServerCertificate == null ? null : theServerCertificate.getCertificate(pFactory);
    }

    /**
     * Set the server certificate.
     *
     * @param pCertificate the certificate
     * @return this object
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 setServerCertificate(final GordianCertificate pCertificate) throws GordianException {
        theServerCertificate = pCertificate == null ? null : new GordianCertificateASN1(pCertificate);
        return this;
    }

    /**
     * Get the signer certificate.
     *
     * @param pFactory the factory
     * @return the certificate (or null)
     * @throws GordianException on error
     */
    GordianCertificate getSignerCertificate(final GordianFactory pFactory) throws GordianException {
        return theSignerCertificate == null ? null : theSignerCertificate.getCertificate(pFactory);
    }

    /**
     * Set the signer certificate.
     *
     * @param pCertificate the certificate
     * @return this object
     * @throws GordianException on error
     */
    GordianCoreAgreementMessageASN1 setSignerCertificate(final GordianCertificate pCertificate) throws GordianException {
        theSignerCertificate = pCertificate == null ? null : new GordianCertificateASN1(pCertificate);
        return this;
    }

    /**
     * Get the initVector.
     *
     * @return the initVector (or null)
     */
    byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Set the initVector.
     *
     * @param pInitVector the initVector Tag
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
        return this;
    }

    /**
     * Get the encapsulated.
     *
     * @return the encapsulated (or null)
     */
    public byte[] getEncapsulated() {
        return theEncapsulated;
    }

    /**
     * Set the encapsulated.
     *
     * @param pEncapsulated the encapsulated
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setEncapsulated(final byte[] pEncapsulated) {
        theEncapsulated = pEncapsulated;
        return this;
    }

    /**
     * Get the ephemeral.
     *
     * @return the ephemeral (or null)
     */
    public X509EncodedKeySpec getEphemeral() {
        return theEphemeral;
    }

    /**
     * Set the ephemeral.
     *
     * @param pEphemeral the ephemeral
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setEphemeral(final X509EncodedKeySpec pEphemeral) {
        theEphemeral = pEphemeral;
        return this;
    }

    /**
     * Get the confirmation.
     *
     * @return the confirmation Tag (or null)
     */
    byte[] getConfirmation() {
        return theConfirmation;
    }

    /**
     * Set the confirmation.
     *
     * @param pConfirmation the confirmation Tag
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setConfirmation(final byte[] pConfirmation) {
        theConfirmation = pConfirmation;
        return this;
    }

    /**
     * Get the signature algorithmId.
     *
     * @return the signature algorithmId (or null)
     */
    AlgorithmIdentifier getSignatureId() {
        return theSignatureId;
    }

    /**
     * Get the signature.
     *
     * @return the signature (or null)
     */
    byte[] getSignature() {
        return theSignature;
    }

    /**
     * Set the signature and algorithmId.
     *
     * @param pSignatureId the signature algorithmId
     * @param pSignature   the signature
     * @return this object
     */
    GordianCoreAgreementMessageASN1 setSignature(final AlgorithmIdentifier pSignatureId,
                                                 final byte[] pSignature) {
        theSignatureId = pSignatureId;
        theSignature = pSignature;
        return this;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Create the vector */
        final ASN1EncodableVector v = new ASN1EncodableVector();

        /* Add id section */
        final ASN1EncodableVector vId = new ASN1EncodableVector();
        vId.add(new ASN1Integer(theMessageType.getId()));
        if (theClientId != null) {
            vId.add(new DERTaggedObject(false, TAG_CLIENTID, new ASN1Integer(theClientId)));
        }
        if (theServerId != null) {
            vId.add(new DERTaggedObject(false, TAG_SERVERID, new ASN1Integer(theServerId)));
        }
        v.add(new DERSequence(vId));

        /* Add algorithm section */
        final ASN1EncodableVector vAlg = new ASN1EncodableVector();
        vAlg.add(theAgreementId);
        if (theResultId != null) {
            vAlg.add(new DERTaggedObject(false, TAG_RESULTALGID, theResultId));
        }
        if (theSignatureId != null) {
            vAlg.add(new DERTaggedObject(false, TAG_SIGNALGID, theSignatureId));
        }
        v.add(new DERSequence(vAlg));

        /* Add optional components */
        if (theClientCertificate != null) {
            v.add(new DERTaggedObject(false, TAG_CLIENTCERTIFICATE, theClientCertificate));
        }
        if (theServerCertificate != null) {
            v.add(new DERTaggedObject(false, TAG_SERVERCERTIFICATE, theServerCertificate));
        }
        if (theSignerCertificate != null) {
            v.add(new DERTaggedObject(false, TAG_SIGNERCERTIFICATE, theSignerCertificate));
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
        if (theSignature != null) {
            v.add(new DERTaggedObject(false, TAG_SIGNATURE, new BEROctetString(theSignature)));
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

        /* Check that the fields are equal */
        return pThat instanceof GordianCoreAgreementMessageASN1 myThat
                && Objects.equals(getMessageType(), myThat.getMessageType())
                && Objects.equals(getClientId(), myThat.getClientId())
                && Objects.equals(getServerId(), myThat.getServerId())
                && Objects.equals(getAgreementId(), myThat.getAgreementId())
                && Objects.equals(getResultId(), myThat.getResultId())
                && Objects.equals(getSignatureId(), myThat.getSignatureId())
                && Objects.equals(getEphemeral(), myThat.getEphemeral())
                && Objects.equals(theClientCertificate, myThat.theClientCertificate)
                && Objects.equals(theServerCertificate, myThat.theServerCertificate)
                && Objects.equals(theSignerCertificate, myThat.theSignerCertificate)
                && Arrays.equals(getSignature(), myThat.getSignature())
                && Arrays.equals(getEncapsulated(), myThat.getEncapsulated())
                && Arrays.equals(getConfirmation(), myThat.getConfirmation())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageType(), getClientId(), getServerId(),
                getAgreementId(), getResultId(), getSignatureId(), getEphemeral(),
                theClientCertificate, theServerCertificate, theSignerCertificate)
                ^ Arrays.hashCode(getSignature())
                ^ Arrays.hashCode(getEncapsulated())
                ^ Arrays.hashCode(getConfirmation())
                ^ Arrays.hashCode(getInitVector());
    }

    /**
     * The messageType.
     */
    public enum GordianAgreementMessageType {
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
         *
         * @param pId the id
         */
        GordianAgreementMessageType(final int pId) {
            theId = pId;
        }

        /**
         * Obtain id for messageType.
         *
         * @return the id
         */
        int getId() {
            return theId;
        }

        /**
         * Determine the MessageType from the id.
         *
         * @param pId the id
         * @return the messageType
         * @throws GordianException on error
         */
        private static GordianAgreementMessageType determineType(final int pId) throws GordianException {
            for (GordianAgreementMessageType myType : values()) {
                if (pId == myType.getId()) {
                    return myType;
                }
            }
            throw new GordianDataException("Unexpected messageType: " + pId);
        }
    }
}
