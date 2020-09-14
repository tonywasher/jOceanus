/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeyPairSetAgree.
 * <pre>
 * GordianKeyPairSetAgreeASN1 ::= SEQUENCE OF {
 *      algId AlgorithmIdentifier
 *      result AlgorithmIdentifier
 *      agreements agrees
 * }
 *
 * agreements ::= SEQUENCE OF OCTET STRINGS
 * </pre>
 */
public class GordianKeyPairSetAgreeASN1
        extends GordianASN1Object {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The ResultType.
     */
    private final AlgorithmIdentifier theResultType;

    /**
     * The messages.
     */
    private final List<byte[]> theMessages;

    /**
     * Create the ASN1 sequence.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @param pResult the resultId
     */
    public GordianKeyPairSetAgreeASN1(final GordianKeyPairSetSpec pKeyPairSetSpec,
                                      final AlgorithmIdentifier pResult) {
        /* Store the Spec */
        theSpec = pKeyPairSetSpec;
        theResultType = pResult;
        theMessages = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeyPairSetAgreeASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the list */
            theMessages = new ArrayList<>();

            /* Extract the parameters from the sequence */
            Enumeration<?> en = pSequence.getObjects();
            final AlgorithmIdentifier myId = AlgorithmIdentifier.getInstance(en.nextElement());
            theResultType = AlgorithmIdentifier.getInstance(en.nextElement());
            final ASN1Sequence myMsgs = ASN1Sequence.getInstance(en.nextElement());

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* Build the keyPairSetSpec */
            theSpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(myId);

            /* Build the list from the messages sequence */
            en = myMsgs.getObjects();
            while (en.hasMoreElements()) {
                final byte[] myBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();
                addMessage(myBytes);
            }

            /* Check that we have the right number of messages */
            if (theMessages.size() != theSpec.numKeyPairs()) {
                throw new GordianDataException("Invalid message");
            }

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
    public static GordianKeyPairSetAgreeASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeyPairSetAgreeASN1) {
            return (GordianKeyPairSetAgreeASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeyPairSetAgreeASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public GordianKeyPairSetSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the resultId.
     * @return the resultId
     */
    public AlgorithmIdentifier getResultId() {
        return theResultType;
    }

    /**
     * Obtain the iterator of the messages.
     * @return the iterator
     */
    public Iterator<byte[]> msgIterator() {
        return theMessages.iterator();
    }

    /**
     * Add a message to the list.
     * @param pMsg the message to add
     */
    void addMessage(final byte[] pMsg) {
        theMessages.add(pMsg);
    }

    /**
     * Obtain the Message.
     * @return the message
     */
    byte[] getEncodedMessage() throws OceanusException {
        try {
            return toASN1Primitive().getEncoded();
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode message", e);
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build the msgSequence */
        final ASN1EncodableVector ms = new ASN1EncodableVector();
        for (byte[] mySign : theMessages) {
            ms.add(new DEROctetString(mySign));
        }

        /* Build the overall sequence */
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(GordianKeyPairSetAlgId.determineAlgorithmId(theSpec).toASN1Primitive());
        v.add(theResultType);
        v.add(new DERSequence(ms));
        return new DERSequence(v);
    }
}
