/*******************************************************************************
 * JFieldSet: Java Swing Field Set
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
package net.sourceforge.JFieldSet;

import java.awt.Color;
import java.awt.Font;

import javax.swing.table.JTableHeader;

import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDateDay.JDateDayFormatter;
import net.sourceforge.JDecimal.JDecimalFormatter;
import net.sourceforge.JFieldSet.Renderer.BooleanRenderer;
import net.sourceforge.JFieldSet.Renderer.CalendarRenderer;
import net.sourceforge.JFieldSet.Renderer.DecimalRenderer;
import net.sourceforge.JFieldSet.Renderer.IntegerRenderer;
import net.sourceforge.JFieldSet.Renderer.RowCell;
import net.sourceforge.JFieldSet.Renderer.StringRenderer;

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
     * The Configuration.
     */
    private RenderConfig theConfig;

    /**
     * Populate RenderData interface.
     */
    public interface PopulateRenderData {
        /**
         * Get render data for row.
         * @param pData the Render details
         */
        void populateRenderData(final RenderData pData);
    }

    /**
     * Constructor.
     * @param pManager the data manager
     * @param pConfig the render configuration
     */
    public RenderManager(final JDataManager pManager,
                         final RenderConfig pConfig) {
        /* Store the parameters */
        theDataManager = pManager;
        theConfig = pConfig;

        /* Process the configuration */
        processConfiguration();
    }

    /**
     * Set configuration.
     * @param pConfig the render configuration
     */
    public void setConfig(final RenderConfig pConfig) {
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
     * @param pFormatter the date formatter
     * @return the calendar renderer
     */
    public CalendarRenderer allocateCalendarRenderer(final JDateDayFormatter pFormatter) {
        /* Return a new CalendarRenderer object */
        return new CalendarRenderer(this, pFormatter);
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
        private Color theForeGround = theConfig.getStandardColor();

        /**
         * The background colour.
         */
        private Color theBackGround = getStandardBackground();

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
            theForeGround = theConfig.getStandardColor();
            theBackGround = getStandardBackground();
            theFont = (isFixed) ? FONT_NUMERIC : FONT_STANDARD;
            theToolTipText = null;
        }

        /**
         * Process Table Row.
         * @param pRow the Table row
         * @param pField the field id
         */
        public void processTableRow(final JFieldSetItem pRow,
                                    final JDataField pField) {
            /* Obtain the render state */
            RenderState myState = pRow.getRenderState(pField);

            /* Obtain the foreground and background for the state */
            Color myFore = getForeground(myState);
            Color myBack = getStandardBackground();

            /* Determine toolTip */
            String myTip = myState.isError() ? pRow.getFieldErrors(pField) : null;

            /* For selected items flip the foreground/background */
            if (isSelected()) {
                Color myTemp = myFore;
                myFore = myBack;
                myBack = myTemp;
            }

            /* Select the font */
            Font myFont;
            if (isFixed) {
                myFont = myState.isChanged() ? FONT_NUMCHANGED : FONT_NUMERIC;
            } else {
                myFont = myState.isChanged() ? FONT_CHANGED : FONT_STANDARD;
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
         * @param pFields the field IDs
         */
        public void processRowHeader(final JFieldSetItem pRow,
                                     final JDataField[] pFields) {
            /* Initialise toolTip */
            theToolTipText = null;

            /* Has the row changed */
            RenderState myState = pRow.getRenderState();

            /* Determine the colour */
            Color myFore = getForeground(myState);
            Color myBack = getStandardBackground();

            /* If the item is an error */
            if (myState.isError()) {
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
        return getForeground(pItem.getRenderState(pField));
    }

    /**
     * Determine foreground for the state.
     * @param pState the render state
     * @return the foreground colour
     */
    protected Color getForeground(final RenderState pState) {
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
    public Font getFont(final JFieldSetItem pItem,
                        final JDataField pField,
                        final boolean isFixed) {
        /* Switch on the state */
        switch (pItem.getRenderState(pField)) {
            case CHANGED:
                return (isFixed ? FONT_NUMCHANGED : FONT_CHANGED);
            default:
                return (isFixed ? FONT_NUMERIC : FONT_STANDARD);
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
        /* Switch on the state */
        switch (pItem.getRenderState(pField)) {
            case ERROR:
                return pItem.getFieldErrors(pField);
            default:
                return null;
        }
    }
}
