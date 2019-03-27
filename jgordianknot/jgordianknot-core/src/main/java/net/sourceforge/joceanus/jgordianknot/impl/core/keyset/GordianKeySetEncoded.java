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
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySet.
 */
public class GordianKeySetEncoded
        extends ASN1Object {
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
    GordianKeySetEncoded(final GordianCoreKeySet pKeySet,
                         final GordianKeySet pWrapper) throws OceanusException {
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
    private GordianKeySetEncoded(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the map */
            theMap = new LinkedHashMap<>();

            /* Build the map from the sequence */
            final Enumeration e = pSequence.getObjects();
            while (e.hasMoreElements()) {
                final ASN1Sequence k = ASN1Sequence.getInstance(e.nextElement());
                final Enumeration ek = k.getObjects();
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
    public static GordianKeySetEncoded getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeySetEncoded) {
            return (GordianKeySetEncoded) pObject;
        } else if (pObject != null) {
            return new GordianKeySetEncoded(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * GordianKeySetEncoded ::= SEQUENCE OF securedKey
     *
     * securedKey ::= SEQUENCE {
     *      wrappedKeyId INTEGER
     *      wrappedKey OCTET STRING
     * }
     * </pre>
     * @return the ASN1 Encoding
     */
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();

        for (Map.Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
            final ASN1EncodableVector k = new ASN1EncodableVector();
            k.add(new ASN1Integer(myEntry.getKey()));
            k.add(new DEROctetString(myEntry.getValue()));
            v.add(new DERSequence(k).toASN1Primitive());
        }

        return new DERSequence(v);
    }

    /**
     * Derive the keySet from the details.
     * @param pKeySet the keySet to populate
     * @param pWrapper the wrapping keySet
     * @throws OceanusException on error
     */
    void populateKeySet(final GordianCoreKeySet pKeySet,
                        final GordianKeySet pWrapper) throws OceanusException {
        for (Map.Entry<Integer, byte[]> myEntry : theMap.entrySet()) {
            final GordianSymKeyType myKeyType = GordianSymKeyType.values()[myEntry.getKey() - 1];
            final GordianSymKeySpec mySpec = new GordianSymKeySpec(myKeyType, GordianLength.LEN_128);
            final GordianKey<GordianSymKeySpec> myKey = pWrapper.deriveKey(myEntry.getValue(), mySpec);
            pKeySet.declareSymKey(myKey);
        }
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
        int myLength = pWrappedKeyLen + 1 + getLengthLength(pWrappedKeyLen);

        /* KeyType has type + length + value (all single byte) */
        myLength += 2 + getLengthLength(1);

        /* Hdr has type + length */
        myLength += 1 + getLengthLength(myLength);

        /* We have pNumKeys of these */
        myLength *= pNumKeys;

        /* Sequence hdr is type + length + data */
        myLength += 1 + getLengthLength(myLength);
        return myLength;
    }

    /**
     * Obtain the byte Length of an encoded length value.
     * @param pLength the length
     * @return the byte length
     */
    private static int getLengthLength(final int pLength) {
        /* Handle small lengths */
        if (pLength <= Byte.MAX_VALUE) {
            return 1;
        }

        /*  Loop while we work out the length */
        int myLen = pLength >> Byte.SIZE;
        int myResult = 2;
        while (myLen > 0) {
            myResult++;
            myLen >>= Byte.SIZE;
        }

        /* Return the length */
        return myResult;
    }
}
