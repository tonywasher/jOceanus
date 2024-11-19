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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * ASN1 Encoding of keyPairLock.
 * <pre>
 * GordianKeyPairLockASN1 ::= SEQUENCE {
 *      agreement GordianAgreementMessageASN1
 *      lock GordianPasswordLockASN1
 * }
 * </pre>
 */
public class GordianKeyPairLockASN1
        extends GordianASN1Object {
    /**
     * The AgreementMessageASN1.
     */
    private final GordianAgreementMessageASN1 theAgreement;

    /**
     * The PasswordLockASN1.
     */
    private final GordianPasswordLockASN1 theLock;

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreement
     * @param pLock the passwordLock
     */
    public GordianKeyPairLockASN1(final GordianAgreementMessageASN1 pAgreement,
                                  final GordianPasswordLockASN1 pLock) {
        /* Store the Spec */
        theAgreement = pAgreement;
        theLock = pLock;
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
            theLock = GordianPasswordLockASN1.getInstance(en.nextElement());

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
     * Obtain the agreementASN1.
     * @return the agreementASN1
     */
    public GordianAgreementMessageASN1 getAgreement() {
        return theAgreement;
    }

    /**
     * Obtain the passwordLockASN1.
     * @return the passwordLockASN1
     */
    public GordianPasswordLockASN1 getPasswordLock() {
        return theLock;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theAgreement);
        v.add(theLock);
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
                && Objects.equals(getPasswordLock(), myThat.getPasswordLock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAgreement(), getPasswordLock());
    }
}
