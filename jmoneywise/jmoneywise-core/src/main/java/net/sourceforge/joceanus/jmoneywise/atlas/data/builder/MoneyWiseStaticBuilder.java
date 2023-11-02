/*******************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import java.util.Currency;
import java.util.Locale;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType.CashCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType.DepositCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType.PayeeTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType.TransactionCategoryTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Static Builder.
 */
public class MoneyWiseStaticBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseStaticBuilder(final MoneyWiseData pDataSet) {
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
        /* Create default currency */
        buildDefaultCurrency(pDefault);

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
        final PayeeTypeList myTypes = theDataSet.getPayeeTypes();
        myTypes.ensureMap();
        for (PayeeTypeClass myClass : PayeeTypeClass.values()) {
            final PayeeType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the payeeType.
     * @param pType the class of the payeeType.
     * @return the payeeType
     */
    public PayeeType lookupPayeeType(final PayeeTypeClass pType) {
        return theDataSet.getPayeeTypes().findItemByClass(pType);
    }

    /**
     * build securityTypes.
     * @throws OceanusException on error
     */
    private void buildSecurityTypes() throws OceanusException {
        final SecurityTypeList myTypes = theDataSet.getSecurityTypes();
        myTypes.ensureMap();
        for (SecurityTypeClass myClass : SecurityTypeClass.values()) {
            final SecurityType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the securityType.
     * @param pType the class of the security.
     * @return the securityType
     */
    public SecurityType lookupSecurityType(final SecurityTypeClass pType) {
        return theDataSet.getSecurityTypes().findItemByClass(pType);
    }

    /**
     * build portfolioTypes.
     * @throws OceanusException on error
     */
    private void buildPortfolioTypes() throws OceanusException {
        final PortfolioTypeList myTypes = theDataSet.getPortfolioTypes();
        myTypes.ensureMap();
        for (PortfolioTypeClass myClass : PortfolioTypeClass.values()) {
            final PortfolioType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the portfolioType.
     * @param pType the class of the portfolioType.
     * @return the portfolioType
     */
    public PortfolioType lookupPortfolioType(final PortfolioTypeClass pType) {
        return theDataSet.getPortfolioTypes().findItemByClass(pType);
    }

    /**
     * build depositCategories.
     * @throws OceanusException on error
     */
    private void buildDepositCategories() throws OceanusException {
        final DepositCategoryTypeList myTypes = theDataSet.getDepositCategoryTypes();
        myTypes.ensureMap();
        for (DepositCategoryClass myClass : DepositCategoryClass.values()) {
            final DepositCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the depositCategoryType.
     * @param pType the class of the categoryType.
     * @return the depositCategoryType
     */
    public DepositCategoryType lookupCategoryType(final DepositCategoryClass pType) {
        return theDataSet.getDepositCategoryTypes().findItemByClass(pType);
    }

    /**
     * build cashCategories.
     * @throws OceanusException on error
     */
    private void buildCashCategories() throws OceanusException {
        final CashCategoryTypeList myTypes = theDataSet.getCashCategoryTypes();
        myTypes.ensureMap();
        for (CashCategoryClass myClass : CashCategoryClass.values()) {
            final CashCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the cashCategoryType.
     * @param pType the class of the categoryType.
     * @return the cashCategoryType
     */
    public CashCategoryType lookupCategoryType(final CashCategoryClass pType) {
        return theDataSet.getCashCategoryTypes().findItemByClass(pType);
    }

    /**
     * build loanCategories.
     * @throws OceanusException on error
     */
    private void buildLoanCategories() throws OceanusException {
        final LoanCategoryTypeList myTypes = theDataSet.getLoanCategoryTypes();
        myTypes.ensureMap();
        for (LoanCategoryClass myClass : LoanCategoryClass.values()) {
            final LoanCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the loanCategoryType.
     * @param pType the class of the categoryType.
     * @return the loanCategoryType
     */
    public LoanCategoryType lookupCategoryType(final LoanCategoryClass pType) {
        return theDataSet.getLoanCategoryTypes().findItemByClass(pType);
    }

    /**
     * build taxBases.
     * @throws OceanusException on error
     */
    private void buildTaxBases() throws OceanusException {
        final TaxBasisList myBases = theDataSet.getTaxBases();
        myBases.ensureMap();
        for (TaxBasisClass myClass : TaxBasisClass.values()) {
            final TaxBasis myBasis = myBases.addBasicItem(myClass.toString());
            myBasis.adjustMapForItem();
        }
    }

    /**
     * Obtain the taxBasis.
     * @param pBasis the class of the taxBasis.
     * @return the taxBasis
     */
    public TaxBasis lookupTaxBasis(final TaxBasisClass pBasis) {
        return theDataSet.getTaxBases().findItemByClass(pBasis);
    }

    /**
     * build transactionCategories.
     * @throws OceanusException on error
     */
    private void buildTransCategories() throws OceanusException {
        final TransactionCategoryTypeList myTypes = theDataSet.getTransCategoryTypes();
        myTypes.ensureMap();
        for (TransactionCategoryClass myClass : TransactionCategoryClass.values()) {
            final TransactionCategoryType myType = myTypes.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the transCategoryType.
     * @param pType the class of the categoryType.
     * @return the transCategoryType
     */
    public TransactionCategoryType lookupCategoryType(final TransactionCategoryClass pType) {
        return theDataSet.getTransCategoryTypes().findItemByClass(pType);
    }

    /**
     * build AccountInfo.
     * @throws OceanusException on error
     */
    private void buildAccountInfo() throws OceanusException {
        final AccountInfoTypeList myInfo = theDataSet.getActInfoTypes();
        myInfo.ensureMap();
        for (AccountInfoClass myClass : AccountInfoClass.values()) {
            final AccountInfoType myType = myInfo.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the accountInfo.
     * @param pInfo the class of the accountInfo.
     * @return the accountInfo
     */
    public AccountInfoType lookupAccountInfo(final AccountInfoClass pInfo) {
        return theDataSet.getActInfoTypes().findItemByClass(pInfo);
    }

    /**
     * build TransInfo.
     * @throws OceanusException on error
     */
    private void buildTransInfo() throws OceanusException {
        final TransactionInfoTypeList myInfo = theDataSet.getTransInfoTypes();
        myInfo.ensureMap();
        for (TransactionInfoClass myClass : TransactionInfoClass.values()) {
            final TransactionInfoType myType = myInfo.addBasicItem(myClass.toString());
            myType.adjustMapForItem();
        }
    }

    /**
     * Obtain the transactionInfo.
     * @param pInfo the class of the transInfo.
     * @return the transInfo
     */
    public TransactionInfoType lookupTransInfo(final TransactionInfoClass pInfo) {
        return theDataSet.getTransInfoTypes().findItemByClass(pInfo);
    }

    /**
     * add currency.
     * @param pCurrency the currency class
     * @return the new currency
     * @throws OceanusException on error
     */
    public AssetCurrency buildCurrency(final AssetCurrencyClass pCurrency) throws OceanusException {
        final AssetCurrencyList myList = theDataSet.getAccountCurrencies();
        final AssetCurrency myCurr = myList.addBasicItem(pCurrency.name());
        myCurr.adjustMapForItem();
        return myCurr;
    }

    /**
     * add currency.
     * @param pCurrency the currency
     * @return the new currency
     * @throws OceanusException on error
     */
    public AssetCurrency buildDefaultCurrency(final Currency pCurrency) throws OceanusException {
        final AssetCurrencyList myList = theDataSet.getAccountCurrencies();
        myList.ensureMap();
        final AssetCurrency myCurr = myList.addBasicItem(pCurrency.getCurrencyCode());
        myCurr.setDefault(Boolean.TRUE);
        myCurr.adjustMapForItem();
        return myCurr;
    }

    /**
     * Obtain the currency.
     * @param pCurr the class of the accountInfo.
     * @return the currency
     */
    public AssetCurrency lookupCurrency(final AssetCurrencyClass pCurr) {
        return theDataSet.getAccountCurrencies().findItemByClass(pCurr);
    }
}
