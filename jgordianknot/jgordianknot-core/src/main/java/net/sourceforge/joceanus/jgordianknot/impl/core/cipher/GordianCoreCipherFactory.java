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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    public BiPredicate<GordianSymCipherSpec, Boolean> supportedSymCipherSpecs() {
        return this::validSymCipherSpec;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedSymKeyTypes() {
        return this::validSymKeyType;
    }

    @Override
    public Predicate<GordianStreamKeyType> supportedStreamKeyTypes() {
        return this::validStreamKeyType;
    }

    /**
     * Create a wrapCipher.
     * @param pBlockCipher the underlying block cipher
     * @return the wrapCipher
     */
    protected GordianWrapper createKeyWrapper(final GordianCipher<GordianSymKeySpec> pBlockCipher) {
        return new GordianCoreWrapper(theFactory, (GordianCoreCipher<GordianSymKeySpec>) pBlockCipher);
    }

    /**
     * Check SymKeySpec.
     * @param pSymKeySpec the symKeySpec
     * @return true/false
     */
    public boolean validSymKeySpec(final GordianSymKeySpec pSymKeySpec) {
        /* Reject invalid keySpec */
        if (pSymKeySpec == null || !pSymKeySpec.isValid()) {
            return false;
        }

        /* Access details */
        final GordianLength myLen = pSymKeySpec.getBlockLength();
        final boolean isRestricted = theFactory.isRestricted();

        /* Reject restrictedSpecs where the block length is too large */
        if (isRestricted
                && myLen.getLength() > GordianLength.LEN_128.getLength()) {
            return false;
        }

        /* Reject Speck-64 for unrestricted */
        if (!isRestricted
                && GordianSymKeyType.SPECK.equals(pSymKeySpec.getSymKeyType())
                && GordianLength.LEN_64.equals(myLen)) {
            return false;
        }

        /* Check validity */
        final GordianSymKeyType myType = pSymKeySpec.getSymKeyType();
        return supportedSymKeyTypes().test(myType)
                && myType.isLengthValid(myLen);
    }

    /**
     * validate the symCipherSpec.
     * @param pCipherSpec the cipherSpec.
     * @param isAAD is this cipherSpec for an AADCipher?
     * @return true/false
     */
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec,
                                         final Boolean isAAD) {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            return false;
        }

        /* Reject null modes and wrong AAD modes */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
        if (myMode == null
                || isAAD != myMode.isAAD()) {
            return false;
        }

        /* Check that the mode is valid for the keyType */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        final GordianSymKeyType myKeyType = myKeySpec.getSymKeyType();
        if (!myMode.validForSymKey(myKeyType)) {
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
     * @throws OceanusException on error
     */
    protected void checkKeySpec(final GordianKeySpec pKeySpec) throws OceanusException {
        /* Assume failure */
        boolean bValid = false;

        /* If this is a streamKey */
        if (pKeySpec instanceof GordianStreamKeyType) {
            /* Check validity of StreamKey */
            bValid = supportedStreamKeyTypes().test((GordianStreamKeyType) pKeySpec);

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
     * @throws OceanusException on error
     */
    public void checkSymKeySpec(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeySpecs().test(pKeySpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    /**
     * Check the symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @param isAAD should the cipher be AAD or not?
     * @throws OceanusException on error
     */
    public void checkSymCipherSpec(final GordianSymCipherSpec pCipherSpec,
                                   final boolean isAAD) throws OceanusException {
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
        if (!validSymCipherSpec(pCipherSpec, isAAD)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Check the streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    public void checkStreamCipherSpec(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Reject invalid cipherSpec */
        if (pCipherSpec == null || !pCipherSpec.isValid()) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }

        /* Check validity of StreamKey */
        final GordianStreamKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedStreamKeyTypes().test(myKeyType)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(myKeyType));
        }
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @return true/false
     */
    public boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return pKeyType != null && validSymKeyTypeForRestriction(pKeyType, theFactory.isRestricted());
    }

    /**
     * Check SymKeyType.
     * @param pKeyType the symKeyType
     * @param pRestricted is the symKeyType restricted?
     * @return true/false
     */
    public static boolean validSymKeyTypeForRestriction(final GordianSymKeyType pKeyType,
                                                        final boolean pRestricted) {
        return pKeyType.validForRestriction(pRestricted);
    }

    /**
     * Check StreamKeyType.
     * @param pKeyType the streamKeyType
     * @return true/false
     */
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        return pKeyType != null && pKeyType.validForRestriction(theFactory.isRestricted());
    }

    /**
     * Check standard block symKeyType.
     * @param pKeyType the symKeyType
     * @param pRestricted is the symKeyType restricted?
     * @return true/false
     */
    public static boolean validStdBlockSymKeyTypeForRestriction(final GordianSymKeyType pKeyType,
                                                                final boolean pRestricted) {
        return validSymKeyTypeForRestriction(pKeyType, pRestricted)
                && pKeyType.getDefaultLength().equals(GordianLength.LEN_128);
    }
}