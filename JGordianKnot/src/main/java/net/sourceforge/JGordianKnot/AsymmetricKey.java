/*******************************************************************************
 * JGordianKnot: Security Suite
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.JGordianKnot;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JGordianKnot.DataHayStack.AsymModeNeedle;
import net.sourceforge.JGordianKnot.DataHayStack.SymKeyNeedle;

/**
 * Asymmetric Key class. Note that the RSA asymmetric key cannot be used for bulk encryption due to
 * limitations in the RSA implementation. The Asymmetric Keys should only be used for Signatures and Wrapping
 * keys.
 */
public class AsymmetricKey implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AsymmetricKey.class.getSimpleName());

    /**
     * Field ID for Key Mode.
     */
    public static final JDataField FIELD_KEYMODE = FIELD_DEFS.declareLocalField("KeyMode");

    /**
     * Field ID for Cipher Map.
     */
    public static final JDataField FIELD_CIPHERMAP = FIELD_DEFS.declareLocalField("CipherMap");

    /**
     * Field ID for Symmetric Key Map.
     */
    public static final JDataField FIELD_SYMKEYMAP = FIELD_DEFS.declareLocalField("SymKeyMap");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_KEYMODE.equals(pField)) {
            return theKeyMode;
        }
        if (FIELD_CIPHERMAP.equals(pField)) {
            return theCipherMap;
        }
        if (FIELD_SYMKEYMAP.equals(pField)) {
            return theSymKeyMap;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return "AsymmetricKey(" + theKeyType + ")";
    }

    /**
     * Encoded Size for Public Keys.
     */
    public static final int PUBLICSIZE = 512;

    /**
     * Encrypted Size for Private Keys.
     */
    public static final int PRIVATESIZE = 1280;

    /**
     * The Public/Private Key Pair.
     */
    private final KeyPair theKeyPair;

    /**
     * The Key Mode.
     */
    private final AsymKeyMode theKeyMode;

    /**
     * The Key Type.
     */
    private final AsymKeyType theKeyType;

    /**
     * The security generator.
     */
    private final SecurityGenerator theGenerator;

    /**
     * The Key Agreement object.
     */
    private KeyAgreement theKeyAgreement = null;

    /**
     * The External Definition.
     */
    private final byte[] theExternalKeyDef;

    /**
     * The Encoded Public Key.
     */
    private final byte[] thePublicKeyDef;

    /**
     * The Encoded Private Key.
     */
    private final byte[] thePrivateKeyDef;

    /**
     * The CipherSet.
     */
    private CipherSet theCipherSet = null;

    /**
     * The CipherSet map.
     */
    private final Map<AsymmetricKey, CipherSet> theCipherMap;

    /**
     * The Symmetric Key Map.
     */
    private final Map<SymmetricKey, byte[]> theSymKeyMap;

    /**
     * Obtain the Asymmetric Key type.
     * @return the key type
     */
    public AsymKeyType getKeyType() {
        return theKeyMode.getAsymKeyType();
    }

    /**
     * Obtain the Asymmetric Key mode.
     * @return the key mode
     */
    public AsymKeyMode getKeyMode() {
        return theKeyMode;
    }

    /**
     * Is the Asymmetric Key a public only key.
     * @return true/false
     */
    public boolean isPublicOnly() {
        return (theKeyPair.getPrivate() == null);
    }

    /**
     * Obtain the Private Key.
     * @return the private key
     */
    protected PrivateKey getPrivateKey() {
        return theKeyPair.getPrivate();
    }

    /**
     * Obtain the Public Key.
     * @return the private key
     */
    protected PublicKey getPublicKey() {
        return theKeyPair.getPublic();
    }

    /**
     * Obtain the External Key definition.
     * @return the key definition
     */
    public byte[] getExternalDef() {
        return theExternalKeyDef;
    }

    /**
     * Constructor for new key.
     * @param pGenerator the security generator
     * @param pKeyMode the key mode
     * @throws JDataException on error
     */
    protected AsymmetricKey(final SecurityGenerator pGenerator,
                            final AsymKeyMode pKeyMode) throws JDataException {
        /* Store the key mode and the generator */
        theKeyMode = pKeyMode;
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;

        /* Generate the new KeyPair */
        theKeyPair = theGenerator.generateKeyPair(theKeyType);

        /* Access the encoded formats */
        thePrivateKeyDef = getPrivateKey().getEncoded();
        thePublicKeyDef = getPublicKey().getEncoded();

        /* Determine the external definition */
        AsymModeNeedle myNeedle = new AsymModeNeedle(theKeyMode, thePublicKeyDef);
        theExternalKeyDef = myNeedle.getExternal();

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic()) {
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        } else {
            theCipherMap = null;
        }

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();

        /* Check whether the PublicKey is too large */
        if (theExternalKeyDef.length > PUBLICSIZE) {
            throw new JDataException(ExceptionClass.DATA, "PublicKey too large: " + theExternalKeyDef.length);
        }
    }

    /**
     * Constructor from public KeySpec.
     * @param pGenerator the security generator
     * @param pKeySpec the public KeySpec
     * @throws JDataException on error
     */
    protected AsymmetricKey(final SecurityGenerator pGenerator,
                            final byte[] pKeySpec) throws JDataException {
        /* Parse the KeySpec */
        AsymModeNeedle myNeedle = new AsymModeNeedle(pKeySpec);

        /* Store the key mode and the generator */
        theKeyMode = myNeedle.getAsymKeyMode();
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;
        theExternalKeyDef = Arrays.copyOf(pKeySpec, pKeySpec.length);

        /* Derive the KeyPair */
        theKeyPair = theGenerator.deriveKeyPair(theKeyType, null, myNeedle.getPublicKey());

        /* Access the encoded formats */
        thePublicKeyDef = getPublicKey().getEncoded();
        thePrivateKeyDef = null;

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic()) {
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        } else {
            theCipherMap = null;
        }

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    /**
     * Constructor from full specification.
     * @param pGenerator the security generator
     * @param pPrivateKey the private KeySpec
     * @param pKeySpec the public KeySpec
     * @throws JDataException on error
     */
    protected AsymmetricKey(final SecurityGenerator pGenerator,
                            final byte[] pPrivateKey,
                            final byte[] pKeySpec) throws JDataException {
        /* Parse the KeySpec */
        AsymModeNeedle myNeedle = new AsymModeNeedle(pKeySpec);

        /* Store the key mode and the generator */
        theKeyMode = myNeedle.getAsymKeyMode();
        theKeyType = theKeyMode.getAsymKeyType();
        theGenerator = pGenerator;
        theExternalKeyDef = Arrays.copyOf(pKeySpec, pKeySpec.length);

        /* Derive the KeyPair */
        theKeyPair = theGenerator.deriveKeyPair(theKeyType, pPrivateKey, myNeedle.getPublicKey());

        /* Access the encoded formats */
        thePrivateKeyDef = getPrivateKey().getEncoded();
        thePublicKeyDef = getPublicKey().getEncoded();

        /* Create the map for elliptic keys */
        if (theKeyType.isElliptic()) {
            theCipherMap = new HashMap<AsymmetricKey, CipherSet>();
        } else {
            theCipherMap = null;
        }

        /* Build the SymmetricKey map */
        theSymKeyMap = new HashMap<SymmetricKey, byte[]>();
    }

    @Override
    public int hashCode() {
        /* Calculate and return the hashCode for this asymmetric key */
        int hashCode = 1;
        if (thePrivateKeyDef != null) {
            hashCode += Arrays.hashCode(thePrivateKeyDef);
        }
        hashCode *= SecurityGenerator.HASH_PRIME;
        hashCode += theKeyMode.hashCode();
        hashCode *= SecurityGenerator.HASH_PRIME;
        hashCode += Arrays.hashCode(thePublicKeyDef);
        return hashCode;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is an Asymmetric Key */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        AsymmetricKey myThat = (AsymmetricKey) pThat;

        /* Not equal if different modes */
        if (!myThat.getKeyMode().equals(theKeyMode)) {
            return false;
        }

        /* Ensure that the private/public keys are identical */
        if (!Arrays.equals(myThat.thePrivateKeyDef, thePrivateKeyDef)) {
            return false;
        }
        return (Arrays.equals(myThat.thePublicKeyDef, thePublicKeyDef));
    }

    /**
     * Get CipherSet for partner Elliptic Curve.
     * @param pPartner partner asymmetric key
     * @return the new CipherSet
     * @throws JDataException on error
     */
    public CipherSet getCipherSet(final AsymmetricKey pPartner) throws JDataException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic()) || (pPartner.getKeyType() != theKeyType)) {
            throw new JDataException(ExceptionClass.LOGIC,
                    "Shared Keys require both partners to be similar Elliptic");
        }

        /* Look for an already resolved CipherSet */
        CipherSet mySet = theCipherMap.get(pPartner);

        /* Return it if found */
        if (mySet != null) {
            return mySet;
        }

        /* Access the Shared Secret */
        byte[] mySecret = getSharedSecret(pPartner);

        /* Build the CipherSet */
        mySet = new CipherSet(theGenerator, theKeyMode);

        /* Apply the Secret */
        mySet.buildCiphers(mySecret);

        /* Store the Set into the map */
        theCipherMap.put(pPartner, mySet);

        /* Return the Cipher Set */
        return mySet;
    }

    /**
     * Get CipherSet for internal Elliptic Curve.
     * @return the cipher set
     * @throws JDataException on error
     */
    public CipherSet getCipherSet() throws JDataException {
        /* Return PreExisting set */
        if (theCipherSet != null) {
            return theCipherSet;
        }

        /* Build the internal CipherSet */
        theCipherSet = getCipherSet(this);

        /* Return the Cipher Set */
        return theCipherSet;
    }

    /**
     * derive a SymmetricKey from secured key definition.
     * @param pSecuredKeyDef the secured key definition
     * @return the Symmetric key
     * @throws JDataException on error
     */
    public SymmetricKey deriveSymmetricKey(final byte[] pSecuredKeyDef) throws JDataException {
        SymmetricKey mySymKey;

        /* Cannot unwrap unless we have the private key */
        if (isPublicOnly()) {
            throw new JDataException(ExceptionClass.LOGIC, "Cannot unwrap without private key");
        }

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                CipherSet mySet = getCipherSet();

                /* Unwrap the Key */
                mySymKey = mySet.deriveSymmetricKey(pSecuredKeyDef);

                /* else we use RAS semantics */
            } else {
                /* Initialise the cipher */
                Cipher myCipher = theGenerator.accessCipher(theKeyType.getAlgorithm());
                myCipher.init(Cipher.UNWRAP_MODE, getPrivateKey());

                /* Parse the KeySpec */
                SymKeyNeedle myNeedle = new SymKeyNeedle(pSecuredKeyDef);

                /* unwrap the key */
                SymKeyType myType = myNeedle.getSymKeyType();
                SecretKey myKey = (SecretKey) myCipher.unwrap(myNeedle.getEncodedKey(),
                                                              myType.getAlgorithm(), Cipher.SECRET_KEY);

                /* Build the symmetric key */
                mySymKey = new SymmetricKey(theGenerator, myKey, myType);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to unwrap key", e);
        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to unwrap key", e);
        }

        /* Add the key definition to the map */
        theSymKeyMap.put(mySymKey, pSecuredKeyDef);

        /* Return the new key */
        return mySymKey;
    }

    /**
     * Get the Secured Key Definition for a Symmetric Key.
     * @param pKey the Symmetric Key to secure
     * @return the secured key definition
     * @throws JDataException on error
     */
    public byte[] secureSymmetricKey(final SymmetricKey pKey) throws JDataException {
        byte[] myWrappedKey;

        /* Look for an entry in the map and return it if found */
        myWrappedKey = theSymKeyMap.get(pKey);
        if (myWrappedKey != null) {
            return myWrappedKey;
        }

        /* Protect against exceptions */
        try {
            /* If we are elliptic */
            if (theKeyType.isElliptic()) {
                /* Access the internal CipherSet */
                CipherSet mySet = getCipherSet();

                /* Wrap the Key */
                myWrappedKey = mySet.secureSymmetricKey(pKey);

                /* else we are using RSA semantics */
            } else {
                /* Initialise the cipher */
                Cipher myCipher = theGenerator.accessCipher(theKeyType.getAlgorithm());
                myCipher.init(Cipher.WRAP_MODE, getPublicKey());

                /* wrap the key */
                myWrappedKey = myCipher.wrap(pKey.getSecretKey());

                /* Determine the external definition */
                SymKeyNeedle myNeedle = new SymKeyNeedle(pKey.getKeyType(), myWrappedKey);
                myWrappedKey = myNeedle.getExternal();
            }

        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Invalid key", e);

        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Invalid BlockSize", e);
        }

        /* Add the key definition to the map */
        theSymKeyMap.put(pKey, myWrappedKey);

        /* Return to caller */
        return myWrappedKey;
    }

    /**
     * Obtain shared secret for partner Asymmetric Key.
     * @param pPartner partner asymmetric key
     * @return the shared secret
     * @throws JDataException on error
     */
    private synchronized byte[] getSharedSecret(final AsymmetricKey pPartner) throws JDataException {
        /* Both keys must be elliptic */
        if ((!theKeyType.isElliptic()) || (!theKeyMode.equals(pPartner.getKeyMode()))) {
            throw new JDataException(ExceptionClass.LOGIC,
                    "Shared Keys require both partners to be similar Elliptic");
        }

        /* Cannot generate unless we have the private key */
        if (isPublicOnly()) {
            throw new JDataException(ExceptionClass.LOGIC, "Cannot generate secret without private key");
        }

        /* Protect against exceptions */
        try {
            /* If we do not currently have a key Agreement */
            if (theKeyAgreement == null) {
                /* Create the key agreement */
                theKeyAgreement = theGenerator.accessKeyAgreement("ECDH");
            }

            /* Process the key agreement */
            theKeyAgreement.init(getPrivateKey());
            theKeyAgreement.doPhase(pPartner.getPublicKey(), true);

            /* Generate the secret */
            return theKeyAgreement.generateSecret();

            /* Handle exceptions */
        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Failed to negotiate key agreement", e);
        }
    }

    /**
     * Obtain a signature for this key.
     * @param bSign initialise for signature rather than verify
     * @return the signature object
     * @throws JDataException on error
     */
    public Signature getSignature(final boolean bSign) throws JDataException {
        /* Cannot sign unless we have the private key */
        if ((bSign) && (isPublicOnly())) {
            throw new JDataException(ExceptionClass.LOGIC, "Cannot sign without private key");
        }

        /* Protect against exceptions */
        try {
            /* Create a signature */
            Signature mySignature = theGenerator.accessSignature(theKeyType.getSignature());
            if (bSign) {
                mySignature.initSign(getPrivateKey(), theGenerator.getRandom());
            } else {
                mySignature.initVerify(getPublicKey());
            }

            /* Complete the signature */
            return mySignature;

            /* Catch exceptions */
        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, "Exception building signature", e);
        }
    }

    /**
     * Encrypt string.
     * @param pString string to encrypt
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    public byte[] encryptString(final String pString,
                                final AsymmetricKey pTarget) throws JDataException {
        /* Target must be identical key type */
        if (!theKeyMode.equals(pTarget.getKeyMode())) {
            throw new JDataException(ExceptionClass.LOGIC,
                    "Asymmetric Encryption must be between similar partners");
        }

        /* If we are elliptic */
        if (theKeyType.isElliptic()) {
            /* Access the target CipherSet */
            CipherSet mySet = getCipherSet(pTarget);

            /* Encrypt the string */
            return mySet.encryptString(pString);

            /* else handle RSA semantics */
        } else {
            return encryptRSAString(pString, pTarget);
        }

    }

    /**
     * Encrypt RSA string.
     * @param pString string to encrypt
     * @param pTarget target partner of encryption
     * @return Encrypted bytes
     * @throws JDataException on error
     */
    private byte[] encryptRSAString(final String pString,
                                    final AsymmetricKey pTarget) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create the cipher */
            Cipher myCipher = theGenerator.accessCipher(theKeyType.getCipher());
            myCipher.init(Cipher.ENCRYPT_MODE, pTarget.getPublicKey());

            /* Convert the string to a byte array */
            byte[] myBytes = DataConverter.stringToByteArray(pString);

            /* Determine the block sizes */
            int iBlockSize = myCipher.getBlockSize();
            int iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = myBytes.length;
            int iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            byte[] myOutput = new byte[iNumBlocks * iOutSize];

            /* Initialise offsets */
            int iOffset = 0;
            int iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                int iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize) {
                    iNumBytes = iBlockSize;
                }

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(myBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length) {
                myOutput = Arrays.copyOf(myOutput, iOutOffs);
            }

            /* Return to caller */
            return myOutput;
        } catch (ShortBufferException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }

    /**
     * Decrypt string.
     * @param pBytes encrypted string to decrypt
     * @param pSource source partner of encryption
     * @return Decrypted string
     * @throws JDataException on error
     */
    public String decryptString(final byte[] pBytes,
                                final AsymmetricKey pSource) throws JDataException {
        /* Cannot decrypt unless we have the private key */
        if (isPublicOnly()) {
            throw new JDataException(ExceptionClass.LOGIC, "Cannot decrypt without private key");
        }

        /* Source must be identical key type */
        if (!theKeyMode.equals(pSource.getKeyMode())) {
            throw new JDataException(ExceptionClass.LOGIC,
                    "Asymmetric Encryption must be between similar partners");
        }

        /* If we are elliptic */
        if (theKeyType.isElliptic()) {
            /* Access the required CipherSet */
            CipherSet mySet = getCipherSet(pSource);

            /* Decrypt the string */
            return mySet.decryptString(pBytes);

            /* else handle RSA semantics */
        } else {
            return decryptRSAString(pBytes);
        }
    }

    /**
     * Decrypt RSA string.
     * @param pBytes encrypted string to decrypt
     * @return Decrypted string
     * @throws JDataException on error
     */
    private String decryptRSAString(final byte[] pBytes) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create the cipher */
            Cipher myCipher = theGenerator.accessCipher(theKeyType.getCipher());
            myCipher.init(Cipher.DECRYPT_MODE, getPrivateKey());

            /* Determine the block sizes */
            int iBlockSize = myCipher.getBlockSize();
            int iOutSize = myCipher.getOutputSize(iBlockSize);

            /* Determine the number of blocks */
            int iDataLen = pBytes.length;
            int iNumBlocks = 1 + ((iDataLen - 1) / iBlockSize);

            /* Allocate the output buffer */
            byte[] myOutput = new byte[iNumBlocks * iOutSize];

            /* Initialise offsets */
            int iOffset = 0;
            int iOutOffs = 0;

            /* Loop through the bytes in units of iBlockSize */
            while (iDataLen > 0) {
                /* Determine the length of data to process */
                int iNumBytes = iDataLen;
                if (iNumBytes > iBlockSize) {
                    iNumBytes = iBlockSize;
                }

                /* Encrypt the data */
                iOutSize = myCipher.doFinal(pBytes, iOffset, iNumBytes, myOutput, iOutOffs);

                /* Adjust offsets */
                iDataLen -= iNumBytes;
                iOffset += iNumBytes;
                iOutOffs += iOutSize;
            }

            /* Adjust output array correctly */
            if (iOutOffs < myOutput.length) {
                myOutput = Arrays.copyOf(myOutput, iOutOffs);
            }

            /* Create the string */
            return DataConverter.byteArrayToString(myOutput);
        } catch (ShortBufferException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (IllegalBlockSizeException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (BadPaddingException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);

        } catch (InvalidKeyException e) {
            throw new JDataException(ExceptionClass.CRYPTO, e.getMessage(), e);
        }
    }
}
