/*******************************************************************************
 * jGordianKnot: Security Suite
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-swing/src/main/java/net/sourceforge/joceanus/jgordianknot/manager/swing/SwingPasswordDialog.java $
 * $Revision: 589 $
 * $Author: Tony $
 * $Date: 2015-04-02 15:53:05 +0100 (Thu, 02 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.manager.javafx;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.joceanus.jgordianknot.manager.MgrResource;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class GordianFXPasswordDialog
        extends Stage {
    /**
     * Minimum password length.
     */
    private static final int MIN_PASSWORD_LEN = 8;

    /**
     * password field width.
     */
    private static final int PASSWORD_FIELD_LEN = 240;

    /**
     * approximate width.
     */
    private static final int APPROX_WIDTH = 400;

    /**
     * approximate height.
     */
    private static final int APPROX_HEIGHT = 100;

    /**
     * adding width.
     */
    private static final int PADDING_SIZE = 5;

    /**
     * Text for Password Label.
     */
    private static final String NLS_PASSWORD = MgrResource.LABEL_PASSWORD.getValue();

    /**
     * Text for Confirm Label.
     */
    private static final String NLS_CONFIRM = MgrResource.LABEL_CONFIRM.getValue();

    /**
     * Text for OK Button.
     */
    private static final String NLS_OK = MgrResource.BUTTON_OK.getValue();

    /**
     * Text for Cancel Button.
     */
    private static final String NLS_CANCEL = MgrResource.BUTTON_CANCEL.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_ERROR = MgrResource.TITLE_ERROR.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_CONFIRMERROR = MgrResource.ERROR_CONFIRM.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR1 = MgrResource.ERROR_LENGTH1.getValue();

    /**
     * Text for Error Panel.
     */
    private static final String NLS_LENGTHERR2 = MgrResource.ERROR_LENGTH2.getValue();

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GordianFXPasswordDialog.class);

    /**
     * Obtained password.
     */
    private char[] thePassword = null;

    /**
     * Confirmation password.
     */
    private char[] theConfirm = null;

    /**
     * The container box.
     */
    private final BorderPane theContainer;

    /**
     * OK Button.
     */
    private final Button theOKButton;

    /**
     * Cancel Button.
     */
    private final Button theCancelButton;

    /**
     * Error field.
     */
    private final Label theErrorField;

    /**
     * Password field.
     */
    private final PasswordField thePassField;

    /**
     * The error panel.
     */
    private final StackPane theError;

    /**
     * Confirmation field.
     */
    private final PasswordField theConfirmField;

    /**
     * Is the password set.
     */
    private boolean isPasswordSet = false;

    /**
     * Do we need to confirm the password.
     */
    private final boolean needConfirm;

    /**
     * Constructor.
     * @param pParent the parent stage for the dialog
     * @param pTitle the title
     * @param pNeedConfirm true/false
     */
    public GordianFXPasswordDialog(final Stage pParent,
                                   final String pTitle,
                                   final boolean pNeedConfirm) {
        /* Initialise the dialog */
        initModality(Modality.WINDOW_MODAL);
        initOwner(pParent);

        /* Store the parameters */
        setTitle(pTitle);
        needConfirm = pNeedConfirm;

        /* Create the Password label */
        Label myPassLabel = new Label(NLS_PASSWORD);
        myPassLabel.setFocusTraversable(false);

        /* Create the confirm label */
        Label myConfLabel = new Label(NLS_CONFIRM);
        myConfLabel.setFocusTraversable(false);

        /* Create OK button */
        theOKButton = new Button(NLS_OK);
        theOKButton.setMaxWidth(Double.MAX_VALUE);
        theOKButton.setOnAction(e -> processPassword());

        /* Create cancel button */
        theCancelButton = new Button(NLS_CANCEL);
        theCancelButton.setOnAction(e -> processCancel());

        /* Create password field */
        thePassField = new PasswordField();
        thePassField.setPrefWidth(PASSWORD_FIELD_LEN);
        thePassField.setOnAction(e -> processPassword());

        /* Create confirm field */
        theConfirmField = new PasswordField();
        theConfirmField.setPrefWidth(PASSWORD_FIELD_LEN);
        theConfirmField.setOnAction(e -> processPassword());

        /* Create the error panel */
        theErrorField = new Label();
        BorderPane myErrorBox = new BorderPane();
        myErrorBox.setCenter(theErrorField);
        theError = TethysFXGuiUtils.getTitledPane(NLS_ERROR, myErrorBox);
        BorderPane.setMargin(theError, new Insets(PADDING_SIZE, 0, 0, 0));

        /* Set the Error panel to be red */
        theErrorField.setTextFill(Color.RED);

        /* Create the panel */
        GridPane myForm = new GridPane();
        myForm.setHgap(PADDING_SIZE);
        myForm.setVgap(PADDING_SIZE);

        /* Layout the password panel */
        myForm.add(myPassLabel, 0, 0);
        GridPane.setHalignment(myPassLabel, HPos.RIGHT);
        myForm.add(thePassField, 1, 0);
        if (needConfirm) {
            myForm.add(myConfLabel, 0, 1);
            GridPane.setHalignment(myConfLabel, HPos.RIGHT);
            myForm.add(theConfirmField, 1, 1);
        }
        myForm.add(theOKButton, 2, 0);
        if (needConfirm) {
            myForm.add(theCancelButton, 2, 1);
        }

        /* Create the scene */
        theContainer = new BorderPane();
        theContainer.setPadding(new Insets(PADDING_SIZE, PADDING_SIZE, PADDING_SIZE, PADDING_SIZE));
        theContainer.setCenter(myForm);
        Scene myScene = new Scene(theContainer);
        setScene(myScene);
        TethysFXGuiUtils.addStyleSheet(myScene);
    }

    /**
     * Obtain the password.
     * @return the password
     */
    protected char[] getPassword() {
        return thePassword;
    }

    /**
     * Is the password set.
     * @return true/false
     */
    public boolean isPasswordSet() {
        return isPasswordSet;
    }

    /**
     * Release resources.
     */
    public void release() {
        if (thePassword != null) {
            Arrays.fill(thePassword, (char) 0);
        }
        if (theConfirm != null) {
            Arrays.fill(theConfirm, (char) 0);
        }
    }

    /**
     * Obtain characters from a Password field.
     * @param pField the field to obtain the characters from
     * @return the characters as an array
     */
    private char[] getPasswordAsCharArray(final PasswordField pField) {
        /* Allocate the array */
        CharSequence myChars = pField.getCharacters();
        int myLen = myChars.length();
        char[] myArray = new char[myLen];
        for (int i = 0; i < myLen; i++) {
            myArray[i] = myChars.charAt(i);
        }
        return myArray;
    }

    /**
     * process cancel.
     */
    private void processCancel() {
        /* Note that we have set the password */
        isPasswordSet = false;

        /* Close the dialog */
        close();
    }

    /**
     * process password.
     */
    private void processPassword() {
        /* Access the password */
        thePassword = getPasswordAsCharArray(thePassField);

        /* If we need to confirm the password */
        if (needConfirm) {
            /* Access the confirm password */
            theConfirm = getPasswordAsCharArray(theConfirmField);

            /* If the password is less than the minimum length */
            if (thePassword.length < MIN_PASSWORD_LEN) {
                /* Set error and return */
                setError(NLS_LENGTHERR1
                         + " "
                         + MIN_PASSWORD_LEN
                         + " "
                         + NLS_LENGTHERR2);
                return;
            }

            /* If the confirm password does not match */
            if (!Arrays.equals(thePassword, theConfirm)) {
                /* Set error and return */
                setError(NLS_CONFIRMERROR);
                return;
            }
        }

        /* Note that we have set the password */
        isPasswordSet = true;

        /* Close the dialog */
        close();
    }

    /**
     * set the error.
     * @param pError the error to display
     */
    public void setError(final String pError) {
        /* Set the string to the error field */
        theErrorField.setText(pError);

        /* Show that we need to update the password */
        isPasswordSet = false;

        /* Set the error into the dialog */
        theContainer.setBottom(theError);

        /* ReSize the dialog */
        sizeToScene();
    }

    /**
     * show the dialog.
     */
    public void showDialog() {
        /* Centre on parent */
        Window myParent = getOwner();
        if (myParent != null) {
            double myX = (myParent.getWidth() - APPROX_WIDTH) / 2;
            double myY = (myParent.getHeight() - APPROX_HEIGHT) / 2;
            setX(myParent.getX() + myX);
            setY(myParent.getY() + myY);
        }

        /* Show the dialog */
        showAndWait();
    }

    /**
     * Show the dialog under an invokeAndWait clause.
     * @param pDialog the dialog to show
     * @return successful dialog usage true/false
     */
    protected static boolean showTheDialog(final GordianFXPasswordDialog pDialog) {
        /* If this is the event dispatcher thread */
        if (Platform.isFxApplicationThread()) {
            /* invoke the dialog directly */
            pDialog.showDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                Platform.runLater(() -> pDialog.showDialog());
            } catch (IllegalStateException e) {
                LOGGER.error("Failed to display dialog", e);
            }
        }

        /* Return to caller */
        return pDialog.isPasswordSet();
    }
}
