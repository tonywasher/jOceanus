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
package net.sourceforge.joceanus.jmoneywise.atlas.data.ids;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;

/**
 * Transaction DataIds.
 */
public enum MoneyWiseTransDataId
        implements PrometheusDataFieldId {
    /**
     * Pair.
     */
    PAIR(MoneyWiseDataResource.TRANSACTION_ASSETPAIR, TransactionBase.FIELD_PAIR),

    /**
     * Account.
     */
    ACCOUNT(MoneyWiseDataResource.TRANSACTION_ACCOUNT, TransactionBase.FIELD_ACCOUNT),

    /**
     * Partner.
     */
    PARTNER(MoneyWiseDataResource.TRANSACTION_PARTNER, TransactionBase.FIELD_PARTNER),

    /**
     * Direction.
     */
    DIRECTION(MoneyWiseDataResource.TRANSACTION_DIRECTION, TransactionBase.FIELD_DIRECTION),

    /**
     * Amount.
     */
    AMOUNT(MoneyWiseDataResource.TRANSACTION_AMOUNT, TransactionBase.FIELD_AMOUNT),

    /**
     * Category.
     */
    CATEGORY(MoneyWiseDataType.TRANSCATEGORY, TransactionBase.FIELD_CATEGORY),

    /**
     * Reconciled.
     */
    RECONCILED(MoneyWiseDataResource.TRANSACTION_RECONCILED, TransactionBase.FIELD_RECONCILED),

    /**
     * Date.
     */
    DATE(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, Transaction.FIELD_DATE),

    /**
     * TaxYear.
     */
    TAXYEAR(MoneyWiseDataResource.MONEYWISEDATA_FIELD_TAXYEAR, Transaction.FIELD_TAXYEAR),

    /**
     * Reference.
     */
    REFERENCE(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.REFERENCE), TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE)),

    /**
     * Comments.
     */
    COMMENTS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.COMMENTS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS)),

    /**
     * Tag.
     */
    TAG(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.TRANSTAG), TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG)),

    /**
     * EmployerNatIns.
     */
    EMPLOYERNATINS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.EMPLOYERNATINS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYERNATINS)),

    /**
     * EmployeeNatIns.
     */
    EMPLOYEENATINS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.EMPLOYEENATINS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYEENATINS)),

    /**
     * DeemedBenefit.
     */
    DEEMEDBENEFIT(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.DEEMEDBENEFIT), TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT)),

    /**
     * Withheld.
     */
    WITHHELD(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.WITHHELD), TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD)),

    /**
     * TaxCredit.
     */
    TAXCREDIT(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.TAXCREDIT), TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT)),

    /**
     * AccountDeltaUnits.
     */
    ACCOUNTDELTAUNITS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.ACCOUNTDELTAUNITS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS)),

    /**
     * PartnerDeltaUnits.
     */
    PARTNERDELTAUNITS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.PARTNERDELTAUNITS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS)),

    /**
     * Dilution.
     */
    DILUTION(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.DILUTION), TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION)),

    /**
     * QualifyYears.
     */
    QUALIFYYEARS(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.QUALIFYYEARS), TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS)),

    /**
     * ReturnedCash.
     */
    RETURNEDCASH(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.RETURNEDCASH), TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH)),

    /**
     * ReturnedCashAccount.
     */
    RETURNEDCASHACCOUNT(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.RETURNEDCASHACCOUNT), TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT)),

    /**
     * PartnerAmount.
     */
    PARTNERAMOUNT(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.PARTNERAMOUNT), TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT)),

    /**
     * ExchangeRate.
     */
    XCHANGERATE(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.XCHANGERATE), TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE)),

    /**
     * Price.
     */
    PRICE(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.PRICE), TransactionInfoSet.getFieldForClass(TransactionInfoClass.PRICE)),

    /**
     * Commission.
     */
    COMMISSION(StaticDataResource.getKeyForTransInfo(TransactionInfoClass.COMMISSION), TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMISSION));

    /**
     * The Value.
     */
    private final String theValue;

    /**
     * The Lethe Field.
     */
    private final MetisLetheField theField;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MoneyWiseTransDataId(final MetisDataFieldId pKeyName,
                         final MetisLetheField pField) {
        theValue = pKeyName.getId();
        theField = pField;
    }

    @Override
    public String getId() {
        return theValue;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public MetisLetheField getLetheField() {
        return theField;
    }
}
