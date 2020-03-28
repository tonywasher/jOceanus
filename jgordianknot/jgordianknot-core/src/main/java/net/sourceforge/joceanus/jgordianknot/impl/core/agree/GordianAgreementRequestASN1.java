/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.agree;

import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of Agreement Request.
 * <pre>
 * GordianAgreementRequestASN1 ::= SEQUENCE  {
 *      id AlgorithmIdentifier
 *      result AlgorithmIdentifier
 *      initVector OCTET STRING
 *      data OCTET STRING OPTIONAL
 * }
 * </pre>
 */
public class GordianAgreementRequestASN1
        extends GordianASN1Object {
    /**
     * The AgreementSpec.
     */
    private final AlgorithmIdentifier theAgreement;

    /**
     * The ResultType.
     */
    private final AlgorithmIdentifier theResultType;

    /**
     * The initVector.
     */
    private final byte[] theInitVector;

    /**
     * The Associated Data.
     */
    private final byte[] theData;

    /**
     * Create the ASN1 sequence.
     * @param pAgreement the agreementId
     * @param pResult the resultId
     * @param pInitVector theInitVector
     * @param pData the associated data
     */
    public GordianAgreementRequestASN1(final AlgorithmIdentifier pAgreement,
                                       final AlgorithmIdentifier pResult,
                                       final byte[] pInitVector,
                                       final byte[] pData) {
        /* Store the Details */
        theAgreement = pAgreement;
        theResultType = pResult;
        theInitVector = pInitVector;
        theData = pData;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianAgreementRequestASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            theAgreement = AlgorithmIdentifier.getInstance(en.nextElement());
            theResultType = AlgorithmIdentifier.getInstance(en.nextElement());
            theInitVector = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            theData = en.hasMoreElements()
                      ? ASN1OctetString.getInstance(en.nextElement()).getOctets()
                      : null;

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
    public static GordianAgreementRequestASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianAgreementRequestASN1) {
            return (GordianAgreementRequestASN1) pObject;
        } else if (pObject != null) {
            return new GordianAgreementRequestASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public AlgorithmIdentifier getAgreementId() {
        return theAgreement;
    }

    /**
     * Obtain the resultId.
     * @return the resultId
     */
    public AlgorithmIdentifier getResultId() {
        return theResultType;
    }

    /**
     * Obtain the initVector.
     * @return the initVector
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Obtain the data.
     * @return the data
     */
    public byte[] getData() {
        return theData;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theAgreement);
        v.add(theResultType);
        v.add(new BEROctetString(theInitVector));
        if (theData != null) {
            v.add(new BEROctetString(theData));
        }

        return new DERSequence(v);
    }
}
