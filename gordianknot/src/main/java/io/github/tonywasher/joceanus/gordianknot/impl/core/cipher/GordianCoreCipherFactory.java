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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianWrapper;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
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
    public GordianNewSymKeySpecBuilder newSymKeySpecBuilder() {
        return GordianCoreSymKeySpecBuilder.newInstance();
    }

    @Override
    public GordianNewStreamKeySpecBuilder newStreamKeySpecBuilder() {
        return GordianCoreStreamKeySpecBuilder.newInstance();
    }

    @Override
    public GordianNewSymCipherSpecBuilder newSymCipherSpecBuilder() {
        return GordianCoreSymCipherSpecBuilder.newInstance();
    }

    @Override
    public GordianNewStreamCipherSpecBuilder newStreamCipherSpecBuilder() {
        return GordianCoreStreamCipherSpecBuilder.newInstance();
    }

    @Override
    public GordianNewPBESpecBuilder newPBESpecBuilder() {
        return GordianCorePBESpecBuilder.newInstance();
    }

    @Override
    public Predicate<GordianNewSymKeySpec> supportedSymKeySpecs() {
        return this::validSymKeySpec;
    }

    @Override
    public Predicate<GordianNewSymCipherSpec> supportedSymCipherSpecs() {
        return this::validSymCipherSpec;
    }

    @Override
    public Predicate<GordianNewSymKeyType> supportedSymKeyTypes() {
        return t -> theFactory.getValidator().validSymKeyType(t);
    }

    @Override
    public Predicate<GordianNewStreamKeySpec> supportedStreamKeySpecs() {
        return this::validStreamKeySpec;
    }

    @Override
    public Predicate<GordianNewStreamCipherSpec> supportedStreamCipherSpecs() {
        return this::validStreamCipherSpec;
    }

    @Override
    public Predicate<GordianNewStreamKeyType> supportedStreamKeyTypes() {
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
    protected GordianWrapper createKeyWrapper(final GordianKey<GordianNewSymKeySpec> pKey,
                                              final GordianCoreCipher<GordianNewSymKeySpec> pBlockCipher) {
        return new GordianCoreWrapper(theFactory, pKey, pBlockCipher);
    }

    /**
     * Check SymKeySpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return true/false
     */
    public boolean validSymKeySpec(final GordianNewSymKeySpec pSymKeySpec) {
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
    protected boolean validSymCipherSpec(final GordianNewSymCipherSpec pCipherSpec) {
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
        final GordianNewSymKeyType myKeyType = myKeySpec.getSymKeyType();
        if (!myMode.validForSymKey(myKeySpec)) {
            return false;
        }

        /* Disallow AAD for RC5-64 */
        if (GordianNewSymKeyType.RC5.equals(myKeyType)
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
        final GordianNewPadding myPadding = pCipherSpec.getPadding();
        return myMode.hasPadding()
                ? myPadding != null
                : GordianNewPadding.NONE.equals(myPadding);
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
        if (pKeySpec instanceof GordianNewStreamKeySpec mySpec) {
            /* Check validity of StreamKey */
            bValid = supportedStreamKeySpecs().test(mySpec);

            /* If this is a symKeySpec */
        } else if (pKeySpec instanceof GordianNewSymKeySpec mySpec) {
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
    public void checkSymKeySpec(final GordianNewSymKeySpec pKeySpec) throws GordianException {
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
    public void checkSymCipherSpec(final GordianNewSymCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }

        /* Check validity of SymKey */
        final GordianNewSymKeySpec myKeySpec = pCipherSpec.getKeySpec();
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
    public void checkStreamCipherSpec(final GordianNewStreamCipherSpec pCipherSpec) throws GordianException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        }

        /* Check validity of StreamKey */
        final GordianNewStreamKeySpec myKeySpec = pCipherSpec.getKeySpec();
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
    protected boolean validStreamCipherSpec(final GordianNewStreamCipherSpec pCipherSpec) {
        return true;
    }

    /**
     * Check StreamKeySpec.
     *
     * @param pKeySpec the streamKeySpec
     * @return true/false
     */
    protected boolean validStreamKeySpec(final GordianNewStreamKeySpec pKeySpec) {
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
    public AlgorithmIdentifier getIdentifierForSpec(final GordianNewCipherSpec<?> pSpec) {
        return getCipherAlgIds().getIdentifierForSpec(pSpec);
    }

    /**
     * Obtain cipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    public GordianNewCipherSpec<?> getCipherSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
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
        final GordianNewPBESpec myPBESpec = pPBECipherSpec.getPBESpec();
        if (myPBESpec instanceof GordianNewPBEDigestAndCountSpec myCountSpec) {
            final GordianNewDigestSpec mySpec = myCountSpec.getDigestSpec();
            final GordianNewDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
            return myBuilder.sha2(GordianLength.LEN_512).equals(mySpec);
        }

        /* OK */
        return true;
    }

    @Override
    public List<GordianNewSymCipherSpec> listAllSupportedSymCipherSpecs(final GordianNewSymKeySpec pSpec) {
        return listAllSymCipherSpecs(pSpec)
                .stream()
                .filter(s -> supportedSymCipherSpecs().test(s))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewSymKeySpec> listAllSupportedSymKeySpecs(final GordianLength pKeyLen) {
        return listAllSymKeySpecs(pKeyLen)
                .stream()
                .filter(supportedSymKeySpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewSymKeyType> listAllSupportedSymKeyTypes() {
        return Arrays.stream(GordianNewSymKeyType.values())
                .filter(supportedSymKeyTypes())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewStreamCipherSpec> listAllSupportedStreamCipherSpecs(final GordianLength pKeyLen) {
        return GordianCoreStreamCipherSpecBuilder.listAllSupportedStreamCipherSpecs(pKeyLen);
    }

    @Override
    public List<GordianNewStreamKeySpec> listAllSupportedStreamKeySpecs(final GordianLength pKeyLen) {
        return listAllStreamKeySpecs(pKeyLen)
                .stream()
                .filter(supportedStreamKeySpecs())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewStreamKeyType> listAllSupportedStreamKeyTypes() {
        return Arrays.stream(GordianNewStreamKeyType.values())
                .filter(supportedStreamKeyTypes())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<GordianNewSymKeySpec> listAllSymKeySpecs(final GordianLength pKeyLen) {
        return GordianCoreSymKeySpecBuilder.listAllPossibleSymKeySpecs(pKeyLen);
    }

    @Override
    public List<GordianNewSymCipherSpec> listAllSymCipherSpecs(final GordianNewSymKeySpec pSpec) {
        return GordianCoreSymCipherSpecBuilder.listAllPossibleSymCipherSpecs(pSpec);
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     *
     * @param pKeyLen the keyLength
     * @return the list
     */
    private static List<GordianNewStreamKeySpec> listAllStreamKeySpecs(final GordianLength pKeyLen) {
        return GordianCoreStreamKeySpecBuilder.listAllPossibleStreamKeySpecs(pKeyLen);
    }
}
