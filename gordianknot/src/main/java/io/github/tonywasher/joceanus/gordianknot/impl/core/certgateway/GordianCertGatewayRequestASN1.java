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

import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayRequest;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.crmf.CertReqMsg;

/**
 * Certificate Gateway Request ASN1.
 * <pre>
 * GordianCertResponseASN1 ::= SEQUENCE {
 *      certReq     CertReqMsg
 * }
 * </pre>
 */
public class GordianCertGatewayRequestASN1
        extends GordianASN1Object
        implements GordianCertGatewayRequest {
    /**
     * The certificate request.
     */
    private final CertReqMsg theRequest;

    /**
     * Create the ASN1 sequence.
     *
     * @param pRequest the request
     */
    GordianCertGatewayRequestASN1(final CertReqMsg pRequest) {
        /* Store the Details */
        theRequest = pRequest;
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertGatewayRequestASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            theRequest = CertReqMsg.getInstance(pSequence);

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
    public static GordianCertGatewayRequestASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertGatewayRequestASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertGatewayRequestASN1(ASN1Sequence.getInstance(pObject));
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
    public static GordianCertGatewayRequestASN1 getInstance(final ASN1TaggedObject pObject,
                                                            final boolean pExplicit) throws GordianException {
        return getInstance(ASN1Sequence.getInstance(pObject, pExplicit));
    }

    @Override
    public CertReqMsg getCertificateRequest() {
        return theRequest;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return theRequest.toASN1Primitive();
    }
}
