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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Id Manager.
 */
public class GordianIdManager {
    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Id Cache Map.
     */
    private final Map<GordianLength, GordianIdCache> theCacheMap;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @throws OceanusException on error
     */
    GordianIdManager(final GordianCoreFactory pFactory) throws OceanusException  {
        /* Store the factory */
        theFactory = pFactory;

        /* Create the map */
        theCacheMap = new EnumMap<>(GordianLength.class);

        /* Loop through the KeyLengths */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            /* Build the appropriate cache */
            final GordianLength myLength = myIterator.next();
            final GordianIdCache myCache = new GordianIdCache(pFactory, myLength);
            theCacheMap.put(myLength, myCache);
        }
    }

    /**
     * Obtain random SymKeySpec.
     * @param pKeyLen the keyLength
     * @return the random symKeySpec
     */
    public GordianSymKeySpec generateRandomSymKeySpec(final GordianLength pKeyLen) {
        /* Access the list to select from */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final List<GordianSymKeySpec> myValid = myCiphers.listAllSupportedSymKeySpecs(pKeyLen);

        /* Determine a random index into the list and return the spec */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        final int myIndex = myRandom.nextInt(myValid.size());
        return myValid.get(myIndex);
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
    public int deriveKeySetSymKeyTypesFromSeed(final GordianLength pKeyLen,
                                               final int pSeed,
                                               final GordianSymKeyType[] pKeyTypes) {
        /* Utilise the relevant cache */
        final GordianIdCache myCache = theCacheMap.get(pKeyLen);
        return myCache.deriveKeySetSymKeyTypesFromSeed(pSeed, pKeyTypes);
    }

    /**
     * Obtain random StreamKeySpec.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Key that is suitable for processing large amounts of data
     * @return the random streamKeySpec
     */
    public GordianStreamKeySpec generateRandomStreamKeySpec(final GordianLength pKeyLen,
                                                            final boolean pLargeData) {
        /* Access the list to select from */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        List<GordianStreamKeySpec> myValid = myCiphers.listAllSupportedStreamKeySpecs(pKeyLen);
        if (pLargeData) {
            myValid = myValid.stream().filter(s -> s.getStreamKeyType().supportsLargeData()).collect(Collectors.toList());
        }

        /* Determine a random index into the list and return the spec */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        final int myIndex = myRandom.nextInt(myValid.size());
        return myValid.get(myIndex);
     }

    /**
     * Obtain random DigestSpec.
     * @param pLargeData only generate a Digest that is suitable for processing large amounts of data
     * @return the random digestSpec
     */
    public GordianDigestSpec generateRandomDigestSpec(final boolean pLargeData) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        List<GordianDigestSpec> myValid = myDigests.listAllSupportedSpecs();
        if (pLargeData) {
            myValid = myValid.stream().filter(s -> s.getDigestType().supportsLargeData()).collect(Collectors.toList());
        }

        /* Determine a random index into the list and return the spec */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        final int myIndex = myRandom.nextInt(myValid.size());
        return myValid.get(myIndex);
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
    public int deriveExternalDigestTypesFromSeed(final int pSeed,
                                                 final GordianDigestType[] pDigestTypes) {
        /* Utilise the 128bit cache */
        final GordianIdCache myCache = theCacheMap.get(GordianLength.LEN_128);
        return myCache.deriveExternalDigestTypesFromSeed(pSeed, pDigestTypes);
    }

    /**
     * generate random GordianMacSpec.
     * @param pKeyLen the keyLength
     * @param pLargeData only generate a Mac that is suitable for parsing large amounts of data
     * @return the new MacSpec
     */
    public GordianMacSpec generateRandomMacSpec(final GordianLength pKeyLen,
                                                final boolean pLargeData) {
        /* Access the list to select from */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        List<GordianMacSpec> myValid = myMacs.listAllSupportedSpecs(pKeyLen);
        if (pLargeData) {
            myValid = myValid.stream().filter(s -> s.getMacType().supportsLargeData()).collect(Collectors.toList());
        }

        /* Determine a random index into the list and return the spec */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        final int myIndex = myRandom.nextInt(myValid.size());
        return myValid.get(myIndex);
    }
}
