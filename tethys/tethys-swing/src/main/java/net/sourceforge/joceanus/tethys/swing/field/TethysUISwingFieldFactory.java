/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.field;

import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIRawDecimalEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingCharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingCharArrayTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingIntegerTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingLongTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingMoneyTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingPriceTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRateTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRatioTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingRawDecimalTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingShortTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingStringTextAreaField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingStringTextField;
import net.sourceforge.joceanus.tethys.swing.field.TethysUISwingDataTextField.TethysUISwingUnitsTextField;

/**
 * Swing field factory.
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
    public TethysUISwingStringTextField newStringField() {
        return new TethysUISwingStringTextField(theFactory);
    }

    @Override
    public TethysUISwingStringTextAreaField newStringAreaField() {
        return new TethysUISwingStringTextAreaField(theFactory);
    }

    @Override
    public TethysUISwingCharArrayTextField newCharArrayField() {
        return new TethysUISwingCharArrayTextField(theFactory);
    }

    @Override
    public TethysUISwingCharArrayTextAreaField newCharArrayAreaField() {
        return new TethysUISwingCharArrayTextAreaField(theFactory);
    }

    @Override
    public TethysUISwingShortTextField newShortField() {
        return new TethysUISwingShortTextField(theFactory);
    }

    @Override
    public TethysUISwingIntegerTextField newIntegerField() {
        return new TethysUISwingIntegerTextField(theFactory);
    }

    @Override
    public TethysUISwingLongTextField newLongField() {
        return new TethysUISwingLongTextField(theFactory);
    }

    @Override
    public TethysUIRawDecimalEditField newRawDecimalField() {
        return new TethysUISwingRawDecimalTextField(theFactory);
    }

    @Override
    public TethysUISwingMoneyTextField newMoneyField() {
        return new TethysUISwingMoneyTextField(theFactory);
    }

    @Override
    public TethysUISwingPriceTextField newPriceField() {
        return new TethysUISwingPriceTextField(theFactory);
    }

    @Override
    public TethysUISwingRateTextField newRateField() {
        return new TethysUISwingRateTextField(theFactory);
    }

    @Override
    public TethysUISwingUnitsTextField newUnitsField() {
        return new TethysUISwingUnitsTextField(theFactory);
    }

    @Override
    public TethysUISwingRatioTextField newRatioField() {
        return new TethysUISwingRatioTextField(theFactory);
    }

    @Override
    public TethysUIDateButtonField newDateField() {
        return new TethysUISwingDateButtonField(theFactory);
    }

    @Override
    public <T> TethysUIScrollButtonField<T> newScrollField(final Class<T> pClazz) {
        return new TethysUISwingScrollButtonField<>(theFactory, pClazz);
    }

    @Override
    public <T extends Comparable<? super T>> TethysUIListButtonField<T> newListField() {
        return new TethysUISwingListButtonField<>(theFactory);
    }

    @Override
    public <T> TethysUIIconButtonField<T> newIconField(final Class<T> pClazz) {
        return new TethysUISwingIconButtonField<>(theFactory, pClazz);
    }

    @Override
    public TethysUISwingColorButtonField newColorField() {
        return new TethysUISwingColorButtonField(theFactory);
    }
}
