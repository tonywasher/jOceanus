/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.table.JTableHeader;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceItem;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceType;

/**
 * Class to determine rendering details for an item.
 * @author Tony Washer
 */
public class RenderData {
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
     * The error colour.
     */
    private static final Color COLOR_ERROR = Color.red;

    /**
     * The changed colour.
     */
    private static final Color COLOR_CHANGED = Color.magenta.darker();

    /**
     * The new colour.
     */
    private static final Color COLOR_NEW = Color.blue;

    /**
     * The deleted colour.
     */
    private static final Color COLOR_DELETED = Color.lightGray;

    /**
     * The recovered colour.
     */
    private static final Color COLOR_RECOVERED = Color.darkGray;

    /**
     * The standard colour.
     */
    private static final Color COLOR_STANDARD = Color.black;

    /**
     * The background colour.
     */
    private static final Color COLOR_BACK = Color.white;

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
    private Color theForeGround = COLOR_STANDARD;

    /**
     * The background colour.
     */
    private Color theBackGround = COLOR_BACK;

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
    private boolean isFixed = false;

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
    protected RenderData(final boolean pFixed) {
        isFixed = pFixed;
    }

    /**
     * Record location and selection.
     * @param row the row
     * @param col the column
     * @param pSelected is the item selected?
     */
    protected void setPosition(final int row,
                               final int col,
                               final boolean pSelected) {
        theRow = row;
        theCol = col;
        isSelected = pSelected;
    }

    /**
     * Set default values.
     */
    public void setDefaults() {
        /* Set the data */
        theForeGround = COLOR_STANDARD;
        theBackGround = COLOR_BACK;
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
        Color myBack = getBackground();
        String myTip = null;
        Font myFont;

        /* Has the field changed */
        boolean isChanged = pRow.fieldChanged(iField).isDifferent();

        /* Determine the colour */
        if (pRow.isDeleted()) {
            myFore = COLOR_DELETED;
        } else if ((pRow.hasErrors()) && (pRow.hasErrors(iField))) {
            myFore = COLOR_ERROR;
            myTip = pRow.getFieldErrors(iField);
        } else if (isChanged) {
            myFore = COLOR_CHANGED;
        } else if (pRow.getState() == DataState.NEW) {
            myFore = COLOR_NEW;
        } else if (pRow.getState() == DataState.RECOVERED) {
            myFore = COLOR_RECOVERED;
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
            theForeGround = COLOR_DELETED;
        } else if (pRow.hasErrors()) {
            theForeGround = COLOR_STANDARD;
            theBackGround = COLOR_ERROR;
            theToolTipText = pRow.getFieldErrors(iFields);
        } else if (isChanged) {
            theForeGround = COLOR_CHANGED;
        } else if (pRow.getState() == DataState.NEW) {
            theForeGround = COLOR_NEW;
        } else if (pRow.getState() == DataState.RECOVERED) {
            theForeGround = COLOR_RECOVERED;
        }
    }

    /**
     * Determine Standard foreground.
     * @param pItem the Item
     * @param iField the Field number
     * @return the standard foreground for the item
     */
    public static Color getForeground(final DataItem pItem,
                                      final JDataField iField) {
        /* Handle deleted items */
        if (pItem.isDeleted()) {
            return COLOR_DELETED;
        }

        /* If the field exists */
        if (iField != null) {
            /* Handle error items */
            if ((pItem.hasErrors()) && (pItem.hasErrors(iField))) {
                return COLOR_ERROR;
            }

            /* Handle changed items */
            if (pItem.fieldChanged(iField).isDifferent()) {
                return COLOR_CHANGED;
            }
        }

        /* Switch on Status */
        switch (pItem.getState()) {
            case NEW:
                return COLOR_NEW;
            case RECOVERED:
                return COLOR_RECOVERED;
            default:
                return COLOR_STANDARD;
        }
    }

    /**
     * Determine Standard foreground.
     * @param pPreference the preference
     * @return the standard foreground for the item
     */
    protected static Color getForeground(final PreferenceItem pPreference) {
        /* Handle changed items */
        return (pPreference.isChanged()) ? COLOR_CHANGED : COLOR_STANDARD;
    }

    /**
     * Determine Standard background.
     * @return the standard background
     */
    public static Color getBackground() {
        return COLOR_BACK;
    }

    /**
     * Determine Standard Font.
     * @param pItem the Item
     * @param iField the Field number
     * @param isFixed is the field fixed width
     * @return the standard Font for the item
     */
    public static Font getFont(final DataItem pItem,
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
    public static Font getFont(final PreferenceItem pPreference) {
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
    public static String getToolTip(final DataItem pItem,
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
