/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase.AssetBaseList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing a debit/credit pair of assets.
 * @author Tony Washer
 */
public final class AssetPair
        implements MetisDataObjectFormat {
    /**
     * Id shift.
     */
    private static final int ID_SHIFT = 8;

    /**
     * Name separator.
     */
    private static final String SEP_NAME = "-";

    /**
     * Returned separator.
     */
    private static final String SEP_RETURNED = ":";

    /**
     * Account type.
     */
    private final AssetType theAccount;

    /**
     * Partner type.
     */
    private final AssetType thePartner;

    /**
     * The returnedCash type.
     */
    private final AssetType theReturnedCash;

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
        this(pAccount, pPartner, null, pDirection);
    }

    /**
     * Constructor.
     * @param pAccount the account type
     * @param pPartner the partner type
     * @param pReturned the returned cash type
     * @param pDirection the direction
     */
    private AssetPair(final AssetType pAccount,
                      final AssetType pPartner,
                      final AssetType pReturned,
                      final AssetDirection pDirection) {
        theAccount = pAccount;
        thePartner = pPartner;
        theReturnedCash = pReturned;
        theDirection = pDirection;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
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
     * Obtain returned cash type.
     * @return the returned cash type.
     */
    public AssetType getReturnedCashType() {
        return theReturnedCash;
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
        return getEncodedId(theAccount, thePartner, theReturnedCash, theDirection);
    }

    /**
     * Obtain encoded id for AssetPair.
     * @param pAsset the asset type
     * @param pPartner the partner type
     * @param pReturned the returned cash type
     * @param pDirection the direction
     * @return the id
     */
    public static Integer getEncodedId(final AssetType pAsset,
                                       final AssetType pPartner,
                                       final AssetType pReturned,
                                       final AssetDirection pDirection) {
        int myId = pReturned == null
                                     ? 0
                                     : pReturned.getId();
        myId <<= ID_SHIFT;
        myId += pAsset.getId();
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
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theAccount);
            myBuilder.append(SEP_NAME);
            myBuilder.append(theDirection);
            myBuilder.append(SEP_NAME);
            myBuilder.append(thePartner);
            if (theReturnedCash != null) {
                myBuilder.append(SEP_RETURNED);
                myBuilder.append(theReturnedCash);
            }
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
     * @throws OceanusException on error
     */
    protected void resolveDataLink(final MoneyWiseData pData,
                                   final TransactionBase<?> pOwner,
                                   final MetisLetheField pField) throws OceanusException {
        /* Access the values */
        if (pField.equals(Transaction.FIELD_ACCOUNT)) {
            resolveDataLink(pData, pOwner, theAccount, pField);
        } else if (pField.equals(Transaction.FIELD_PARTNER)) {
            resolveDataLink(pData, pOwner, thePartner, pField);
        } else if (pField.equals(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT))) {
            resolveDataLink(pData, pOwner, theReturnedCash, pField);
        }
    }

    /**
     * Resolve DataLink.
     * @param pData the dataSet
     * @param pOwner the owning Transaction
     * @param pAssetType the asset type
     * @param pField the field to resolve
     * @throws OceanusException on error
     */
    private static void resolveDataLink(final MoneyWiseData pData,
                                        final TransactionBase<?> pOwner,
                                        final AssetType pAssetType,
                                        final MetisLetheField pField) throws OceanusException {
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
    private static AssetBaseList<?, ?> getListForClass(final MoneyWiseData pData,
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
     * @throws OceanusException on error
     */
    private static void resolveDataLink(final TransactionBase<?> pOwner,
                                        final AssetBaseList<?, ?> pList,
                                        final MetisLetheField pField) throws OceanusException {
        /* Access the values */
        final MetisValueSet myValues = pOwner.getValueSet();

        /* Access value for field */
        Object myValue = myValues.getValue(pField);

        /* Convert AssetBase reference to Id */
        if (myValue instanceof AssetBase) {
            myValue = ((AssetBase<?, ?>) myValue).getId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            final AssetBase<?, ?> myItem = pList.findItemById((Integer) myValue);
            if (myItem == null) {
                pOwner.addError(Transaction.ERROR_UNKNOWN, pField);
                throw new MoneyWiseDataException(pOwner, Transaction.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            final AssetBase<?, ?> myItem = pList.findItemByName((String) myValue);
            if (myItem == null) {
                pOwner.addError(Transaction.ERROR_UNKNOWN, pField);
                throw new MoneyWiseDataException(pOwner, Transaction.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);
        }
    }

    /**
     * Resolve Security Holding link.
     * @param pOwner the owning Transaction
     * @param pMap the security holding map
     * @param pField the field
     * @throws OceanusException on error
     */
    public static void resolveDataLink(final DataItem<?> pOwner,
                                       final SecurityHoldingMap pMap,
                                       final MetisLetheField pField) throws OceanusException {
        /* Access the values */
        final MetisValueSet myValues = pOwner.getValueSet();

        /* Access value for field */
        Object myValue = myValues.getValue(pField);

        /* Convert SecurityHolding reference to Id */
        if (myValue instanceof SecurityHolding) {
            myValue = ((SecurityHolding) myValue).getId();
        }

        /* Lookup Id reference */
        if (myValue instanceof Integer) {
            final SecurityHolding myItem = pMap.findHoldingById((Integer) myValue);
            if (myItem == null) {
                pOwner.addError(DataItem.ERROR_UNKNOWN, pField);
                throw new MoneyWiseDataException(pOwner, DataItem.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            final SecurityHolding myItem = pMap.findHoldingByName((String) myValue);
            if (myItem == null) {
                pOwner.addError(DataItem.ERROR_UNKNOWN, pField);
                throw new MoneyWiseDataException(pOwner, DataItem.ERROR_RESOLUTION);
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
            final Map<Integer, AssetPair> myMap = new HashMap<>();

            /* Loop through the Account AssetTypes */
            for (AssetType myAccount : AssetType.values()) {
                /* Check asset is allowed as base account */
                if (!myAccount.isBaseAccount()) {
                    continue;
                }

                /* Loop through the Partner AssetTypes */
                for (AssetType myPartner : AssetType.values()) {
                    /* Ignore securities */
                    if (myPartner.isSecurity()) {
                        continue;
                    }

                    /* Add the standard TO/FROM pairs */
                    addPairToMap(myMap, new AssetPair(myAccount, myPartner, AssetDirection.TO));
                    addPairToMap(myMap, new AssetPair(myAccount, myPartner, AssetDirection.FROM));

                    /* If the account is security holding */
                    if (myAccount.isSecurityHolding()) {
                        /* Allow returned cash for Deposit and portfolio */
                        addPairToMap(myMap, new AssetPair(myAccount, myPartner, AssetType.DEPOSIT, AssetDirection.TO));
                        addPairToMap(myMap, new AssetPair(myAccount, myPartner, AssetType.PORTFOLIO, AssetDirection.TO));
                    }
                }
            }

            /* return the map */
            return myMap;
        }

        /**
         * Add pair to map.
         * @param pMap the pair map
         * @param pPair the pair to add
         */
        private static void addPairToMap(final Map<Integer, AssetPair> pMap,
                                         final AssetPair pPair) {
            pMap.put(pPair.getEncodedId(), pPair);
        }

        /**
         * Obtain default pair.
         * @return the assetPair
         */
        public AssetPair getDefaultPair() {
            /* Access default Id */
            final Integer myId = getEncodedId(AssetType.DEPOSIT, AssetType.DEPOSIT, null, AssetDirection.TO);

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

            /* Look up the AccountType and remove it */
            final AssetType myAccount = checkName(pName.substring(0, iIndex));
            String myRemainder = pName.substring(iIndex + 1);

            /* Locate the second separator in the name */
            iIndex = myRemainder.indexOf(SEP_NAME);
            if (iIndex == -1) {
                return null;
            }

            /* Look up the direction and remove it */
            final AssetDirection myDirection = AssetDirection.fromName(myRemainder.substring(0, iIndex));
            myRemainder = myRemainder.substring(iIndex + 1);

            /* Locate the returned separator in the name */
            AssetType myReturned = null;
            iIndex = myRemainder.indexOf(SEP_RETURNED);
            if (iIndex != -1) {
                myReturned = checkName(myRemainder.substring(iIndex + 1));
                if (myReturned == null) {
                    return null;
                }
                myRemainder = myRemainder.substring(0, iIndex);
            }

            /* Resolve the partner */
            final AssetType myPartner = checkName(myRemainder);
            if ((myAccount == null)
                || (myPartner == null)
                || (myDirection == null)) {
                return null;
            }

            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(myAccount, myPartner, myReturned, myDirection);
            return MAP_PAIR.get(myId);
        }

        /**
         * LookUp assetType name.
         * @param pName the name
         * @return the assetType
         */
        private static AssetType checkName(final String pName) {
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
            final AssetType myType = getAssetType(pAccount);

            /* Handle no change */
            if (myType.equals(pCurr.getAccountType())) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(myType, pCurr.getPartnerType(), pCurr.getReturnedCashType(), pCurr.getDirection());
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
            final AssetType myType = getAssetType(pPartner);

            /* Handle no change */
            if (myType.equals(pCurr.getPartnerType())) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(pCurr.getAccountType(), myType, pCurr.getReturnedCashType(), pCurr.getDirection());
            return MAP_PAIR.get(myId);
        }

        /**
         * Adjust Returned class.
         * @param pCurr the current pair
         * @param pReturned the returned asset
         * @return the updated assetPair
         */
        public AssetPair adjustReturned(final AssetPair pCurr,
                                        final TransactionAsset pReturned) {
            /* Access new Asset type */
            final AssetType myType = pReturned == null
                                                       ? null
                                                       : getAssetType(pReturned);

            /* Handle no change */
            if (MetisDataDifference.isEqual(myType, pCurr.getReturnedCashType())) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(pCurr.getAccountType(), pCurr.getPartnerType(), myType, pCurr.getDirection());
            return MAP_PAIR.get(myId);
        }

        /**
         * Switch direction.
         * @param pCurr the current pair
         * @return the updated assetPair
         */
        public AssetPair switchDirection(final AssetPair pCurr) {
            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(pCurr.getAccountType(), pCurr.getPartnerType(), pCurr.getReturnedCashType(), pCurr.getDirection().reverse());
            return MAP_PAIR.get(myId);
        }

        /**
         * Flip Assets.
         * @param pCurr the current pair
         * @return the updated assetPair
         */
        public AssetPair flipAssets(final AssetPair pCurr) {
            /* LookUp the relevant pair */
            final Integer myId = getEncodedId(pCurr.getPartnerType(), pCurr.getAccountType(), pCurr.getReturnedCashType(), pCurr.getDirection().reverse());
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
        AssetDirection(final int uId) {
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
