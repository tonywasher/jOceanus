/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.field.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.field.JFieldSetItem;
import net.sourceforge.joceanus.jmetis.field.JFieldState;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.BooleanCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.ComboBoxCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.DateDayCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.DilutionCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.IntegerCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.PriceCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.RateCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.RatioCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.ScrollListButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.RowCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;

/**
 * Class to determine rendering details for an item.
 * @author Tony Washer
 */
public class JFieldManager
        implements JOceanusEventProvider {
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
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

    /**
     * General formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * The Configuration.
     */
    private JFieldConfig theConfig;

    /**
     * The Error Border.
     */
    private Border theErrorBorder;

    /**
     * Constructor.
     * @param pConfig the render configuration
     */
    public JFieldManager(final JFieldConfig pConfig) {
        /* Store the parameters */
        theConfig = pConfig;

        /* Create the event manager */
        theEventManager = new JOceanusEventManager();

        /* Create data formatter */
        theFormatter = new JDataFormatter();
        theFormatter.setAccountingWidth(ACCOUNTING_WIDTH);
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the configuration.
     * @return the configuration
     */
    public JFieldConfig getConfig() {
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
    public interface PopulateFieldData {
        /**
         * Get field data for row.
         * @param pData the Field details
         */
        void populateFieldData(final JFieldData pData);
    }

    /**
     * Set configuration.
     * @param pConfig the render configuration
     */
    public void setConfig(final JFieldConfig pConfig) {
        /* Store the parameters */
        theConfig = pConfig;

        /* Allocate the Error border */
        theErrorBorder = BorderFactory.createLineBorder(theConfig.getErrorColor());

        /* Fire event */
        theEventManager.fireStateChanged();
    }

    /**
     * Allocate a RenderData object.
     * @param isFixed is the data fixed width
     * @return the render data
     */
    protected JFieldData allocateRenderData(final boolean isFixed) {
        /* Return a new RenderData object */
        return new JFieldData(this, isFixed);
    }

    /**
     * Determine render data for FieldSetElement.
     * @param <X> the item type
     * @param pElement the fieldSet element
     * @param pItem the data item
     * @return the render data
     */
    protected <X extends JFieldSetItem> JFieldData determineRenderData(final JFieldElement<X> pElement,
                                                                       final X pItem) {
        /* Allocate the render data */
        JFieldData myData = new JFieldData(this, pElement.isFixedWidth());

        /* Determine the render data */
        myData.determineData(pElement, pItem);

        /* Return the data */
        return myData;
    }

    /**
     * Allocate a StringRenderer object.
     * @return the string renderer
     */
    public StringCellRenderer allocateStringCellRenderer() {
        /* Return a new StringRenderer object */
        return new StringCellRenderer(this);
    }

    /**
     * Allocate a BooleanRenderer object.
     * @return the boolean renderer
     */
    public BooleanCellRenderer allocateBooleanCellRenderer() {
        /* Return a new BooleanRenderer object */
        return new BooleanCellRenderer(this);
    }

    /**
     * Allocate an IconButtonRenderer object.
     * @param <T> the type of the object
     * @param pEditor the cell editor
     * @return the icon renderer
     */
    public <T> IconButtonCellRenderer<T> allocateIconButtonCellRenderer(final IconButtonCellEditor<T> pEditor) {
        /* Return a new IconRenderer object */
        return new IconButtonCellRenderer<T>(this, pEditor);
    }

    /**
     * Allocate an IntegerRenderer object.
     * @return the integer renderer
     */
    public IntegerCellRenderer allocateIntegerCellRenderer() {
        /* Return a new IntegerRenderer object */
        return new IntegerCellRenderer(this);
    }

    /**
     * Allocate a CalendarRenderer object.
     * @return the calendar renderer
     */
    public CalendarCellRenderer allocateCalendarCellRenderer() {
        /* Return a new CalendarRenderer object */
        return new CalendarCellRenderer(this, theFormatter.getDateFormatter());
    }

    /**
     * Allocate a DecimalRenderer object.
     * @return the decimal renderer
     */
    public DecimalCellRenderer allocateDecimalCellRenderer() {
        /* Return a new DecimalRenderer object */
        return new DecimalCellRenderer(this, theFormatter.getDecimalFormatter());
    }

    /**
     * Allocate a RowRenderer object.
     * @return the row renderer
     */
    public RowCellRenderer allocateRowRenderer() {
        /* Return a new RowRenderer object */
        return new RowCellRenderer(this);
    }

    /**
     * Allocate a StringEditor object.
     * @return the string editor
     */
    public StringCellEditor allocateStringCellEditor() {
        /* Return a new StringEditor object */
        return new StringCellEditor();
    }

    /**
     * Allocate an IntegerEditor object.
     * @return the integer editor
     */
    public IntegerCellEditor allocateIntegerCellEditor() {
        /* Return a new IntegerEditor object */
        return new IntegerCellEditor();
    }

    /**
     * Allocate a CalendarEditor object.
     * @return the calendar editor
     */
    public CalendarCellEditor allocateCalendarCellEditor() {
        /* Return a new CalendarEditor object */
        return new CalendarCellEditor(theFormatter.getDateFormatter());
    }

    /**
     * Allocate a DateDayEditor object.
     * @return the dateDay editor
     */
    public DateDayCellEditor allocateDateDayCellEditor() {
        /* Return a new DateDayEditor object */
        return new DateDayCellEditor(theFormatter.getDateFormatter());
    }

    /**
     * Allocate a ComboBoxEditor object.
     * @param <T> the type of the object
     * @param pClass the class of the objects
     * @return the ComboBox editor
     */
    public <T> ComboBoxCellEditor<T> allocateComboBoxCellEditor(final Class<T> pClass) {
        /* Return a new ComboBoxEditor object */
        return new ComboBoxCellEditor<T>(pClass);
    }

    /**
     * Allocate a BooleanEditor object.
     * @return the boolean editor
     */
    public BooleanCellEditor allocateBooleanCellEditor() {
        /* Return a new BooleanEditor object */
        return new BooleanCellEditor();
    }

    /**
     * Allocate an IconButtonEditor object.
     * @param <T> the type of the object
     * @param pClass the class of the objects
     * @param pComplex use complex state true/false
     * @return the icon editor
     */
    public <T> IconButtonCellEditor<T> allocateIconButtonCellEditor(final Class<T> pClass,
                                                                    final boolean pComplex) {
        /* Return a new IconButtonEditor object */
        return new IconButtonCellEditor<T>(pClass, pComplex);
    }

    /**
     * Allocate a ScrollButtonEditor object.
     * @param <T> the type of the object
     * @param pClass the class of the objects
     * @return the ScrollButton editor
     */
    public <T> ScrollButtonCellEditor<T> allocateScrollButtonCellEditor(final Class<T> pClass) {
        /* Return a new ScrollButtonEditor object */
        return new ScrollButtonCellEditor<T>(pClass);
    }

    /**
     * Allocate a ScrollListButtonEditor object.
     * @param <T> the type of the object
     * @return the ScrollListButton editor
     */
    public <T> ScrollListButtonCellEditor<T> allocateScrollListButtonCellEditor() {
        /* Return a new ScrollListButtonEditor object */
        return new ScrollListButtonCellEditor<T>();
    }

    /**
     * Allocate a MoneyEditor object.
     * @return the money editor
     */
    public MoneyCellEditor allocateMoneyCellEditor() {
        /* Return a new MoneyEditor object */
        return new MoneyCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RateEditor object.
     * @return the rate editor
     */
    public RateCellEditor allocateRateCellEditor() {
        /* Return a new RateEditor object */
        return new RateCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a UnitsEditor object.
     * @return the units editor
     */
    public UnitsCellEditor allocateUnitsCellEditor() {
        /* Return a new UnitsEditor object */
        return new UnitsCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RatioEditor object.
     * @return the ratio editor
     */
    public RatioCellEditor allocateRatioCellEditor() {
        /* Return a new RatioEditor object */
        return new RatioCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a PriceEditor object.
     * @return the price editor
     */
    public PriceCellEditor allocatePriceCellEditor() {
        /* Return a new PriceEditor object */
        return new PriceCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Allocate a RateEditor object.
     * @return the rate editor
     */
    public DilutionCellEditor allocateDilutionCellEditor() {
        /* Return a new DilutionEditor object */
        return new DilutionCellEditor(theFormatter.getDecimalParser());
    }

    /**
     * Determine Standard foreground.
     * @param pItem the Item
     * @param pField the Field number
     * @return the standard foreground for the item
     */
    public Color getForeground(final JFieldSetItem pItem,
                               final JDataField pField) {
        /* Access foreground for item */
        return getForeground(pItem.getFieldState(pField));
    }

    /**
     * Determine foreground for the state.
     * @param pState the render state
     * @return the foreground colour
     */
    protected Color getForeground(final JFieldState pState) {
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
    public Font determineFont(final JFieldSetItem pItem,
                              final JDataField pField,
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
    protected Font determineFont(final JFieldState pState,
                                 final boolean isFixed) {
        /* Switch on the state */
        switch (pState) {
            case CHANGED:
                return isFixed
                              ? FONT_NUMCHANGED
                              : FONT_CHANGED;
            default:
                return isFixed
                              ? FONT_NUMERIC
                              : FONT_STANDARD;
        }
    }

    /**
     * Determine Highlighted Font.
     * @param pItem the Item
     * @param pField the Field
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    public Font determineHiFont(final JFieldSetItem pItem,
                                final JDataField pField,
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
    protected Font determineHiFont(final JFieldState pState,
                                   final boolean isFixed) {
        /* Switch on the state */
        switch (pState) {
            case CHANGED:
                return isFixed
                              ? FONT_HI_NUMCHANGED
                              : FONT_HI_CHANGED;
            default:
                return isFixed
                              ? FONT_HI_NUMERIC
                              : FONT_HI_STANDARD;
        }
    }

    /**
     * Determine Standard ToolTip.
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    public String getToolTip(final JFieldSetItem pItem,
                             final JDataField pField) {
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
    protected String getToolTip(final JFieldState pState,
                                final JFieldSetItem pItem,
                                final JDataField pField) {
        /* Switch on the state */
        switch (pState) {
            case ERROR:
                return pItem.getFieldErrors(pField);
            default:
                return null;
        }
    }

    /**
     * Determine Standard ToolTip.
     * @param <X> the item type
     * @param pItem the Item
     * @param pField the Field
     * @return the standard ToolTip for the item
     */
    public <X extends JFieldSetItem> String determineToolTip(final X pItem,
                                                             final JDataField pField) {
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
    protected <X extends JFieldSetItem> String determineToolTip(final JFieldState pState,
                                                                final X pItem,
                                                                final JDataField pField) {
        /* Switch on the state */
        switch (pState) {
            case ERROR:
                return pItem.getFieldErrors(pField);
            default:
                return null;
        }
    }
}
