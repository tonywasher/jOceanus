/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.data.builder;

import java.util.Currency;
import java.util.Locale;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType.MoneyWiseCashCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType.MoneyWiseDepositCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryType.MoneyWiseLoanCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType.MoneyWisePayeeTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType.MoneyWiseTransCategoryTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Static Builder.
 */
public class MoneyWiseStaticBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseStaticBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * build basic Static.
     * @throws OceanusException on error
     * @return the builder
     */
    public MoneyWiseStaticBuilder buildBasic() throws OceanusException {
        return buildBasic(Currency.getInstance(Locale.getDefault()));
    }

    /**
     * build basic Static.
     * @param pDefault the default currency
     * @throws OceanusException on error
     * @return the builder
     */
    public MoneyWiseStaticBuilder buildBasic(final Currency pDefault) throws OceanusException {
        /* Create reporting currency */
        buildReportingCurrency(pDefault);

        /* Build account types */
        buildPayeeTypes();
        buildSecurityTypes();
        buildPortfolioTypes();
        buildDepositCategories();
        buildCashCategories();
        buildLoanCategories();

        /* Build additional detail */
        buildAccountInfo();
        buildTransCategories();
        buildTransInfo();
        buildTaxBases();
        return this;
    }

    /**
     * build payeeTypes.
     * @throws OceanusException on error
     */
    private void buildPayeeTypes() throws OceanusException {
        final MoneyWisePayeeTypeList myTypes = theDataSet.getPayeeTypes();
        myTypes.ensureMap();
        for (MoneyWisePayeeClass myClass : MoneyWisePayeeClass.values()) {
            final MoneyWisePayeeType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the payeeType.
     * @param pType the class of the payeeType.
     * @return the payeeType
     */
    public MoneyWisePayeeType lookupPayeeType(final MoneyWisePayeeClass pType) {
        return theDataSet.getPayeeTypes().findItemByClass(pType);
    }

    /**
     * build securityTypes.
     * @throws OceanusException on error
     */
    private void buildSecurityTypes() throws OceanusException {
        final MoneyWiseSecurityTypeList myTypes = theDataSet.getSecurityTypes();
        myTypes.ensureMap();
        for (MoneyWiseSecurityClass myClass : MoneyWiseSecurityClass.values()) {
            final MoneyWiseSecurityType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the securityType.
     * @param pType the class of the security.
     * @return the securityType
     */
    public MoneyWiseSecurityType lookupSecurityType(final MoneyWiseSecurityClass pType) {
        return theDataSet.getSecurityTypes().findItemByClass(pType);
    }

    /**
     * build portfolioTypes.
     * @throws OceanusException on error
     */
    private void buildPortfolioTypes() throws OceanusException {
        final MoneyWisePortfolioTypeList myTypes = theDataSet.getPortfolioTypes();
        myTypes.ensureMap();
        for (MoneyWisePortfolioClass myClass : MoneyWisePortfolioClass.values()) {
            final MoneyWisePortfolioType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the portfolioType.
     * @param pType the class of the portfolioType.
     * @return the portfolioType
     */
    public MoneyWisePortfolioType lookupPortfolioType(final MoneyWisePortfolioClass pType) {
        return theDataSet.getPortfolioTypes().findItemByClass(pType);
    }

    /**
     * build depositCategories.
     * @throws OceanusException on error
     */
    private void buildDepositCategories() throws OceanusException {
        final MoneyWiseDepositCategoryTypeList myTypes = theDataSet.getDepositCategoryTypes();
        myTypes.ensureMap();
        for (MoneyWiseDepositCategoryClass myClass : MoneyWiseDepositCategoryClass.values()) {
            final MoneyWiseDepositCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the depositCategoryType.
     * @param pType the class of the categoryType.
     * @return the depositCategoryType
     */
    public MoneyWiseDepositCategoryType lookupCategoryType(final MoneyWiseDepositCategoryClass pType) {
        return theDataSet.getDepositCategoryTypes().findItemByClass(pType);
    }

    /**
     * build cashCategories.
     * @throws OceanusException on error
     */
    private void buildCashCategories() throws OceanusException {
        final MoneyWiseCashCategoryTypeList myTypes = theDataSet.getCashCategoryTypes();
        myTypes.ensureMap();
        for (MoneyWiseCashCategoryClass myClass : MoneyWiseCashCategoryClass.values()) {
            final MoneyWiseCashCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the cashCategoryType.
     * @param pType the class of the categoryType.
     * @return the cashCategoryType
     */
    public MoneyWiseCashCategoryType lookupCategoryType(final MoneyWiseCashCategoryClass pType) {
        return theDataSet.getCashCategoryTypes().findItemByClass(pType);
    }

    /**
     * build loanCategories.
     * @throws OceanusException on error
     */
    private void buildLoanCategories() throws OceanusException {
        final MoneyWiseLoanCategoryTypeList myTypes = theDataSet.getLoanCategoryTypes();
        myTypes.ensureMap();
        for (MoneyWiseLoanCategoryClass myClass : MoneyWiseLoanCategoryClass.values()) {
            final MoneyWiseLoanCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the loanCategoryType.
     * @param pType the class of the categoryType.
     * @return the loanCategoryType
     */
    public MoneyWiseLoanCategoryType lookupCategoryType(final MoneyWiseLoanCategoryClass pType) {
        return theDataSet.getLoanCategoryTypes().findItemByClass(pType);
    }

    /**
     * build taxBases.
     * @throws OceanusException on error
     */
    private void buildTaxBases() throws OceanusException {
        final MoneyWiseTaxBasisList myBases = theDataSet.getTaxBases();
        myBases.ensureMap();
        for (MoneyWiseTaxClass myClass : MoneyWiseTaxClass.values()) {
            final MoneyWiseTaxBasis myBasis = myBases.addBasicItem(myClass.toString());
            myBasis.adjustMapForItem();
        }
    }

    /**
     * Obtain the taxBasis.
     * @param pBasis the class of the taxBasis.
     * @return the taxBasis
     */
    public MoneyWiseTaxBasis lookupTaxBasis(final MoneyWiseTaxClass pBasis) {
        return theDataSet.getTaxBases().findItemByClass(pBasis);
    }

    /**
     * build transactionCategories.
     * @throws OceanusException on error
     */
    private void buildTransCategories() throws OceanusException {
        final MoneyWiseTransCategoryTypeList myTypes = theDataSet.getTransCategoryTypes();
        myTypes.ensureMap();
        for (MoneyWiseTransCategoryClass myClass : MoneyWiseTransCategoryClass.values()) {
            final MoneyWiseTransCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the transCategoryType.
     * @param pType the class of the categoryType.
     * @return the transCategoryType
     */
    public MoneyWiseTransCategoryType lookupCategoryType(final MoneyWiseTransCategoryClass pType) {
        return theDataSet.getTransCategoryTypes().findItemByClass(pType);
    }

    /**
     * build AccountInfo.
     * @throws OceanusException on error
     */
    private void buildAccountInfo() throws OceanusException {
        final MoneyWiseAccountInfoTypeList myInfo = theDataSet.getActInfoTypes();
        myInfo.ensureMap();
        for (MoneyWiseAccountInfoClass myClass : MoneyWiseAccountInfoClass.values()) {
            final MoneyWiseAccountInfoType myType = myInfo.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the accountInfo.
     * @param pInfo the class of the accountInfo.
     * @return the accountInfo
     */
    public MoneyWiseAccountInfoType lookupAccountInfo(final MoneyWiseAccountInfoClass pInfo) {
        return theDataSet.getActInfoTypes().findItemByClass(pInfo);
    }

    /**
     * build TransInfo.
     * @throws OceanusException on error
     */
    private void buildTransInfo() throws OceanusException {
        final MoneyWiseTransInfoTypeList myInfo = theDataSet.getTransInfoTypes();
        myInfo.ensureMap();
        for (MoneyWiseTransInfoClass myClass : MoneyWiseTransInfoClass.values()) {
            final MoneyWiseTransInfoType myType = myInfo.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the transactionInfo.
     * @param pInfo the class of the transInfo.
     * @return the transInfo
     */
    public MoneyWiseTransInfoType lookupTransInfo(final MoneyWiseTransInfoClass pInfo) {
        return theDataSet.getTransInfoTypes().findItemByClass(pInfo);
    }

    /**
     * add currency.
     * @param pCurrency the currency class
     * @return the new currency
     * @throws OceanusException on error
     */
    public MoneyWiseCurrency buildCurrency(final MoneyWiseCurrencyClass pCurrency) throws OceanusException {
        final MoneyWiseCurrencyList myList = theDataSet.getAccountCurrencies();
        final MoneyWiseCurrency myCurr = myList.addBasicItem(pCurrency.name());
        myCurr.adjustMapForItem();
        return myCurr;
    }

    /**
     * add currency.
     * @param pCurrency the currency
     * @return the new currency
     * @throws OceanusException on error
     */
    public MoneyWiseCurrency buildReportingCurrency(final Currency pCurrency) throws OceanusException {
        final MoneyWiseCurrencyList myList = theDataSet.getAccountCurrencies();
        myList.ensureMap();
        final MoneyWiseCurrency myCurr = myList.addBasicItem(pCurrency.getCurrencyCode());
        myCurr.setReporting(Boolean.TRUE);
        myCurr.adjustMapForItem();
        return myCurr;
    }

    /**
     * Obtain the currency.
     * @param pCurr the class of the accountInfo.
     * @return the currency
     */
    public MoneyWiseCurrency lookupCurrency(final MoneyWiseCurrencyClass pCurr) {
        return theDataSet.getAccountCurrencies().findItemByClass(pCurr);
    }
}
