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
package net.sourceforge.joceanus.gordianknot.impl.core.zip;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeyPairLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLockType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianKeyPairLockASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.lock.GordianPasswordLockASN1;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of Lock.
 * <pre>
 * GordianZipLockASN1 ::= SEQUENCE  {*
 *      lockType CHOICE {
 *          password    [0] GordianPasswordLockASN1
 *          factory     [1] GordianPasswordLockASN1
 *          keyPair     [2] GordianKeyPairLockASN1
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
     * KeySet and Password.
     */
    private static final int TAG_KEYSET = 0;

    /**
     * Factory and Password.
     */
    private static final int TAG_FACTORY = 1;

    /**
     * KeyPair and Password.
     */
    private static final int TAG_KEYPAIR = 2;

    /**
     * The zipLockType.
     */
    private final GordianZipLockType theLockType;

    /**
     * The passwordLockASN1.
     */
    private final GordianPasswordLockASN1 thePasswordLock;

    /**
     * The keyPairLockASN1.
     */
    private final GordianKeyPairLockASN1 theKeyPairLock;

    /**
     * Create the ASN1 sequence.
     * @param pLock the lock
     */
    public GordianZipLockASN1(final GordianKeySetLock pLock) {
        /* Store the Details */
        theLockType = GordianZipLockType.KEYSET_PASSWORD;
        thePasswordLock = (GordianPasswordLockASN1) pLock.getLockASN1();
        theKeyPairLock = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pLock the lock
     */
    public GordianZipLockASN1(final GordianFactoryLock pLock) {
         /* Store the Details */
        theLockType = GordianZipLockType.FACTORY_PASSWORD;
        thePasswordLock = (GordianPasswordLockASN1) pLock.getLockASN1();
        theKeyPairLock = null;
    }

    /**
     * Create the ASN1 sequence.
     * @param pLock the lock
     */
    public GordianZipLockASN1(final GordianKeyPairLock pLock) {
        /* Store the Details */
        theLockType = GordianZipLockType.KEYPAIR_PASSWORD;
        thePasswordLock = null;
        theKeyPairLock = (GordianKeyPairLockASN1) pLock.getLockASN1();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianZipLockASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            switch (myTagged.getTagNo()) {
                case TAG_KEYSET:
                    theLockType = GordianZipLockType.KEYSET_PASSWORD;
                    thePasswordLock = GordianPasswordLockASN1.getInstance(myTagged.getBaseObject());
                    theKeyPairLock = null;
                    break;
                case TAG_FACTORY:
                    theLockType = GordianZipLockType.FACTORY_PASSWORD;
                    thePasswordLock = GordianPasswordLockASN1.getInstance(myTagged.getBaseObject());
                    theKeyPairLock = null;
                    break;
                case TAG_KEYPAIR:
                    theLockType = GordianZipLockType.KEYPAIR_PASSWORD;
                    thePasswordLock = null;
                    theKeyPairLock = GordianKeyPairLockASN1.getInstance(ASN1Sequence.getInstance(myTagged.getBaseObject()));
                    break;
                 default:
                     throw new GordianDataException("Unexpected tag# in ASN1 sequence");
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
     * @throws GordianException on error
     */
    public static GordianZipLockASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianZipLockASN1 myASN1) {
            return myASN1;
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
     * Obtain the passwordLock.
     * @return the passwordLock
     */
    public GordianPasswordLockASN1 getPasswordLockASN1() {
        return thePasswordLock;
    }

    /**
     * Obtain the keyPairLock.
     * @return the keyPairLock
     */
    public GordianKeyPairLockASN1 getKeyPairLockASN1() {
        return theKeyPairLock;
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
        switch (theLockType) {
            case KEYSET_PASSWORD:
                v.add(new DERTaggedObject(false, TAG_KEYSET, thePasswordLock));
                break;
            case FACTORY_PASSWORD:
                v.add(new DERTaggedObject(false, TAG_FACTORY, thePasswordLock));
                break;
            case KEYPAIR_PASSWORD:
            default:
                v.add(new DERTaggedObject(false, TAG_KEYPAIR, theKeyPairLock));
                break;
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
        return pThat instanceof GordianZipLockASN1 myThat
                && theLockType.equals(myThat.getLockType())
                && Objects.equals(thePasswordLock, myThat.getPasswordLockASN1())
                && Objects.equals(theKeyPairLock, myThat.getKeyPairLockASN1());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theLockType, thePasswordLock, theKeyPairLock);
    }
}
