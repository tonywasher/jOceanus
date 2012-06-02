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

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.sourceforge.JDateDay.DateDayCellRenderer;
import net.sourceforge.JDecimal.Decimal;
import uk.co.tolcroft.models.ui.DataTable.DataTableModel;

/**
 * Cell renderers.
 * @author Tony Washer
 */
public final class Renderer {
    /**
     * Error string.
     */
    private static final String ERROR_VALUE = "Error";

    /**
     * Get the error value.
     * @return the error value
     */
    public static String getError() {
        return ERROR_VALUE;
    }

    /**
     * Private constructor to avoid instantiation.
     */
    private Renderer() {
    }

    /**
     * Render the component.
     * @param pTable the table
     * @param pComponent the component
     * @param pData the render data
     * @param pAlignment the alignment
     */
    private static void renderComponent(final JTable pTable,
                                        final DefaultTableCellRenderer pComponent,
                                        final RenderData pData,
                                        final int pAlignment) {
        /* Access the table model. */
        DataTableModel myModel = (DataTableModel) pTable.getModel();

        /* Determine the render data */
        myModel.getRenderData(pData);

        /* Apply the Render Data */
        pComponent.setForeground(pData.getForeGround());
        pComponent.setBackground(pData.getBackGround());
        pComponent.setFont(pData.getFont());
        pComponent.setHorizontalAlignment(pAlignment);
        pComponent.setToolTipText(pData.getToolTip());
    }

    /**
     * String Cell Renderer.
     */
    public static class StringRenderer extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * The Render Data.
         */
        private final RenderData theData;

        /**
         * Cell alignment.
         */
        private final int theAlignment;

        /**
         * Constructor.
         */
        public StringRenderer() {
            this(false, SwingConstants.LEFT);
        }

        /**
         * Constructor for fixed width.
         * @param pFixed is the item fixed width?
         * @param pAlignment the alignment
         */
        public StringRenderer(final boolean pFixed,
                              final int pAlignment) {
            theData = new RenderData(pFixed);
            theAlignment = pAlignment;
        }

        @Override
        public void setValue(final Object value) {
            Object o = value;

            /* Convert null value */
            if (value == null) {
                o = "";
            }

            /* Pass value on */
            super.setValue(o);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable table,
                                                        final Object value,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int row,
                                                        final int column) {
            /* Pass call on */
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            /* Declare the Cell position */
            theData.setPosition(row, column, isSelected);

            /* Determine the render data */
            renderComponent(table, this, theData, theAlignment);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Integer Cell Renderer.
     */
    public static class IntegerRenderer extends StringRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * Constructor.
         */
        public IntegerRenderer() {
            super(true, SwingConstants.RIGHT);
        }

        @Override
        public void setValue(final Object value) {
            Object o = value;

            /* If the value is an integer */
            if (value instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) value;
                if (i == 0) {
                    o = "";
                } else {
                    o = i.toString();
                }
            }

            /* Pass value on */
            super.setValue(o);
        }
    }

    /**
     * Calendar Cell Renderer.
     */
    public static class CalendarCell extends DateDayCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 1947211408966548011L;

        /**
         * The Render Data.
         */
        private final RenderData theData;

        /**
         * Constructor.
         */
        public CalendarCell() {
            theData = new RenderData(true);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable table,
                                                        final Object value,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int row,
                                                        final int column) {
            /* Pass call on */
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            /* Declare the Cell position */
            theData.setPosition(row, column, isSelected);

            /* Determine the render data */
            renderComponent(table, this, theData, SwingConstants.LEFT);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Decimal Cell Renderer.
     */
    public static class DecimalRenderer extends StringRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6571410292897989673L;

        /**
         * Constructor.
         */
        public DecimalRenderer() {
            super(true, SwingConstants.RIGHT);
        }

        @Override
        public void setValue(final Object value) {
            Object o = value;

            /* If the value is a Decimal */
            if (value instanceof Decimal) {
                Decimal myDec = (Decimal) value;
                o = myDec.format(true);
            }

            /* Pass value down */
            super.setValue(o);
        }
    }

    /**
     * Row Cell Renderer.
     */
    public static class RowCell extends StringRenderer {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8710214547908947657L;

        /**
         * Constructor.
         */
        public RowCell() {
            super(true, SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object value) {
            Object o = value;

            /* If the value is an integer */
            if (value instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) value;
                if (i == 0) {
                    o = "";
                } else {
                    o = i.toString();
                }
            }

            /* Pass value on */
            super.setValue(o);
        }
    }
}
