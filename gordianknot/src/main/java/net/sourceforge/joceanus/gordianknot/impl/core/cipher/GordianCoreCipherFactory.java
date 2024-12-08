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
package net.sourceforge.joceanus.gordianknot.impl.core.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBEDigestAndCountSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianAsconKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianBlakeXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianElephantKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianISAPKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSkeinXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSparkleKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianStreamSubKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Core Cipher factory.
 */
public abstract class GordianCoreCipherFactory
    implements GordianCipherFactory {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Cipher AlgIds.
     */
    private GordianCipherAlgId theCipherAlgIds;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    protected GordianCoreCipherFactory(final GordianCoreFactory pFactory) {
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
    public Predicate<GordianSymKeySpec> supportedSymKeySpecs() {
        return this::validSymKeySpec;
    }

    @Override
    public Predicate<GordianSymCipherSpec> supportedSymCipherSpecs() {
        return this::validSymCipherSpec;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedSymKeyTypes() {
        return t -> theFactory.getValidator().validSymKeyType(t);
    }

    @Override
    public Predicate<GordianStreamKeySpec> supportedStreamKeySpecs() {
        return this::validStreamKeySpec;
    }

    @Override
    public Predicate<GordianStreamCipherSpec> supportedStreamCipherSpecs() {
        return this::validStreamCipherSpec;
    }

    @Override
    public Predicate<GordianStreamKeyType> supportedStreamKeyTypes() {
        return this::validStreamKeyType;
    }

    /**
     * Obtain predicate for supported PBECipherSpecs.
     * @return the predicate
     */
    public Predicate<GordianPBECipherSpec<? extends GordianKeySpec>> supportedPBECipherSpecs() {
        return this::validPBECipherSpec;
    }

    /**
     * Create a wrapCipher.
     * @param pKey the key
     * @param pBlockCipher the underlying block cipher
     * @return the wrapCipher
     */
    protected GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey,
                                              final GordianCoreCipher<GordianSymKeySpec> pBlockCipher) {
        return new GordianCoreWrapper(theFactory, pKey, pBlockCipher);
    }

    /**
     * Check SymKeySpec.
     * @param pSymKeySpec the symKeySpec
     * @return true/false
     */
    public boolean validSymKeySpec(final GordianSymKeySpec pSymKeySpec) {
        /* Reject invalid keySpec */
        if (pSymKeySpec == null
                || !pSymKeySpec.isValid()) {
            return false;
        }
        return supportedSymKeyTypes().test(pSymKeySpec.getSymKeyType());
    }

    /**
     * validate the symCipherSpec.
     * @param pCipherSpec the cipherSpec.
     * @return true/false
     */
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            return false;
        }

        /* Reject unsupported keySpecs */
        if (!supportedSymKeySpecs().test(pCipherSpec.getKeyType())) {
            return false;
        }

        /* Reject null modes */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
        if (myMode == null) {
            return false;
        }

        /* Check that the mode is valid for the keyType */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        final GordianSymKeyType myKeyType = myKeySpec.getSymKeyType();
        if (!myMode.validForSymKey(myKeySpec)) {
            return false;
        }

        /* Disallow AAD for RC5-64 */
        if (GordianSymKeyType.RC5.equals(myKeyType)
                && GordianLength.LEN_128.equals(myKeySpec.getBlockLength())
                && myMode.isAAD()) {
            return false;
        }

        /* Determine whether we have a short block length */
        final int myLen = myKeySpec.getBlockLength().getLength();
        final boolean shortBlock = myLen < GordianLength.LEN_128.getLength();

        /* Reject modes which do not allow short blocks */
        if (shortBlock && !myMode.allowShortBlock()) {
            return false;
        }

        /* Reject modes which do not allow non-standard blocks */
        final boolean stdBlock = myLen == GordianLength.LEN_128.getLength();
        if (!stdBlock && myMode.needsStdBlock()) {
            return false;
        }

        /* Reject bad padding */
        final GordianPadding myPadding = pCipherSpec.getPadding();
        return myMode.hasPadding()
               ? myPadding != null
               : GordianPadding.NONE.equals(myPadding);
    }

    /**
     * Check the keySpec.
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    protected void checkKeySpec(final GordianKeySpec pKeySpec) throws GordianException {
        /* Assume failure */
        boolean bValid = false;

        /* If this is a streamKeySpec */
        if (pKeySpec instanceof GordianStreamKeySpec) {
            /* Check validity of StreamKey */
            bValid = supportedStreamKeySpecs().test((GordianStreamKeySpec) pKeySpec);

            /* If this is a symKeySpec */
        } else  if (pKeySpec instanceof GordianSymKeySpec) {
            /* Check validity of SymKey */
            bValid = supportedSymKeySpecs().test((GordianSymKeySpec) pKeySpec);
        }

        /* Report error */
        if (!bValid) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    /**
     * Check the symKeySpec.
     * @param pKeySpec the symKeySpec
     * @throws GordianException on error
     */
    public void checkSymKeySpec(final GordianSymKeySpec pKeySpec) throws GordianException {
        /* Check validity of SymKey */
        if (!supportedSymKeySpecs().test(pKeySpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    /**
     * Check the symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    public void checkSymCipherSpec(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }

        /* Check validity of SymKey */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        if (!supportedSymKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!validSymCipherSpec(pCipherSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Check the streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    public void checkStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }

        /* Check validity of StreamKey */
        final GordianStreamKeySpec myKeySpec = pCipherSpec.getKeyType();
        if (!supportedStreamKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(myKeySpec));
        }

        /* Check validity of Mode */
        if (!validStreamCipherSpec(pCipherSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Check StreamCipherSpec.
     * @param pCipherSpec the streamCipherSpec
     * @return true/false
     */
    protected boolean validStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) {
        return true;
    }

    /**
     * Check StreamKeySpec.
     * @param pKeySpec the streamKeySpec
     * @return true/false
     */
    protected boolean validStreamKeySpec(final GordianStreamKeySpec pKeySpec) {
        /* Reject invalid keySpec */
        if (pKeySpec == null
                || !pKeySpec.isValid()) {
            return false;
        }
        return supportedStreamKeyTypes().test(pKeySpec.getStreamKeyType());
    }

    /**
     * Check StreamKeyType.
     * @param pKeyType the streamKeyType
     * @return true/false
     */
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        return pKeyType != null;
    }

    /**
     * Obtain Identifier for cipherSpec.
     * @param pSpec the cipherSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianCipherSpec<?> pSpec) {
        return getCipherAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain cipherSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    public GordianCipherSpec<?> getCipherSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getCipherAlgIds().getCipherSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the cipher algorithm Ids.
     * @return the cipher Algorithm Ids
     */
    private GordianCipherAlgId getCipherAlgIds() {
        if (theCipherAlgIds == null) {
            theCipherAlgIds = new GordianCipherAlgId(theFactory);
        }
        return theCipherAlgIds;
    }

    /**
     * Check SymCipherSpec and PBESpec combination.
     * @param pPBECipherSpec the PBESpec
     * @return true/false
     */
    public boolean validPBECipherSpec(final GordianPBECipherSpec<? extends GordianKeySpec> pPBECipherSpec) {
        /* Check basic validity */
        if (pPBECipherSpec == null || !pPBECipherSpec.isValid()) {
            return false;
        }

        /* Digest if specified must be SHA512 currently */
        final GordianPBESpec myPBESpec = pPBECipherSpec.getPBESpec();
        if (myPBESpec instanceof GordianPBEDigestAndCountSpec) {
            final GordianDigestSpec mySpec = ((GordianPBEDigestAndCountSpec) myPBESpec).getDigestSpec();
            return GordianDigestSpecBuilder.sha2(GordianLength.LEN_512).equals(mySpec);
        }

        /* OK */
        return true;
    }

    @Override
    public List<GordianSymCipherSpec> listAllSupportedSymCipherSpecs(final GordianSymKeySpec pSpec) {
        return listAllSymCipherSpecs(pSpec)
                .stream()
                .filter(s -> supportedSymCipherSpecs().test(s))
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianSymKeySpec> listAllSupportedSymKeySpecs(final GordianLength pKeyLen) {
        return listAllSymKeySpecs(pKeyLen)
                .stream()
                .filter(supportedSymKeySpecs())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianSymKeyType> listAllSupportedSymKeyTypes() {
        return Arrays.stream(GordianSymKeyType.values())
                .filter(supportedSymKeyTypes())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianStreamCipherSpec> listAllSupportedStreamCipherSpecs(final GordianLength pKeyLen) {
        final List<GordianStreamCipherSpec> myResult = new ArrayList<>();
        for (GordianStreamKeySpec mySpec : listAllSupportedStreamKeySpecs(pKeyLen)) {
            /* Add the standard cipher */
            final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(mySpec);
            myResult.add(myCipherSpec);

            /* Add the AAD Cipher if supported */
            if (mySpec.supportsAEAD()) {
                final GordianStreamCipherSpec myAADSpec = GordianStreamCipherSpecBuilder.stream(mySpec, true);
                if (supportedStreamCipherSpecs().test(myAADSpec)) {
                    myResult.add(myAADSpec);
                }
            }
        }
        return myResult;
    }

    @Override
    public List<GordianStreamKeySpec> listAllSupportedStreamKeySpecs(final GordianLength pKeyLen) {
        return listAllStreamKeySpecs(pKeyLen)
                .stream()
                .filter(supportedStreamKeySpecs())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianStreamKeyType> listAllSupportedStreamKeyTypes() {
        return Arrays.stream(GordianStreamKeyType.values())
                .filter(supportedStreamKeyTypes())
                .collect(Collectors.toList());
    }

    @Override
    public List<GordianSymKeySpec> listAllSymKeySpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianSymKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each symKey type */
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* For each supported block length */
            for (final GordianLength myBlkLen : myType.getSupportedBlockLengths()) {
                /* Add spec if valid for blkLen and keyLen */
                if (myType.validBlockAndKeyLengths(myBlkLen, pKeyLen)) {
                    myList.add(new GordianSymKeySpec(myType, myBlkLen, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }

    @Override
    public List<GordianSymCipherSpec> listAllSymCipherSpecs(final GordianSymKeySpec pSpec) {
        /* Create the array list */
        final List<GordianSymCipherSpec> myList = new ArrayList<>();

        /* Loop through the modes */
        for (GordianCipherMode myMode : GordianCipherMode.values()) {
            /* If the mode has padding */
            if (myMode.hasPadding()) {
                /* Loop through the paddings */
                for (GordianPadding myPadding : GordianPadding.values()) {
                    myList.add(new GordianSymCipherSpec(pSpec, myMode, myPadding));
                }

                /* else no padding */
            } else {
                myList.add(new GordianSymCipherSpec(pSpec, myMode, GordianPadding.NONE));
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    private static List<GordianStreamKeySpec> listAllStreamKeySpecs(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each streamKey type */
        for (final GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            /* if valid for keyLength */
            if (myType.validForKeyLength(pKeyLen)) {
                /* If we need a subType */
                if (myType.needsSubKeyType()) {
                    /* Add all valid subKeyTypes */
                    myList.addAll(listStreamSubKeys(myType, pKeyLen));

                    /* Else just add the spec */
                } else {
                    myList.add(new GordianStreamKeySpec(myType, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes Specs.
     * @param pKeyType the keyType
     * @param pKeyLen the keyLength
     * @return the list
     */
    private static List<GordianStreamKeySpec> listStreamSubKeys(final GordianStreamKeyType pKeyType,
                                                                final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Loop through the subKeyTypes */
        for (GordianStreamSubKeyType mySubKeyType : listStreamSubKeys(pKeyType)) {
            /* Add valid subKeySpec */
            final GordianStreamKeySpec mySpec = new GordianStreamKeySpec(pKeyType, pKeyLen, mySubKeyType);
            if (mySpec.isValid()) {
                myList.add(mySpec);
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes.
     * @param pKeyType the keyType
     * @return the list
     */
    private static List<GordianStreamSubKeyType> listStreamSubKeys(final GordianStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case SALSA20:
                return Arrays.asList(GordianSalsa20Key.values());
            case CHACHA20:
                return Arrays.asList(GordianChaCha20Key.values());
            case VMPC:
                return Arrays.asList(GordianVMPCKey.values());
            case SKEINXOF:
                return Arrays.asList(GordianSkeinXofKey.values());
            case BLAKE2XOF:
                return Arrays.asList(GordianBlakeXofKey.values());
            case ASCON:
                return Arrays.asList(GordianAsconKey.values());
            case ELEPHANT:
                return Arrays.asList(GordianElephantKey.values());
            case ISAP:
                return Arrays.asList(GordianISAPKey.values());
            case SPARKLE:
                return Arrays.asList(GordianSparkleKey.values());
            default:
                return Collections.emptyList();
        }
    }
}
