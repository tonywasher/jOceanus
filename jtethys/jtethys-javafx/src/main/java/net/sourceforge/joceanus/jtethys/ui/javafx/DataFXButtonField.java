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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.sourceforge.jdatebutton.javafx.ArrowIcon;
import net.sourceforge.joceanus.jtethys.javafx.GuiUtils;
import net.sourceforge.joceanus.jtethys.ui.DataEditField;
import net.sourceforge.joceanus.jtethys.ui.javafx.ScrollFXContextMenu.ContextEvent;

/**
 * Generic class for displaying and editing a button data field.
 */
public abstract class DataFXButtonField {
    /**
     * Private constructor.
     */
    private DataFXButtonField() {
    }

    /**
     * DataEditButtonField class.
     * @param <T> the data type
     * @param <B> the button type
     */
    public abstract static class DataFXEditButtonField<T, B extends Button>
            extends DataEditField<T, Node, Node> {
        /**
         * The node.
         */
        private final BorderPane theNode;

        /**
         * The button.
         */
        private final B theButton;

        /**
         * The label.
         */
        private final Label theLabel;

        /**
         * The command button.
         */
        private final Button theCmdButton;

        /**
         * Do we show the command button?
         */
        private boolean doShowCmdButton;

        /**
         * Constructor.
         * @param pButton the button
         */
        protected DataFXEditButtonField(final B pButton) {
            /* Create resources */
            theNode = new BorderPane();
            theLabel = new Label();
            theButton = pButton;

            /* Create the command button */
            theCmdButton = new Button();
            theCmdButton.setGraphic(ArrowIcon.DOWN.getArrow());
            theCmdButton.setFocusTraversable(false);

            /* declare the menu */
            declareMenu(new ScrollFXContextMenu<String>());

            /* Set maximum widths for fields */
            theLabel.setMaxWidth(Integer.MAX_VALUE);
            theButton.setMaxWidth(Integer.MAX_VALUE);

            /* Set alignment */
            theLabel.setAlignment(Pos.CENTER_LEFT);

            /* Default to readOnly */
            theNode.setCenter(theLabel);

            /* handle command button action */
            theCmdButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent t) {
                    handleMenuRequest();
                }
            });

            /* Set context menu listener */
            getMenu().addEventHandler(ContextEvent.MENU_SELECT, new EventHandler<ContextEvent<?>>() {
                @Override
                public void handle(final ContextEvent<?> e) {
                    /* Handle the close of the menu */
                    handleMenuClosed();
                }
            });
        }

        @Override
        public ScrollFXContextMenu<String> getMenu() {
            return (ScrollFXContextMenu<String>) super.getMenu();
        }

        @Override
        protected void showMenu() {
            getMenu().showMenuAtPosition(theCmdButton, Side.RIGHT);
        }

        @Override
        public Node getNode() {
            return theNode;
        }

        /**
         * Set the font.
         * @param pFont the font for the field
         */
        public void setFont(final Font pFont) {
            /* Apply font to the two nodes */
            theLabel.setFont(pFont);
            theButton.setFont(pFont);
        }

        /**
         * Set the textFill colour.
         * @param pColor the colour
         */
        public void setTextFill(final Color pColor) {
            /* Apply font to the two nodes */
            theLabel.setTextFill(pColor);
            theButton.setStyle("-fx-text-inner-color: " + GuiUtils.colorToHexString(pColor));
        }

        /**
         * Show the command button.
         * @param pShow true/false
         */
        public void showCommandButton(final boolean pShow) {
            /* Remove any button that is displaying */
            theNode.setRight(null);
            doShowCmdButton = pShow;

            /* If we have a button to display */
            if (isEditable() && doShowCmdButton) {
                theNode.setRight(theCmdButton);
            }
        }

        @Override
        public void setEditable(final boolean pEditable) {
            /* Obtain current setting */
            boolean isEditable = isEditable();

            /* If we are changing */
            if (pEditable != isEditable) {
                /* If we are setting editable */
                if (pEditable) {
                    theNode.setCenter(theButton);
                    if (doShowCmdButton) {
                        theNode.setRight(theCmdButton);
                    }
                } else {
                    theNode.setCenter(theLabel);
                    theNode.setRight(null);
                }

                /* Pass call on */
                super.setEditable(pEditable);
            }
        }
    }
}
