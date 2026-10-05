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

import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayConfirm;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertFailure;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Certificate Gateway Confirm ASN1.
 * <pre>
 * GordianCertAckASN1 ::= SEQUENCE {
 *      certRespId      INTEGER
 *      status          GordianCertGatewayStatusASN1
 *      digestValue     OCTET STRING OPTIONAL
 * }
 * </pre>
 */

public final class GordianCertGatewayConfirmASN1
        extends GordianASN1Object
        implements GordianCertGatewayConfirm {
    /**
     * The responseId.
     */
    private final int theRespId;

    /**
     * The status.
     */
    private final GordianCertGatewayStatusASN1 theStatus;

    /**
     * The digestValue.
     */
    private final byte[] theDigestValue;

    /**
     * Create the ASN1 sequence.
     *
     * @param pRespId      the responseId
     * @param pStatus      the status
     * @param pDigestValue the digestValue
     */
    private GordianCertGatewayConfirmASN1(final int pRespId,
                                          final GordianCertGatewayStatusASN1 pStatus,
                                          final byte[] pDigestValue) {
        /* Store the Details */
        theRespId = pRespId;
        theStatus = pStatus;
        theDigestValue = pDigestValue == null ? null : pDigestValue.clone();
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertGatewayConfirmASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the responseId from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theRespId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            theStatus = GordianCertGatewayStatusASN1.getInstance(en.nextElement());

            /* Extract the digestValue from the sequence */
            theDigestValue = en.hasMoreElements() ? ASN1OctetString.getInstance(en.nextElement()).getOctets() : null;

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
    public static GordianCertGatewayConfirmASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertGatewayConfirmASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertGatewayConfirmASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Parse the ASN1 Tagged object.
     *
     * @param pObject   the object to parse
     * @param pExplicit is the tag explicit?
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertGatewayConfirmASN1 getInstance(final ASN1TaggedObject pObject,
                                                            final boolean pExplicit) throws GordianException {
        return getInstance(ASN1Sequence.getInstance(pObject, pExplicit));
    }

    /**
     * Create the certificate confirm.
     *
     * @param pRespId  the responseId
     * @param pFailure the failure code
     * @return the response
     */
    public static GordianCertGatewayConfirmASN1 createCertConfirm(final int pRespId,
                                                                  final GordianCertFailure pFailure) {
        /* Create the status */
        final GordianCertGatewayStatusASN1 myStatus = GordianCertGatewayStatusASN1.createCertStatus(pFailure);

        /* Return the ASN1 */
        return new GordianCertGatewayConfirmASN1(pRespId, myStatus, null);
    }

    /**
     * Create the certificate confirm.
     *
     * @param pRespId      the responseId
     * @param pDigestValue the digestValue
     * @return the response
     */
    public static GordianCertGatewayConfirmASN1 createCertConfirm(final int pRespId,
                                                                  final byte[] pDigestValue) {
        /* Create the status */
        final GordianCertGatewayStatusASN1 myStatus = GordianCertGatewayStatusASN1.createCertStatus();

        /* Return the ASN1 */
        return new GordianCertGatewayConfirmASN1(pRespId, myStatus, pDigestValue);
    }

    @Override
    public int getResponseId() {
        return theRespId;
    }

    @Override
    public GordianCertGatewayStatusASN1 getStatus() {
        return theStatus;
    }

    /**
     * Obtain the digestValue.
     *
     * @return the digestValue
     */
    public byte[] getDigestValue() {
        return theDigestValue;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(theRespId));
        v.add(theStatus);
        if (theDigestValue != null) {
            v.add(new DEROctetString(theDigestValue));
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
        return pThat instanceof GordianCertGatewayConfirmASN1 myThat
                && Objects.equals(theRespId, myThat.theRespId)
                && Objects.equals(theStatus, myThat.theStatus)
                && Arrays.equals(theDigestValue, myThat.theDigestValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theStatus, theStatus, Arrays.hashCode(theDigestValue));
    }
}
