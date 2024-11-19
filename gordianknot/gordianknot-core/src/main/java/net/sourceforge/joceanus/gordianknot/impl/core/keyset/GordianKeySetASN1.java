/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * ASN1 Encoding of KeySet.
 * <pre>
 * GordianKeySetASN1 ::= SEQUENCE {
 *      keySetSpec GordianKeySetSpecASN1
 *      keySet keySetDefinition
 * } securedKey
 *
 * keySetDefinition ::= SEQUENCE OF keyDefinition
 *
 * keyDefinition ::= SEQUENCE {
 *      keyId INTEGER
 *      key OCTET STRING
 * }
 * </pre>
 */
public class GordianKeySetASN1
        extends GordianASN1Object {
    /**
     * The keySet.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * The map of keyTypes to key.
     */
    private final Map<Integer, byte[]> theMap;

    /**
     * Create the ASN1 sequence.
     * @param pKeySet the keySet
     */
    GordianKeySetASN1(final GordianCoreKeySet pKeySet) {
        /* Store the KeySetSpec */
        theSpec = pKeySet.getKeySetSpec();

        /* Create the map */
        theMap = new LinkedHashMap<>();

        /* Loop through the keys placing keyBytes into the map */
        final Map<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> myMap = pKeySet.getSymKeyMap();
        for (Entry<GordianSymKeySpec, GordianKey<GordianSymKeySpec>> myEntry : myMap.entrySet()) {
            theMap.put(myEntry.getKey().getSymKeyType().ordinal() + 1,
                    ((GordianCoreKey<GordianSymKeySpec>) myEntry.getValue()).getKeyBytes());
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

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

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
        for (Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
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
     * @return the new keySet
     * @throws OceanusException on error
     */
    GordianCoreKeySet buildKeySet(final GordianCoreFactory pFactory) throws OceanusException {
        /* Create the new keySet */
        final GordianCoreKeySetFactory myKeySetFactory = (GordianCoreKeySetFactory) pFactory.getKeySetFactory();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        final GordianCoreKeySet myKeySet = myKeySetFactory.createKeySet(theSpec);

        /* Declare the keys */
        for (Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
            final GordianSymKeyType myKeyType = GordianSymKeyType.values()[myEntry.getKey() - 1];
            final GordianSymKeySpec mySpec = new GordianSymKeySpec(myKeyType, GordianLength.LEN_128, theSpec.getKeyLength());
            final GordianCoreKeyGenerator<GordianSymKeySpec> myGenerator =
                    (GordianCoreKeyGenerator<GordianSymKeySpec>) myCipherFactory.getKeyGenerator(mySpec);

            /* Generate and declare the key */
            final GordianKey<GordianSymKeySpec> myKey = myGenerator.buildKeyFromBytes(myEntry.getValue());
            myKeySet.declareSymKey(myKey);
        }

        /* Return the keySet */
        return myKeySet;
    }

    /**
     * Obtain the byte length for a given wrapped keyLength and # of keys.
     * @param pKeyLen the wrapped key length
     * @param pNumKeys the number of keys
     * @return the byte length
     */
    static int getEncodedLength(final int pKeyLen,
                                final int pNumKeys) {
        /* Key length is type + length + value */
        int myLength = GordianASN1Util.getLengthByteArrayField(pKeyLen);

        /* KeyType is guaranteed single byte value */
        myLength += GordianASN1Util.getLengthIntegerField(1);

        /* Calculate the length of the sequence */
        myLength = GordianASN1Util.getLengthSequence(myLength);

        /* We have pNumKeys of these in a sequence */
        myLength = GordianASN1Util.getLengthSequence(myLength * pNumKeys);

        /* Return the sequence length */
        return GordianASN1Util.getLengthSequence(myLength + GordianKeySetSpecASN1.getEncodedLength());
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

        /* Make sure that the classes are the same */
        if (!(pThat instanceof GordianKeySetASN1)) {
            return false;
        }
        final GordianKeySetASN1 myThat = (GordianKeySetASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theSpec, myThat.theSpec)
                && Objects.equals(theMap, myThat.theMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSpec, theMap);
    }
}
