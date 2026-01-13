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
package net.sourceforge.joceanus.gordianknot.impl.core.cert;

import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificateId;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.Arrays;
import java.util.Objects;

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
    private final byte[] theId;

    /**
     * Constructor.
     *
     * @param pName the name
     * @param pId   the id
     */
    public GordianCoreCertificateId(final X500Name pName,
                                    final byte[] pId) {
        theName = pName;
        theId = pId == null ? null : pId.clone();
    }

    @Override
    public X500Name getName() {
        return theName;
    }

    @Override
    public byte[] getId() {
        return theId == null ? null : theId.clone();
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
        if (!(pThat instanceof GordianCoreCertificateId myThat)) {
            return false;
        }

        /* Compare fields */
        return theName.equals(myThat.getName())
                && Arrays.equals(theId, myThat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theName, Arrays.hashCode(theId));
    }
}
