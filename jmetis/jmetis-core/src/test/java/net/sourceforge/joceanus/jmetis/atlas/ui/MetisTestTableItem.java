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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;

/**
 * Metis Table item.
 */
public class MetisTestTableItem
        extends MetisFieldVersionedItem {
    /**
     * The Next itemId.
     */
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    /**
     * Report fields.
     */
    private static final MetisFieldVersionedSet<MetisTestTableItem> FIELD_DEFS = MetisFieldVersionedSet.newVersionedFieldSet(MetisTestTableItem.class);

    /**
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareStringField(MetisTestDataField.NAME, 20);
        FIELD_DEFS.declareCharArrayField(MetisTestDataField.PASSWORD, 50);
        FIELD_DEFS.declareDateField(MetisTestDataField.DATE);
        FIELD_DEFS.declareBooleanField(MetisTestDataField.BOOLEAN);
        FIELD_DEFS.declareShortField(MetisTestDataField.SHORT);
        FIELD_DEFS.declareIntegerField(MetisTestDataField.INTEGER);
        FIELD_DEFS.declareLongField(MetisTestDataField.LONG);
        FIELD_DEFS.declareMoneyField(MetisTestDataField.MONEY);
        FIELD_DEFS.declarePriceField(MetisTestDataField.PRICE);
        FIELD_DEFS.declareUnitsField(MetisTestDataField.UNITS);
        FIELD_DEFS.declareRateField(MetisTestDataField.RATE);
        FIELD_DEFS.declareRatioField(MetisTestDataField.RATIO);
        FIELD_DEFS.declareDilutionField(MetisTestDataField.DILUTION);
        FIELD_DEFS.declareDilutedPriceField(MetisTestDataField.DILUTEDPRICE);
        FIELD_DEFS.declareLinkField(MetisTestDataField.SCROLL);
        FIELD_DEFS.declareLinkSetField(MetisTestDataField.LIST);
        FIELD_DEFS.declareIntegerField(MetisTestDataField.UPDATES);
    }

    /**
     * Constructor.
     */
    public MetisTestTableItem() {
        /* Set new id */
        setIndexedId(NEXT_ID.getAndIncrement());
    }

    /**
     * Constructor.
     * @param pHelper the Helper
     * @param pName the Name
     * @throws OceanusException on error
     */
    public void initValues(final TethysScrollUITestHelper<?, ?> pHelper,
                           final String pName) throws OceanusException {
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
        setList(pHelper.buildSelectedList());
        setUpdates(Integer.valueOf(0));
    }

    @Override
    public MetisFieldSet<MetisTestTableItem> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return getVersionedField(MetisTestDataField.NAME).getFieldValue(this, String.class);
    }

    /**
     * Set the name property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setName(final String pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.NAME).setFieldValue(this, pValue);
    }

    /**
     * Obtain the password property.
     * @return the password property
     */
    public char[] getPassword() {
        return getVersionedField(MetisTestDataField.PASSWORD).getFieldValue(this, char[].class);
    }

    /**
     * Set the password property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setPassword(final char[] pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.PASSWORD).setFieldValue(this, pValue);
    }

    /**
     * Obtain the boolean property.
     * @return the boolean property
     */
    public Boolean getBoolean() {
        return getVersionedField(MetisTestDataField.BOOLEAN).getFieldValue(this, Boolean.class);
    }

    /**
     * Set the boolean property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setBoolean(final Boolean pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.BOOLEAN).setFieldValue(this, pValue);
    }

    /**
     * Obtain the extra boolean property.
     * @return the boolean property
     */
    public Boolean getXtraBoolean() {
        return getVersionedField(MetisTestDataField.XTRABOOL).getFieldValue(this, Boolean.class);
    }

    /**
     * Set the extra boolean property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setXtraBoolean(final Boolean pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.XTRABOOL).setFieldValue(this, pValue);
    }

    /**
     * Obtain the short property.
     * @return the short property
     */
    public Short getShort() {
        return getVersionedField(MetisTestDataField.SHORT).getFieldValue(this, Short.class);
    }

    /**
     * Set the short property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setShort(final Short pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.SHORT).setFieldValue(this, pValue);
    }

    /**
     * Obtain the integer property.
     * @return the integer property
     */
    public Integer getInteger() {
        return getVersionedField(MetisTestDataField.INTEGER).getFieldValue(this, Integer.class);
    }

    /**
     * Set the integer property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setInteger(final Integer pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.INTEGER).setFieldValue(this, pValue);
    }

    /**
     * Obtain the long property.
     * @return the long property
     */
    public Long getLong() {
        return getVersionedField(MetisTestDataField.LONG).getFieldValue(this, Long.class);
    }

    /**
     * Set the long property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setLong(final Long pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.LONG).setFieldValue(this, pValue);
    }

    /**
     * Obtain the money property.
     * @return the money property
     */
    public TethysMoney getMoney() {
        return getVersionedField(MetisTestDataField.MONEY).getFieldValue(this, TethysMoney.class);
    }

    /**
     * Set the money property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setMoney(final TethysMoney pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.MONEY).setFieldValue(this, pValue);
    }

    /**
     * Obtain the price property.
     * @return the price property
     */
    public TethysPrice getPrice() {
        return getVersionedField(MetisTestDataField.PRICE).getFieldValue(this, TethysPrice.class);
    }

    /**
     * Set the price property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setPrice(final TethysPrice pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.PRICE).setFieldValue(this, pValue);
    }

    /**
     * Obtain the units property.
     * @return the units property
     */
    public TethysUnits getUnits() {
        return getVersionedField(MetisTestDataField.UNITS).getFieldValue(this, TethysUnits.class);
    }

    /**
     * Set the units property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setUnits(final TethysUnits pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.UNITS).setFieldValue(this, pValue);
    }

    /**
     * Obtain the rate property.
     * @return the rate property
     */
    public TethysRate getRate() {
        return getVersionedField(MetisTestDataField.RATE).getFieldValue(this, TethysRate.class);
    }

    /**
     * Set the rate property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setRate(final TethysRate pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.RATE).setFieldValue(this, pValue);
    }

    /**
     * Obtain the ratio property.
     * @return the ratio property
     */
    public TethysRatio getRatio() {
        return getVersionedField(MetisTestDataField.RATIO).getFieldValue(this, TethysRatio.class);
    }

    /**
     * Set the ratio property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setRatio(final TethysRatio pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.RATIO).setFieldValue(this, pValue);
    }

    /**
     * Obtain the dilution property.
     * @return the dilution property
     */
    public TethysDilution getDilution() {
        return getVersionedField(MetisTestDataField.DILUTION).getFieldValue(this, TethysDilution.class);
    }

    /**
     * Set the dilution property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setDilution(final TethysDilution pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.DILUTION).setFieldValue(this, pValue);
    }

    /**
     * Obtain the dilutedPrice property.
     * @return the dilutedPrice property
     */
    public TethysDilutedPrice getDilutedPrice() {
        return getVersionedField(MetisTestDataField.DILUTEDPRICE).getFieldValue(this, TethysDilutedPrice.class);
    }

    /**
     * Set the dilutedPrice property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setDilutedPrice(final TethysDilutedPrice pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.DILUTEDPRICE).setFieldValue(this, pValue);
    }

    /**
     * Obtain the date property.
     * @return the date property
     */
    public TethysDate getDate() {
        return getVersionedField(MetisTestDataField.DATE).getFieldValue(this, TethysDate.class);
    }

    /**
     * Set the date property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setDate(final TethysDate pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.DATE).setFieldValue(this, pValue);
    }

    /**
     * Obtain the scroll property.
     * @return the scroll property
     */
    public String getScroll() {
        return getVersionedField(MetisTestDataField.SCROLL).getFieldValue(this, String.class);
    }

    /**
     * Set the scroll property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setScroll(final String pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.SCROLL).setFieldValue(this, pValue);
    }

    /**
     * Obtain the list property.
     * @return the list property
     */
    @SuppressWarnings("unchecked")
    public List<TethysListId> getList() {
        return getVersionedField(MetisTestDataField.LIST).getFieldValue(this, List.class);
    }

    /**
     * Set the list property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    public void setList(final List<TethysListId> pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.LIST).setFieldValue(this, pValue);
    }

    /**
     * Obtain the updates property.
     * @return the updates property
     */
    public Integer getUpdates() {
        return getVersionedField(MetisTestDataField.UPDATES).getFieldValue(this, Integer.class);
    }

    /**
     * increment updates.
     * @throws OceanusException on error
     */
    public void incrementUpdates() throws OceanusException {
        setUpdates(getUpdates() + 1);
    }

    /**
     * Set the updates property.
     * @param pValue the new value
     * @throws OceanusException on error
     */
    private void setUpdates(final Integer pValue) throws OceanusException {
        getVersionedField(MetisTestDataField.UPDATES).setFieldValue(this, pValue);
    }
}
