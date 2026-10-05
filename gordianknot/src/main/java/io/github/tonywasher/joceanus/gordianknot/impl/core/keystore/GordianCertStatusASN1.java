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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keystore;

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
import java.util.HashMap;
import java.util.Map;

/**
 * ASN1 Encoding of CertificateAck.
 * <pre>
 * GordianCertStatusASN1 ::= SEQUENCE {
 *      GordianCertStatus   INTEGER
 *      GordianCertFailure  INTEGER OPTIONAL
 * }
 * </pre>
 */
public final class GordianCertStatusASN1
        extends GordianASN1Object {
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
    private GordianCertStatusASN1(final GordianCertStatus pStatus,
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
    private GordianCertStatusASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theStatus = GordianCertStatus.fromValue(ASN1Integer.getInstance(en.nextElement()).getValue().intValue());
            theFailure = en.hasMoreElements()
                    ? GordianCertFailure.fromValue(ASN1Integer.getInstance(en.nextElement()).getValue().intValue())
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
    public static GordianCertStatusASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertStatusASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertStatusASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Create the certificate status.
     *
     * @param pStatus the status
     * @return the status
     */
    public static GordianCertStatusASN1 createCertStatus(final GordianCertStatus pStatus) {
        return new GordianCertStatusASN1(pStatus, null);
    }

    /**
     * Create the certificate status.
     *
     * @param pStatus  the status
     * @param pFailure the failure
     * @return the status
     */
    public static GordianCertStatusASN1 createCertStatus(final GordianCertStatus pStatus,
                                                         final GordianCertFailure pFailure) {
        return new GordianCertStatusASN1(pStatus, pFailure);
    }

    /**
     * Obtain the status.
     *
     * @return the status
     */
    public GordianCertStatus getStatus() {
        return theStatus;
    }

    /**
     * Obtain the failure.
     *
     * @return the failure
     */
    public GordianCertFailure getFailure() {
        return theFailure;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(theStatus.getValue()));
        if (theFailure != null) {
            v.add(new ASN1Integer(theFailure.getValue()));
        }
        return new DERSequence(v);
    }

    /**
     * Status.
     */
    public enum GordianCertStatus {
        /**
         * Accepted.
         */
        ACCEPTED(0),

        /**
         * Granted with Mods
         */
        GRANTEDWITHMODS(1),

        /**
         * Rejection.
         */
        REJECTION(2);

        /**
         * The lookUp map.
         */
        private static final Map<Integer, GordianCertStatus> LOOKUP = createLookup();

        /**
         * The value.
         */
        private final int theValue;

        /**
         * Constructor.
         *
         * @param pValue the value
         */
        GordianCertStatus(final int pValue) {
            theValue = pValue;
        }

        /**
         * Obtain the value.
         *
         * @return the Value
         */
        public int getValue() {
            return theValue;
        }

        /**
         * Lookup the Enum from the value
         *
         * @param pValue the value
         * @return the enum (or null)
         */
        public static GordianCertStatus fromValue(final int pValue) {
            return LOOKUP.get(pValue);
        }

        /**
         * Create lookUp map.
         *
         * @return the lookUp map
         */
        private static Map<Integer, GordianCertStatus> createLookup() {
            final Map<Integer, GordianCertStatus> myMap = new HashMap<>();
            for (GordianCertStatus myStatus : GordianCertStatus.values()) {
                myMap.put(myStatus.getValue(), myStatus);
            }
            return myMap;
        }
    }

    /**
     * Failure.
     */
    public enum GordianCertFailure {
        /**
         * Bad Message.
         */
        BADMESSAGE(1),

        /**
         * Bad Usage.
         */
        BADUSAGE(19),

        /**
         * Bad ProofOfPossession.
         */
        BADPOP(9),

        /**
         * Duplicate Certificate.
         */
        DUPLICATE(26),

        /**
         * System Unavailable.
         */
        SYSTEMUNAVAIL(24),

        /**
         * System Failure.
         */
        SYSTEMFAIL(25);

        /**
         * The lookUp map.
         */
        private static final Map<Integer, GordianCertFailure> LOOKUP = createLookup();

        /**
         * The value.
         */
        private final int theValue;

        /**
         * Constructor.
         *
         * @param pValue the value
         */
        GordianCertFailure(final int pValue) {
            theValue = pValue;
        }

        /**
         * Obtain the value.
         *
         * @return the Value
         */
        public int getValue() {
            return theValue;
        }

        /**
         * Lookup the Enum from the value
         *
         * @param pValue the vaue
         * @return the enum (or null)
         */
        public static GordianCertFailure fromValue(final int pValue) {
            return LOOKUP.get(pValue);
        }

        /**
         * Create lookUp map.
         *
         * @return the lookUp map
         */
        private static Map<Integer, GordianCertFailure> createLookup() {
            final Map<Integer, GordianCertFailure> myMap = new HashMap<>();
            for (GordianCertFailure myFailure : GordianCertFailure.values()) {
                myMap.put(myFailure.getValue(), myFailure);
            }
            return myMap;
        }
    }
}
