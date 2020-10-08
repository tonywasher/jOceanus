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
package net.sourceforge.joceanus.jgordianknot.impl.core.zip;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementClientHelloASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianKeyPairSetAgreeASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of ZipLock.
 * <pre>
 * GordianZipLockASN1 ::= SEQUENCE  {
 *      id OCTET STRING
 *      hashBytes GordianKeySetHashASN1
 *      clientHello GordianAgreementClientHelloASN1 OPTIONAL
 * }
 * </pre>
 */
public class GordianZipLockASN1
        extends GordianASN1Object {
    /**
     * The MessageId.
     */
    private static final byte[] MSG_ID = new byte[] { 'Z', 'L' };

    /**
     * The zipLockType.
     */
    private final GordianZipLockType theLockType;

    /**
     * The keySetHashASN1.
     */
    private final GordianKeySetHashASN1 theKeySetHash;

    /**
     * The keyPair clientHelloASN1.
     */
    private final GordianAgreementClientHelloASN1 theKeyPairHello;

    /**
     * The keyPairSet clientHelloASN1.
     */
    private final GordianKeyPairSetAgreeASN1 theKeyPairSetHello;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash) {
        /* Store the Details */
        theLockType = GordianZipLockType.PASSWORD;
        theKeySetHash = pKeySetHash;
        theKeyPairHello = null;
        theKeyPairSetHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                              final GordianAgreementClientHelloASN1 pClientHello) {
        /* Store the Details */
        theLockType = GordianZipLockType.KEYPAIR_PASSWORD;
        theKeySetHash = pKeySetHash;
        theKeyPairHello = pClientHello;
        theKeyPairSetHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                              final GordianKeyPairSetAgreeASN1 pClientHello) {
        /* Store the Details */
        theLockType = GordianZipLockType.KEYPAIRSET_PASSWORD;
        theKeySetHash = pKeySetHash;
        theKeyPairHello = null;
        theKeyPairSetHello = pClientHello;
    }
    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianZipLockASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Check MessageId */
            final byte[] myId = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theLockType = deriveLockType(myId);

            /* Access message parts */
            theKeySetHash = GordianKeySetHashASN1.getInstance(en.nextElement());
            switch (theLockType) {
                case PASSWORD:
                    theKeyPairHello = null;
                    theKeyPairSetHello = null;
                    break;
                case KEYPAIR_PASSWORD:
                    theKeyPairHello = GordianAgreementClientHelloASN1.getInstance(en.nextElement());
                    theKeyPairSetHello = null;
                    break;
                case KEYPAIRSET_PASSWORD:
                default:
                    theKeyPairHello = null;
                    theKeyPairSetHello = GordianKeyPairSetAgreeASN1.getInstance(en.nextElement());
                    break;
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
    public static GordianZipLockASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianZipLockASN1) {
            return (GordianZipLockASN1) pObject;
        } else if (pObject != null) {
            return new GordianZipLockASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the lockType.
     * @return the lockType
     */
    public GordianZipLockType getLockType() {
        return theLockType;
    }

    /**
     * Obtain the keySetHash.
     * @return the keySetHash
     */
    public GordianKeySetHashASN1 getKeySetHash() {
        return theKeySetHash;
    }

    /**
     * Obtain the keyPair clientHello.
     * @return the clientHello
     */
    public GordianAgreementClientHelloASN1 getKeyPairHello() {
        return theKeyPairHello;
    }

    /**
     * Obtain the keyPairSet clientHello.
     * @return the clientHello
     */
    public GordianKeyPairSetAgreeASN1 getKeyPairSetHello() {
        return theKeyPairSetHello;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new BEROctetString(createMessageId()));
        v.add(theKeySetHash.toASN1Primitive());
        if (theKeyPairHello != null) {
            v.add(theKeyPairHello.toASN1Primitive());
        }
        if (theKeyPairSetHello != null) {
            v.add(theKeyPairSetHello.toASN1Primitive());
        }
        return new DERSequence(v);
    }

    /**
     * Create messageId.
     * @return the messageId
     */
    private byte[] createMessageId() {
        final byte[] myId = Arrays.copyOf(MSG_ID, MSG_ID.length + 1);
        myId[MSG_ID.length] = (byte) ('1' + theLockType.ordinal());
        return myId;
    }

    /**
     * Derive lockType.
     * @param pMsgId the msgId
     * @return the lockType
     * @throws OceanusException on error
     */
    private static GordianZipLockType deriveLockType(final byte[] pMsgId) throws OceanusException {
        /* If the header is correct */
        final int myLen = MSG_ID.length;
        if (pMsgId.length == myLen + 1
            && ByteBuffer.wrap(pMsgId, 0, myLen).equals(ByteBuffer.wrap(MSG_ID))) {
            /* Check that id is valid */
            final int myZipType = pMsgId[myLen] - '1';
            final GordianZipLockType[] myTypes = GordianZipLockType.values();
            if (myZipType >= 0 && myZipType < myTypes.length) {
                /* Return zipType */
                return myTypes[myZipType];
            }
        }

        /* Reject message */
        throw new GordianDataException("Incorrect message type");
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
        if (!(pThat instanceof GordianZipLockASN1)) {
            return false;
        }
        final GordianZipLockASN1 myThat = (GordianZipLockASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theKeySetHash, myThat.getKeySetHash())
                && Objects.equals(theKeyPairHello, myThat.getKeyPairHello())
                && Objects.equals(theKeyPairSetHello, myThat.getKeyPairSetHello());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySetHash, theKeyPairHello, theKeyPairSetHello);
    }
}
