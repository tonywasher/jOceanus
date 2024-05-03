/* *****************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.ui.fieldset;

import java.util.function.Consumer;
import java.util.function.Function;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIScrollPaneManager;

/**
 * FieldSet TextArea.
 * @param <T> the item type
 */
public class PrometheusFieldSetTextArea<T>
        implements PrometheusFieldSetPanel<T> {
    /**
     * The gui factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<T> theFieldSet;

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The fieldId.
     */
    private MetisDataFieldId theFieldId;

    /**
     * The text area.
     */
    private TethysUICharArrayTextAreaField theTextArea;

    /**
     * The value factory.
     */
    private Function<T, char[]> theValueFactory;

    /**
     * Is the field visible?
     */
    private boolean isVisible;

    /**
     * The current item.
     */
    private T theItem;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pFieldSet the fieldSet
     */
    PrometheusFieldSetTextArea(final TethysUIFactory<?> pFactory,
                               final PrometheusFieldSet<T> pFieldSet) {
        /* Store the factory */
        theFactory = pFactory;
        theFieldSet = pFieldSet;

        /* Create the panel */
        thePanel = theFactory.paneFactory().newBorderPane();
    }


    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * Add field to panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final MetisDataFieldId pFieldId,
                         final TethysUICharArrayTextAreaField pField,
                         final Function<T, char[]> pValueFactory) {
        /* Store details */
        theFieldId = pFieldId;
        theTextArea = pField;
        theValueFactory = pValueFactory;

        /* create the scrollable pane */
        final TethysUIScrollPaneManager myScroll = theFactory.paneFactory().newScrollPane();
        myScroll.setContent(pField);

        /* Add to the panel and adjust label widths */
        thePanel.setCentre(myScroll);

        /* Register the field with the fieldSet */
        theFieldSet.registerField(pFieldId, this);

        /* Pass newData event to fieldSet */
        pField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theFieldSet.newData(pFieldId, e.getDetails()));
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setItem(final T pItem) {
        /* Store the item */
        theItem = pItem;

        /* Store the value */
        final char[] myValue = theValueFactory.apply(pItem);
        theTextArea.setValue(myValue);
    }

    @Override
    public void adjustChanged() {
        /* Ignore if we have no item */
        if (theItem == null) {
            return;
        }

        /* Update the changed flags */
        final boolean isChanged = theFieldSet.isChanged(theItem, theFieldId);
        theTextArea.setTheAttributeState(TethysUIFieldAttribute.CHANGED, isChanged);
        theTextArea.adjustField();
    }

    @Override
    public void setEditable(final boolean isEditable) {
        theTextArea.setEditable(isEditable);
    }

    @Override
    public void setEditable(final MetisDataFieldId pFieldId,
                            final boolean pEditable) {
        theTextArea.setEditable(pEditable);
    }

    @Override
    public void setVisible(final MetisDataFieldId pFieldId,
                           final boolean pVisible) {
        /* adjust the element */
        theTextArea.setVisible(pVisible);
        isVisible = pVisible;
    }

    @Override
    public void setReporter(final Consumer<String> pReporter) {
        theTextArea.setReporter(pReporter);
    }
}

