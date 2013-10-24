/*******************************************************************************
 * jFieldSet: Java Swing Field Set
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jfieldset;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.BooleanCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.ComboBoxCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.DilutionCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.IntegerCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.PriceCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.RateCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.RowCellRenderer;
import net.sourceforge.joceanus.jfieldset.JFieldCellRenderer.StringCellRenderer;

/**
 * Class to determine rendering details for an item.
 * @author Tony Washer
 */
public class JFieldManager {
    /**
     * Money accounting format width.
     */
    private static final int ACCOUNTING_WIDTH = 10;

    /**
     * The standard font.
     */
    private static final Font FONT_STANDARD = new Font("Arial", Font.PLAIN, 12);

    /**
     * The numeric font.
     */
    private static final Font FONT_NUMERIC = new Font("Courier", Font.PLAIN, 12);

    /**
     * The changed font.
     */
    private static final Font FONT_CHANGED = new Font("Arial", Font.ITALIC, 12);

    /**
     * The changed numeric font.
     */
    private static final Font FONT_NUMCHANGED = new Font("Courier", Font.ITALIC, 12);

    /**
     * The Data Manager.
     */
    private final JDataManager theDataManager;

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
     * Obtain the data formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
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
     * Constructor.
     * @param pManager the data manager
     * @param pConfig the render configuration
     */
    public JFieldManager(final JDataManager pManager,
                         final JFieldConfig pConfig) {
        /* Store the parameters */
        theDataManager = pManager;
        theConfig = pConfig;

        /* Create data formatter */
        theFormatter = new JDataFormatter();
        theFormatter.setAccountingWidth(ACCOUNTING_WIDTH);

        /* Process the configuration */
        processConfiguration();
    }

    /**
     * Set configuration.
     * @param pConfig the render configuration
     */
    public void setConfig(final JFieldConfig pConfig) {
        /* Store the parameters */
        theConfig = pConfig;

        /* Process the configuration */
        processConfiguration();
    }

    /**
     * Process Configuration.
     */
    private void processConfiguration() {
        Color myStdColor = theConfig.getStandardColor();
        Color myChgColor = theConfig.getChangedColor();
        Color myLinkColor = theConfig.getLinkColor();
        Color myChgLinkColor = theConfig.getChangedLinkColor();

        /* Declare preferences to data manager */
        theDataManager.setFormatter(myStdColor, myChgColor, myLinkColor, myChgLinkColor);

        /* Allocate the Error border */
        theErrorBorder = BorderFactory.createLineBorder(theConfig.getErrorColor());
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
     * Allocate a DecimalRenderer object.
     * @return the decimal renderer
     */
    public BooleanCellRenderer allocateBooleanCellRenderer() {
        /* Return a new BooleanRenderer object */
        return new BooleanCellRenderer(this);
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
     * Allocate a ComboBoxEditor object.
     * @return the ComboBox editor
     */
    public ComboBoxCellEditor allocateComboBoxCellEditor() {
        /* Return a new ComboBoxEditor object */
        return new ComboBoxCellEditor();
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
     * Determine Standard Font.
     * @param pItem the Item
     * @param pField the Field
     * @param isFixed is the item fixed width
     * @return the standard Font for the item
     */
    public Font determineFont(final JFieldSetItem pItem,
                              final JDataField pField,
                              final boolean isFixed) {
        /* Return the toolTip */
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
                return (isFixed
                        ? FONT_NUMCHANGED
                        : FONT_CHANGED);
            default:
                return (isFixed
                        ? FONT_NUMERIC
                        : FONT_STANDARD);
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
