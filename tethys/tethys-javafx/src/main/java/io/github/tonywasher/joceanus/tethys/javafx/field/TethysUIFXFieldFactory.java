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
package io.github.tonywasher.joceanus.tethys.javafx.field;

import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIListButtonField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIRawDecimalEditField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldFactory;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXCharArrayTextAreaField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXCharArrayTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXIntegerTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXLongTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXMoneyTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXPriceTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRateTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRatioTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXRawDecimalTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXShortTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXStringTextAreaField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXStringTextField;
import io.github.tonywasher.joceanus.tethys.javafx.field.TethysUIFXDataTextField.TethysUIFXUnitsTextField;

/**
 * JavaFX field factory.
 */
public class TethysUIFXFieldFactory
        implements TethysUIFieldFactory {
    /**
     * The Factory.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory.
     */
    public TethysUIFXFieldFactory(final TethysUICoreFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;
    }

    @Override
    public TethysUIFXStringTextField newStringField() {
        return new TethysUIFXStringTextField(theFactory);
    }

    @Override
    public TethysUIFXStringTextAreaField newStringAreaField() {
        return new TethysUIFXStringTextAreaField(theFactory);
    }

    @Override
    public TethysUIFXCharArrayTextField newCharArrayField() {
        return new TethysUIFXCharArrayTextField(theFactory);
    }

    @Override
    public TethysUIFXCharArrayTextAreaField newCharArrayAreaField() {
        return new TethysUIFXCharArrayTextAreaField(theFactory);
    }

    @Override
    public TethysUIFXShortTextField newShortField() {
        return new TethysUIFXShortTextField(theFactory);
    }

    @Override
    public TethysUIFXIntegerTextField newIntegerField() {
        return new TethysUIFXIntegerTextField(theFactory);
    }

    @Override
    public TethysUIFXLongTextField newLongField() {
        return new TethysUIFXLongTextField(theFactory);
    }

    @Override
    public TethysUIRawDecimalEditField newRawDecimalField() {
        return new TethysUIFXRawDecimalTextField(theFactory);
    }

    @Override
    public TethysUIFXMoneyTextField newMoneyField() {
        return new TethysUIFXMoneyTextField(theFactory);
    }

    @Override
    public TethysUIFXPriceTextField newPriceField() {
        return new TethysUIFXPriceTextField(theFactory);
    }

    @Override
    public TethysUIFXRateTextField newRateField() {
        return new TethysUIFXRateTextField(theFactory);
    }

    @Override
    public TethysUIFXUnitsTextField newUnitsField() {
        return new TethysUIFXUnitsTextField(theFactory);
    }

    @Override
    public TethysUIFXRatioTextField newRatioField() {
        return new TethysUIFXRatioTextField(theFactory);
    }

    @Override
    public TethysUIDateButtonField newDateField() {
        return new TethysUIFXDateButtonField(theFactory);
    }

    @Override
    public <T> TethysUIScrollButtonField<T> newScrollField(final Class<T> pClazz) {
        return new TethysUIFXScrollButtonField<>(theFactory, pClazz);
    }

    @Override
    public <T extends Comparable<? super T>> TethysUIListButtonField<T> newListField() {
        return new TethysUIFXListButtonField<>(theFactory);
    }

    @Override
    public <T> TethysUIIconButtonField<T> newIconField(final Class<T> pClazz) {
        return new TethysUIFXIconButtonField<>(theFactory, pClazz);
    }

    @Override
    public TethysUIFXColorButtonField newColorField() {
        return new TethysUIFXColorButtonField(theFactory);
    }
}
