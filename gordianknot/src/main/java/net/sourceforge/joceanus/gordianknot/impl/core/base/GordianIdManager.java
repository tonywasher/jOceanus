/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacType;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Security Id Manager.
 */
public class GordianIdManager {
    /**
     * The Factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @throws GordianException on error
     */
    GordianIdManager(final GordianCoreFactory pFactory) throws GordianException  {
        /* Store the factory */
        theFactory = pFactory;
    }

    /**
     * Obtain random SymKeySpec.
     * @param pKeyLen the keyLength
     * @return the random symKeySpec
     */
    public GordianSymKeySpec generateRandomSymKeySpec(final GordianLength pKeyLen) {
        /* Access the list of symKeySpecs and unique symKeyTypes */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final List<GordianSymKeySpec> mySpecs = myCiphers.listAllSupportedSymKeySpecs(pKeyLen);
        final List<GordianSymKeyType> myTypes = mySpecs.stream().map(GordianSymKeySpec::getSymKeyType).toList();

        /* Determine a random index into the list and obtain the symKeyType */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(myTypes.size());
        final GordianSymKeyType myKeyType = myTypes.get(myIndex);

        /* Select from among possible keySpecs of this type */
        mySpecs.removeIf(s -> s.getSymKeyType() != myKeyType);
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
    }

    /**
     * Obtain set of random keySet SymKeySpecs.
     * @param pKeyLen the keyLength
     * @param pCount the count
     * @return the random symKeySpecs
     */
    public GordianSymKeySpec[] generateRandomKeySetSymKeySpecs(final GordianLength pKeyLen,
                                                               final int pCount) {
        /* Access the list to select from */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final List<GordianSymKeySpec> mySpecs = myCiphers.listAllSupportedSymKeySpecs(pKeyLen);

        /* Remove the short block specs that cannot support SIC Mode */
        mySpecs.removeIf(s -> s.getBlockLength() == GordianLength.LEN_64);

        /* Create the Access list and loop to populate */
        final GordianSymKeySpec[] myResult = new GordianSymKeySpec[pCount];
        for (int i = 0; i < pCount; i++) {
             myResult[i] = selectSymKeySpecFromList(mySpecs);
        }

        /* Return the result  */
        return myResult;
    }

    /**
     * Obtain a random symKeySpec and remove all of the same symKeyType.
     * @param pList the list of symKeySpecs
     * @return the random symKeySpec
     */
    public GordianSymKeySpec selectSymKeySpecFromList(final List<GordianSymKeySpec> pList) {
        /* Select the random Spec */
        final List<GordianSymKeyType> myTypes = pList.stream().map(GordianSymKeySpec::getSymKeyType).toList();
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(pList.size());
        final GordianSymKeyType myType = myTypes.get(myIndex);

        /* Strip out the possible specs */
        final List<GordianSymKeySpec> mySpecs = pList.stream().filter(mySpec -> mySpec.getSymKeyType() == myType).toList();
        pList.removeIf(mySpec -> mySpec.getSymKeyType() == myType);

        /* Return the result */
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
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
        final List<GordianStreamKeySpec> mySpecs = myCiphers.listAllSupportedStreamKeySpecs(pKeyLen);
        if (pLargeData) {
            mySpecs.removeIf(s -> !s.getStreamKeyType().supportsLargeData());
        }
        final List<GordianStreamKeyType> myTypes = mySpecs.stream().map(GordianStreamKeySpec::getStreamKeyType).toList();

        /* Determine a random index into the list and obtain the streamKeyType */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(myTypes.size());
        final GordianStreamKeyType myKeyType = myTypes.get(myIndex);

        /* Select from among possible keySpecs of this type */
        mySpecs.removeIf(s -> s.getStreamKeyType() != myKeyType);
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
     }

    /**
     * Obtain random DigestSpec.
     * @param pLargeData only generate a Digest that is suitable for processing large amounts of data
     * @return the random digestSpec
     */
    public GordianDigestSpec generateRandomDigestSpec(final boolean pLargeData) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final List<GordianDigestSpec> mySpecs = myDigests.listAllSupportedSpecs();
        if (pLargeData) {
            mySpecs.removeIf(s -> !s.getDigestType().supportsLargeData());
        }
        final List<GordianDigestType> myTypes = mySpecs.stream().map(GordianDigestSpec::getDigestType).toList();

        /* Determine a random index into the list and obtain the digestType */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(myTypes.size());
        final GordianDigestType myDigestType = myTypes.get(myIndex);

