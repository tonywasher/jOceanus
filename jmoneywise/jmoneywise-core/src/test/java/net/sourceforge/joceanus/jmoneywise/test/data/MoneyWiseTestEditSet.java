/* *****************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import java.util.Iterator;

import org.junit.jupiter.api.Assertions;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseDepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransInfoType;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataList.PrometheusListStyle;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusListKey;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

public class MoneyWiseTestEditSet {
    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseTestEditSet(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Check separate editSets.
     * @throws OceanusException on error
     */
    void checkSeparateEditSets(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Craete view */
        final MoneyWiseView myView = new MoneyWiseView(pToolkit, new MoneyWiseUKTaxYearCache());
        myView.setData(theDataSet);

        /* Build and validate statics */
        PrometheusEditSet myEditSet = new PrometheusEditSet(myView);
        populateStaticsEditSet(myEditSet);
        validateEditSet(myEditSet);

        /* Build and validate categories */
        myEditSet = new PrometheusEditSet(myView);
        populateCategoryEditSet(myEditSet);
        validateEditSet(myEditSet);

        /* Build and validate accounts */
        myEditSet = new PrometheusEditSet(myView);
        populateAccountEditSet(myEditSet);
        validateEditSet(myEditSet);

        /* Build and validate transactions */
        myEditSet = new PrometheusEditSet(myView);
        populateTransEditSet(myEditSet);
        validateEditSet(myEditSet);
    }

    /**
     * Check separate editSets.
     * @throws OceanusException on error
     */
    void checkCombinedEditSet(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Craete view */
        final MoneyWiseView myView = new MoneyWiseView(pToolkit, new MoneyWiseUKTaxYearCache());
        myView.setData(theDataSet);

        /* Build and validate editSet */
        PrometheusEditSet myEditSet = new PrometheusEditSet(myView);
        populateStaticsEditSet(myEditSet);
        populateCategoryEditSet(myEditSet);
        populateAccountEditSet(myEditSet);
        populateTransEditSet(myEditSet);
        validateEditSet(myEditSet);
    }

    /**
     * Populate editSet for statics.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void populateStaticsEditSet(final PrometheusEditSet pEditSet) throws OceanusException  {
        /* Register account types */
        final PrometheusEditEntry<MoneyWiseDepositCategoryType> myDepCats = pEditSet.registerType(MoneyWiseStaticDataType.DEPOSITTYPE);
        myDepCats.setDataList(theDataSet.getDepositCategoryTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseCashCategoryType> myCashCats = pEditSet.registerType(MoneyWiseStaticDataType.CASHTYPE);
        myCashCats.setDataList(theDataSet.getCashCategoryTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseLoanCategoryType> myLoanCats = pEditSet.registerType(MoneyWiseStaticDataType.LOANTYPE);
        myLoanCats.setDataList(theDataSet.getLoanCategoryTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWisePayeeType> myPayTypes = pEditSet.registerType(MoneyWiseStaticDataType.PAYEETYPE);
        myPayTypes.setDataList(theDataSet.getPayeeTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWisePortfolioType> myPortTypes = pEditSet.registerType(MoneyWiseStaticDataType.PORTFOLIOTYPE);
        myPortTypes.setDataList(theDataSet.getPortfolioTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseSecurityType> mySecTypes = pEditSet.registerType(MoneyWiseStaticDataType.SECURITYTYPE);
        mySecTypes.setDataList(theDataSet.getSecurityTypes().deriveList(PrometheusListStyle.EDIT));

        /* Register remaining types */
        final PrometheusEditEntry<MoneyWiseTransCategoryType> myTranCats = pEditSet.registerType(MoneyWiseStaticDataType.TRANSTYPE);
        myTranCats.setDataList(theDataSet.getTransCategoryTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseCurrency> myCurrencies = pEditSet.registerType(MoneyWiseStaticDataType.CURRENCY);
        myCurrencies.setDataList(theDataSet.getAccountCurrencies().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseTaxBasis> myTaxBases = pEditSet.registerType(MoneyWiseStaticDataType.TAXBASIS);
        myTaxBases.setDataList(theDataSet.getTaxBases().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseAccountInfoType> myActInfoTypes = pEditSet.registerType(MoneyWiseStaticDataType.ACCOUNTINFOTYPE);
        myActInfoTypes.setDataList(theDataSet.getActInfoTypes().deriveList(PrometheusListStyle.EDIT));
        final PrometheusEditEntry<MoneyWiseTransInfoType> myTranInfoTypes = pEditSet.registerType(MoneyWiseStaticDataType.TRANSINFOTYPE);
        myTranInfoTypes.setDataList(theDataSet.getTransInfoTypes().deriveList(PrometheusListStyle.EDIT));
    }

    /**
     * Populate editSet for categories.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void populateCategoryEditSet(final PrometheusEditSet pEditSet) throws OceanusException  {
        /* Register category types */
        pEditSet.registerType(MoneyWiseBasicDataType.TRANSTAG);
        theDataSet.getTransactionTags().deriveEditList(pEditSet);
        pEditSet.registerType(MoneyWiseBasicDataType.REGION);
        theDataSet.getRegions().deriveEditList(pEditSet);
        final PrometheusEditEntry<MoneyWiseDepositCategory> myDepCats = pEditSet.registerType(MoneyWiseBasicDataType.DEPOSITCATEGORY);
        myDepCats.setDataList(theDataSet.getDepositCategories().deriveEditList(pEditSet));
        final PrometheusEditEntry<MoneyWiseCashCategory> myCashCats = pEditSet.registerType(MoneyWiseBasicDataType.CASHCATEGORY);
        myCashCats.setDataList(theDataSet.getCashCategories().deriveEditList(pEditSet));
        final PrometheusEditEntry<MoneyWiseLoanCategory> myLoanCats = pEditSet.registerType(MoneyWiseBasicDataType.LOANCATEGORY);
        myLoanCats.setDataList(theDataSet.getLoanCategories().deriveEditList(pEditSet));
        final PrometheusEditEntry<MoneyWiseTransCategory> myTranCats = pEditSet.registerType(MoneyWiseBasicDataType.TRANSCATEGORY);
        myTranCats.setDataList(theDataSet.getTransCategories().deriveEditList(pEditSet));
    }

    /**
     * Populate editSet for accounts.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void populateAccountEditSet(final PrometheusEditSet pEditSet) throws OceanusException  {
        /* Register and derive payee types */
        pEditSet.registerType(MoneyWiseBasicDataType.PAYEE);
        pEditSet.registerType(MoneyWiseBasicDataType.PAYEEINFO);
        theDataSet.getPayees().deriveEditList(pEditSet);

        /* Register and derive security types */
        pEditSet.registerType(MoneyWiseBasicDataType.SECURITY);
        pEditSet.registerType(MoneyWiseBasicDataType.SECURITYPRICE);
        pEditSet.registerType(MoneyWiseBasicDataType.SECURITYINFO);
        theDataSet.getSecurities().deriveEditList(pEditSet);
        theDataSet.getSecurityPrices().deriveEditList(pEditSet);

        /* Register and derive deposit types */
        pEditSet.registerType(MoneyWiseBasicDataType.DEPOSIT);
        pEditSet.registerType(MoneyWiseBasicDataType.DEPOSITRATE);
        pEditSet.registerType(MoneyWiseBasicDataType.DEPOSITINFO);
        theDataSet.getDeposits().deriveEditList(pEditSet);
        theDataSet.getDepositRates().deriveEditList(pEditSet);

        /* Register and derive cash types */
        pEditSet.registerType(MoneyWiseBasicDataType.CASH);
        pEditSet.registerType(MoneyWiseBasicDataType.CASHINFO);
        theDataSet.getCash().deriveEditList(pEditSet);

        /* Register and derive loan types */
        pEditSet.registerType(MoneyWiseBasicDataType.LOAN);
        pEditSet.registerType(MoneyWiseBasicDataType.LOANINFO);
        theDataSet.getLoans().deriveEditList(pEditSet);

        /* Register and derive portfolio types */
        pEditSet.registerType(MoneyWiseBasicDataType.PORTFOLIO);
        pEditSet.registerType(MoneyWiseBasicDataType.PORTFOLIOINFO);
        theDataSet.getPortfolios().deriveEditList(pEditSet);
    }

    /**
     * Populate editSet for transactions.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void populateTransEditSet(final PrometheusEditSet pEditSet) throws OceanusException  {
        /* Register and derive transactions */
        pEditSet.registerType(MoneyWiseBasicDataType.TRANSACTION);
        pEditSet.registerType(MoneyWiseBasicDataType.TRANSACTIONINFO);
        final MoneyWiseTransactionList myTrans = theDataSet.getTransactions();
        final MoneyWiseTransactionList myNewTrans = myTrans.deriveEditList(pEditSet);
        myNewTrans.relinkEditAssetEvents();
    }

    /**
     * Validate editSet.
     * @param pEditSet the editSet
     */
    void validateEditSet(final PrometheusEditSet pEditSet) {
        final Iterator<PrometheusEditEntry<?>> myIterator = pEditSet.listIterator();
        while (myIterator.hasNext()) {
            final PrometheusEditEntry<?> myEntry = myIterator.next();
            final PrometheusDataList<?> myList = myEntry.getDataList();

            /* Validate the list in the editSet */
            validateListInEditSet(myList, pEditSet);
        }
    }

    /**
     * Validate list in editSet.
     * @param pList the list
     * @param pEditSet the editSet
     */
    void validateListInEditSet(final PrometheusDataList<?> pList,
                               final PrometheusEditSet pEditSet) {
        final Iterator<?> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            final PrometheusDataItem myItem = (PrometheusDataItem) myIterator.next();

            /* Validate the item in the editSet */
            validateItemInEditSet(myItem, pEditSet);
        }
    }

    /**
     * Validate item in editSet.
     * @param pItem the item
     * @param pEditSet the editSet
     */
    void validateItemInEditSet(final PrometheusDataItem pItem,
                               final PrometheusEditSet pEditSet) {
        final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
        final Iterator<MetisFieldDef> myIterator = myFieldSet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();
            final Object myValue = myField.getFieldValue(pItem);
            if (myValue instanceof PrometheusDataItem) {
                final PrometheusDataItem myReferenced = (PrometheusDataItem) myValue;
                if (PrometheusDataResource.DATAITEM_BASE.equals(myField.getFieldId())) {
                    if (myReferenced.getList().getStyle() != PrometheusListStyle.CORE) {
                        Assertions.assertEquals(PrometheusListStyle.CORE, myReferenced.getList().getStyle(), "Base item not CORE");

                    }
                } else {
                    validateReferencedItemInEditSet(myReferenced, pEditSet);
                }
            }
            if (myValue instanceof MoneyWiseSecurityHolding) {
                final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) myValue;
                validateReferencedItemInEditSet(myHolding.getPortfolio(), pEditSet);
                validateReferencedItemInEditSet(myHolding.getSecurity(), pEditSet);
            }
        }
    }

    /**
     * Validate item in editSet.
     * @param pItem the item
     * @param pEditSet the editSet
     */
    void validateReferencedItemInEditSet(final PrometheusDataItem pItem,
                                         final PrometheusEditSet pEditSet) {
        final PrometheusListKey myKey = pItem.getItemType();
        final PrometheusListStyle myStyle = pEditSet.hasDataType(myKey) ? PrometheusListStyle.EDIT : PrometheusListStyle.CORE;
        Assertions.assertEquals(myStyle, pItem.getList().getStyle(), "Referenced item is incorrect style");
    }
}
