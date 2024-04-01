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

import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1.GordianMessageType;
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
 *      }
 * }
 * </pre>
 */
public class GordianZipLockASN1
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
     * The zipLockType.
     */
    private final GordianZipLockType theLockType;

    /**
     * The keySetHashASN1.
     */
    private final GordianKeySetHashASN1 theKeySetHash;

    /**
     * The key.
     */
    private final byte[] theKey;

    /**
     * The keyPair messageASN1.
     */
    private final GordianAgreementMessageASN1 theKeyPairHello;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash) {
        /* Store the Details */
        theLockType = GordianZipLockType.PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = null;
        theKeyPairHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pKey the key
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                              final byte[] pKey) {
        /* Store the Details */
        theLockType = GordianZipLockType.PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = pKey;
        theKeyPairHello = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                              final GordianAgreementMessageASN1 pClientHello) {
        /* Store the Details */
        theLockType = GordianZipLockType.KEYPAIR_PASSWORD;
        theKeySetHash = pKeySetHash;
        theKey = null;
        theKeyPairHello = pClientHello;
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

            /* Access message parts */
            theKeySetHash = GordianKeySetHashASN1.getInstance(en.nextElement());
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            switch (myTagged.getTagNo()) {
                case TAG_KEY:
                    theLockType = GordianZipLockType.KEY_PASSWORD;
                    theKey = ASN1OctetString.getInstance(myTagged, false).getOctets();
                    theKeyPairHello = null;
                    break;
                case TAG_KEYPAIR:
                    theLockType = GordianZipLockType.KEYPAIR_PASSWORD;
                    theKey = null;
                    theKeyPairHello = GordianAgreementMessageASN1.getInstance(ASN1Sequence.getInstance(myTagged, false));
                    theKeyPairHello.checkMessageType(GordianMessageType.CLIENTHELLO);
                    break;
                case TAG_PASSWORD:
                default:
                    theLockType = GordianZipLockType.PASSWORD;
                    theKey = null;
                    theKeyPairHello = null;
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
    public GordianAgreementMessageASN1 getKeyPairHello() {
        return theKeyPairHello;
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
        if (!(pThat instanceof GordianZipLockASN1)) {
            return false;
        }
        final GordianZipLockASN1 myThat = (GordianZipLockASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theKeySetHash, myThat.getKeySetHash())
                && Arrays.equals(theKey, myThat.getKey())
                && Objects.equals(theKeyPairHello, myThat.getKeyPairHello());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySetHash, theKeyPairHello)
                ^ Arrays.hashCode(theKey);
    }
}
