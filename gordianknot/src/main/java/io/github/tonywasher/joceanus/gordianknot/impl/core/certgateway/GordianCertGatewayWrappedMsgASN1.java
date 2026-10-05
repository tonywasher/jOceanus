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
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayMsg;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayRequest;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayResponse;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayWrappedMsg;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

import java.util.Enumeration;

/**
 * Certificate Gateway Wrapped Message ASN1.
 * <pre>
 * GordianCertResponseASN1 ::= SEQUENCE {
 *      msgType     INTEGER
 *      CHOICE {
 *          request     [1] GordianCertGatewayRequestASN1,
 *          response    [2] GordianCertGatewayResponseASN1
 *          confirm     [3] GordianCertGatewayConfirmASN1
 *      }
 * }
 * </pre>
 */
public final class GordianCertGatewayWrappedMsgASN1
        extends GordianASN1Object
        implements GordianCertGatewayWrappedMsg {
    /**
     * Request Tag.
     */
    private static final int TAG_REQUEST = 1;

    /**
     * Response Tag.
     */
    private static final int TAG_RESPONSE = 2;

    /**
     * Confirm Tag.
     */
    private static final int TAG_CONFIRM = 3;

    /**
     * The messageType.
     */
    private final GordianCertGatewayMessageType theType;

    /**
     * The request.
     */
    private final GordianCertGatewayRequestASN1 theRequest;

    /**
     * The response.
     */
    private final GordianCertGatewayResponseASN1 theResponse;

    /**
     * The confirm.
     */
    private final GordianCertGatewayConfirmASN1 theConfirm;

    /**
     * Create the ASN1 sequence.
     *
     * @param pRequest the request
     */
    private GordianCertGatewayWrappedMsgASN1(final GordianCertGatewayRequestASN1 pRequest) {
        theType = GordianCertGatewayMessageType.CERTREQUEST;
        theRequest = pRequest;
        theResponse = null;
        theConfirm = null;
    }

    /**
     * Create the ASN1 sequence.
     *
     * @param pResponse the response
     */
    private GordianCertGatewayWrappedMsgASN1(final GordianCertGatewayResponseASN1 pResponse) {
        theType = GordianCertGatewayMessageType.CERTRESPONSE;
        theRequest = null;
        theResponse = pResponse;
        theConfirm = null;
    }

    /**
     * Create the ASN1 sequence.
     *
     * @param pConfirm the confirmation
     */
    private GordianCertGatewayWrappedMsgASN1(final GordianCertGatewayConfirmASN1 pConfirm) {
        theType = GordianCertGatewayMessageType.CERTCONFIRM;
        theRequest = null;
        theResponse = null;
        theConfirm = pConfirm;
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertGatewayWrappedMsgASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theType = getTypeForValue(ASN1Integer.getInstance(en.nextElement()).getValue().intValue());

            /* Extract the certificate from the sequence */
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            switch (myTagged.getTagNo()) {
                case TAG_REQUEST:
                    theRequest = GordianCertGatewayRequestASN1.getInstance(myTagged, false);
                    theResponse = null;
                    theConfirm = null;
                    break;
                case TAG_RESPONSE:
                    theRequest = null;
                    theResponse = GordianCertGatewayResponseASN1.getInstance(myTagged, false);
                    theConfirm = null;
                    break;
                case TAG_CONFIRM:
                    theRequest = null;
                    theResponse = null;
                    theConfirm = GordianCertGatewayConfirmASN1.getInstance(myTagged, false);
                    break;
                default:
                    throw new GordianDataException("Unexpected tag");
            }

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
    public static GordianCertGatewayWrappedMsgASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertGatewayWrappedMsgASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertGatewayWrappedMsgASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Create the wrapped message.
     *
     * @param pMessage the message
     * @return the wrapped message
     * @throws GordianException on error
     */
    public static GordianCertGatewayWrappedMsgASN1 createWrappedMessage(final GordianCertGatewayMsg pMessage) throws GordianException {
        return switch (pMessage) {
            case GordianCertGatewayRequestASN1 myReq -> new GordianCertGatewayWrappedMsgASN1(myReq);
            case GordianCertGatewayResponseASN1 myResp -> new GordianCertGatewayWrappedMsgASN1(myResp);
            case GordianCertGatewayConfirmASN1 myConf -> new GordianCertGatewayWrappedMsgASN1(myConf);
            default -> throw new GordianDataException("Unexpected message type");
        };
    }

    @Override
    public GordianCertGatewayMessageType getMessageType() {
        return theType;
    }

    @Override
    public GordianCertGatewayRequest getRequest() {
        return theRequest;
    }

    @Override
    public GordianCertGatewayResponse getResponse() {
        return theResponse;
    }

    @Override
    public GordianCertGatewayConfirm getConfirm() {
        return theConfirm;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(getValueForType(theType)));
        if (theRequest != null) {
            v.add(new DERTaggedObject(false, TAG_REQUEST, theRequest));
        } else if (theResponse != null) {
            v.add(new DERTaggedObject(false, TAG_RESPONSE, theResponse));
        } else if (theConfirm != null) {
            v.add(new DERTaggedObject(false, TAG_CONFIRM, theConfirm));
        }
        return new DERSequence(v);
    }

    /**
     * Obtain the integer value for the type
     *
     * @param pType the msgType
     * @return the integer value
     */
    private static int getValueForType(final GordianCertGatewayMessageType pType) {
        return switch (pType) {
            case CERTREQUEST -> TAG_REQUEST;
            case CERTRESPONSE -> TAG_RESPONSE;
            case CERTCONFIRM -> TAG_CONFIRM;
        };
    }

    /**
     * Obtain the msgType for the integer
     *
     * @param pValue the value
     * @return the msgType
     * @throws GordianException on error
     */
    private static GordianCertGatewayMessageType getTypeForValue(final int pValue) throws GordianException {
        for (GordianCertGatewayMessageType myType : GordianCertGatewayMessageType.values()) {
            if (getValueForType(myType) == pValue) {
                return myType;
            }
        }
        throw new GordianDataException("Unrecognised value: " + pValue);
    }
}
