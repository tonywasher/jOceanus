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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Security Id Manager.
 */
public class GordianIdManager {
    /**
     * The Id Cache Map.
     */
    private final Map<GordianLength, GordianIdCache> theCacheMap;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pKeySetFactory the keySet factory
     */
    GordianIdManager(final GordianCoreFactory pFactory,
                     final GordianCoreKeySetFactory pKeySetFactory) {
        /* Create the map */
        theCacheMap = new EnumMap<>(GordianLength.class);

        /* Loop through the KeyLengths */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            /* Build the appropriate cache */
            final GordianLength myLength = myIterator.next();
            final GordianIdCache myCache = new GordianIdCache(pFactory, myLength, pKeySetFactory);
            theCacheMap.put(myLength, myCache);
        }
    }

    /**
     * Obtain random SymKeyType.
     * @param pKeyLen the keyLength
     * @return the random symKeyType
     */
    public GordianSymKeyType generateRandomSymKeyType(final GordianLength pKeyLen) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.generateRandomSymKeyType();
    }

    /**
     * Obtain set of random keySet SymKeyTypes.
     * @param pKeyLen the keyLength
     * @param pCount the count
     * @return the random symKeyTypes
     */
    public GordianSymKeyType[] generateRandomKeySetSymKeyTypes(final GordianLength pKeyLen,
                                                               final int pCount) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.generateRandomKeySetSymKeyTypes(pCount);
    }

    /**
     * Derive set of keySet SymKeyTypes from seed.
     * @param pKeyLen the keyLength
     * @param pSeed the seed
     * @param pKeyTypes the array of symKeyTypes to be filled in
     * @return the remaining seed
     */
    int deriveKeySetSymKeyTypesFromSeed(final GordianLength pKeyLen,
                                        final int pSeed,
                                        final GordianSymKeyType[] pKeyTypes) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.deriveKeySetSymKeyTypesFromSeed(pSeed, pKeyTypes);
    }

    /**
     * Obtain random StreamKeyType.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for for parsing large amounts of data
     * @return the random streamKeyType
     */
    public GordianStreamKeyType generateRandomStreamKeyType(final GordianLength pKeyLen,
                                                            final boolean pLargeData) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.generateRandomStreamKeyType(pLargeData);
     }

    /**
     * Obtain random DigestType.
     * @return the random digestType
     */
    public GordianDigestType generateRandomDigestType() {
        /* Utilise the 128bit cache */
        final GordianIdCache myCache = theCacheMap.get(GordianLength.LEN_128);
        return myCache.generateRandomDigestType();
    }

    /**
     * Obtain random hMacDigestType.
     * @return the random digestType
     */
    private GordianDigestType generateRandomHMacDigestType() {
        /* Utilise the 128bit cache */
        final GordianIdCache myCache = theCacheMap.get(GordianLength.LEN_128);
        return myCache.generateRandomHMacDigestType();
    }

    /**
     * Derive set of keyHashDigestTypes from seed.
     * @param pSeed the seed
     * @param pDigestTypes the array of digestTypes to be filled in
     * @return the remaining seed
     */
    public int deriveKeyHashDigestTypesFromSeed(final int pSeed,
                                                final GordianDigestType[] pDigestTypes) {
        /* Utilise the 128bit cache */
        final GordianIdCache myCache = theCacheMap.get(GordianLength.LEN_128);
        return myCache.deriveKeyHashDigestTypesFromSeed(pSeed, pDigestTypes);
    }

    /**
     * Derive set of standard externalDigestTypes from seed.
     * @param pSeed the seed
     * @param pDigestTypes the array of digestTypes to be filled in
     * @return the remaining seed
     */
    int deriveExternalDigestTypesFromSeed(final int pSeed,
                                          final GordianDigestType[] pDigestTypes) {
        /* Utilise the 128bit cache */
        final GordianIdCache myCache = theCacheMap.get(GordianLength.LEN_128);
        return myCache.deriveExternalDigestTypesFromSeed(pSeed, pDigestTypes);
    }

    /**
     * generate random GordianMacSpec.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for for parsing large amounts of data
     * @return the new MacSpec
     */
    public GordianMacSpec generateRandomMacSpec(final GordianLength pKeyLen,
                                                final boolean pLargeData) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.generateRandomMacSpec(pLargeData);
    }
}
