/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.field;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRawDecimalEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIValidatedEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataButtonField.TethysUISwingColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataButtonField.TethysUISwingDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataButtonField.TethysUISwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataButtonField.TethysUISwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataButtonField.TethysUISwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.field.TethysUISwingDataTextField.TethysUISwingUnitsTextField;

/**
 * Swing field factory
 */
public class TethysUISwingFieldFactory
        implements TethysUIFieldFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUISwingFieldFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIValidatedEditField<String> newStringField() {
        return new TethysUISwingStringTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<char[]> newCharArrayField() {
        return new TethysUISwingCharArrayTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Short> newShortField() {
        return new TethysUISwingShortTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Integer> newIntegerField() {
        return new TethysUISwingIntegerTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Long> newLongField() {
        return new TethysUISwingLongTextField(theFactory);
    }

    @Override
    public TethysUIRawDecimalEditField newRawDecimalField() {
        return new TethysUISwingRawDecimalTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysMoney> newMoneyField() {
        return new TethysUISwingMoneyTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysPrice> newPriceField() {
        return new TethysUISwingPriceTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysDilutedPrice> newDilutedPriceField() {
        return new TethysUISwingDilutedPriceTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysRate> newRateField() {
        return new TethysUISwingRateTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysUnits> newUnitsField() {
        return new TethysUISwingUnitsTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysDilution> newDilutionField() {
        return new TethysUISwingDilutionTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysRatio> newRatioField() {
        return new TethysUISwingRatioTextField(theFactory);
    }

    @Override
    public TethysUIDateButtonField newDateField() {
        return new TethysUISwingDateButtonField(theFactory);
    }

    @Override
    public <T> TethysUIScrollButtonField<T> newScrollField() {
        return new TethysUISwingScrollButtonField<>(theFactory);
    }

    @Override
    public <T extends Comparable<T>> TethysUIListButtonField<T> newListField() {
        return new TethysUISwingListButtonField<>(theFactory);
    }

    @Override
    public <T> TethysUIIconButtonField<T> newIconField() {
        return new TethysUISwingIconButtonField<>(theFactory);
    }

    @Override
    public TethysUIDataEditField<String> newColorField() {
        return new TethysUISwingColorButtonField(theFactory);
    }
}
