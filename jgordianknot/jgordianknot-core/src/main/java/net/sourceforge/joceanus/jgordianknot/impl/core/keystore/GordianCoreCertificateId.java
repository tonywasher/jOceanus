/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.util.Objects;

import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificateId;

/**
 * Certificate Id implementation.
 */
public final class GordianCoreCertificateId
        implements GordianCertificateId {
    /**
     * The Name.
     */
    private final X500Name theName;

    /**
     * The ID.
     */
    private final DERBitString theId;

    /**
     * Constructor.
     * @param pName the name
     * @param pId the id
     */
    GordianCoreCertificateId(final X500Name pName,
                             final DERBitString pId) {
        theName = pName;
        theId = pId;
    }

    /**
     * Obtain the issuer Id for a certificate.
     * @param pCertificate the certificate
     * @return get the issuer id
     */
    public static GordianCoreCertificateId getSubjectId(final GordianCoreCertificate pCertificate) {
        return new GordianCoreCertificateId(pCertificate.getSubjectName(), DERBitString.convert(pCertificate.getSubjectId()));
    }

    /**
     * Obtain the issuer Id for a certificate.
     * @param pCertificate the certificate
     * @return get the issuer id
     */
    public static GordianCoreCertificateId getIssuerId(final GordianCoreCertificate pCertificate) {
        return new GordianCoreCertificateId(pCertificate.getIssuerName(), DERBitString.convert(pCertificate.getIssuerId()));
    }

    @Override
    public X500Name getName() {
        return theName;
    }

    @Override
    public DERBitString getId() {
        return theId;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial case */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Ensure object is correct class */
        if (!(pThat instanceof GordianCoreCertificateId)) {
            return false;
        }
        final GordianCoreCertificateId myThat = (GordianCoreCertificateId) pThat;

        /* Compare fields */
        return theName.equals(myThat.getName())
                && Objects.equals(theId, myThat.getId());
    }

    @Override
    public int hashCode() {
        return theName.hashCode()
                + (theId == null
                   ? 0
                   : theId.hashCode());
    }
}
