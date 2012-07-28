/*******************************************************************************
 * JDataModels: Data models
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataModels.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataState;
import net.sourceforge.JDataModels.data.PreferenceSet.PreferenceItem;
import net.sourceforge.JDataModels.data.PreferenceSet.PreferenceManager;
import net.sourceforge.JDataModels.data.PreferenceSet.PreferenceType;
import net.sourceforge.JDataModels.ui.Renderer.BooleanRenderer;
import net.sourceforge.JDataModels.ui.Renderer.CalendarRenderer;
import net.sourceforge.JDataModels.ui.Renderer.DecimalRenderer;
import net.sourceforge.JDataModels.ui.Renderer.IntegerRenderer;
import net.sourceforge.JDataModels.ui.Renderer.RowCell;
import net.sourceforge.JDataModels.ui.Renderer.StringRenderer;
import net.sourceforge.JDecimal.JDecimalFormatter;

/**
 * Class to determine rendering details for an item.
 * @author Tony Washer
 */
public class RenderManager {
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
     * The Preferences.
     */
    private final RenderPreferences thePreferences;

    /**
     * The error colour.
     */
    private Color theErrorColor;

    /**
     * The changed colour.
     */
    private Color theChangedColor;

    /**
     * The new colour.
     */
    private Color theNewColor;

    /**
     * The deleted colour.
     */
    private Color theDeletedColor;

    /**
     * The recovered colour.
     */
    private Color theRecoveredColor;

    /**
     * The standard colour.
     */
    private Color theStandardColor;

    /**
     * The background colour.
     */
    private Color theBackgroundColor;

    /**
     * Populate RenderData interface.
     */
    protected interface PopulateRenderData {
        /**
         * Get render data for row.
         * @param pData the Render details
         */
        void populateRenderData(final RenderData pData);
    }

    /**
     * Constructor.
     * @param pManager the data manager
     * @throws JDataException on error
     */
    public RenderManager(final JDataManager pManager) throws JDataException {
        /* Store the parameters */
        theDataManager = pManager;

        /* Access the preferences */
        thePreferences = PreferenceManager.getPreferenceSet(RenderPreferences.class);

        /* Process the preferences */
        processPreferences();

        /* Add a listener */
        thePreferences.addChangeListener(new RenderListener());
    }

    /**
     * Process Preferences.
     */
    private void processPreferences() {
        /* Record the preferences */
        theStandardColor = thePreferences.getColorValue(RenderPreferences.NAME_STANDARD);
        theBackgroundColor = thePreferences.getColorValue(RenderPreferences.NAME_BACKGROUND);
        theErrorColor = thePreferences.getColorValue(RenderPreferences.NAME_CHANGED);
        theNewColor = thePreferences.getColorValue(RenderPreferences.NAME_NEW);
        theChangedColor = thePreferences.getColorValue(RenderPreferences.NAME_CHANGED);
        theDeletedColor = thePreferences.getColorValue(RenderPreferences.NAME_DELETED);
        theRecoveredColor = thePreferences.getColorValue(RenderPreferences.NAME_RECOVERED);
        Color myLinkColor = thePreferences.getColorValue(RenderPreferences.NAME_LINK);
        Color myChgLinkColor = thePreferences.getColorValue(RenderPreferences.NAME_CHGLINK);

        /* Declare preferences to data manager */
        theDataManager.setFormatter(theStandardColor, theChangedColor, myLinkColor, myChgLinkColor);
    }

