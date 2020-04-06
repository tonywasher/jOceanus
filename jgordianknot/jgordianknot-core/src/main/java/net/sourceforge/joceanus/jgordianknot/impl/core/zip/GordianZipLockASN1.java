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
     * The keySetHashASN1.
     */
    private final GordianKeySetHashASN1 theKeySetHash;

    /**
     * The clientHelloASN1.
     */
    private final GordianAgreementClientHelloASN1 theClientHello;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash) {
        this(pKeySetHash, null);
    }

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHash the keySetHash
     * @param pClientHello the clientHello
     */
    public GordianZipLockASN1(final GordianKeySetHashASN1 pKeySetHash,
                              final GordianAgreementClientHelloASN1 pClientHello) {
        /* Store the Details */
        theKeySetHash = pKeySetHash;
        theClientHello = pClientHello;
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
            if (!Arrays.equals(myId, MSG_ID)) {
                throw new GordianDataException("Incorrect message type");
            }

            /* Access message parts */
            theKeySetHash = GordianKeySetHashASN1.getInstance(en.nextElement());
            theClientHello = en.hasMoreElements()
                      ? GordianAgreementClientHelloASN1.getInstance(en.nextElement())
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
    public static GordianZipLockASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianZipLockASN1) {
            return (GordianZipLockASN1) pObject;
        } else if (pObject != null) {
            return new GordianZipLockASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the keySetHash.
     * @return the keySetHash
     */
    public GordianKeySetHashASN1 getKeySetHash() {
        return theKeySetHash;
    }

    /**
     * Obtain the clientHello.
     * @return the clientHello
     */
    public GordianAgreementClientHelloASN1 getClientHello() {
        return theClientHello;
    }

    /**
     * Obtain the lockType.
     * @return the lockType
     */
    public GordianZipLockType getLockType() {
        return theClientHello == null
               ? GordianZipLockType.PASSWORD
               : GordianZipLockType.KEYPAIR_PASSWORD;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new BEROctetString(MSG_ID));
        v.add(theKeySetHash.toASN1Primitive());
        if (theClientHello != null) {
            v.add(theClientHello.toASN1Primitive());
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
                && Objects.equals(theClientHello, myThat.getClientHello());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySetHash, theClientHello);
    }
}
