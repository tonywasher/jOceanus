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
package net.sourceforge.joceanus.jtethys.ui.javafx.field;

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
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataButtonField.TethysUIFXColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataButtonField.TethysUIFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataButtonField.TethysUIFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataButtonField.TethysUIFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataButtonField.TethysUIFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXCharArrayTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXRawDecimalTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.field.TethysUIFXDataTextField.TethysUIFXUnitsTextField;

/**
 * JavaFX field factory
 */
public class TethysUIFXFieldFactory
        implements TethysUIFieldFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    public TethysUIFXFieldFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIValidatedEditField<String> newStringField() {
        return new TethysUIFXStringTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<char[]> newCharArrayField() {
        return new TethysUIFXCharArrayTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Short> newShortField() {
        return new TethysUIFXShortTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Integer> newIntegerField() {
        return new TethysUIFXIntegerTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<Long> newLongField() {
        return new TethysUIFXLongTextField(theFactory);
    }

    @Override
    public TethysUIRawDecimalEditField newRawDecimalField() {
        return new TethysUIFXRawDecimalTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysMoney> newMoneyField() {
        return new TethysUIFXMoneyTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysPrice> newPriceField() {
        return new TethysUIFXPriceTextField(theFactory);
    }

    @Override
    public TethysUICurrencyEditField<TethysDilutedPrice> newDilutedPriceField() {
        return new TethysUIFXDilutedPriceTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysRate> newRateField() {
        return new TethysUIFXRateTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysUnits> newUnitsField() {
        return new TethysUIFXUnitsTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysDilution> newDilutionField() {
        return new TethysUIFXDilutionTextField(theFactory);
    }

    @Override
    public TethysUIValidatedEditField<TethysRatio> newRatioField() {
        return new TethysUIFXRatioTextField(theFactory);
    }

    @Override
    public TethysUIDateButtonField newDateField() {
        return new TethysUIFXDateButtonField(theFactory);
    }

    @Override
    public <T> TethysUIScrollButtonField<T> newScrollField() {
        return new TethysUIFXScrollButtonField<>(theFactory);
    }

    @Override
    public <T extends Comparable<T>> TethysUIListButtonField<T> newListField() {
        return new TethysUIFXListButtonField<>(theFactory);
    }

    @Override
    public <T> TethysUIIconButtonField<T> newIconField() {
        return new TethysUIFXIconButtonField<>(theFactory);
    }

    @Override
    public TethysUIDataEditField<String> newColorField() {
        return new TethysUIFXColorButtonField(theFactory);
    }
}
