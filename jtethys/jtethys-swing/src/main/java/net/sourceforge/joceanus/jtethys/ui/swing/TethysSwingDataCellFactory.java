/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;
import java.awt.Point;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Swing Table Cell Factory.
 */
public class TethysSwingDataCellFactory {
    /**
     * Data Cell.
     * @param <T> the table item class
     * @param <C> the column item class
     */
    public static class TethysSwingDataTextCell<T, C>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The Text field.
         */
        private final TethysSwingDataTextField<C> theField;

        /**
         * The Data class.
         */
        private final Class<C> theClass;

        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The Editor.
         */
        private final TethysSwingDataTextCellEditor theEditor;

        /**
         * The Renderer.
         */
        private final TethysSwingDataTextCellRenderer theRenderer;

        /**
         * The Active location.
         */
        private Point thePoint;

        /**
         * Is the Active location selected?
         */
        private boolean isSelected;

        /**
         * Constructor.
         * @param pField the edit field
         * @param pClass the field class
         */
        protected TethysSwingDataTextCell(final TethysSwingDataTextField<C> pField,
                                          final Class<C> pClass) {
            /* Record the parameters */
            theField = pField;
            theClass = pClass;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the editor and renderer */
            theEditor = new TethysSwingDataTextCellEditor();
            theRenderer = new TethysSwingDataTextCellRenderer();
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Obtain the editor.
         * @return the editor
         */
        public TethysSwingDataTextCellEditor getEditor() {
            return theEditor;
        }

        /**
         * Obtain the renderer.
         * @return the renderer
         */
        public TethysSwingDataTextCellRenderer getRendererr() {
            return theRenderer;
        }

        /**
         * Obtain the field.
         * @return the field
         */
        protected TethysSwingDataTextField<C> getField() {
            return theField;
        }

        /**
         * Obtain the point.
         * @return the point
         */
        public Point getPoint() {
            return thePoint;
        }

        /**
         * Is the active item selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Data Cell Editor.
         */
        private class TethysSwingDataTextCellEditor
                extends AbstractCellEditor
                implements TableCellEditor {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -3591698125380052152L;

            /**
             * Constructor.
             */
            protected TethysSwingDataTextCellEditor() {
                /* Add listeners */
                theField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> stopCellEditing());
                theField.getEventRegistrar().addEventListener(TethysUIEvent.EDITFOCUSLOST, e -> cancelCellEditing());
            }

            @Override
            public C getCellEditorValue() {
                return theField.getValue();
            }

            @Override
            public Component getTableCellEditorComponent(final JTable pTable,
                                                         final Object pValue,
                                                         final boolean isSelected,
                                                         final int pRow,
                                                         final int pCol) {
                /* Store the location */
                int myRow = pTable.convertRowIndexToModel(pRow);
                int myCol = pTable.convertColumnIndexToModel(pCol);
                thePoint = new Point(myCol, myRow);

                /* Set field value and start edit */
                theField.setValue(theClass.cast(pValue));
                theField.startCellEditing();

                /* Return the field */
                return theField.getNode();
            }

            @Override
            public boolean stopCellEditing() {
                /* If we are OK with the value */
                if (!theEventManager.fireEvent(TethysUIEvent.CELLPRECOMMIT, TethysSwingDataTextCell.this)) {
                    /* Pass call onwards */
                    boolean bComplete = super.stopCellEditing();

                    /* Notify of commit */
                    if (bComplete) {
                        theEventManager.fireEvent(TethysUIEvent.CELLCOMMITTED, TethysSwingDataTextCell.this);
                    }

                    /* Return success */
                    return bComplete;
                }

                /* Return failure */
                return false;
            }
        }

        /**
         * Data Cell Editor.
         */
        private class TethysSwingDataTextCellRenderer
                extends DefaultTableCellRenderer {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -5226192429038913966L;

            @Override
            public JComponent getTableCellRendererComponent(final JTable pTable,
                                                            final Object pValue,
                                                            final boolean pSelected,
                                                            final boolean hasFocus,
                                                            final int pRow,
                                                            final int pCol) {
                /* Store the location */
                int myRow = pTable.convertRowIndexToModel(pRow);
                int myCol = pTable.convertColumnIndexToModel(pCol);
                thePoint = new Point(myCol, myRow);
                isSelected = pSelected;

                /* Set details and stop editing */
                theField.setValue(theClass.cast(pValue));
                theField.setEditable(false);

                /* Format the cell */
                theEventManager.fireEvent(TethysUIEvent.CELLFORMAT, TethysSwingDataTextCell.this);

                /* Return this as the render item */
                return theField.getNode();
            }
        }
    }
}
