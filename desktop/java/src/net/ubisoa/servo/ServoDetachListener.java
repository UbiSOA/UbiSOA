/* - ServoDetachListener -
 * Clear all the fields and disable all the controls
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package net.ubisoa.servo;

import com.phidgets.ServoPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.DetachListener;
import com.phidgets.event.DetachEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSlider;

public class ServoDetachListener implements DetachListener{
    
    private JFrame appFrame;
    private JTextField attachedTxt;
    private JTextArea nameTxt;
    private JTextField serialTxt;
    private JTextField versionTxt;
    private JTextField numServoTxt;
    private JComboBox servoCmb;
    private JComboBox servoTypeCmb;
    private JTextField positionTxt;
    private JSlider positionScrl;
    private JCheckBox engagedChk;
    
    /** Creates a new instance of ServoDetachListener */
    public ServoDetachListener(JFrame appFrame, JTextField attachedTxt, 
            JTextArea nameTxt, JTextField serialTxt, JTextField versionTxt, 
            JTextField numServoTxt, JComboBox servoCmb, JComboBox servoTypeCmb, 
            JTextField positionTxt, JSlider positionScrl, JCheckBox engagedChk)
    {
        this.appFrame = appFrame;
        this.attachedTxt = attachedTxt;
        this.nameTxt = nameTxt;
        this.serialTxt = serialTxt;
        this.versionTxt = versionTxt;
        this.numServoTxt = numServoTxt;
        this.servoCmb = servoCmb;
        this.servoTypeCmb = servoTypeCmb;
        this.positionTxt = positionTxt;
        this.positionScrl = positionScrl;
        this.engagedChk = engagedChk;
    }

    public void detached(DetachEvent de)
    {
        try
         {
            ServoPhidget detached = (ServoPhidget)de.getSource();
            attachedTxt.setText(Boolean.toString(detached.isAttached()));
            nameTxt.setText("");
            serialTxt.setText("");
            versionTxt.setText("");
            numServoTxt.setText("");
            positionTxt.setText("");
            
            servoCmb.setEnabled(false);
            servoCmb.removeAllItems();
            servoTypeCmb.setEnabled(false);
            servoTypeCmb.removeAllItems();
            engagedChk.setEnabled(false);
            engagedChk.setSelected(false);
            positionScrl.setEnabled(false);
            positionScrl.setValue(0);
         }
        catch (PhidgetException ex)
        {
            JOptionPane.showMessageDialog(appFrame, ex.getDescription(), "Phidget error " + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