    /**
     * Listener class.
     */
    private final class RenderListener implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent evt) {
            /* Process preferences */
            processPreferences();
        }
    }

    /**
     * Initialise DataManager.
     */
    public void initialiseDataManager() {

    }

    /**
     * Allocate a RenderData object.
     * @param isFixed is the data fixed width
     * @return the render data
     */
    protected RenderData allocateRenderData(final boolean isFixed) {
        /* Return a new RenderData object */
        return new RenderData(isFixed);
    }

    /**
     * Allocate a StringRenderer object.
     * @return the string renderer
     */
    public StringRenderer allocateStringRenderer() {
        /* Return a new StringRenderer object */
        return new StringRenderer(this);
    }

    /**
     * Allocate an IntegerRenderer object.
     * @return the integer renderer
     */
    public IntegerRenderer allocateIntegerRenderer() {
        /* Return a new IntegerRenderer object */
        return new IntegerRenderer(this);
    }

    /**
     * Allocate a CalendarRenderer object.
     * @return the calendar renderer
     */
    public CalendarRenderer allocateCalendarRenderer() {
        /* Return a new CalendarRenderer object */
        return new CalendarRenderer(this);
    }

    /**
     * Allocate a DecimalRenderer object.
     * @return the decimal renderer
     */
    public DecimalRenderer allocateDecimalRenderer() {
        /* Return a new DecimalRenderer object */
        return new DecimalRenderer(this);
    }

    /**
     * Allocate a DecimalRenderer object.
     * @param pFormatter the decimal formatter
     * @return the decimal renderer
     */
    public DecimalRenderer allocateDecimalRenderer(final JDecimalFormatter pFormatter) {
        /* Return a new DecimalRenderer object */
        return new DecimalRenderer(this, pFormatter);
    }

    /**
     * Allocate a DecimalRenderer object.
     * @return the decimal renderer
     */
    public BooleanRenderer allocateBooleanRenderer() {
        /* Return a new BooleanRenderer object */
        return new BooleanRenderer(this);
    }

    /**
     * Allocate a RowRenderer object.
     * @return the row renderer
     */
    public RowCell allocateRowRenderer() {
        /* Return a new RowRenderer object */
        return new RowCell(this);
    }

    /**
     * The Render Data class.
     */
    public final class RenderData {
        /**
         * The toolTip text.
         */
        private String theToolTipText = null;

        /**
         * The font.
         */
        private Font theFont = FONT_STANDARD;

        /**
         * The foreground colour.
         */
        private Color theForeGround = theStandardColor;

        /**
         * The background colour.
         */
        private Color theBackGround = theBackgroundColor;

        /**
         * The row.
         */
        private int theRow = 0;

        /**
         * The column.
         */
        private int theCol = 0;

        /**
         * Is the row selected?
         */
        private boolean isSelected = false;

        /**
         * Is the item fixed width?
         */
        private final boolean isFixed;

        /**
         * Get the foreground.
         * @return the foreground
         */
        public Color getForeGround() {
            return theForeGround;
        }

        /**
         * Get the background.
         * @return the background
         */
        public Color getBackGround() {
            return theBackGround;
        }

        /**
         * Get the font.
         * @return the font
         */
        public Font getFont() {
            return theFont;
        }

        /**
         * Get the toolTip.
         * @return the toolTip
         */
        public String getToolTip() {
            return theToolTipText;
        }

        /**
         * Get the row.
         * @return the row
         */
        public int getRow() {
            return theRow;
        }

        /**
         * Get the column.
         * @return the column
         */
        public int getCol() {
            return theCol;
        }

        /**
         * Is the row selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Is the item fixed width?
         * @return true/false
         */
        public boolean isFixed() {
            return isFixed;
        }

        /**
         * Constructor.
         * @param pFixed is the item fixed width?
         */
        private RenderData(final boolean pFixed) {
            isFixed = pFixed;
        }

        /**
         * Record location and selection.
         * @param pRowIndex the row
         * @param pColIndex the column
         * @param pSelected is the item selected?
         */
        protected void setPosition(final int pRowIndex,
                                   final int pColIndex,
                                   final boolean pSelected) {
            theRow = pRowIndex;
            theCol = pColIndex;
            isSelected = pSelected;
        }

        /**
         * Set default values.
         */
        public void setDefaults() {
            /* Set the data */
            theForeGround = theStandardColor;
            theBackGround = theBackgroundColor;
            theFont = (isFixed) ? FONT_NUMERIC : FONT_STANDARD;
            theToolTipText = null;
        }

        /**
         * Process Table Row.
         * @param pRow the Table row
         * @param iField the field id
         */
        public void processTableRow(final DataItem pRow,
                                    final JDataField iField) {
            /* Default is black on white */
            Color myFore = getForeground(pRow, iField);
            Color myBack = getStandardBackground();
            String myTip = null;
            Font myFont;

            /* Has the field changed */
            boolean isChanged = pRow.fieldChanged(iField).isDifferent();

            /* Determine the colour */
            if (pRow.isDeleted()) {
                myFore = theDeletedColor;
            } else if ((pRow.hasErrors()) && (pRow.hasErrors(iField))) {
                myFore = theErrorColor;
                myTip = pRow.getFieldErrors(iField);
            } else if (isChanged) {
                myFore = theChangedColor;
            } else if (pRow.getState() == DataState.NEW) {
                myFore = theNewColor;
            } else if (pRow.getState() == DataState.RECOVERED) {
                myFore = theRecoveredColor;
            }

            /* For selected items flip the foreground/background */
            if (isSelected()) {
                Color myTemp = myFore;
                myFore = myBack;
                myBack = myTemp;
            }

            /* Select the font */
            if (isFixed) {
                myFont = isChanged ? FONT_NUMCHANGED : FONT_NUMERIC;
            } else {
                myFont = isChanged ? FONT_CHANGED : FONT_STANDARD;
            }

            /* Set the data */
            theForeGround = myFore;
            theBackGround = myBack;
            theFont = myFont;
            theToolTipText = myTip;
        }

        /**
         * Initialise data from Table Header.
         * @param pHeader the Table Header
         */
        public void initFromHeader(final JTableHeader pHeader) {
            theBackGround = pHeader.getBackground();
            theForeGround = pHeader.getForeground();
            theFont = pHeader.getFont();
        }

        /**
         * Process Table Row.
         * @param pRow the Table row
         * @param iFields the field IDs
         */
        public void processRowHeader(final DataItem pRow,
                                     final JDataField[] iFields) {
            /* Has the row changed */
            boolean isChanged = pRow.hasHistory();

            /* Determine the colour */
            if (pRow.isDeleted()) {
                theForeGround = theDeletedColor;
            } else if (pRow.hasErrors()) {
                theForeGround = theStandardColor;
                theBackGround = theErrorColor;
                theToolTipText = pRow.getFieldErrors(iFields);
            } else if (isChanged) {
                theForeGround = theChangedColor;
            } else if (pRow.getState() == DataState.NEW) {
                theForeGround = theNewColor;
            } else if (pRow.getState() == DataState.RECOVERED) {
                theForeGround = theRecoveredColor;
            }
        }
    }

    /**
     * Determine Standard foreground.
     * @param pItem the Item
     * @param iField the Field number
     * @return the standard foreground for the item
     */
    public Color getForeground(final DataItem pItem,
                               final JDataField iField) {
        /* Handle deleted items */
        if (pItem.isDeleted()) {
            return theDeletedColor;
        }

        /* If the field exists */
        if (iField != null) {
            /* Handle error items */
            if ((pItem.hasErrors()) && (pItem.hasErrors(iField))) {
                return theErrorColor;
            }

            /* Handle changed items */
            if (pItem.fieldChanged(iField).isDifferent()) {
                return theChangedColor;
            }
        }

        /* Switch on Status */
        switch (pItem.getState()) {
            case NEW:
                return theNewColor;
            case RECOVERED:
                return theRecoveredColor;
            default:
                return theStandardColor;
        }
    }

    /**
     * Determine Standard foreground.
     * @param pPreference the preference
     * @return the standard foreground for the item
     */
    protected Color getForeground(final PreferenceItem pPreference) {
        /* Handle changed items */
        return (pPreference.isChanged()) ? theChangedColor : theStandardColor;
    }

    /**
     * Determine Standard background.
     * @return the standard background
     */
    public Color getStandardBackground() {
        return theBackgroundColor;
    }

    /**
     * Determine Standard Font.
     * @param pItem the Item
     * @param iField the Field number
     * @param isFixed is the field fixed width
     * @return the standard Font for the item
     */
    public Font getFont(final DataItem pItem,
                        final JDataField iField,
                        final boolean isFixed) {
        if (pItem.fieldChanged(iField).isDifferent()) {
            return (isFixed ? FONT_NUMCHANGED : FONT_CHANGED);
        } else {
            return (isFixed ? FONT_NUMERIC : FONT_STANDARD);
        }
    }

    /**
     * Determine Standard Font.
     * @param pPreference the Item
     * @return the standard Font for the item
     */
    public Font getFont(final PreferenceItem pPreference) {
        boolean isFixed = pPreference.getType() == PreferenceType.Integer;
        if (pPreference.isChanged()) {
            return (isFixed ? FONT_NUMCHANGED : FONT_CHANGED);
        } else {
            return (isFixed ? FONT_NUMERIC : FONT_STANDARD);
        }
    }

    /**
     * Determine Standard ToolTip.
     * @param pItem the Item
     * @param iField the Field number
     * @return the standard ToolTip for the item
     */
    public String getToolTip(final DataItem pItem,
                             final JDataField iField) {
        /* Handle deleted items */
        if (pItem.isDeleted()) {
            return null;
        }

        /* Handle error items */
        if ((pItem.hasErrors()) && (pItem.hasErrors(iField))) {
            return pItem.getFieldErrors(iField);
        }

        /* Return no ToolTip */
        return null;
    }
}