        /* Select from among possible digestSpecs of this type */
        mySpecs.removeIf(s -> s.getDigestType() != myDigestType);
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
    }

    /**
     * Derive set of keySet SymKeyTypes from seed.
     * @param pRandom the seeded random
     * @param pKeyLen the keyLength
     * @param pCount the number of distinct digestTypes to select
     * @return the remaining seed
     */
    public GordianSymKeyType[] deriveKeySetSymKeyTypesFromSeed(final Random pRandom,
                                                               final GordianLength pKeyLen,
                                                               final int pCount) {
        /* Access the list to select from */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianSymKeyType> myTypes = myCiphers.listAllSupportedSymKeyTypes().stream()
                .filter(myValidator.supportedKeySetSymKeyTypes(pKeyLen))
                .collect(Collectors.toList());

        /* Allocate the array to return */
        final GordianSymKeyType[] myResult = new GordianSymKeyType[pCount];

        /* Loop selecting digestTypes */
        for (int i = 0; i < pCount; i++) {
            /* Select from the list and remove the selected item */
            final int myIndex = pRandom.nextInt(myTypes.size());
            final GordianSymKeyType myType = myTypes.get(myIndex);
            myTypes.removeIf(t -> t == myType);
            myResult[i] = myType;
        }

        /* return the selected symKeyTypes */
        return myResult;
    }

    /**
     * Derive secret lockDigestType from seed.
     * @param pRandom the seeded random
     * @return the selected keyHashDigestTypes
     */
    public GordianDigestType deriveLockSecretTypeFromSeed(final Random pRandom) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianDigestType> myTypes = myDigests.listAllSupportedTypes().stream()
                .filter(myValidator.supportedLockDigestTypes())
                .filter(myValidator.isExternalHashDigest())
                .toList();

        /* Select from the list and remove the selected item */
        final int myIndex = pRandom.nextInt(myTypes.size());
        return myTypes.get(myIndex);
    }

    /**
     * Derive set of lockDigestTypes from seed.
     * @param pRandom the seeded random
     * @param pCount the number of distinct digestTypes to select
     * @return the selected keyHashDigestTypes
     */
    public GordianDigestType[] deriveLockDigestTypesFromSeed(final Random pRandom,
                                                             final int pCount) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianDigestType> myTypes = myDigests.listAllSupportedTypes().stream()
                .filter(myValidator.supportedLockDigestTypes())
                .collect(Collectors.toList());

        /* Allocate the array to return */
        final GordianDigestType[] myResult = new GordianDigestType[pCount];

        /* Loop selecting digestTypes */
        for (int i = 0; i < pCount; i++) {
            /* Select from the list and remove the selected item */
            final int myIndex = pRandom.nextInt(myTypes.size());
            final GordianDigestType myType = myTypes.get(myIndex);
            myTypes.removeIf(t -> t == myType);
            myResult[i] = myType;
        }

        /* return the selected digestTypes */
        return myResult;
    }

    /**
     * Derive a Lock externalDigestTypes from seededRandom.
     * @param pRandom the seeded random
     * @return the selected externalDigestType
     */
    public GordianDigestType deriveExternalDigestTypeFromSeed(final Random pRandom) {
        /* Access the list to select from */
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianDigestType> myTypes = myValidator.listAllExternalDigestTypes();

        /* Select from the list */
        final int myIndex = pRandom.nextInt(myTypes.size());
        return myTypes.get(myIndex);
    }

    /**
     * Derive set of keyGenDigestTypes from seed.
     * @param pRandom the seeded random
     * @param pCount the number of distinct digestTypes to select
     * @return the selected keyGenDigestTypes
     */
    public GordianDigestType[] deriveKeyGenDigestTypesFromSeed(final Random pRandom,
                                                               final int pCount) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianDigestType> myTypes = myDigests.listAllSupportedTypes().stream()
                .filter(myValidator.supportedKeyGenDigestTypes())
                .collect(Collectors.toList());

        /* Allocate the array to return */
        final GordianDigestType[] myResult = new GordianDigestType[pCount];

        /* Loop selecting digestTypes */
        for (int i = 0; i < pCount; i++) {
            /* Select from the list and remove the selected item */
            final int myIndex = pRandom.nextInt(myTypes.size());
            final GordianDigestType myType = myTypes.get(myIndex);
            myTypes.removeIf(t -> t == myType);
            myResult[i] = myType;
        }

        /* return the selected digestTypes */
        return myResult;
    }

    /**
     * Derive agreementDigestType from seed.
     * @param pRandom the seeded random
     * @return the selected agreementDigestType
     */
    public GordianDigestType deriveAgreementDigestTypeFromSeed(final Random pRandom) {
        /* Access the list to select from */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianValidator myValidator = theFactory.getValidator();
        final List<GordianDigestType> myTypes = myDigests.listAllSupportedTypes().stream()
                .filter(myValidator.supportedAgreementDigestTypes())
                .toList();

        /* Select from the list */
        final int myIndex = pRandom.nextInt(myTypes.size());
        return myTypes.get(myIndex);
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
        final List<GordianMacSpec> mySpecs = myMacs.listAllSupportedSpecs(pKeyLen);

        /* Modify list (if required) to remove macs that do not support largeData */
        if (pLargeData) {
            mySpecs.removeIf(s -> !s.getMacType().supportsLargeData());
        }

        /* Modify list to remove rawPoly1305 */
        mySpecs.remove(GordianMacSpecBuilder.poly1305Mac());

        /* Extract the macTypes */
        final List<GordianMacType> myTypes = mySpecs.stream().map(GordianMacSpec::getMacType).toList();

        /* Determine a random index into the list and obtain the macType */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(myTypes.size());
        final GordianMacType myMacType = myTypes.get(myIndex);

        /* Select from among possible macSpecs of this type */
        mySpecs.removeIf(s -> s.getMacType() != myMacType);
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
    }
}
