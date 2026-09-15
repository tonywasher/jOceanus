/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cert;

import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertValidity;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import org.bouncycastle.asn1.x509.TBSCertificate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Certificate Validity implementation.
 */

public class GordianCoreCertValidity
        implements GordianCertValidity {
    /**
     * The Unlimited Validity.
     */
    public static final GordianCertValidity UNLIMITED = new GordianCoreCertValidity(null, null);

    /**
     * The notBefore time.
     */
    private final ZonedDateTime theNotBefore;

    /**
     * The notAfter time.
     */
    private final ZonedDateTime theNotAfter;

    /**
     * Constructor.
     *
     * @param pNotBefore the notBeforeTime
     * @param pNotAfter  the notAfter time
     */
    GordianCoreCertValidity(final ZonedDateTime pNotBefore,
                            final ZonedDateTime pNotAfter) {
        theNotBefore = pNotBefore == null ? null : pNotBefore.withZoneSameInstant(GordianBaseData.CLOCK.getZone());
        theNotAfter = pNotAfter == null ? null : pNotAfter.withZoneSameInstant(GordianBaseData.CLOCK.getZone());
    }

    @Override
    public ZonedDateTime notBefore() {
        return theNotBefore;
    }

    @Override
    public ZonedDateTime notAfter() {
        return theNotAfter;
    }

    @Override
    public boolean isValidNow() {
        final ZoneId myZone = GordianBaseData.CLOCK.getZone();
        return isValidAtDateTime(ZonedDateTime.now(myZone));
    }

    @Override
    public boolean isValidAtDateTime(final ZonedDateTime pDateTime) {
        final ZonedDateTime myDateTime = pDateTime.withZoneSameInstant(GordianBaseData.CLOCK.getZone());
        final boolean isBefore = theNotBefore != null && myDateTime.isBefore(theNotBefore);
        final boolean isAfter = theNotAfter != null && myDateTime.isAfter(theNotAfter);
        return !isBefore && !isAfter;
    }

    @Override
    public GordianCertValidity notBefore(final ZonedDateTime pNotBefore) throws GordianException {
        return new GordianCoreCertValidity(pNotBefore, theNotAfter);
    }

    @Override
    public GordianCertValidity notAfter(final ZonedDateTime pNotAfter) throws GordianException {
        return new GordianCoreCertValidity(theNotBefore, pNotAfter);
    }

    /**
     * Build the Validity from a TBSCertificate.
     *
     * @param pCertificate the TBS Certificate
     * @return the validity
     */
    static GordianCertValidity fromCertificate(final TBSCertificate pCertificate) {
        final Instant myStartInstant = pCertificate.getStartDate().getDate().toInstant();
        final Instant myEndInstant = pCertificate.getEndDate().getDate().toInstant();
        final ZonedDateTime myStart = myStartInstant.atZone(GordianBaseData.CLOCK.getZone());
        final ZonedDateTime myEnd = myEndInstant.atZone(GordianBaseData.CLOCK.getZone());
        return new GordianCoreCertValidity(myStart, myEnd);
    }

    /**
     * Restrict Validity.
     *
     * @param pValidity the restricting validity
     * @return the restricted validity
     */
    GordianCertValidity restrictValidity(final GordianCertValidity pValidity) {
        final ZonedDateTime myNotBefore = latest(theNotBefore, pValidity.notBefore());
        final ZonedDateTime myNotAfter = earliest(theNotAfter, pValidity.notAfter());
        return Objects.equals(theNotBefore, myNotBefore) && Objects.equals(theNotAfter, myNotAfter)
                ? this : new GordianCoreCertValidity(myNotBefore, myNotAfter);
    }

    /**
     * Obtain the latest of the two dateTimes.
     *
     * @param pFirst  the first dateTime.
     * @param pSecond the second dateTime
     * @return the latest dateTime
     */
    private static ZonedDateTime earliest(final ZonedDateTime pFirst,
                                          final ZonedDateTime pSecond) {
        if (pFirst == null) {
            return pSecond;
        }
        if (pSecond == null) {
            return pFirst;
        }
        return pFirst.isBefore(pSecond) ? pFirst : pSecond;
    }

    /**
     * Obtain the latest of the two dateTimes.
     *
     * @param pFirst  the first dateTime.
     * @param pSecond the second dateTime
     * @return the latest dateTime
     */
    private static ZonedDateTime latest(final ZonedDateTime pFirst,
                                        final ZonedDateTime pSecond) {
        if (pFirst == null) {
            return pSecond;
        }
        if (pSecond == null) {
            return pFirst;
        }
        return pFirst.isAfter(pSecond) ? pFirst : pSecond;
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
        return pThat instanceof GordianCoreCertValidity myThat
                && Objects.equals(theNotBefore, myThat.theNotBefore)
                && Objects.equals(theNotAfter, myThat.theNotAfter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theNotBefore, theNotAfter);
    }
}
