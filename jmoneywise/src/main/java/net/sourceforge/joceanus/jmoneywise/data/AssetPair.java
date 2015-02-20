/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase.AssetBaseList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class representing a debit/credit pair of assets.
 * @author Tony Washer
 */
public final class AssetPair
        implements JDataFormat {
    /**
     * Id shift.
     */
    private static final int ID_SHIFT = 8;

    /**
     * Name separator.
     */
    private static final String SEP_NAME = "-";

    /**
     * Account type.
     */
    private final AssetType theAccount;

    /**
     * Partner type.
     */
    private final AssetType thePartner;

    /**
     * Asset direction.
     */
    private final AssetDirection theDirection;

    /**
     * AssetPair name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pAccount the account type
     * @param pPartner the partner type
     * @param pDirection the direction
     */
    private AssetPair(final AssetType pAccount,
                      final AssetType pPartner,
                      final AssetDirection pDirection) {
        theAccount = pAccount;
        thePartner = pPartner;
        theDirection = pDirection;
    }

    @Override
    public String formatObject() {
        return toString();
    }

    /**
     * Obtain asset type.
     * @return the asset type.
     */
    public AssetType getAccountType() {
        return theAccount;
    }

    /**
     * Obtain partner type.
     * @return the partner type.
     */
    public AssetType getPartnerType() {
        return thePartner;
    }

    /**
     * Obtain asset direction.
     * @return the asset direction.
     */
    public AssetDirection getDirection() {
        return theDirection;
    }

    /**
     * Obtain encoded id for AssetPair.
     * @return the id
     */
    public Integer getEncodedId() {
        return getEncodedId(theAccount, thePartner, theDirection);
    }

    /**
     * Obtain encoded id for AssetPair.
     * @param pAsset the asset type
     * @param pPartner the partner type
     * @param pDirection the direction
     * @return the id
     */
    public static Integer getEncodedId(final AssetType pAsset,
                                       final AssetType pPartner,
                                       final AssetDirection pDirection) {
        int myId = pAsset.getId();
        myId <<= ID_SHIFT;
        myId += pPartner.getId();
        myId <<= ID_SHIFT;
        return myId + pDirection.getId();
    }

    @Override
    public String toString() {
        /* If we have not yet built the name */
        if (theName == null) {
            /* Build it */
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theAccount);
            myBuilder.append(SEP_NAME);
            myBuilder.append(theDirection);
            myBuilder.append(SEP_NAME);
            myBuilder.append(thePartner);
            theName = myBuilder.toString();
        }

        /* return the name */
        return theName;
    }

    /**
     * Resolve DataLink.
     * @param pData the dataSet
     * @param pOwner the owning Transaction
     * @param pField the field to resolve
     * @throws JOceanusException on error
     */
    protected void resolveDataLink(final MoneyWiseData pData,
                                   final TransactionBase<?> pOwner,
                                   final JDataField pField) throws JOceanusException {
        /* Access the values */
        if (pField.equals(Transaction.FIELD_ACCOUNT)) {
            resolveDataLink(pData, pOwner, theAccount, pField);
        } else if (pField.equals(Transaction.FIELD_PARTNER)) {
            resolveDataLink(pData, pOwner, thePartner, pField);
        }
    }

    /**
     * Resolve DataLink.
     * @param pData the dataSet
     * @param pOwner the owning Transaction
     * @param pAssetType the asset type
     * @param pField the field to resolve
     * @throws JOceanusException on error
     */
    private static void resolveDataLink(final MoneyWiseData pData,
                                        final TransactionBase<?> pOwner,
                                        final AssetType pAssetType,
                                        final JDataField pField) throws JOceanusException {
        /* Handle security holding differently */
        if (pAssetType.isSecurityHolding()) {
            resolveDataLink(pOwner, pData.getSecurityHoldingsMap(), pField);
        } else {
            resolveDataLink(pOwner, getListForClass(pData, pAssetType), pField);
        }
    }

    /**
     * Access list for dataType.
     * @param pData the dataSet
     * @param pClass the Asset type class
     * @return the list
     */
    private static AssetBaseList<?> getListForClass(final MoneyWiseData pData,
                                                    final AssetType pClass) {
        /* Switch on the class */
        switch (pClass) {
            case DEPOSIT:
                return pData.getDeposits();
            case CASH:
            case AUTOEXPENSE:
                return pData.getCash();
            case LOAN:
                return pData.getLoans();
            case PORTFOLIO:
                return pData.getPortfolios();
            case SECURITY:
                return pData.getSecurities();
            case PAYEE:
                return pData.getPayees();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Resolve DataLink.
     * @param pOwner the owning Transaction
     * @param pList the item list
     * @param pField the field
     * @throws JOceanusException on error
     */
    private static void resolveDataLink(final TransactionBase<?> pOwner,
                                        final AssetBaseList<?> pList,
                                        final JDataField pField) throws JOceanusException {
        /* Access the values */
        ValueSet myValues = pOwner.getValueSet();

        /* Access value for field */
        Object myValue = myValues.getValue(pField);

        /* Convert AssetBase reference to Id */
        if (myValue instanceof AssetBase<?>) {
            myValue = ((AssetBase<?>) myValue).getId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            AssetBase<?> myItem = pList.findItemById((Integer) myValue);
            if (myItem == null) {
                pOwner.addError(Transaction.ERROR_UNKNOWN, pField);
                throw new JMoneyWiseDataException(pOwner, Transaction.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            AssetBase<?> myItem = pList.findItemByName((String) myValue);
            if (myItem == null) {
                pOwner.addError(Transaction.ERROR_UNKNOWN, pField);
                throw new JMoneyWiseDataException(pOwner, Transaction.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);
        }
    }

    /**
     * Resolve Security Holding link.
     * @param pOwner the owning Transaction
     * @param pMap the security holding map
     * @param pField the field
     * @throws JOceanusException on error
     */
    public static void resolveDataLink(final DataItem<?> pOwner,
                                       final SecurityHoldingMap pMap,
                                       final JDataField pField) throws JOceanusException {
        /* Access the values */
        ValueSet myValues = pOwner.getValueSet();

        /* Access value for field */
        Object myValue = myValues.getValue(pField);

        /* Convert SecurityHolding reference to Id */
        if (myValue instanceof SecurityHolding) {
            myValue = ((SecurityHolding) myValue).getId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            SecurityHolding myItem = pMap.findHoldingById((Integer) myValue);
            if (myItem == null) {
                pOwner.addError(DataItem.ERROR_UNKNOWN, pField);
                throw new JMoneyWiseDataException(pOwner, DataItem.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            SecurityHolding myItem = pMap.findHoldingByName((String) myValue);
            if (myItem == null) {
                pOwner.addError(DataItem.ERROR_UNKNOWN, pField);
                throw new JMoneyWiseDataException(pOwner, DataItem.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);
        }
    }

    /**
     * AssetPairManager.
     */
    public static final class AssetPairManager {
        /**
         * AssetPairMap.
         */
        private static final Map<Integer, AssetPair> MAP_PAIR = generatePairMap();

        /**
         * Generate the pair map.
         * @return the pair map.
         */
        private static Map<Integer, AssetPair> generatePairMap() {
            /* Create the new map */
            Map<Integer, AssetPair> myMap = new HashMap<Integer, AssetPair>();

            /* Loop through the Account AssetTypes */
            for (AssetType myAccount : AssetType.values()) {
                /* Check asset is allowed as base account */
                if (myAccount.isBaseAccount()) {
                    /* Loop through the Partner AssetTypes */
                    for (AssetType myPartner : AssetType.values()) {
                        /* Ignore securities */
                        if (myPartner.isSecurity()) {
                            continue;
                        }

                        /* Create the new To AssetPair and store */
                        AssetPair myPair = new AssetPair(myAccount, myPartner, AssetDirection.TO);
                        myMap.put(myPair.getEncodedId(), myPair);

                        /* Create the new From AssetPair and store */
                        myPair = new AssetPair(myAccount, myPartner, AssetDirection.FROM);
                        myMap.put(myPair.getEncodedId(), myPair);
                    }
                }
            }

            /* return the map */
            return myMap;
        }

        /**
         * Obtain default pair.
         * @return the assetPair
         */
        public AssetPair getDefaultPair() {
            /* Access default Id */
            Integer myId = getEncodedId(AssetType.DEPOSIT, AssetType.DEPOSIT, AssetDirection.TO);

            /* LookUp the relevant pair */
            return MAP_PAIR.get(myId);
        }

        /**
         * LookUp encoded Pair.
         * @param pId the encoded id
         * @return the assetPair
         */
        public AssetPair lookUpPair(final Integer pId) {
            /* LookUp the relevant pair */
            return MAP_PAIR.get(pId);
        }

        /**
         * LookUp encoded name.
         * @param pName the encoded name
         * @return the assetPair
         */
        public AssetPair lookUpName(final String pName) {
            /* Locate the first separator in the name */
            int iIndex = pName.indexOf(SEP_NAME);
            if (iIndex == -1) {
                return null;
            }

            /* Look up the AccountType */
            AssetType myAccount = checkName(pName.substring(0, iIndex));
            String myRemainder = pName.substring(iIndex + 1);

            /* Locate the second separator in the name */
            iIndex = myRemainder.indexOf(SEP_NAME);
            if (iIndex == -1) {
                return null;
            }

            /* Look up the partnerType and direction */
            AssetDirection myDirection = AssetDirection.fromName(myRemainder.substring(0, iIndex));
            AssetType myPartner = checkName(myRemainder.substring(iIndex + 1));
            if ((myAccount == null)
                || (myPartner == null)
                || (myDirection == null)) {
                return null;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(myAccount, myPartner, myDirection);
            return MAP_PAIR.get(myId);
        }

        /**
         * LookUp assetType name.
         * @param pName the name
         * @return the assetType
         */
        private AssetType checkName(final String pName) {
            /* Locate the separator in the name */
            for (AssetType myType : AssetType.values()) {
                if (pName.equals(myType.toString())) {
                    return myType;
                }
            }
            return null;
        }

        /**
         * Adjust Account type.
         * @param pCurr the current pair
         * @param pAccount the account asset
         * @return the updated assetPair
         */
        public AssetPair adjustAccount(final AssetPair pCurr,
                                       final TransactionAsset pAccount) {
            /* Access new Asset type */
            AssetType myType = getAssetType(pAccount);

            /* Handle no change */
            if (myType.equals(pCurr.getAccountType())) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(myType, pCurr.getPartnerType(), pCurr.getDirection());
            return MAP_PAIR.get(myId);
        }

        /**
         * Adjust Partner class.
         * @param pCurr the current pair
         * @param pPartner the partner asset
         * @return the updated assetPair
         */
        public AssetPair adjustPartner(final AssetPair pCurr,
                                       final TransactionAsset pPartner) {
            /* Access new Asset type */
            AssetType myType = getAssetType(pPartner);

            /* Handle no change */
            if (myType.equals(pCurr.getPartnerType())) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(pCurr.getAccountType(), myType, pCurr.getDirection());
            return MAP_PAIR.get(myId);
        }

        /**
         * Switch direction.
         * @param pCurr the current pair
         * @return the updated assetPair
         */
        public AssetPair switchDirection(final AssetPair pCurr) {
            /* LookUp the relevant pair */
            Integer myId = getEncodedId(pCurr.getAccountType(), pCurr.getPartnerType(), pCurr.getDirection().reverse());
            return MAP_PAIR.get(myId);
        }

        /**
         * Flip Assets.
         * @param pCurr the current pair
         * @return the updated assetPair
         */
        public AssetPair flipAssets(final AssetPair pCurr) {
            /* LookUp the relevant pair */
            Integer myId = getEncodedId(pCurr.getPartnerType(), pCurr.getAccountType(), pCurr.getDirection().reverse());
            return MAP_PAIR.get(myId);
        }

        /**
         * Obtain type for asset.
         * @param pAsset the asset
         * @return the class
         */
        private static AssetType getAssetType(final TransactionAsset pAsset) {
            return (pAsset == null)
                                   ? AssetType.CASH
                                   : pAsset.getAssetType();
        }
    }

    /**
     * Asset Direction.
     */
    public enum AssetDirection {
        /**
         * Payment.
         */
        TO(1),

        /**
         * From.
         */
        FROM(2);

        /**
         * The String name.
         */
        private String theName;

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Constructor.
         * @param uId the Id
         */
        private AssetDirection(final int uId) {
            theId = uId;
        }

        /**
         * Obtain class Id.
         * @return the Id
         */
        public int getId() {
            return theId;
        }

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = MoneyWiseDataResource.getKeyForAssetDirection(this).getValue();
            }

            /* return the name */
            return theName;
        }

        /**
         * Reverse.
         * @return the reversed direction
         */
        public AssetDirection reverse() {
            return this == TO
                             ? FROM
                             : TO;
        }

        /**
         * Is this the from direction?
         * @return true/false
         */
        public boolean isFrom() {
            return this == FROM;
        }

        /**
         * Is this the to direction?
         * @return true/false
         */
        public boolean isTo() {
            return this == TO;
        }

        /**
         * get value from name.
         * @param pName the name value
         * @return the corresponding enum object
         */
        private static AssetDirection fromName(final String pName) {
            for (AssetDirection myDir : values()) {
                if (pName.equals(myDir.toString())) {
                    return myDir;
                }
            }
            return null;
        }
    }
}
