/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.tethys.test.ui;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

import java.util.List;

/**
 * Tethys Table item.
 */
public class TethysTestTableItem {
    /**
     * Name property.
     */
    private String theName;

    /**
     * Password property.
     */
    private char[] thePassword;

    /**
     * Boolean property.
     */
    private Boolean theBoolean;

    /**
     * XtraBoolean property.
     */
    private Boolean theXtraBoolean;

    /**
     * Short property.
     */
    private Short theShort;

    /**
     * Integer property.
     */
    private Integer theInteger;

    /**
     * Long property.
     */
    private Long theLong;

    /**
     * Date property.
     */
    private OceanusDate theDate;

    /**
     * Money property.
     */
    private OceanusMoney theMoney;

    /**
     * Price property.
     */
    private OceanusPrice thePrice;

    /**
     * Units property.
     */
    private OceanusUnits theUnits;

    /**
     * Rate property.
     */
    private OceanusRate theRate;

    /**
     * Ratio property.
     */
    private OceanusRatio theRatio;

    /**
     * Scroll property.
     */
    private String theScroll;

    /**
     * List property.
     */
    private List<TethysTestListId> theList;

    /**
     * Updates property.
     */
    private Integer theUpdates;

    /**
     * Constructor.
     *
     * @param pHelper the Helper
     * @param pName   the Name
     */
    TethysTestTableItem(final TethysTestHelper pHelper,
                        final String pName) {
        theName = pName;
        theDate = new OceanusDate();
        theBoolean = Boolean.FALSE;
        theXtraBoolean = Boolean.FALSE;
        theShort = TethysTestHelper.SHORT_DEF;
        theInteger = TethysTestHelper.INT_DEF;
        theLong = TethysTestHelper.LONG_DEF;
        theMoney = TethysTestHelper.MONEY_DEF;
        thePrice = TethysTestHelper.PRICE_DEF;
        theUnits = TethysTestHelper.UNITS_DEF;
        theRate = TethysTestHelper.RATE_DEF;
        theRatio = TethysTestHelper.RATIO_DEF;
        theList = pHelper.buildSelectedList();
        theUpdates = 0;
    }

    /**
     * Obtain the name property.
     *
     * @return the name property
     */
    public String getName() {
        return theName;
    }

    /**
     * Set the name property.
     *
     * @param pValue the new value
     */
    public void setName(final String pValue) {
        theName = pValue;
    }

    /**
     * Obtain the password property.
     *
     * @return the password property
     */
    public char[] getPassword() {
        return thePassword;
    }

    /**
     * Set the password property.
     *
     * @param pValue the new value
     */
    public void setPassword(final char[] pValue) {
        thePassword = pValue;
    }

    /**
     * Obtain the boolean property.
     *
     * @return the boolean property
     */
    public Boolean getBoolean() {
        return theBoolean;
    }

    /**
     * Set the boolean property.
     *
     * @param pValue the new value
     */
    public void setBoolean(final Boolean pValue) {
        theBoolean = pValue;
    }

    /**
     * Obtain the extra boolean property.
     *
     * @return the boolean property
     */
    public Boolean getXtraBoolean() {
        return theXtraBoolean;
    }

    /**
     * Set the extra boolean property.
     *
     * @param pValue the new value
     */
    public void setXtraBoolean(final Boolean pValue) {
        theXtraBoolean = pValue;
    }

    /**
     * Obtain the short property.
     *
     * @return the short property
     */
    public Short getShort() {
        return theShort;
    }

    /**
     * Set the short property.
     *
     * @param pValue the new value
     */
    public void setShort(final Short pValue) {
        theShort = pValue;
    }

    /**
     * Obtain the integer property.
     *
     * @return the integer property
     */
    public Integer getInteger() {
        return theInteger;
    }

    /**
     * Set the integer property.
     *
     * @param pValue the new value
     */
    public void setInteger(final Integer pValue) {
        theInteger = pValue;
    }

    /**
     * Obtain the long property.
     *
     * @return the long property
     */
    public Long getLong() {
        return theLong;
    }

    /**
     * Set the long property.
     *
     * @param pValue the new value
     */
    public void setLong(final Long pValue) {
        theLong = pValue;
    }

    /**
     * Obtain the money property.
     *
     * @return the money property
     */
    public OceanusMoney getMoney() {
        return theMoney;
    }

    /**
     * Set the money property.
     *
     * @param pValue the new value
     */
    public void setMoney(final OceanusMoney pValue) {
        theMoney = pValue;
    }

    /**
     * Obtain the price property.
     *
     * @return the price property
     */
    public OceanusPrice getPrice() {
        return thePrice;
    }

    /**
     * Set the price property.
     *
     * @param pValue the new value
     */
    public void setPrice(final OceanusPrice pValue) {
        thePrice = pValue;
    }

    /**
     * Obtain the units property.
     *
     * @return the units property
     */
    public OceanusUnits getUnits() {
        return theUnits;
    }

    /**
     * Set the units property.
     *
     * @param pValue the new value
     */
    public void setUnits(final OceanusUnits pValue) {
        theUnits = pValue;
    }

    /**
     * Obtain the rate property.
     *
     * @return the rate property
     */
    public OceanusRate getRate() {
        return theRate;
    }

    /**
     * Set the rate property.
     *
     * @param pValue the new value
     */
    public void setRate(final OceanusRate pValue) {
        theRate = pValue;
    }

    /**
     * Obtain the ratio property.
     *
     * @return the ratio property
     */
    public OceanusRatio getRatio() {
        return theRatio;
    }

    /**
     * Set the ratio property.
     *
     * @param pValue the new value
     */
    public void setRatio(final OceanusRatio pValue) {
        theRatio = pValue;
    }


    /**
     * Obtain the date property.
     *
     * @return the date property
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Set the date property.
     *
     * @param pValue the new value
     */
    public void setDate(final OceanusDate pValue) {
        theDate = pValue;
    }

    /**
     * Obtain the scroll property.
     *
     * @return the scroll property
     */
    public String getScroll() {
        return theScroll;
    }

    /**
     * Set the scroll property.
     *
     * @param pValue the new value
     */
    public void setScroll(final String pValue) {
        theScroll = pValue;
    }

    /**
     * Obtain the list property.
     *
     * @return the list property
     */
    public List<TethysTestListId> getList() {
        return theList;
    }

    /**
     * Set the list property.
     *
     * @param pValue the new value
     */
    public void setList(final List<TethysTestListId> pValue) {
        theList = pValue;
    }

    /**
     * Obtain the updates property.
     *
     * @return the updates property
     */
    public Integer getUpdates() {
        return theUpdates;
    }

    /**
     * increment updates.
     */
    public void incrementUpdates() {
        theUpdates = theUpdates + 1;
    }
}

