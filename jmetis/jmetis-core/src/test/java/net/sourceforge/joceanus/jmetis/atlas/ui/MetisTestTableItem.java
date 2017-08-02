/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionControl;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
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
        implements MetisDataVersionedItem {
    /**
     * The Next itemId.
     */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisTestTableItem.class);

    /**
     * Name Field Id.
     */
    private static final MetisDataField FIELD_NAME = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.NAME.toString(), MetisDataType.STRING, 20);

    /**
     * Password Field Id.
     */
    private static final MetisDataField FIELD_PASSWORD = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.PASSWORD.toString(), MetisDataType.STRING, 30);

    /**
     * Date Field Id.
     */
    private static final MetisDataField FIELD_DATE = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.DATE.toString(), MetisDataType.DATE);

    /**
     * Boolean Field Id.
     */
    private static final MetisDataField FIELD_BOOL = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.BOOLEAN.toString(), MetisDataType.BOOLEAN);

    /**
     * XtraBoolean Field Id.
     */
    private static final MetisDataField FIELD_XTRABOOL = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.XTRABOOL.toString(), MetisDataType.BOOLEAN);

    /**
     * Short Field Id.
     */
    private static final MetisDataField FIELD_SHORT = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.SHORT.toString(), MetisDataType.SHORT);

    /**
     * Integer Field Id.
     */
    private static final MetisDataField FIELD_INT = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.INTEGER.toString(), MetisDataType.INTEGER);

    /**
     * Long Field Id.
     */
    private static final MetisDataField FIELD_LONG = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.LONG.toString(), MetisDataType.LONG);

    /**
     * Money Field Id.
     */
    private static final MetisDataField FIELD_MONEY = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.MONEY.toString(), MetisDataType.MONEY);

    /**
     * Price Field Id.
     */
    private static final MetisDataField FIELD_PRICE = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.PRICE.toString(), MetisDataType.PRICE);

    /**
     * Units Field Id.
     */
    private static final MetisDataField FIELD_UNITS = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.UNITS.toString(), MetisDataType.UNITS);

    /**
     * Rate Field Id.
     */
    private static final MetisDataField FIELD_RATE = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.RATE.toString(), MetisDataType.RATE);

    /**
     * Dilution Field Id.
     */
    private static final MetisDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.DILUTION.toString(), MetisDataType.DILUTION);

    /**
     * Ratio Field Id.
     */
    private static final MetisDataField FIELD_RATIO = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.RATIO.toString(), MetisDataType.RATIO);

    /**
     * DilutedPrice Field Id.
     */
    private static final MetisDataField FIELD_DILUTEDPRICE = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.DILUTEDPRICE.toString(), MetisDataType.PRICE);

    /**
     * Scroll Field Id.
     */
    private static final MetisDataField FIELD_SCROLL = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.SCROLL.toString(), MetisDataType.LINK);

    /**
     * List Field Id.
     */
    private static final MetisDataField FIELD_LIST = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.LIST.toString(), MetisDataType.LINKSET);

    /**
     * Updates Field Id.
     */
    private static final MetisDataField FIELD_UPDATES = FIELD_DEFS.declareEqualityVersionedField(TethysDataId.UPDATES.toString(), MetisDataType.INTEGER);

    /**
     * The version control.
     */
    private final MetisDataVersionControl theControl;

    /**
     * Constructor.
     */
    public MetisTestTableItem() {
        /* Create version control */
        theControl = FIELD_DEFS.newVersionControl(this);
        theControl.setIndexedId(NEXT_ID.getAndIncrement());
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

    /**
     * Obtain the current valueSet.
     * @return the valueSet
     */
    private MetisDataVersionValues getValueSet() {
        return theControl.getValueSet();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_NAME.equals(pField)) {
            return getName();
        }

        /* Pass remaining values to the control */
        return theControl.getFieldValue(pField);
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    @Override
    public MetisDataVersionControl getVersionControl() {
        return theControl;
    }

    @Override
    public Integer getIndexedId() {
        return theControl.getIndexedId();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return getValueSet().getValue(FIELD_NAME, String.class);
    }

    /**
     * Set the name property.
     * @param pValue the new value
     */
    public void setName(final String pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Obtain the password property.
     * @return the password property
     */
    public char[] getPassword() {
        return getValueSet().getValue(FIELD_PASSWORD, char[].class);
    }

    /**
     * Set the password property.
     * @param pValue the new value
     */
    public void setPassword(final char[] pValue) {
        getValueSet().setValue(FIELD_PASSWORD, pValue);
    }

    /**
     * Obtain the boolean property.
     * @return the boolean property
     */
    public Boolean getBoolean() {
        return getValueSet().getValue(FIELD_BOOL, Boolean.class);
    }

    /**
     * Set the boolean property.
     * @param pValue the new value
     */
    public void setBoolean(final Boolean pValue) {
        getValueSet().setValue(FIELD_BOOL, pValue);
    }

    /**
     * Obtain the extra boolean property.
     * @return the boolean property
     */
    public Boolean getXtraBoolean() {
        return getValueSet().getValue(FIELD_XTRABOOL, Boolean.class);
    }

    /**
     * Set the extra boolean property.
     * @param pValue the new value
     */
    public void setXtraBoolean(final Boolean pValue) {
        getValueSet().setValue(FIELD_XTRABOOL, pValue);
    }

    /**
     * Obtain the short property.
     * @return the short property
     */
    public Short getShort() {
        return getValueSet().getValue(FIELD_SHORT, Short.class);
    }

    /**
     * Set the short property.
     * @param pValue the new value
     */
    public void setShort(final Short pValue) {
        getValueSet().setValue(FIELD_SHORT, pValue);
    }

    /**
     * Obtain the integer property.
     * @return the integer property
     */
    public Integer getInteger() {
        return getValueSet().getValue(FIELD_INT, Integer.class);
    }

    /**
     * Set the integer property.
     * @param pValue the new value
     */
    public void setInteger(final Integer pValue) {
        getValueSet().setValue(FIELD_INT, pValue);
    }

    /**
     * Obtain the long property.
     * @return the long property
     */
    public Long getLong() {
        return getValueSet().getValue(FIELD_LONG, Long.class);
    }

    /**
     * Set the long property.
     * @param pValue the new value
     */
    public void setLong(final Long pValue) {
        getValueSet().setValue(FIELD_LONG, pValue);
    }

    /**
     * Obtain the money property.
     * @return the money property
     */
    public TethysMoney getMoney() {
        return getValueSet().getValue(FIELD_MONEY, TethysMoney.class);
    }

    /**
     * Set the money property.
     * @param pValue the new value
     */
    public void setMoney(final TethysMoney pValue) {
        getValueSet().setValue(FIELD_MONEY, pValue);
    }

    /**
     * Obtain the price property.
     * @return the price property
     */
    public TethysPrice getPrice() {
        return getValueSet().getValue(FIELD_PRICE, TethysPrice.class);
    }

    /**
     * Set the price property.
     * @param pValue the new value
     */
    public void setPrice(final TethysPrice pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Obtain the units property.
     * @return the units property
     */
    public TethysUnits getUnits() {
        return getValueSet().getValue(FIELD_UNITS, TethysUnits.class);
    }

    /**
     * Set the units property.
     * @param pValue the new value
     */
    public void setUnits(final TethysUnits pValue) {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Obtain the rate property.
     * @return the rate property
     */
    public TethysRate getRate() {
        return getValueSet().getValue(FIELD_RATE, TethysRate.class);
    }

    /**
     * Set the rate property.
     * @param pValue the new value
     */
    public void setRate(final TethysRate pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    /**
     * Obtain the ratio property.
     * @return the ratio property
     */
    public TethysRatio getRatio() {
        return getValueSet().getValue(FIELD_RATIO, TethysRatio.class);
    }

    /**
     * Set the ratio property.
     * @param pValue the new value
     */
    public void setRatio(final TethysRatio pValue) {
        getValueSet().setValue(FIELD_RATIO, pValue);
    }

    /**
     * Obtain the dilution property.
     * @return the dilution property
     */
    public TethysDilution getDilution() {
        return getValueSet().getValue(FIELD_DILUTION, TethysDilution.class);
    }

    /**
     * Set the dilution property.
     * @param pValue the new value
     */
    public void setDilution(final TethysDilution pValue) {
        getValueSet().setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Obtain the dilutedPrice property.
     * @return the dilutedPrice property
     */
    public TethysDilutedPrice getDilutedPrice() {
        return getValueSet().getValue(FIELD_DILUTEDPRICE, TethysDilutedPrice.class);
    }

    /**
     * Set the dilutedPrice property.
     * @param pValue the new value
     */
    public void setDilutedPrice(final TethysDilutedPrice pValue) {
        getValueSet().setValue(FIELD_DILUTEDPRICE, pValue);
    }

    /**
     * Obtain the date property.
     * @return the date property
     */
    public TethysDate getDate() {
        return getValueSet().getValue(FIELD_DATE, TethysDate.class);
    }

    /**
     * Set the date property.
     * @param pValue the new value
     */
    public void setDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Obtain the scroll property.
     * @return the scroll property
     */
    public String getScroll() {
        return getValueSet().getValue(FIELD_SCROLL, String.class);
    }

    /**
     * Set the scroll property.
     * @param pValue the new value
     */
    public void setScroll(final String pValue) {
        getValueSet().setValue(FIELD_SCROLL, pValue);
    }

    /**
     * Obtain the list property.
     * @return the list property
     */
    @SuppressWarnings("unchecked")
    public TethysItemList<TethysListId> getList() {
        return (TethysItemList<TethysListId>) getValueSet().getValue(FIELD_LIST, TethysItemList.class);
    }

    /**
     * Set the list property.
     * @param pValue the new value
     */
    public void setList(final TethysItemList<TethysListId> pValue) {
        getValueSet().setValue(FIELD_LIST, pValue);
    }

    /**
     * Obtain the updates property.
     * @return the updates property
     */
    public Integer getUpdates() {
        return getValueSet().getValue(FIELD_UPDATES, Integer.class);
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
        getValueSet().setValue(FIELD_UPDATES, pValue);
    }
}
