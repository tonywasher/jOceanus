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
import java.util.LinkedHashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySet.
 * <pre>
 * GordianKeySetASN1 ::= SEQUENCE OF {
 *      keySetSpec GordianKeySetSpecASN1
 *      securedKeys securedKeySet
 * } securedKey
 *
 * securedKeySet ::= SEQUENCE OF securedKey
 *
 * securedKey ::= SEQUENCE {
 *      wrappedKeyId INTEGER
 *      wrappedKey OCTET STRING
 * }
 * </pre>
 */
public class GordianKeySetASN1
        extends ASN1Object {
    /**
     * The keySetSpec.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * The map of keyTypes to secured key.
     */
    private final Map<Integer, byte[]> theMap;

    /**
     * Create the ASN1 sequence.
     * @param pKeySet the keySet
     * @param pWrapper the wrapping keySet
     * @throws OceanusException on error
     */
    GordianKeySetASN1(final GordianCoreKeySet pKeySet,
                      final GordianKeySet pWrapper) throws OceanusException {
        /* Store the Spec */
        theSpec = pKeySet.getKeySetSpec();

        /* Create the map */
        theMap = new LinkedHashMap<>();

        /* Loop through the keys placing wrapped keys into the map */
        final Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> myMap = pKeySet.getSymKeyMap();
        for (Map.Entry<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> myEntry : myMap.entrySet()) {
            theMap.put(myEntry.getKey().getSymKeyType().ordinal() + 1,
                    pWrapper.secureKey(myEntry.getValue()));
        }
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeySetASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the map */
            theMap = new LinkedHashMap<>();

            /* Build the map from the sequence */
            Enumeration<?> en = pSequence.getObjects();
            theSpec = GordianKeySetSpecASN1.getInstance(en.nextElement()).getSpec();
            final ASN1Sequence myKeySet = ASN1Sequence.getInstance(en.nextElement());

            /* Build the map from the keySet sequence */
            en = myKeySet.getObjects();
            while (en.hasMoreElements()) {
                final ASN1Sequence k = ASN1Sequence.getInstance(en.nextElement());
                final Enumeration<?> ek = k.getObjects();
                theMap.put(ASN1Integer.getInstance(ek.nextElement()).getValue().intValue(),
                        ASN1OctetString.getInstance(ek.nextElement()).getOctets());
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
    public static GordianKeySetASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeySetASN1) {
            return (GordianKeySetASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeySetASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build the keySetSequence */
        final ASN1EncodableVector ks = new ASN1EncodableVector();
        for (Map.Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
            final ASN1EncodableVector k = new ASN1EncodableVector();
            k.add(new ASN1Integer(myEntry.getKey()));
            k.add(new DEROctetString(myEntry.getValue()));
            ks.add(new DERSequence(k).toASN1Primitive());
        }

        /* Build the overall sequence */
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GordianKeySetSpecASN1(theSpec).toASN1Primitive());
        v.add(new DERSequence(ks));
        return new DERSequence(v);
    }

    /**
     * Build a keySet from the details.
     * @param pFactory the keySet factory
     * @param pWrapper the wrapping keySet
     * @return the new keySet
     * @throws OceanusException on error
     */
    GordianCoreKeySet buildKeySet(final GordianCoreKeySetFactory pFactory,
                                  final GordianKeySet pWrapper) throws OceanusException {
        /* Create the new keySet */
        final GordianCoreKeySet myKeySet = pFactory.createKeySet(theSpec);

        /* Declare the keys */
        for (Map.Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
            final GordianSymKeyType myKeyType = GordianSymKeyType.values()[myEntry.getKey() - 1];
            final GordianSymKeySpec mySpec = new GordianSymKeySpec(myKeyType, GordianLength.LEN_128, theSpec.getKeyLength());
            final GordianKey<GordianSymKeySpec> myKey = pWrapper.deriveKey(myEntry.getValue(), mySpec);
            myKeySet.declareSymKey(myKey);
        }

        /* Return the keySet */
        return myKeySet;
    }

    /**
     * Obtain the byte length for a given wrapped keyLength and # of keys.
     * @param pWrappedKeyLen the wrapped key length
     * @param pNumKeys the number of keys
     * @return the byte length
     */
    static int getEncodedLength(final int pWrappedKeyLen,
                                final int pNumKeys) {
        /* Key length is type + length + value */
        int myLength = GordianASN1Util.getLengthByteArrayField(pWrappedKeyLen);

        /* KeyType is guaranteed single byte value */
        myLength += GordianASN1Util.getLengthIntegerField(1);

        /* Calculate the length of the sequence */
        myLength = GordianASN1Util.getLengthSequence(myLength);

        /* We have pNumKeys of these in a sequence */
        myLength = GordianASN1Util.getLengthSequence(myLength * pNumKeys);

        /* Return the sequence length */
        return GordianASN1Util.getLengthSequence(myLength + GordianKeySetSpecASN1.getEncodedLength());
    }
}
