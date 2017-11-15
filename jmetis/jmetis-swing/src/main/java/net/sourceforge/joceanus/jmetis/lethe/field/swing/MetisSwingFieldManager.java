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
package net.sourceforge.joceanus.jmetis.lethe.field.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldEvent;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldCalendarCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldDilutionCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIntegerCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldListButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldMoneyCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldPriceCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldRateCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldRatioCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldUnitsCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldDecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIntegerCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldRowCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Class to determine rendering details for an item.
 */
public class MetisSwingFieldManager
        implements TethysEventProvider<MetisLetheFieldEvent> {
    /**
     * Money accounting format width.
     */
    private static final int ACCOUNTING_WIDTH = 10;

    /**
     * Standard Font pitch.
     */
    private static final int PITCH_STD = 12;

    /**
     * Highlighted Font Pitch.
     */
    private static final int PITCH_HILITE = 14;

    /**
     * Value Font.
     */
    private static final String FONTFACE_VALUE = "Arial";

    /**
     * Numeric Font.
     */
    private static final String FONTFACE_NUMERIC = "Courier";

    /**
     * The standard font.
     */
    private static final Font FONT_STANDARD = new Font(FONTFACE_VALUE, Font.PLAIN, PITCH_STD);

    /**
     * The numeric font.
     */
    private static final Font FONT_NUMERIC = new Font(FONTFACE_NUMERIC, Font.PLAIN, PITCH_STD);

    /**
     * The changed font.
     */
    private static final Font FONT_CHANGED = new Font(FONTFACE_VALUE, Font.ITALIC, PITCH_STD);

    /**
     * The changed numeric font.
     */
    private static final Font FONT_NUMCHANGED = new Font(FONTFACE_NUMERIC, Font.ITALIC, PITCH_STD);

    /**
     * The standard font.
     */
    private static final Font FONT_HI_STANDARD = new Font(FONTFACE_VALUE, Font.BOLD, PITCH_HILITE);

    /**
     * The numeric font.
     */
    private static final Font FONT_HI_NUMERIC = new Font(FONTFACE_NUMERIC, Font.BOLD, PITCH_HILITE);

    /**
     * The changed font.
     */
    private static final Font FONT_HI_CHANGED = new Font(FONTFACE_VALUE, Font.BOLD + Font.ITALIC, PITCH_HILITE);

    /**
     * The changed numeric font.
     */
    private static final Font FONT_HI_NUMCHANGED = new Font(FONTFACE_NUMERIC, Font.BOLD + Font.ITALIC, PITCH_HILITE);

    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisLetheFieldEvent> theEventManager;

    /**
     * General formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The Configuration.
     */
    private MetisSwingFieldConfig theConfig;

    /**
     * The Error Border.
     */
    private Border theErrorBorder;

    /**
     * Constructor.
     * @param pGuiFactory the gui factory
     * @param pConfig the render configuration
     */
    public MetisSwingFieldManager(final TethysSwingGuiFactory pGuiFactory,
                             final MetisSwingFieldConfig pConfig) {
        /* Store the parameters */
        theFactory = pGuiFactory;
        theConfig = pConfig;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create data formatter */
        theFormatter = new MetisDataFormatter();
        theFormatter.setAccountingWidth(ACCOUNTING_WIDTH);
    }

    @Override
    public TethysEventRegistrar<MetisLetheFieldEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public MetisDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the configuration.
     * @return the configuration
     */
    public MetisSwingFieldConfig getConfig() {
        return theConfig;
    }

    /**
     * Get the error Border.
     * @return the state
     */
    protected Border getErrorBorder() {
        return theErrorBorder;
    }

    /**
     * Populate FieldData interface.
     */
    @FunctionalInterface
    public interface PopulateFieldData {
        /**
         * Get field data for row.
         * @param pData the Field details
         */
        void populateFieldData(MetisSwingFieldData pData);
    }

    /**
     * Set configuration.
     * @param pConfig the render configuration
     */
    public void setConfig(final MetisSwingFieldConfig pConfig) {
        /* Store the parameters */
        theConfig = pConfig;

        /* Allocate the Error border */
        theErrorBorder = BorderFactory.createLineBorder(theConfig.getErrorColor());

        /* Fire event */
        theEventManager.fireEvent(MetisLetheFieldEvent.FIELDUPDATED);
    }

    /**
     * Allocate a RenderData object.
     * @param isFixed is the data fixed width
     * @return the render data
     */
    protected MetisSwingFieldData allocateRenderData(final boolean isFixed) {
        /* Return a new RenderData object */
        return new MetisSwingFieldData(this, isFixed);
    }

    /**
     * Determine render data for FieldSetElement.
     * @param <X> the item type
     * @param pElement the fieldSet element
     * @param pItem the data item
     * @return the render data
     */
    protected <X extends MetisFieldSetItem> MetisSwingFieldData determineRenderData(final MetisSwingFieldElement<X> pElement,
                                                                               final X pItem) {
        /* Allocate the render data */
        final MetisSwingFieldData myData = new MetisSwingFieldData(this, pElement.isFixedWidth());

        /* Determine the render data */
        myData.determineData(pElement, pItem);

        /* Return the data */
        return myData;
    }

    /**
     * Allocate a StringRenderer object.
     * @return the string renderer
     */
    public MetisFieldStringCellRenderer allocateStringCellRenderer() {
        /* Return a new StringRenderer object */
        return new MetisFieldStringCellRenderer(this);
    }

    /**
     * Allocate an IconButtonRenderer object.
     * @param <T> the type of the object
     * @param pClazz the class of the objects
     * @return the icon renderer
     */
    public <T> MetisFieldIconButtonCellRenderer<T> allocateIconButtonCellRenderer(final Class<T> pClazz) {
        /* Return a new IconRenderer object */
        return new MetisFieldIconButtonCellRenderer<>(theFactory, this, pClazz);
    }

    /**
     * Allocate an IntegerRenderer object.
     * @return the integer renderer
     */
    public MetisFieldIntegerCellRenderer allocateIntegerCellRenderer() {
        /* Return a new IntegerRenderer object */
        return new MetisFieldIntegerCellRenderer(this);
    }

    /**
     * Allocate a CalendarRenderer object.
     * @return the calendar renderer
     */
    public MetisFieldCalendarCellRenderer allocateCalendarCellRenderer() {
        /* Return a new CalendarRenderer object */
        return new MetisFieldCalendarCellRenderer(this, theFormatter.getDateFormatter());
    }

    /**
     * Allocate a DecimalRenderer object.
     * @return the decimal renderer
     */
    public MetisFieldDecimalCellRenderer allocateDecimalCellRenderer() {
        /* Return a new DecimalRenderer object */
        return new MetisFieldDecimalCellRenderer(this, theFormatter.getDecimalFormatter());
    }

    /**
     * Allocate a RowRenderer object.
     * @return the row renderer
     */
    public MetisFieldRowCellRenderer allocateRowRenderer() {
        /* Return a new RowRenderer object */
        return new MetisFieldRowCellRenderer(this);
    }

    /**
     * Allocate a StringEditor object.
     * @return the string editor
     */
    public MetisFieldStringCellEditor allocateStringCellEditor() {
        /* Return a new StringEditor object */
        return new MetisFieldStringCellEditor();
    }

    /**
     * Allocate an IntegerEditor object.
     * @return the integer editor
     */
    public MetisFieldIntegerCellEditor allocateIntegerCellEditor() {
        /* Return a new IntegerEditor object */
        return new MetisFieldIntegerCellEditor();
    }

    /**
     * Allocate a CalendarEditor object.
     * @return the calendar editor
     */
    public MetisFieldCalendarCellEditor allocateCalendarCellEditor() {
        /* Return a new CalendarEditor object */
        return new MetisFieldCalendarCellEditor(theFactory.newDateButton());
    }

    /**
     * Allocate an IconButtonEditor object.
     * @param <T> the type of the object
     * @param pClazz the class of the objects
     * @return the icon editor
     */
    public <T> MetisFieldIconButtonCellEditor<T> allocateIconButtonCellEditor(final Class<T> pClazz) {
        /* Return a new IconButtonEditor object */
        return new MetisFieldIconButtonCellEditor<>(theFactory.newIconButton(), pClazz);
    }

    /**
     * Allocate a ScrollButtonEditor object.
     * @param <T> the type of the object
     * @param pClazz the class of the objects
     * @return the ScrollButton editor
     */
    public <T> MetisFieldScrollButtonCellEditor<T> allocateScrollButtonCellEditor(final Class<T> pClazz) {
        /* Return a new ScrollButtonEditor object */
        return new MetisFieldScrollButtonCellEditor<>(theFactory.newScrollButton(), pClazz);
    }

    /**
     * Allocate a ScrollListButtonEditor object.
     * @param <T> the type of the object
     * @param pClazz the class of the objects
     * @return the ScrollListButton editor
     */
    public <T extends Comparable<T>> MetisFieldListButtonCellEditor<T> allocateScrollListButtonCellEditor(final Class<T> pClazz) {
        /* Return a new ScrollListButtonEditor object */
        return new MetisFieldListButtonCellEditor<>(theFactory.newListButton(), pClazz);
    }

    /**
     * Allocate a MoneyEditor object.
     * @return the money editor
     */
    public MetisFieldMoneyCellEditor allocateMoneyCellEditor() {
        /* Return a new MoneyEditor object */
        return new MetisFieldMoneyCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RateEditor object.
     * @return the rate editor
     */
    public MetisFieldRateCellEditor allocateRateCellEditor() {
        /* Return a new RateEditor object */
        return new MetisFieldRateCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a UnitsEditor object.
     * @return the units editor
     */
    public MetisFieldUnitsCellEditor allocateUnitsCellEditor() {
        /* Return a new UnitsEditor object */
        return new MetisFieldUnitsCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RatioEditor object.
     * @return the ratio editor
     */
    public MetisFieldRatioCellEditor allocateRatioCellEditor() {
        /* Return a new RatioEditor object */
        return new MetisFieldRatioCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a PriceEditor object.
     * @return the price editor
     */
    public MetisFieldPriceCellEditor allocatePriceCellEditor() {
        /* Return a new PriceEditor object */
        return new MetisFieldPriceCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RateEditor object.
     * @return the rate editor
     */
    public MetisFieldDilutionCellEditor allocateDilutionCellEditor() {
        /* Return a new DilutionEditor object */
        return new MetisFieldDilutionCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Determine Standard foreground.
     * @param pItem the Item
     * @param pField the Field number
     * @return the standard foreground for the item
     */
    public Color getForeground(final MetisFieldSetItem pItem,
                               final MetisField pField) {
        /* Access foreground for item */
        return getForeground(pItem.getFieldState(pField));
    }

    /**
     * Determine foreground for the state.
     * @param pState the render state
     * @return the foreground colour
     */
    protected Color getForeground(final MetisFieldState pState) {
        /* Handle changed items */
        return theConfig.getColorForState(pState);
    }

    /**
     * Determine Standard background.
     * @return the standard background
     */
    public Color getStandardBackground() {
        return theConfig.getBackgroundColor();
    }

    /**
     * Determine Disabled color.
     * @return the disabled color
     */
    public Color getDisabledColor() {
        return theConfig.getDisabledColor();
    }

    /**
     * Determine Zebra color.
     * @return the zebra color
     */
    public Color getZebraColor() {
        return theConfig.getZebraColor();
    }

    /**
     * Determine Standard Font.
     * @param pItem the Item
     * @param pField the Field
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    public Font determineFont(final MetisFieldSetItem pItem,
                              final MetisField pField,
                              final boolean isFixed) {
        /* Return the font */
        return determineFont(pItem.getFieldState(pField), isFixed);
    }

    /**
     * Determine Standard Font.
     * @param pState render state
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    protected Font determineFont(final MetisFieldState pState,
                                 final boolean isFixed) {
        /* Switch on the state */
        if (MetisFieldState.CHANGED.equals(pState)) {
            return isFixed
                           ? FONT_NUMCHANGED
                           : FONT_CHANGED;
        }
        return isFixed
                       ? FONT_NUMERIC
                       : FONT_STANDARD;
    }

    /**
     * Determine Highlighted Font.
     * @param pItem the Item
     * @param pField the Field
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    public Font determineHiFont(final MetisFieldSetItem pItem,
                                final MetisField pField,
                                final boolean isFixed) {
        /* Return the font */
        return determineHiFont(pItem.getFieldState(pField), isFixed);
    }

    /**
     * Determine Highlighted Font.
     * @param pState render state
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    protected Font determineHiFont(final MetisFieldState pState,
                                   final boolean isFixed) {
        /* Switch on the state */
        if (MetisFieldState.CHANGED.equals(pState)) {
            return isFixed
                           ? FONT_HI_NUMCHANGED
                           : FONT_HI_CHANGED;
        }
        return isFixed
                       ? FONT_HI_NUMERIC
                       : FONT_HI_STANDARD;
    }

    /**
     * Determine Standard ToolTip.
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    public String getToolTip(final MetisFieldSetItem pItem,
                             final MetisField pField) {
        /* return the toolTip */
        return getToolTip(pItem.getFieldState(pField), pItem, pField);
    }

    /**
     * Determine Standard ToolTip.
     * @param pState render state
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    protected String getToolTip(final MetisFieldState pState,
                                final MetisFieldSetItem pItem,
                                final MetisField pField) {
        /* Switch on the state */
        return MetisFieldState.ERROR.equals(pState)
                                                    ? pItem.getFieldErrors(pField)
                                                    : null;
    }

    /**
     * Determine Standard ToolTip.
     * @param <X> the item type
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    public <X extends MetisFieldSetItem> String determineToolTip(final X pItem,
                                                                 final MetisField pField) {
        /* return the toolTip */
        return determineToolTip(pItem.getFieldState(pField), pItem, pField);
    }

    /**
     * Determine Standard ToolTip.
     * @param <X> the item type
     * @param pState render state
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    protected <X extends MetisFieldSetItem> String determineToolTip(final MetisFieldState pState,
                                                                    final X pItem,
                                                                    final MetisField pField) {
        return MetisFieldState.ERROR.equals(pState)
                                                    ? pItem.getFieldErrors(pField)
                                                    : null;
    }
}