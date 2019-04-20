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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of Parameters.
 */
public class GordianParametersASN1
        extends ASN1Object {
    /**
     * Base our algorithmId off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier PARAMSALGID = GordianCoreKeySetFactory.KEYSETOID.branch("2");

    /**
     * The Parameters.
     */
    private final GordianParameters theParams;

    /**
     * Create the ASN1 sequence.
     * @param pParameters the parameters
     */
    public GordianParametersASN1(final GordianParameters pParameters) {
        /* Store the Params */
        theParams = pParameters;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianParametersASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration e = pSequence.getObjects();
            final boolean isBC = ASN1Boolean.getInstance(e.nextElement()).isTrue();
            final int myIterations = ASN1Integer.getInstance(e.nextElement()).getValue().intValue();
            final GordianFactoryType myType = isBC
                                              ? GordianFactoryType.BC
                                              : GordianFactoryType.JCA;

            /* Create the parameters */
            theParams = new GordianParameters(myType);
            theParams.setKIterations(myIterations);

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
    public static GordianParametersASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianParametersASN1) {
            return (GordianParametersASN1) pObject;
        } else if (pObject != null) {
            return new GordianParametersASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the parameters.
     * @return the params
     */
    public GordianParameters getParameters() {
        return theParams;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * GordianParametersASN1 ::= SEQUENCE  {
     *      bouncyCastle BOOLEAN
     *      numIterations INTEGER
     * }
     * </pre>
     * @return the ASN1 Encoding
     */
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theParams.getFactoryType() == GordianFactoryType.BC
                    ? ASN1Boolean.TRUE
                    : ASN1Boolean.FALSE);
        v.add(new ASN1Integer(theParams.getKIterations()));

        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    static int getEncodedLength() {
        /* Factory Type has type  + value (all single byte) */
        int myLength  = 2;

        /* Iterations has type + length + value (all single byte) */
        myLength += GordianKeySetASN1.getLengthIntegerField(1);

        /* Calculate the length of the sequence */
        return  GordianKeySetASN1.getLengthSequence(myLength);
    }

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return new AlgorithmIdentifier(PARAMSALGID, toASN1Primitive());
    }
}
