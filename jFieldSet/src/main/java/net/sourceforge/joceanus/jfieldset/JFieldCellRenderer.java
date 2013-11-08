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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.sourceforge.joceanus.jdateday.JDateDayCellRenderer;
import net.sourceforge.joceanus.jdateday.JDateDayFormatter;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JDecimalFormatter;
import net.sourceforge.joceanus.jfieldset.JFieldManager.PopulateFieldData;

/**
 * Cell renderers.
 * @author Tony Washer
 */
public final class JFieldCellRenderer {
    /**
     * Private constructor to avoid instantiation.
     */
    private JFieldCellRenderer() {
    }

    /**
     * Render the component.
     * @param pTable the table
     * @param pComponent the component
     * @param pData the render data
     */
    private static void renderComponent(final JTable pTable,
                                        final JComponent pComponent,
                                        final JFieldData pData) {
        /* Ignore if the model is not applicable */
        TableModel myTableModel = pTable.getModel();
        if (!(myTableModel instanceof PopulateFieldData)) {
            return;
        }

        /* Access the table model. */
        PopulateFieldData myModel = (PopulateFieldData) myTableModel;

        /* Determine the field data */
        myModel.populateFieldData(pData);

        /* Apply the Field Data */
        pComponent.setForeground(pData.getForeGround());
        pComponent.setBackground(pData.getBackGround());
        pComponent.setFont(pData.getFont());
        pComponent.setToolTipText(pData.getToolTip());
    }

    /**
     * String Cell Renderer.
     */
    public static class StringCellRenderer
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * The Render Data.
         */
        private final transient JFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected StringCellRenderer(final JFieldManager pManager) {
            this(pManager.allocateRenderData(false), SwingConstants.LEFT);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pAlignment the alignment
         */
        private StringCellRenderer(final JFieldData pData,
                                   final int pAlignment) {
            theData = pData;
            setHorizontalAlignment(pAlignment);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* Convert null value */
            if (o == null) {
                o = "";
            }

            /* Pass value on */
            super.setValue(o);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            super.getTableCellRendererComponent(pTable, pValue, isSelected, hasFocus, pRowIndex, pColIndex);

            /* Declare the Cell position */
            theData.setPosition(pTable.convertRowIndexToModel(pRowIndex), pTable.convertColumnIndexToModel(pColIndex), isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Integer Cell Renderer.
     */
    public static class IntegerCellRenderer
            extends StringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected IntegerCellRenderer(final JFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) o;
                o = i.toString();
            }

            /* Pass value on */
            super.setValue(o);
        }
    }

    /**
     * Boolean Cell Renderer.
     */
    public static class BooleanCellRenderer
            extends JCheckBox
            implements TableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2197785411706668287L;

        /**
         * The Render Data.
         */
        private final transient JFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected BooleanCellRenderer(final JFieldManager pManager) {
            this(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pAlignment the alignment
         */
        protected BooleanCellRenderer(final JFieldData pData,
                                      final int pAlignment) {
            theData = pData;
            setHorizontalAlignment(pAlignment);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            setSelected((pValue != null)
                        && ((Boolean) pValue).booleanValue());

            /* Declare the Cell position */
            theData.setPosition(pTable.convertRowIndexToModel(pRowIndex), pTable.convertColumnIndexToModel(pColIndex), isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Calendar Cell Renderer.
     */
    public static class CalendarCellRenderer
            extends JDateDayCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 1947211408966548011L;

        /**
         * The Render Data.
         */
        private final transient JFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pFormatter the formatter
         */
        protected CalendarCellRenderer(final JFieldManager pManager,
                                       final JDateDayFormatter pFormatter) {
            super(pFormatter);
            theData = pManager.allocateRenderData(true);
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            super.getTableCellRendererComponent(pTable, pValue, isSelected, hasFocus, pRowIndex, pColIndex);

            /* Declare the Cell position in terms of the model */
            theData.setPosition(pTable.convertRowIndexToModel(pRowIndex), pTable.convertColumnIndexToModel(pColIndex), isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Decimal Cell Renderer.
     */
    public static class DecimalCellRenderer
            extends StringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6571410292897989673L;

        /**
         * Decimal Parser.
         */
        private final transient JDecimalFormatter theFormatter;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pFormatter the formatter
         */
        protected DecimalCellRenderer(final JFieldManager pManager,
                                      final JDecimalFormatter pFormatter) {
            super(pManager.allocateRenderData(true), SwingConstants.RIGHT);
            theFormatter = pFormatter;
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is a Decimal */
            if (o instanceof JDecimal) {
                /* Format it */
                o = theFormatter.formatDecimal((JDecimal) o);
            }

            /* Pass value down */
            super.setValue(o);
        }
    }

    /**
     * Row Cell Renderer.
     */
    public static class RowCellRenderer
            extends StringCellRenderer {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8710214547908947657L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected RowCellRenderer(final JFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) o;
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
