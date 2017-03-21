/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.ui;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListItem.MetisIndexedItem;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;

/**
 * Metis Table item.
 */
public class MetisTestTableItem
        implements MetisIndexedItem, MetisDataValues {
    /**
     * The Next itemId.
     */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisTestTableItem.class.getSimpleName());

    /**
     * Id Field Id.
     */
    private static final MetisField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

    /**
     * Name Field Id.
     */
    private static final MetisField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(TethysDataId.NAME.toString(), MetisDataType.STRING, 20);

    /**
     * Password Field Id.
     */
    private static final MetisField FIELD_PASSWORD = FIELD_DEFS.declareEqualityValueField(TethysDataId.PASSWORD.toString(), MetisDataType.STRING, 30);

    /**
     * Date Field Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(TethysDataId.DATE.toString(), MetisDataType.DATE);

    /**
     * Boolean Field Id.
     */
    private static final MetisField FIELD_BOOL = FIELD_DEFS.declareEqualityValueField(TethysDataId.BOOLEAN.toString(), MetisDataType.BOOLEAN);

    /**
     * XtraBoolean Field Id.
     */
    private static final MetisField FIELD_XTRABOOL = FIELD_DEFS.declareEqualityValueField(TethysDataId.XTRABOOL.toString(), MetisDataType.BOOLEAN);

    /**
     * Short Field Id.
     */
    private static final MetisField FIELD_SHORT = FIELD_DEFS.declareEqualityValueField(TethysDataId.SHORT.toString(), MetisDataType.SHORT);

    /**
     * Integer Field Id.
     */
    private static final MetisField FIELD_INT = FIELD_DEFS.declareEqualityValueField(TethysDataId.INTEGER.toString(), MetisDataType.INTEGER);

    /**
     * Long Field Id.
     */
    private static final MetisField FIELD_LONG = FIELD_DEFS.declareEqualityValueField(TethysDataId.LONG.toString(), MetisDataType.LONG);

    /**
     * Money Field Id.
     */
    private static final MetisField FIELD_MONEY = FIELD_DEFS.declareEqualityValueField(TethysDataId.MONEY.toString(), MetisDataType.MONEY);

    /**
     * Price Field Id.
     */
    private static final MetisField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField(TethysDataId.PRICE.toString(), MetisDataType.PRICE);

    /**
     * Units Field Id.
     */
    private static final MetisField FIELD_UNITS = FIELD_DEFS.declareEqualityValueField(TethysDataId.UNITS.toString(), MetisDataType.UNITS);

    /**
     * Rate Field Id.
     */
    private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityValueField(TethysDataId.RATE.toString(), MetisDataType.RATE);

    /**
     * Dilution Field Id.
     */
    private static final MetisField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField(TethysDataId.DILUTION.toString(), MetisDataType.DILUTION);

    /**
     * Ratio Field Id.
     */
    private static final MetisField FIELD_RATIO = FIELD_DEFS.declareEqualityValueField(TethysDataId.RATIO.toString(), MetisDataType.RATIO);

    /**
     * DilutedPrice Field Id.
     */
    private static final MetisField FIELD_DILUTEDPRICE = FIELD_DEFS.declareEqualityValueField(TethysDataId.DILUTEDPRICE.toString(), MetisDataType.PRICE);

    /**
     * Scroll Field Id.
     */
    private static final MetisField FIELD_SCROLL = FIELD_DEFS.declareEqualityValueField(TethysDataId.SCROLL.toString(), MetisDataType.LINK);

    /**
     * List Field Id.
     */
    private static final MetisField FIELD_LIST = FIELD_DEFS.declareEqualityValueField(TethysDataId.LIST.toString(), MetisDataType.LINKSET);

    /**
     * Updates Field Id.
     */
    private static final MetisField FIELD_UPDATES = FIELD_DEFS.declareEqualityValueField(TethysDataId.UPDATES.toString(), MetisDataType.INTEGER);

    /**
     * The id number of the item.
     */
    private final Integer theId;

    /**
     * ValueSet.
     */
    private MetisValueSet theValueSet;

    /**
     * The history control.
     */
    private final MetisValueSetHistory theHistory;

    /**
     * Constructor.
     */
    public MetisTestTableItem() {
        /* Allocate the next id */
        theId = NEXT_ID.getAndIncrement();

        /* Create history control */
        theHistory = new MetisValueSetHistory();

        /* Allocate initial value set and declare it */
        MetisValueSet myValues = new MetisValueSet(this);
        declareValues(myValues);
        theHistory.setValues(myValues);
    }

    /**
     * Constructor.
     * @param pHelper the Helper
     * @param pName the Name
     */
    public void initValues(final TethysScrollUITestHelper<?, ?> pHelper,
                           final String pName) {
        /* Initialise values */
        setName(pName);
        setDate(new TethysDate());
        setBoolean(Boolean.FALSE);
        setXtraBoolean(Boolean.FALSE);
        setShort(TethysScrollUITestHelper.SHORT_DEF);
        setInteger(TethysScrollUITestHelper.INT_DEF);
        setLong(TethysScrollUITestHelper.LONG_DEF);
        setMoney(TethysScrollUITestHelper.MONEY_DEF);
        setPrice(TethysScrollUITestHelper.PRICE_DEF);
        setUnits(TethysScrollUITestHelper.UNITS_DEF);
        setRate(TethysScrollUITestHelper.RATE_DEF);
        setRatio(TethysScrollUITestHelper.RATIO_DEF);
        setDilution(TethysScrollUITestHelper.DILUTION_DEF);
        setList(pHelper.buildToggleList());
        setUpdates(new Integer(0));
    }

    @Override
    public MetisValueSet getValueSet() {
        return theValueSet;
    }

    @Override
    public MetisValueSetHistory getValueSetHistory() {
        return theHistory;
    }

    @Override
    public boolean skipField(final MetisField pField) {
        return false;
    }

    @Override
    public void declareValues(final MetisValueSet pValues) {
        theValueSet = pValues;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_NAME.equals(pField)) {
            return getName();
        }
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    @Override
    public Integer getIndexedId() {
        return theId;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theValueSet.getValue(FIELD_NAME, String.class);
    }

    /**
     * Set the name property.
     * @param pValue the new value
     */
    public void setName(final String pValue) {
        theValueSet.setValue(FIELD_NAME, pValue);
    }

    /**
     * Obtain the password property.
     * @return the password property
     */
    public char[] getPassword() {
        return theValueSet.getValue(FIELD_PASSWORD, char[].class);
    }

    /**
     * Set the password property.
     * @param pValue the new value
     */
    public void setPassword(final char[] pValue) {
        theValueSet.setValue(FIELD_PASSWORD, pValue);
    }

    /**
     * Obtain the boolean property.
     * @return the boolean property
     */
    public Boolean getBoolean() {
        return theValueSet.getValue(FIELD_BOOL, Boolean.class);
    }

    /**
     * Set the boolean property.
     * @param pValue the new value
     */
    public void setBoolean(final Boolean pValue) {
        theValueSet.setValue(FIELD_BOOL, pValue);
    }

    /**
     * Obtain the extra boolean property.
     * @return the boolean property
     */
    public Boolean getXtraBoolean() {
        return theValueSet.getValue(FIELD_XTRABOOL, Boolean.class);
    }

    /**
     * Set the extra boolean property.
     * @param pValue the new value
     */
    public void setXtraBoolean(final Boolean pValue) {
        theValueSet.setValue(FIELD_XTRABOOL, pValue);
    }

    /**
     * Obtain the short property.
     * @return the short property
     */
    public Short getShort() {
        return theValueSet.getValue(FIELD_SHORT, Short.class);
    }

    /**
     * Set the short property.
     * @param pValue the new value
     */
    public void setShort(final Short pValue) {
        theValueSet.setValue(FIELD_SHORT, pValue);
    }

    /**
     * Obtain the integer property.
     * @return the integer property
     */
    public Integer getInteger() {
        return theValueSet.getValue(FIELD_INT, Integer.class);
    }

    /**
     * Set the integer property.
     * @param pValue the new value
     */
    public void setInteger(final Integer pValue) {
        theValueSet.setValue(FIELD_INT, pValue);
    }

    /**
     * Obtain the long property.
     * @return the long property
     */
    public Long getLong() {
        return theValueSet.getValue(FIELD_LONG, Long.class);
    }

    /**
     * Set the long property.
     * @param pValue the new value
     */
    public void setLong(final Long pValue) {
        theValueSet.setValue(FIELD_LONG, pValue);
    }

    /**
     * Obtain the money property.
     * @return the money property
     */
    public TethysMoney getMoney() {
        return theValueSet.getValue(FIELD_MONEY, TethysMoney.class);
    }

    /**
     * Set the money property.
     * @param pValue the new value
     */
    public void setMoney(final TethysMoney pValue) {
        theValueSet.setValue(FIELD_MONEY, pValue);
    }

    /**
     * Obtain the price property.
     * @return the price property
     */
    public TethysPrice getPrice() {
        return theValueSet.getValue(FIELD_PRICE, TethysPrice.class);
    }

    /**
     * Set the price property.
     * @param pValue the new value
     */
    public void setPrice(final TethysPrice pValue) {
        theValueSet.setValue(FIELD_PRICE, pValue);
    }

    /**
     * Obtain the units property.
     * @return the units property
     */
    public TethysUnits getUnits() {
        return theValueSet.getValue(FIELD_UNITS, TethysUnits.class);
    }

    /**
     * Set the units property.
     * @param pValue the new value
     */
    public void setUnits(final TethysUnits pValue) {
        theValueSet.setValue(FIELD_UNITS, pValue);
    }

    /**
     * Obtain the rate property.
     * @return the rate property
     */
    public TethysRate getRate() {
        return theValueSet.getValue(FIELD_RATE, TethysRate.class);
    }

    /**
     * Set the rate property.
     * @param pValue the new value
     */
    public void setRate(final TethysRate pValue) {
        theValueSet.setValue(FIELD_RATE, pValue);
    }

    /**
     * Obtain the ratio property.
     * @return the ratio property
     */
    public TethysRatio getRatio() {
        return theValueSet.getValue(FIELD_RATIO, TethysRatio.class);
    }

    /**
     * Set the ratio property.
     * @param pValue the new value
     */
    public void setRatio(final TethysRatio pValue) {
        theValueSet.setValue(FIELD_RATIO, pValue);
    }

    /**
     * Obtain the dilution property.
     * @return the dilution property
     */
    public TethysDilution getDilution() {
        return theValueSet.getValue(FIELD_DILUTION, TethysDilution.class);
    }

    /**
     * Set the dilution property.
     * @param pValue the new value
     */
    public void setDilution(final TethysDilution pValue) {
        theValueSet.setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Obtain the dilutedPrice property.
     * @return the dilutedPrice property
     */
    public TethysDilutedPrice getDilutedPrice() {
        return theValueSet.getValue(FIELD_DILUTEDPRICE, TethysDilutedPrice.class);
    }

    /**
     * Set the dilutedPrice property.
     * @param pValue the new value
     */
    public void setDilutedPrice(final TethysDilutedPrice pValue) {
        theValueSet.setValue(FIELD_DILUTEDPRICE, pValue);
    }

    /**
     * Obtain the date property.
     * @return the date property
     */
    public TethysDate getDate() {
        return theValueSet.getValue(FIELD_DATE, TethysDate.class);
    }

    /**
     * Set the date property.
     * @param pValue the new value
     */
    public void setDate(final TethysDate pValue) {
        theValueSet.setValue(FIELD_DATE, pValue);
    }

    /**
     * Obtain the scroll property.
     * @return the scroll property
     */
    public String getScroll() {
        return theValueSet.getValue(FIELD_SCROLL, String.class);
    }

    /**
     * Set the scroll property.
     * @param pValue the new value
     */
    public void setScroll(final String pValue) {
        theValueSet.setValue(FIELD_SCROLL, pValue);
    }

    /**
     * Obtain the list property.
     * @return the list property
     */
    @SuppressWarnings("unchecked")
    public TethysItemList<TethysListId> getList() {
        return (TethysItemList<TethysListId>) theValueSet.getValue(FIELD_LIST, TethysItemList.class);
    }

    /**
     * Set the list property.
     * @param pValue the new value
     */
    public void setList(final TethysItemList<TethysListId> pValue) {
        theValueSet.setValue(FIELD_LIST, pValue);
    }

    /**
     * Obtain the updates property.
     * @return the updates property
     */
    public Integer getUpdates() {
        return theValueSet.getValue(FIELD_UPDATES, Integer.class);
    }

    /**
     * increment updates.
     */
    public void incrementUpdates() {
        setUpdates(getUpdates() + 1);
    }

    /**
     * Set the updates property.
     * @param pValue the new value
     */
    private void setUpdates(final Integer pValue) {
        theValueSet.setValue(FIELD_UPDATES, pValue);
    }
}
