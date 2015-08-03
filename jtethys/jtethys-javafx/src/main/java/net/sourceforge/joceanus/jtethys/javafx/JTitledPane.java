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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/dateday/swing/JDateDayRangeSelect.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.javafx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Creation of a titled pane in javafx.
 */
public final class JTitledPane {
    /**
     * StyleSheet.
     */
    private static final String CSS_STYLE = JTitledPane.class.getResource("jtethys-javafx-titled.css").toExternalForm();

    /**
     * private constructor.
     */
    private JTitledPane() {
    }

    /**
     * Create titled pane wrapper around panel.
     * @param pTitle the title
     * @param pPanel the panel
     * @return the titled pane
     */
    public static StackPane getTitledPane(final String pTitle,
                                          final Pane pPanel) {
        /* Create the panel */
        StackPane myPanel = new StackPane();
        Label myTitle = new Label(pTitle);
        StackPane.setAlignment(myTitle, Pos.TOP_LEFT);
        StackPane.setAlignment(pPanel, Pos.CENTER);
        myPanel.getChildren().addAll(myTitle, pPanel);
        pPanel.getStyleClass().add("-jtethys-titled-content");
        myTitle.getStyleClass().add("-jtethys-titled-title");
        myPanel.getStyleClass().add("-jtethys-titled-border");

        /* Return the panel */
        return myPanel;
    }

    /**
     * Add necessary styleSheets to scene.
     * @param pScene the scene
     */
    public static void addStyleSheet(final Scene pScene) {
        pScene.getStylesheets().add(CSS_STYLE);
    }
}
