/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementClientHelloASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianKeyPairSetAgreeASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetHashASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of Lock.
 * <pre>
 * GordianLockASN1 ::= SEQUENCE  {
 *      hashBytes GordianKeySetHashASN1
 *      lockType CHOICE {
 *          password    [0] NULL
 *          key         [1] OCTET STRING
 *          keyPair     [2] GordianAgreementClientHelloASN1
 *          keyPairSet  [3] GordianKeyPairSetAgreeASN1
 *      }
 * }
 * </pre>
 */
public class GordianLockASN1
        extends GordianASN1Object {
    /**
     * LockOID branch.
     */
    public static final ASN1ObjectIdentifier LOCKOID = GordianASN1Util.EXTOID.branch("3");

    /**
     * Password.
     */
    private static final int TAG_PASSWORD = 0;

    /**
     * Key and Password.
     */
    private static final int TAG_KEY = 1;

    /**
     * KeyPair and Password.
     */
    private static final int TAG_KEYPAIR = 2;

    /**
     * KeyPairSet and Password.
     */
    private static final int TAG_KEYPAIRSET = 3;

    /**
     * The zipLockType.
     */
    private final GordianLockType theLockType;

    /**
     * The keySetHashASN1.
     */
    private final GordianKeySetHashASN1 theKeySetHash;

    /**
     * The key.
     */
    private final byte[] theKey;

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
    public GordianLockASN1(final GordianKeySetHashASN1 pKeySetHash) {
        /* Store the Details */
        theLockType = GordianLockType.PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = null;
        theKeyPairHello = null;
        theKeyPairSetHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pKey the key
     */
    public GordianLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                           final byte[] pKey) {
        /* Store the Details */
        theLockType = GordianLockType.PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = pKey;
        theKeyPairHello = null;
        theKeyPairSetHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                           final GordianAgreementClientHelloASN1 pClientHello) {
        /* Store the Details */
        theLockType = GordianLockType.KEYPAIR_PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = null;
        theKeyPairHello = pClientHello;
        theKeyPairSetHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                           final GordianKeyPairSetAgreeASN1 pClientHello) {
        /* Store the Details */
        theLockType = GordianLockType.KEYPAIRSET_PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = null;
        theKeyPairHello = null;
        theKeyPairSetHello = pClientHello;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianLockASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            theKeySetHash = GordianKeySetHashASN1.getInstance(en.nextElement());
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            switch (myTagged.getTagNo()) {
                case TAG_KEY:
                    theLockType = GordianLockType.KEY_PASSWORD;
                    theKey = ASN1OctetString.getInstance(myTagged.getObject()).getOctets();
                    theKeyPairHello = null;
                    theKeyPairSetHello = null;
                    break;
                case TAG_KEYPAIR:
                    theLockType = GordianLockType.KEYPAIR_PASSWORD;
                    theKey = null;
                    theKeyPairHello = GordianAgreementClientHelloASN1.getInstance(myTagged.getObject());
                    theKeyPairSetHello = null;
                    break;
                case TAG_KEYPAIRSET:
                    theLockType = GordianLockType.KEYPAIRSET_PASSWORD;
                    theKey = null;
                    theKeyPairHello = null;
                    theKeyPairSetHello = GordianKeyPairSetAgreeASN1.getInstance(myTagged.getObject());
                    break;
                case TAG_PASSWORD:
                default:
                    theLockType = GordianLockType.PASSWORD;
                    theKey = null;
                    theKeyPairHello = null;
                    theKeyPairSetHello = null;
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
    public static GordianLockASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianLockASN1) {
            return (GordianLockASN1) pObject;
        } else if (pObject != null) {
            return new GordianLockASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the lockType.
     * @return the lockType
     */
    public GordianLockType getLockType() {
        return theLockType;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public byte[] getKey() {
        return theKey;
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

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return new AlgorithmIdentifier(LOCKOID, toASN1Primitive());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theKeySetHash.toASN1Primitive());
        if (theKey != null) {
            final BEROctetString myKey = new BEROctetString(theKey);
            v.add(new DERTaggedObject(false, TAG_KEY, myKey));
        } else if (theKeyPairHello != null) {
            v.add(new DERTaggedObject(false, TAG_KEYPAIR, theKeyPairHello));
        } else if (theKeyPairSetHello != null) {
            v.add(new DERTaggedObject(false, TAG_KEYPAIRSET, theKeyPairSetHello));
        } else {
            v.add(new DERTaggedObject(false, TAG_PASSWORD, DERNull.INSTANCE));
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
        if (!(pThat instanceof GordianLockASN1)) {
            return false;
        }
        final GordianLockASN1 myThat = (GordianLockASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theKeySetHash, myThat.getKeySetHash())
                && Arrays.equals(theKey, myThat.getKey())
                && Objects.equals(theKeyPairHello, myThat.getKeyPairHello())
                && Objects.equals(theKeyPairSetHello, myThat.getKeyPairSetHello());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySetHash, theKeyPairHello, theKeyPairSetHello)
                ^ Arrays.hashCode(theKey);
    }
}