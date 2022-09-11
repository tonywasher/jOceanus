/* *****************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset;

import java.util.function.Function;

import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * FieldSet TextArea.
 * @param <T> the item type
 */
public class PrometheusFieldSetTextArea<T>
        implements PrometheusFieldSetPanel<T> {
    /**
     * The gui factory.
     */
    private final TethysGuiFactory theFactory;

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<T> theFieldSet;

    /**
     * The panel.
     */
    private final TethysBorderPaneManager thePanel;

    /**
     * The fieldId.
     */
    private PrometheusDataFieldId theFieldId;

    /**
     * The text area.
     */
    private TethysCharArrayTextAreaField theTextArea;

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
    PrometheusFieldSetTextArea(final TethysGuiFactory pFactory,
                               final PrometheusFieldSet<T> pFieldSet) {
        /* Store the factory */
        theFactory = pFactory;
        theFieldSet = pFieldSet;

        /* Create the panel */
        thePanel = theFactory.newBorderPane();
    }


    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    /**
     * Add field to panel.
     * @param pFieldId the fieldId
     * @param pField the edit field
     * @param pValueFactory the valueFactory
     */
    public void addField(final PrometheusDataFieldId pFieldId,
                         final TethysCharArrayTextAreaField pField,
                         final Function<T, char[]> pValueFactory) {
        /* Store details */
        theFieldId = pFieldId;
        theTextArea = pField;
        theValueFactory = pValueFactory;

        /* create the scrollable pane */
        final TethysScrollPaneManager myScroll = theFactory.newScrollPane();
        myScroll.setContent(pField);

        /* Add to the panel and adjust label widths */
        thePanel.setCentre(myScroll);

        /* Register the field with the fieldSet */
        theFieldSet.registerField(pFieldId, this);

        /* Pass newData event to fieldSet */
        pField.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> theFieldSet.newData(pFieldId, e.getDetails()));
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
        theTextArea.setTheAttributeState(TethysFieldAttribute.CHANGED, isChanged);
        theTextArea.adjustField();
    }

    @Override
    public void setEditable(final boolean isEditable) {
        theTextArea.setEditable(isEditable);
    }

    @Override
    public void setEditable(final PrometheusDataFieldId pFieldId,
                            final boolean pEditable) {
        theTextArea.setEditable(pEditable);
    }

    @Override
    public void setVisible(final PrometheusDataFieldId pFieldId,
                           final boolean pVisible) {
        /* adjust the element */
        theTextArea.setVisible(pVisible);
        isVisible = pVisible;
    }
}

