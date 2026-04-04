/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianNewCipherParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianWrapper;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec.GordianPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreCipherMode;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Core Cipher factory.
 */
public abstract class GordianCoreCipherFactory
        implements GordianBaseCipherFactory {
    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The Cipher AlgIds.
     */
    private GordianCoreCipherAlgId theCipherAlgIds;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     */
    protected GordianCoreCipherFactory(final GordianBaseFactory pFactory) {
        theFactory = pFactory;
    }

    /**
     * Obtain the factory.
     *
     * @return the factory
     */
    protected GordianBaseFactory getFactory() {
        return theFactory;
    }

    @Override
    public GordianSymKeySpecBuilder newSymKeySpecBuilder() {
        return GordianCoreSymKeySpecBuilder.newInstance();
    }


    @Override
    public GordianStreamKeySpecBuilder newStreamKeySpecBuilder() {
        return GordianCoreStreamKeySpecBuilder.newInstance();
    }

    @Override
    public GordianSymCipherSpecBuilder newSymCipherSpecBuilder() {
        return GordianCoreSymCipherSpecBuilder.newInstance();
    }

    @Override
    public GordianStreamCipherSpecBuilder newStreamCipherSpecBuilder() {
        return GordianCoreStreamCipherSpecBuilder.newInstance();
    }

    @Override
    public GordianPBESpecBuilder newPBESpecBuilder() {
        return GordianCorePBESpecBuilder.newInstance();
    }

    @Override
    public GordianNewCipherParamsBuilder newCipherParamsBuilder() {
        return GordianCoreCipherParamsBuilder.newInstance();
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
        return t -> theFactory.getValidator().validStreamKeyType(t);
    }

    @Override
    public Predicate<GordianPBECipherSpec<? extends GordianKeySpec>> supportedPBECipherSpecs() {
        return this::validPBECipherSpec;
    }

    /**
     * Create a wrapCipher.
     *
     * @param pKey         the key
     * @param pBlockCipher the underlying block cipher
     * @return the wrapCipher
     */
    protected GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey,
                                              final GordianCoreCipher<GordianSymKeySpec> pBlockCipher) {
        return new GordianCoreWrapper(theFactory, pKey, pBlockCipher);
    }

    /**
     * Check SymKeySpec.
     *
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
     *
     * @param pCipherSpec the cipherSpec.
     * @return true/false
     */
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            return false;
        }

        /* Reject unsupported keySpecs */
        if (!supportedSymKeySpecs().test(pCipherSpec.getKeySpec())) {
            return false;
        }

        /* Reject null modes */
        final GordianCoreSymCipherSpec myCipherSpec = (GordianCoreSymCipherSpec) pCipherSpec;
        final GordianCoreCipherMode myMode = myCipherSpec.getCoreCipherMode();
        if (myMode == null) {
            return false;
        }

        /* Check that the mode is valid for the keyType */
        final GordianCoreSymKeySpec myKeySpec = myCipherSpec.getCoreKeySpec();
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
     *
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    protected void checkKeySpec(final GordianKeySpec pKeySpec) throws GordianException {
        /* Assume failure */
        boolean bValid = false;

        /* If this is a streamKeySpec */
        if (pKeySpec instanceof GordianStreamKeySpec mySpec) {
            /* Check validity of StreamKey */
            bValid = supportedStreamKeySpecs().test(mySpec);

            /* If this is a symKeySpec */
        } else if (pKeySpec instanceof GordianSymKeySpec mySpec) {
            /* Check validity of SymKey */
            bValid = supportedSymKeySpecs().test(mySpec);
        }

        /* Report error */
        if (!bValid) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
    }

    /**
     * Check the symKeySpec.
     *
     * @param pKeySpec the symKeySpec
     * @throws GordianException on error
     */
    public void checkSymKeySpec(final GordianSymKeySpec pKeySpec) throws GordianException {
        /* Check validity of SymKey */
        if (!supportedSymKeySpecs().test(pKeySpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
    }

    /**
     * Check the symCipherSpec.
     *
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    public void checkSymCipherSpec(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }

        /* Check validity of SymKey */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeySpec();
        if (!supportedSymKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!validSymCipherSpec(pCipherSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Check the streamCipherSpec.
     *
     * @param pCipherSpec the cipherSpec
     * @throws GordianException on error
     */
    public void checkStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }

        /* Check validity of StreamKey */
        final GordianStreamKeySpec myKeySpec = pCipherSpec.getKeySpec();
        if (!supportedStreamKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(myKeySpec));
        }

        /* Check validity of Mode */
        if (!validStreamCipherSpec(pCipherSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Check StreamCipherSpec.
     *
     * @param pCipherSpec the streamCipherSpec
     * @return true/false
     */
    protected boolean validStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) {
        return true;
    }

    /**
     * Check StreamKeySpec.
     *
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
     * Obtain Identifier for cipherSpec.
     *
     * @param pSpec the cipherSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianCipherSpec<?> pSpec) {
        return getCipherAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain cipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    public GordianCipherSpec<?> getCipherSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return getCipherAlgIds().getCipherSpecForIdentifier(pIdentifier);
    }

    /**
     * Obtain the cipher algorithm Ids.
     *
     * @return the cipher Algorithm Ids
     */
    private GordianCoreCipherAlgId getCipherAlgIds() {
        if (theCipherAlgIds == null) {
            theCipherAlgIds = new GordianCoreCipherAlgId(theFactory);
        }
        return theCipherAlgIds;
    }

    /**
     * Check SymCipherSpec and PBESpec combination.
     *
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
        if (myPBESpec instanceof GordianPBEDigestAndCountSpec myCountSpec) {
            final GordianDigestSpec mySpec = myCountSpec.getDigestSpec();
            final GordianDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
            return myBuilder.sha2(GordianLength.LEN_512).equals(mySpec);
        }

        /* OK */
        return true;
    }

    @Override
    public List<GordianSymCipherSpec> listAllSupportedSymCipherSpecs(final GordianSymKeySpec pSpec) {
        return listAllSymCipherSpecs(pSpec)
                .stream()
                .filter(s -> supportedSymCipherSpecs().test(s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianSymKeySpec> listAllSupportedSymKeySpecs(final GordianLength pKeyLen) {
        return listAllSymKeySpecs(pKeyLen)
                .stream()
                .filter(supportedSymKeySpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianSymKeyType> listAllSupportedSymKeyTypes() {
        return Arrays.stream(GordianSymKeyType.values())
                .filter(supportedSymKeyTypes())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianStreamCipherSpec> listAllSupportedStreamCipherSpecs(final GordianLength pKeyLen) {
        return GordianCoreStreamCipherSpecBuilder.listAllSupportedStreamCipherSpecs(pKeyLen);
    }

    @Override
    public List<GordianStreamKeySpec> listAllSupportedStreamKeySpecs(final GordianLength pKeyLen) {
        return listAllStreamKeySpecs(pKeyLen)
                .stream()
                .filter(supportedStreamKeySpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianStreamKeyType> listAllSupportedStreamKeyTypes() {
        return Arrays.stream(GordianStreamKeyType.values())
                .filter(supportedStreamKeyTypes())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianSymKeySpec> listAllSymKeySpecs(final GordianLength pKeyLen) {
        return GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(pKeyLen);
    }

    @Override
    public List<GordianSymCipherSpec> listAllSymCipherSpecs(final GordianSymKeySpec pSpec) {
        return GordianCoreSymCipherSpecBuilder.listAllPossibleSymCipherSpecs(pSpec);
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    private static List<GordianStreamKeySpec> listAllStreamKeySpecs(final GordianLength pKeyLen) {
        return GordianCoreStreamKeySpecBuilder.listAllPossibleStreamKeySpecs(pKeyLen);
    }
}
