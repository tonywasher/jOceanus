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
package net.sourceforge.joceanus.jtethys.test.ui.javafx;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.test.ui.TethysDataId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.test.ui.TethysScrollUITestHelper;

/**
 * Tethys Table item.
 */
public class TethysFXTableItem {
    /**
     * Name property.
     */
    private final ObjectProperty<String> theName = new SimpleObjectProperty<>(this, TethysDataId.NAME.toString());

    /**
     * Password property.
     */
    private final ObjectProperty<char[]> thePassword = new SimpleObjectProperty<>(this, TethysDataId.PASSWORD.toString());

    /**
     * Boolean property.
     */
    private final ObjectProperty<Boolean> theBoolean = new SimpleObjectProperty<>(this, TethysDataId.BOOLEAN.toString());

    /**
     * XtraBoolean property.
     */
    private final ObjectProperty<Boolean> theXtraBoolean = new SimpleObjectProperty<>(this, TethysDataId.XTRABOOL.toString());
    
    /**
     * Short property.
     */
    private final ObjectProperty<Short> theShort = new SimpleObjectProperty<>(this, TethysDataId.SHORT.toString());

    /**
     * Integer property.
     */
    private final ObjectProperty<Integer> theInteger = new SimpleObjectProperty<>(this, TethysDataId.INTEGER.toString());

    /**
     * Long property.
     */
    private final ObjectProperty<Long> theLong = new SimpleObjectProperty<>(this, TethysDataId.LONG.toString());

    /**
     * Date property.
     */
    private final ObjectProperty<TethysDate> theDate = new SimpleObjectProperty<>(this, TethysDataId.DATE.toString());

    /**
     * Money property.
     */
    private final ObjectProperty<TethysMoney> theMoney = new SimpleObjectProperty<>(this, TethysDataId.MONEY.toString());

    /**
     * Price property.
     */
    private final ObjectProperty<TethysPrice> thePrice = new SimpleObjectProperty<>(this, TethysDataId.PRICE.toString());

    /**
     * Units property.
     */
    private final ObjectProperty<TethysUnits> theUnits = new SimpleObjectProperty<>(this, TethysDataId.UNITS.toString());

    /**
     * Rate property.
     */
    private final ObjectProperty<TethysRate> theRate = new SimpleObjectProperty<>(this, TethysDataId.RATE.toString());

    /**
     * Ratio property.
     */
    private final ObjectProperty<TethysRatio> theRatio = new SimpleObjectProperty<>(this, TethysDataId.RATIO.toString());

    /**
     * Dilution property.
     */
    private final ObjectProperty<TethysDilution> theDilution = new SimpleObjectProperty<>(this, TethysDataId.DILUTION.toString());

    /**
     * DilutedPrice property.
     */
    private final ObjectProperty<TethysDilutedPrice> theDilutedPrice = new SimpleObjectProperty<>(this, TethysDataId.DILUTEDPRICE.toString());

    /**
     * Scroll property.
     */
    private final ObjectProperty<String> theScroll = new SimpleObjectProperty<>(this, TethysDataId.SCROLL.toString());

    /**
     * List property.
     */
    private final ObjectProperty<List<TethysListId>> theList = new SimpleObjectProperty<>(this, TethysDataId.LIST.toString());

    /**
     * Updates property.
     */
    private final ObjectProperty<Integer> theUpdates = new SimpleObjectProperty<>(this, TethysDataId.UPDATES.toString());

    /**
     * Constructor.
     * @param pHelper the Helper
     * @param pName the Name
     */
    protected TethysFXTableItem(final TethysScrollUITestHelper<?, ?> pHelper,
                                final String pName) {
        theName.set(pName);
        theDate.set(new TethysDate());
        theBoolean.set(Boolean.FALSE);
        theXtraBoolean.set(Boolean.FALSE);
        theShort.set(TethysScrollUITestHelper.SHORT_DEF);
        theInteger.set(TethysScrollUITestHelper.INT_DEF);
        theLong.set(TethysScrollUITestHelper.LONG_DEF);
        theMoney.set(TethysScrollUITestHelper.MONEY_DEF);
        thePrice.set(TethysScrollUITestHelper.PRICE_DEF);
        theUnits.set(TethysScrollUITestHelper.UNITS_DEF);
        theRate.set(TethysScrollUITestHelper.RATE_DEF);
        theRatio.set(TethysScrollUITestHelper.RATIO_DEF);
        theDilution.set(TethysScrollUITestHelper.DILUTION_DEF);
        theList.setValue(pHelper.buildSelectedList());
        theUpdates.setValue(Integer.valueOf(0));
    }

    /**
     * Obtain the name property.
     * @return the name property
     */
    public ObjectProperty<String> nameProperty() {
        return theName;
    }

    /**
     * Obtain the password property.
     * @return the password property
     */
    public ObjectProperty<char[]> passwordProperty() {
        return thePassword;
    }

    /**
     * Obtain the boolean property.
     * @return the boolean property
     */
    public ObjectProperty<Boolean> booleanProperty() {
        return theBoolean;
    }

    /**
     * Obtain the extra boolean property.
     * @return the boolean property
     */
    public ObjectProperty<Boolean> xtraBooleanProperty() {
        return theXtraBoolean;
    }

    /**
     * Obtain the short property.
     * @return the short property
     */
    public ObjectProperty<Short> shortProperty() {
        return theShort;
    }

    /**
     * Obtain the integer property.
     * @return the integer property
     */
    public ObjectProperty<Integer> integerProperty() {
        return theInteger;
    }

    /**
     * Obtain the long property.
     * @return the long property
     */
    public ObjectProperty<Long> longProperty() {
        return theLong;
    }

    /**
     * Obtain the money property.
     * @return the money property
     */
    public ObjectProperty<TethysMoney> moneyProperty() {
        return theMoney;
    }

    /**
     * Obtain the price property.
     * @return the price property
     */
    public ObjectProperty<TethysPrice> priceProperty() {
        return thePrice;
    }

    /**
     * Obtain the units property.
     * @return the units property
     */
    public ObjectProperty<TethysUnits> unitsProperty() {
        return theUnits;
    }

    /**
     * Obtain the rate property.
     * @return the rate property
     */
    public ObjectProperty<TethysRate> rateProperty() {
        return theRate;
    }

    /**
     * Obtain the ratio property.
     * @return the ratio property
     */
    public ObjectProperty<TethysRatio> ratioProperty() {
        return theRatio;
    }

    /**
     * Obtain the dilution property.
     * @return the dilution property
     */
    public ObjectProperty<TethysDilution> dilutionProperty() {
        return theDilution;
    }

    /**
     * Obtain the dilutedPrice property.
     * @return the dilutedPrice property
     */
    public ObjectProperty<TethysDilutedPrice> dilutedPriceProperty() {
        return theDilutedPrice;
    }

    /**
     * Obtain the date property.
     * @return the date property
     */
    public ObjectProperty<TethysDate> dateProperty() {
        return theDate;
    }

    /**
     * Obtain the scroll property.
     * @return the scroll property
     */
    public ObjectProperty<String> scrollProperty() {
        return theScroll;
    }

    /**
     * Obtain the list property.
     * @return the list property
     */
    public ObjectProperty<List<TethysListId>> listProperty() {
        return theList;
    }

    /**
     * Obtain the updates property.
     * @return the updates property
     */
    public ObjectProperty<Integer> updatesProperty() {
        return theUpdates;
    }

    /**
     * increment updates.
     */
    public void incrementUpdates() {
        theUpdates.set(theUpdates.get() + 1);
    }
}
