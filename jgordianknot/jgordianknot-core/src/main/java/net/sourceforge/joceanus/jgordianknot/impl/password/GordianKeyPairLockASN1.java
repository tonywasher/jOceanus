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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of keyPairLock.
 * <pre>
 * GordianKeyPairLockASN1 ::= SEQUENCE {
 *      agreement GordianAgreementMessageASN1
 *      lockBytes OCTET STRING
 * }
 * </pre>
 */
public class GordianKeyPairLockASN1
        extends GordianASN1Object {
    /**
     * The PasswordLockSpec.
     */
    private final GordianAgreementMessageASN1 theAgreement;

    /**
     * The lockBytes.
     */
    private final byte[] theLockBytes;

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreement
     * @param pLockBytes the hash bytes
     */
    public GordianKeyPairLockASN1(final GordianAgreementMessageASN1 pAgreement,
                                  final byte[] pLockBytes) {
        /* Store the Spec */
        theAgreement = pAgreement;
        theLockBytes = pLockBytes;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    public GordianKeyPairLockASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theAgreement = GordianAgreementMessageASN1.getInstance(en.nextElement());
            theLockBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();

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
    public static GordianKeyPairLockASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeyPairLockASN1) {
            return (GordianKeyPairLockASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeyPairLockASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the agreement.
     * @return the agreement
     */
    public GordianAgreementMessageASN1 getAgreement() {
        return theAgreement;
    }

    /**
     * Obtain the lockBytes.
     * @return the lockBytes
     */
    public byte[] getLockBytes() {
        return theLockBytes;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theAgreement);
        v.add(new DEROctetString(theLockBytes));
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
        if (!(pThat instanceof GordianKeyPairLockASN1)) {
            return false;
        }
        final GordianKeyPairLockASN1 myThat = (GordianKeyPairLockASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theAgreement, myThat.getAgreement())
                && Arrays.equals(getLockBytes(), myThat.getLockBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreement())
                + Arrays.hashCode(getLockBytes());
    }
}
