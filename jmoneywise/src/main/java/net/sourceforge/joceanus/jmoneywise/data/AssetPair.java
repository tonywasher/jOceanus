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
import net.sourceforge.joceanus.jmoneywise.data.AssetBase.AssetBaseList;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
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
     * Debit type.
     */
    private final AssetType theDebit;

    /**
     * Credit type.
     */
    private final AssetType theCredit;

    @Override
    public String formatObject() {
        return toString();
    }

    /**
     * Obtain debit class.
     * @return the debit class.
     */
    public AssetType getDebitClass() {
        return theDebit;
    }

    /**
     * Obtain credit class.
     * @return the debit class.
     */
    public AssetType getCreditClass() {
        return theCredit;
    }

    /**
     * Obtain encoded id for AssetPair.
     * @return the id
     */
    public Integer getEncodedId() {
        return getEncodedId(theDebit, theCredit);
    }

    /**
     * Obtain encoded id for AssetPair.
     * @param pDebit the debit class
     * @param pCredit the credit class
     * @return the id
     */
    public static Integer getEncodedId(final AssetType pDebit,
                                       final AssetType pCredit) {
        return (pDebit.getId() << ID_SHIFT) + pCredit.getId();
    }

    /**
     * Constructor.
     * @param pDebit the debit class
     * @param pCredit the credit class
     */
    private AssetPair(final AssetType pDebit,
                      final AssetType pCredit) {
        theDebit = pDebit;
        theCredit = pCredit;
    }

    @Override
    public String toString() {
        return theDebit + SEP_NAME + theCredit;
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
        if (pField.equals(Transaction.FIELD_DEBIT)) {
            resolveDataLink(pOwner, getListForClass(pData, theDebit), pField);
        } else if (pField.equals(Transaction.FIELD_CREDIT)) {
            resolveDataLink(pOwner, getListForClass(pData, theCredit), pField);
        }
    }

    /**
     * Resolve Debit DataLink.
     * @param pData the dataSet
     * @param pClass the Asset type class
     * @return the list
     */
    private AssetBaseList<?> getListForClass(final MoneyWiseData pData,
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
    private void resolveDataLink(final TransactionBase<?> pOwner,
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
                throw new JPrometheusDataException(pOwner, Transaction.ERROR_RESOLUTION);
            }
            myValues.setValue(pField, myItem);

            /* Lookup Name reference */
        } else if (myValue instanceof String) {
            AssetBase<?> myItem = pList.findItemByName((String) myValue);
            if (myItem == null) {
                pOwner.addError(Transaction.ERROR_UNKNOWN, pField);
                throw new JPrometheusDataException(pOwner, Transaction.ERROR_RESOLUTION);
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

            /* Loop through the Debit AssetTypes */
            for (AssetType myDebit : AssetType.values()) {
                /* Loop through the Credit AssetTypes */
                for (AssetType myCredit : AssetType.values()) {
                    /* Create the new AssetPair */
                    AssetPair myPair = new AssetPair(myDebit, myCredit);

                    /* Store it in the map */
                    myMap.put(myPair.getEncodedId(), myPair);
                }
            }

            /* return the map */
            return myMap;
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
            /* Locate the separator in the name */
            int iIndex = pName.indexOf(SEP_NAME);
            if (iIndex == -1) {
                return null;
            }

            /* Look up the AssetTypeClasses */
            AssetType myDebit = checkName(pName.substring(0, iIndex));
            AssetType myCredit = checkName(pName.substring(iIndex + 1));
            if ((myDebit == null) || (myCredit == null)) {
                return null;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(myDebit, myCredit);
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
         * Adjust Debit class.
         * @param pCurr the current pair
         * @param pDebit the debit asset
         * @return the updated assetPair
         */
        public AssetPair adjustDebit(final AssetPair pCurr,
                                     final AssetBase<?> pDebit) {
            /* Access new Asset class */
            AssetType myClass = getAssetClass(pDebit);

            /* Handle no change */
            if (pCurr.getDebitClass() == myClass) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(myClass, pCurr.getCreditClass());
            return MAP_PAIR.get(myId);
        }

        /**
         * Adjust Credit class.
         * @param pCurr the current pair
         * @param pCredit the credit asset
         * @return the updated assetPair
         */
        public AssetPair adjustCredit(final AssetPair pCurr,
                                      final AssetBase<?> pCredit) {
            /* Access new Asset class */
            AssetType myClass = getAssetClass(pCredit);

            /* Handle no change */
            if (pCurr.getCreditClass() == myClass) {
                return pCurr;
            }

            /* LookUp the relevant pair */
            Integer myId = getEncodedId(pCurr.getDebitClass(), myClass);
            return MAP_PAIR.get(myId);
        }

        /**
         * Obtain class for asset.
         * @param pAsset the asset
         * @return the class
         */
        private static AssetType getAssetClass(final AssetBase<?> pAsset) {
            return (pAsset == null)
                                   ? AssetType.CASH
                                   : pAsset.getAssetType();
        }
    }
}
