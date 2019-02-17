/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import javafx.application.Application;
import javafx.application.Preloader.StateChangeNotification;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram.MetisApplication;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIcon;

/**
 * javaFX Splash Panel.
 */
public class MetisFXSplash {
    /**
     * The Splash pane.
     */
    private final StackPane thePane;

    /**
     * The Splash image .
     */
    private final Image theImage;

    /**
     * The program info .
     */
    private final MetisProgram theInfo;

    /**
     * The stage.
     */
    private Stage theStage;

    /**
     * Constructor.
     *
     * @param pInfo the program information
     */
    public MetisFXSplash(final MetisProgram pInfo) {
        /* Store parameters */
        theInfo = pInfo;

        /* Create a StackPane */
        thePane = new StackPane();

        /* Load the image */
        final TethysProgram myApp = pInfo.getProgramDefinitions();
        final TethysFXIcon myImage = TethysFXGuiUtils.getIcon(myApp.getSplash());
        theImage = myImage.getImage();

        /* Create the name and version */
        final HBox myName = getCentredText(myApp.getName());
        final HBox myVers = getCentredText(myApp.getVersion());
        final HBox myXtra = getCentredText("");
        final VBox myBox = new VBox();
        final Region mySpacer = new Region();
        VBox.setVgrow(mySpacer, Priority.ALWAYS);
        myBox.getChildren().addAll(mySpacer, myName, myVers, myXtra);
        thePane.getChildren().addAll(myImage.getIcon(), myBox);
        thePane.setStyle("-fx-background-color: transparent;");
    }

    /**
     * Attach to stage.
     *
     * @param pStage the stage
     */
    public void attachToStage(final Stage pStage) {
        /* Store the stage */
        theStage = pStage;

        /* Configure the stage */
        final Scene myScene = new Scene(thePane);
        myScene.setFill(Color.TRANSPARENT);
        pStage.setScene(myScene);
        pStage.initStyle(StageStyle.TRANSPARENT);
        pStage.setAlwaysOnTop(true);

        /* Place the stage in the middle of the primary screen */
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        pStage.setX(bounds.getMinX() + (bounds.getWidth() - theImage.getWidth()) / 2);
        pStage.setY(bounds.getMinY() + (bounds.getHeight() - theImage.getHeight()) / 2);
    }

    /**
     * Handle State change.
     *
     * @param pEvent the event
     */
    public void handleStateChange(final StateChangeNotification pEvent) {
        /* If we are just before application start */
        if (pEvent.getType() == StateChangeNotification.Type.BEFORE_START) {
            /* Hide any visible stage */
            if (theStage != null) {
                theStage.hide();
            }

            /* else if we are just before application start */
        } else if (pEvent.getType() == StateChangeNotification.Type.BEFORE_INIT) {
            /* Pass on info to application if possible */
            final Application myApp = pEvent.getApplication();
            if (myApp instanceof MetisApplication) {
                ((MetisApplication) myApp).setProgramInfo(theInfo);
            }
        }
    }

    /**
     * Create centred box.
     *
     * @param pText the text for the box
     * @return the box
     */
    private HBox getCentredText(final String pText) {
        /* Create the text */
        final Label myText = new Label(pText);
        myText.setStyle("-fx-text-fill:white; -fx-background-color: blue; -fx-font-size: 16;");

        final Region mySpacer1 = new Region();
        final Region mySpacer2 = new Region();
        HBox.setHgrow(mySpacer1, Priority.ALWAYS);
        HBox.setHgrow(mySpacer2, Priority.ALWAYS);

        /* Create the box */
        final HBox myBox = new HBox();
        myBox.getChildren().addAll(mySpacer1, myText, mySpacer2);
        return myBox;
    }
}
