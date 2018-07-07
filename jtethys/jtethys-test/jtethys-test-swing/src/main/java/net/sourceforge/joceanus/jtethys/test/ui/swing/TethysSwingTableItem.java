/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.test.ui.swing;

import java.util.List;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;

/**
 * Tethys Table item.
 */
public class TethysSwingTableItem {
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
    private TethysDate theDate;

    /**
     * Money property.
     */
    private TethysMoney theMoney;

    /**
     * Price property.
     */
    private TethysPrice thePrice;

    /**
     * Units property.
     */
    private TethysUnits theUnits;

    /**
     * Rate property.
     */
    private TethysRate theRate;

    /**
     * Ratio property.
     */
    private TethysRatio theRatio;

    /**
     * Dilution property.
     */
    private TethysDilution theDilution;

    /**
     * DilutedPrice property.
     */
    private TethysDilutedPrice theDilutedPrice;

    /**
     * Scroll property.
     */
    private String theScroll;

    /**
     * List property.
     */
    private List<TethysListId> theList;

    /**
     * Updates property.
     */
    private Integer theUpdates;

    /**
     * Constructor.
     * @param pHelper the Helper
     * @param pName the Name
     */
    protected TethysSwingTableItem(final TethysScrollUITestHelper pHelper,
                                   final String pName) {
        theName = pName;
        theDate = new TethysDate();
        theBoolean = Boolean.FALSE;
        theXtraBoolean = Boolean.FALSE;
        theShort = TethysScrollUITestHelper.SHORT_DEF;
        theInteger = TethysScrollUITestHelper.INT_DEF;
        theLong = TethysScrollUITestHelper.LONG_DEF;
        theMoney = TethysScrollUITestHelper.MONEY_DEF;
        thePrice = TethysScrollUITestHelper.PRICE_DEF;
        theUnits = TethysScrollUITestHelper.UNITS_DEF;
        theRate = TethysScrollUITestHelper.RATE_DEF;
        theRatio = TethysScrollUITestHelper.RATIO_DEF;
        theDilution = TethysScrollUITestHelper.DILUTION_DEF;
        theList = pHelper.buildSelectedList();
        theUpdates = Integer.valueOf(0);
    }

    /**
     * Obtain the name property.
     * @return the name property
     */
    public String getName() {
        return theName;
    }

    /**
     * Set the name property.
     * @param pValue the new value
     */
    public void setName(final String pValue) {
        theName = pValue;
    }

    /**
     * Obtain the password property.
     * @return the password property
     */
    public char[] getPassword() {
        return thePassword;
    }

    /**
     * Set the password property.
     * @param pValue the new value
     */
    public void setPassword(final char[] pValue) {
        thePassword = pValue;
    }

    /**
     * Obtain the boolean property.
     * @return the boolean property
     */
    public Boolean getBoolean() {
        return theBoolean;
    }

    /**
     * Set the boolean property.
     * @param pValue the new value
     */
    public void setBoolean(final Boolean pValue) {
        theBoolean = pValue;
    }

    /**
     * Obtain the extra boolean property.
     * @return the boolean property
     */
    public Boolean getXtraBoolean() {
        return theXtraBoolean;
    }

    /**
     * Set the extra boolean property.
     * @param pValue the new value
     */
    public void setXtraBoolean(final Boolean pValue) {
        theXtraBoolean = pValue;
    }

    /**
     * Obtain the short property.
     * @return the short property
     */
    public Short getShort() {
        return theShort;
    }

    /**
     * Set the short property.
     * @param pValue the new value
     */
    public void setShort(final Short pValue) {
        theShort = pValue;
    }

    /**
     * Obtain the integer property.
     * @return the integer property
     */
    public Integer getInteger() {
        return theInteger;
    }

    /**
     * Set the integer property.
     * @param pValue the new value
     */
    public void setInteger(final Integer pValue) {
        theInteger = pValue;
    }

    /**
     * Obtain the long property.
     * @return the long property
     */
    public Long getLong() {
        return theLong;
    }

    /**
     * Set the long property.
     * @param pValue the new value
     */
    public void setLong(final Long pValue) {
        theLong = pValue;
    }

    /**
     * Obtain the money property.
     * @return the money property
     */
    public TethysMoney getMoney() {
        return theMoney;
    }

    /**
     * Set the money property.
     * @param pValue the new value
     */
    public void setMoney(final TethysMoney pValue) {
        theMoney = pValue;
    }

    /**
     * Obtain the price property.
     * @return the price property
     */
    public TethysPrice getPrice() {
        return thePrice;
    }

    /**
     * Set the price property.
     * @param pValue the new value
     */
    public void setPrice(final TethysPrice pValue) {
        thePrice = pValue;
    }

    /**
     * Obtain the units property.
     * @return the units property
     */
    public TethysUnits getUnits() {
        return theUnits;
    }

    /**
     * Set the units property.
     * @param pValue the new value
     */
    public void setUnits(final TethysUnits pValue) {
        theUnits = pValue;
    }

    /**
     * Obtain the rate property.
     * @return the rate property
     */
    public TethysRate getRate() {
        return theRate;
    }

    /**
     * Set the rate property.
     * @param pValue the new value
     */
    public void setRate(final TethysRate pValue) {
        theRate = pValue;
    }

    /**
     * Obtain the ratio property.
     * @return the ratio property
     */
    public TethysRatio getRatio() {
        return theRatio;
    }

    /**
     * Set the ratio property.
     * @param pValue the new value
     */
    public void setRatio(final TethysRatio pValue) {
        theRatio = pValue;
    }

    /**
     * Obtain the dilution property.
     * @return the dilution property
     */
    public TethysDilution getDilution() {
        return theDilution;
    }

    /**
     * Set the dilution property.
     * @param pValue the new value
     */
    public void setDilution(final TethysDilution pValue) {
        theDilution = pValue;
    }

    /**
     * Obtain the dilutedPrice property.
     * @return the dilutedPrice property
     */
    public TethysDilutedPrice getDilutedPrice() {
        return theDilutedPrice;
    }

    /**
     * Set the dilutedPrice property.
     * @param pValue the new value
     */
    public void setDilutedPrice(final TethysDilutedPrice pValue) {
        theDilutedPrice = pValue;
    }

    /**
     * Obtain the date property.
     * @return the date property
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Set the date property.
     * @param pValue the new value
     */
    public void setDate(final TethysDate pValue) {
        theDate = pValue;
    }

    /**
     * Obtain the scroll property.
     * @return the scroll property
     */
    public String getScroll() {
        return theScroll;
    }

    /**
     * Set the scroll property.
     * @param pValue the new value
     */
    public void setScroll(final String pValue) {
        theScroll = pValue;
    }

    /**
     * Obtain the list property.
     * @return the list property
     */
    public List<TethysListId> getList() {
        return theList;
    }

    /**
     * Set the list property.
     * @param pValue the new value
     */
    public void setList(final List<TethysListId> pValue) {
        theList = pValue;
    }

    /**
     * Obtain the updates property.
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
