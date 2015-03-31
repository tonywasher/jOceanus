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

import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldSetItem;
import net.sourceforge.joceanus.jmetis.field.JFieldState;

/**
 * The Render Data class.
 */
public final class JFieldData {
    /**
     * The Manager.
     */
    private final JFieldManager theManager;

    /**
     * The toolTip text.
     */
    private String theToolTipText = null;

    /**
     * The font.
     */
    private Font theFont;

    /**
     * The foreground colour.
     */
    private Color theForeGround;

    /**
     * The background colour.
     */
    private Color theBackGround;

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
     * RenderState.
     */
    private JFieldState theState;

    /**
     * Constructor.
     * @param pManager the field manager
     * @param pFixed is the item fixed width?
     */
    protected JFieldData(final JFieldManager pManager,
                         final boolean pFixed) {
        /* Record parameters */
        theManager = pManager;
        isFixed = pFixed;

        /* Initialise defaults */
        theForeGround = theManager.getForeground(JFieldState.NORMAL);
        theBackGround = theManager.getStandardBackground();
        theFont = theManager.determineFont(JFieldState.NORMAL, false);
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
     * Get the state.
     * @return the state
     */
    public JFieldState getState() {
        return theState;
    }

    /**
     * Get the error Border.
     * @return the state
     */
    public Border getErrorBorder() {
        return theManager.getErrorBorder();
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
        theForeGround = theManager.getForeground(JFieldState.NORMAL);
        theBackGround = theManager.getStandardBackground();
        theFont = theManager.determineFont(JFieldState.NORMAL, isFixed);
        theToolTipText = null;
    }

    /**
     * Process Table Row.
     * @param pRow the Table row
     * @param pField the field id
     */
    public void processTableRow(final JFieldSetItem pRow,
                                final JDataField pField) {
        /* Obtain the field state */
        theState = pRow.getFieldState(pField);
        boolean isDisabled = pRow.isDisabled();

        /* Obtain the foreground for the state */
        Color myFore = isDisabled
                                 ? theManager.getDisabledColor()
                                 : theManager.getForeground(theState);
        Color myBack = (theRow & 1) == 0
                                        ? theManager.getStandardBackground()
                                        : theManager.getZebraColor();

        /* Determine toolTip */
        String myTip = theState.isError()
                                         ? pRow.getFieldErrors(pField)
                                         : null;

        /* Select the font */
        Font myFont = isSelected()
                                  ? theManager.determineHiFont(theState, isFixed)
                                  : theManager.determineFont(theState, isFixed);

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
     * @param pFields the field IDs
     */
    public void processRowHeader(final JFieldSetItem pRow,
                                 final JDataField[] pFields) {
        /* Initialise toolTip */
        theToolTipText = null;

        /* Obtain the state */
        theState = pRow.getItemState();

        /* Determine the colour */
        Color myFore = theManager.getForeground(theState);
        Color myBack = theManager.getStandardBackground();

        /* If the item is an error */
        if (theState.isError()) {
            /* Flip foreground and background */
            Color myTemp = myFore;
            myFore = myBack;
            myBack = myTemp;

            /* Access toolTip */
            theToolTipText = pRow.getFieldErrors(pFields);
        }

        /* Record foreground and background */
        theForeGround = myFore;
        theBackGround = myBack;
    }

    /**
     * Determine data for FieldSetElement.
     * @param <X> the item type
     * @param pElement the fieldSet element
     * @param pItem the data item
     */
    protected <X extends JFieldSetItem> void determineData(final JFieldElement<X> pElement,
                                                           final X pItem) {
        /* Determine whether we have a null item */
        boolean isNull = pItem == null;

        /* Obtain the state */
        JDataField myField = pElement.getField();
        theState = (isNull)
                           ? JFieldState.NORMAL
                           : pItem.getFieldState(myField);

        /* Determine the standard colours */
        theForeGround = theManager.getForeground(theState);
        theBackGround = theManager.getStandardBackground();

        /* Determine the Font and ToolTip */
        theFont = theManager.determineFont(theState, isFixed);
        theToolTipText = (isNull)
                                 ? null
                                 : theManager.determineToolTip(theState, pItem, myField);
    }
}
