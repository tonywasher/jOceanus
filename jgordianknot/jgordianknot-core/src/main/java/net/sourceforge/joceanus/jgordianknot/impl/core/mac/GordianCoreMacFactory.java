/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.mac;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianSipHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core Mac Factory.
 */
public abstract class GordianCoreMacFactory
    implements GordianMacFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    protected GordianCoreMacFactory(final GordianCoreFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public Predicate<GordianMacSpec> supportedMacSpecs() {
        return this::validMacSpec;
    }

    @Override
    public Predicate<GordianMacType> supportedMacTypes() {
        return this::validMacType;
    }

    @Override
    public Predicate<GordianDigestSpec> supportedHMacDigestSpecs() {
        return this::validHMacSpec;
    }

    @Override
    public Predicate<GordianDigestType> supportedHMacDigestTypes() {
        return this::validHMacDigestType;
    }

    @Override
    public Predicate<GordianSymKeySpec> supportedPoly1305SymKeySpecs() {
        return p -> p == null
                        || (validPoly1305SymKeySpec(p)
                             && p.getBlockLength() == GordianLength.LEN_128);
    }

    @Override
    public Predicate<GordianSymKeySpec> supportedGMacSymKeySpecs() {
        return p -> validGMacSymKeySpec(p)
                && p.getBlockLength() == GordianLength.LEN_128;
    }

    @Override
    public Predicate<GordianSymKeySpec> supportedCMacSymKeySpecs() {
        return this::validCMacSymKeySpec;
    }

    /**
     * Check MacType.
     * @param pMacType the macType
     * @return true/false
     */
    protected boolean validMacType(final GordianMacType pMacType) {
        return pMacType != null;
    }

    /**
     * Check the macSpec.
     * @param pMacSpec the macSpec
     * @throws OceanusException on error
     */
    protected void checkMacSpec(final GordianKeySpec pMacSpec) throws OceanusException {
        /* Check validity of MacSpec */
        if (!(pMacSpec instanceof GordianMacSpec)
                || !supportedMacSpecs().test((GordianMacSpec) pMacSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pMacSpec));
        }
    }

    /**
     * Check HMacDigestType.
     * @param pDigestType the digestType
     * @return true/false
     */
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        return myDigests.validDigestType(pDigestType) && pDigestType.supportsLargeData();
    }

    /**
     * Check HMacSpec.
     * @param pDigestSpec the digestSpec
     * @return true/false
     */
    public boolean validHMacSpec(final GordianDigestSpec pDigestSpec) {
        /* Access details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();

        /* Check validity */
        return supportedHMacDigestTypes().test(myType)
                && myDigests.supportedDigestSpecs().test(pDigestSpec);
    }

    /**
     * Check MacSpec.
     * @param pMacSpec the macSpec
     * @return true/false
     */
    private boolean validMacSpec(final GordianMacSpec pMacSpec) {
        /* Reject invalid macSpec */
        if (pMacSpec == null || !pMacSpec.isValid()) {
            return false;
        }

        /* Check that the macType is supported */
        final GordianMacType myType = pMacSpec.getMacType();
        if (!supportedMacTypes().test(myType)) {
            return false;
        }

        /* Switch on MacType */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianDigestSpec mySpec = pMacSpec.getDigestSpec();
        final GordianSymKeySpec mySymSpec = pMacSpec.getSymKeySpec();
        switch (myType) {
            case HMAC:
                return supportedHMacDigestSpecs().test(mySpec);
            case GMAC:
                return supportedGMacSymKeySpecs().test(mySymSpec);
            case CMAC:
                return supportedCMacSymKeySpecs().test(mySymSpec);
            case POLY1305:
                return supportedPoly1305SymKeySpecs().test(mySymSpec);
            case SKEIN:
                return GordianDigestType.SKEIN.equals(mySpec.getDigestType())
                        && myDigests.supportedDigestSpecs().test(mySpec);
            case BLAKE2:
                return GordianDigestType.BLAKE2.equals(mySpec.getDigestType())
                        && myDigests.supportedDigestSpecs().test(mySpec);
            case BLAKE3:
                return GordianDigestType.BLAKE3.equals(mySpec.getDigestType())
                        && myDigests.supportedDigestSpecs().test(mySpec);
            case KUPYNA:
                return GordianDigestType.KUPYNA.equals(mySpec.getDigestType())
                        && myDigests.supportedDigestSpecs().test(mySpec);
            case KALYNA:
                return GordianSymKeyType.KALYNA.equals(mySymSpec.getSymKeyType())
                        && myCiphers.validSymKeySpec(mySymSpec);
            case CBCMAC:
            case CFBMAC:
                return (!GordianSymKeyType.RC5.equals(mySymSpec.getSymKeyType())
                        || !GordianLength.LEN_128.equals(mySymSpec.getBlockLength()))
                    && myCiphers.validSymKeySpec(mySymSpec);
            case ZUC:
            case VMPC:
            case SIPHASH:
            case GOST:
            case KMAC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine supported Poly1305 algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validPoly1305SymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK:
            case RC5:
                return false;
            default:
                final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
                return myCiphers.validSymKeySpec(pKeySpec);
        }
    }

    /**
     * Determine supported GMAC algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validGMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.RC5.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        return myCiphers.validSymKeySpec(pKeySpec);
    }

    /**
     * Determine supported CMAC algorithms.
     * @param pKeySpec the keySpec
     * @return true/false
     */
    protected boolean validCMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.RC5.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        return myCiphers.validSymKeySpec(pKeySpec);
    }

    @Override
    public List<GordianMacSpec> listAllSupportedSpecs(final GordianLength pKeyLen) {
        return listAllPossibleSpecs(pKeyLen)
                .stream()
                .filter(supportedMacSpecs())
                .collect(Collectors.toList());
    }

    /**
     * List all possible macSpecs for a keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    public List<GordianMacSpec> listAllPossibleSpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianMacSpec> myList = new ArrayList<>();

        /* For each digestSpec */
        for (final GordianDigestSpec mySpec : theFactory.getDigestFactory().listAllPossibleSpecs()) {
            /* Add the hMacSpec */
            myList.add(GordianMacSpec.hMac(mySpec, pKeyLen));

            /* Add KMAC for digestType of SHAKE */
            if (GordianDigestType.SHAKE == mySpec.getDigestType()) {
                myList.add(GordianMacSpec.kMac(pKeyLen, mySpec));
            }
        }

        /* For each SymKey */
        for (final GordianSymKeySpec mySymKeySpec : theFactory.getCipherFactory().listAllSymKeySpecs(pKeyLen)) {
            /* Add gMac/cMac/cfbMac/cbcMac */
            myList.add(GordianMacSpec.gMac(mySymKeySpec));
            myList.add(GordianMacSpec.cMac(mySymKeySpec));
            myList.add(GordianMacSpec.cbcMac(mySymKeySpec));
            myList.add(GordianMacSpec.cfbMac(mySymKeySpec));

            /* Add kalynaMac for keyType of Kalyna */
            if (GordianSymKeyType.KALYNA == mySymKeySpec.getSymKeyType()) {
                myList.add(GordianMacSpec.kalynaMac(mySymKeySpec));
            }
        }

        /* Only add poly1305 for 256bit keyLengths */
        if (GordianLength.LEN_256 == pKeyLen) {
            /* For each SymKey at 128 bits*/
            for (final GordianSymKeySpec mySymKeySpec : theFactory.getCipherFactory().listAllSymKeySpecs(GordianLength.LEN_128)) {
                myList.add(GordianMacSpec.poly1305Mac(mySymKeySpec));
            }

            /* Add raw poly1305 */
            myList.add(GordianMacSpec.poly1305Mac());

            /* Add Blake3 macs */
            for (final GordianLength myLength : GordianDigestType.BLAKE3.getSupportedLengths()) {
                myList.add(GordianMacSpec.blake3Mac(myLength));
            }
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianDigestType.KUPYNA.getSupportedLengths()) {
            myList.add(GordianMacSpec.kupynaMac(pKeyLen, myLength));
        }

        /* Add SkeinMacs */
        for (final GordianLength myLength : GordianDigestType.SKEIN.getSupportedLengths()) {
            myList.add(GordianMacSpec.skeinMac(pKeyLen, myLength));
            if (GordianDigestType.SKEIN.getAlternateStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.skeinMac(pKeyLen, GordianDigestSpec.skeinAlt(myLength)));
            }
        }

        /* Add blakeMacs */
        for (final GordianLength myLength : GordianDigestType.BLAKE2.getSupportedLengths()) {
            GordianMacSpec mySpec = GordianMacSpec.blake2Mac(pKeyLen, myLength);
            if (mySpec.isValid()) {
                myList.add(mySpec);
            }
            if (GordianDigestType.BLAKE2.getAlternateStateForLength(myLength) != null) {
                mySpec = GordianMacSpec.blake2Mac(pKeyLen, GordianDigestSpec.blake2Alt(myLength));
                if (mySpec.isValid()) {
                    myList.add(mySpec);
                }
            }
        }

        /* Add vmpcMac */
        myList.add(GordianMacSpec.vmpcMac(pKeyLen));

        /* Add sipHash for 128bit keys */
        if (GordianLength.LEN_128 == pKeyLen) {
            for (final GordianSipHashSpec mySpec : GordianSipHashSpec.values()) {
                myList.add(GordianMacSpec.sipHash(mySpec));
            }
        }

        /* Add gostHash for 256bit keys */
        if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(GordianMacSpec.gostMac());
        }

        /* Add zucMac */
        if (GordianLength.LEN_128 == pKeyLen) {
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_32));
        } else if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_32));
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_64));
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_128));
        }

        /* Return the list */
        return myList;
    }
}
