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

package io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of CertificateStatus.
 * <pre>
 * GordianCertGatewayStatusASN1 ::= SEQUENCE {
 *      GordianCertStatus   INTEGER
 *      GordianCertFailure  INTEGER OPTIONAL
 * }
 * </pre>
 */
public final class GordianCertGatewayStatusASN1
        extends GordianASN1Object
        implements GordianCertGatewayStatus {
    /**
     * Bad Message.
     */
    private static final int FAILURE_BADMESSAGE = 1;

    /**
     * Bad POP.
     */
    private static final int FAILURE_BADPOP = 2;

    /**
     * Bad Usage.
     */
    private static final int FAILURE_BADUSAGE = 3;

    /**
     * Duplicate.
     */
    private static final int FAILURE_DUPLICATE = 4;

    /**
     * System Failure.
     */
    private static final int FAILURE_SYSTEMFAIL = 5;

    /**
     * System unavailable.
     */
    private static final int FAILURE_SYSTEMUNAVAIL = 6;

    /**
     * The status.
     */
    private final GordianCertStatus theStatus;

    /**
     * The failure.
     */
    private final GordianCertFailure theFailure;

    /**
     * Create the ASN1 sequence.
     *
     * @param pStatus  the status
     * @param pFailure the failure
     */
    private GordianCertGatewayStatusASN1(final GordianCertStatus pStatus,
                                         final GordianCertFailure pFailure) {
        /* Store the Details */
        theStatus = pStatus;
        theFailure = pFailure;
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertGatewayStatusASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theStatus = getStatusForValue(ASN1Integer.getInstance(en.nextElement()).getValue().intValue());
            theFailure = en.hasMoreElements()
                    ? getFailureForValue(ASN1Integer.getInstance(en.nextElement()).getValue().intValue())
                    : null;

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     *
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertGatewayStatusASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertGatewayStatusASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertGatewayStatusASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Create the certificate status.
     *
     * @param pStatus the status
     * @return the status
     */
    public static GordianCertGatewayStatusASN1 createCertStatus(final GordianCertStatus pStatus) {
        return new GordianCertGatewayStatusASN1(pStatus, null);
    }

    /**
     * Create the certificate status.
     *
     * @param pStatus  the status
     * @param pFailure the failure
     * @return the status
     */
    public static GordianCertGatewayStatusASN1 createCertStatus(final GordianCertStatus pStatus,
                                                                final GordianCertFailure pFailure) {
        return new GordianCertGatewayStatusASN1(pStatus, pFailure);
    }

    @Override
    public GordianCertStatus getStatus() {
        return theStatus;
    }

    @Override
    public GordianCertFailure getFailure() {
        return theFailure;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(getValueForStatus(theStatus)));
        if (theFailure != null) {
            v.add(new ASN1Integer(getValueForFailure(theFailure)));
        }
        return new DERSequence(v);
    }

    /**
     * Obtain the integer value for the status
     *
     * @param pStatus the status
     * @return the integer value
     */
    private static int getValueForStatus(final GordianCertStatus pStatus) {
        return switch (pStatus) {
            case ACCEPTED -> 0;
            case GRANTEDWITHMODS -> 1;
            case REJECTION -> 2;
        };
    }

    /**
     * Obtain the status for the integer
     *
     * @param pValue the value
     * @return the status
     * @throws GordianException on error
     */
    private static GordianCertStatus getStatusForValue(final int pValue) throws GordianException {
        for (GordianCertStatus myStatus : GordianCertStatus.values()) {
            if (getValueForStatus(myStatus) == pValue) {
                return myStatus;
            }
        }
        throw new GordianDataException("Unrecognised value: " + pValue);
    }

    /**
     * Obtain the integer value for the failure
     *
     * @param pFailure the failure
     * @return the integer value
     */
    private static int getValueForFailure(final GordianCertFailure pFailure) {
        return switch (pFailure) {
            case BADMESSAGE -> FAILURE_BADMESSAGE;
            case BADPOP -> FAILURE_BADMESSAGE;
            case BADUSAGE -> FAILURE_BADUSAGE;
            case DUPLICATE -> FAILURE_DUPLICATE;
            case SYSTEMFAIL -> FAILURE_SYSTEMFAIL;
            case SYSTEMUNAVAIL -> FAILURE_SYSTEMUNAVAIL;
        };
    }

    /**
     * Obtain the status for the integer
     *
     * @param pValue the value
     * @return the status
     * @throws GordianException on error
     */
    private static GordianCertFailure getFailureForValue(final int pValue) throws GordianException {
        for (GordianCertFailure myFailure : GordianCertFailure.values()) {
            if (getValueForFailure(myFailure) == pValue) {
                return myFailure;
            }
        }
        throw new GordianDataException("Unrecognised value: " + pValue);
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
        return pThat instanceof GordianCertGatewayStatusASN1 myThat
                && Objects.equals(theStatus, myThat.theStatus)
                && Objects.equals(theFailure, myThat.theFailure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theStatus, theFailure);
    }
}
