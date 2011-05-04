/*- ServoAttachListener - 
 * Populate the available fields and controls
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package net.ubisoa.servo;

import com.phidgets.ServoPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachListener;
import com.phidgets.event.AttachEvent;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JSlider;

public class ServoAttachListener implements AttachListener{
    
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
    private Hashtable servoTypes;
    
    /** Creates a new instance of ServoAttachListener */
    public ServoAttachListener(JFrame appFrame, JTextField attachedTxt, 
            JTextArea nameTxt, JTextField serialTxt, JTextField versionTxt, 
            JTextField numServoTxt, JComboBox servoCmb, JComboBox servoTypeCmb, 
            JTextField positionTxt, JSlider positionScrl, JCheckBox engagedChk, 
            Hashtable servoTypes)
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
        this.servoTypes = servoTypes;
    }

    public void attached(AttachEvent ae)
    {
        try
        {
            ServoPhidget attached = (ServoPhidget)ae.getSource();
            attachedTxt.setText(Boolean.toString(attached.isAttached()));
            nameTxt.setText(attached.getDeviceName());
            serialTxt.setText(Integer.toString(attached.getSerialNumber()));
            versionTxt.setText(Integer.toString(attached.getDeviceVersion()));
            numServoTxt.setText(Integer.toString(attached.getMotorCount()));
            
            //the maximum bound of the phidget servo is 232, it's minimum bound is -23
            positionScrl.setMaximum((int)attached.getPositionMax(0));
            positionScrl.setMinimum((int)attached.getPositionMin(0));
        
            servoCmb.setEnabled(true);
            
            for(int i = 0; i < attached.getMotorCount(); i++)
            {
                servoCmb.addItem(new Integer(i));
                attached.setServoType(i, ServoPhidget.PHIDGET_SERVO_HITEC_HS322HD);
            }
            
            servoCmb.setSelectedIndex(0);
            
            Enumeration e = servoTypes.keys();
            while(e.hasMoreElements())
            {
                servoTypeCmb.addItem(e.nextElement());
            }
            
            servoTypeCmb.setEnabled(true);
            
            try
            {
                positionTxt.setText(Double.toString(attached.getPosition(0)));
                positionScrl.setValue((int)attached.getPosition(0));
                engagedChk.setSelected(attached.getEngaged(0));
            }
            catch(PhidgetException ex2)
            {
                positionTxt.setText("Unknown");
                positionScrl.setValue(0);
                engagedChk.setSelected(false);
            }
            
            engagedChk.setEnabled(true);
            positionScrl.setEnabled(true);
        }
        catch (PhidgetException ex)
        {
            JOptionPane.showMessageDialog(appFrame, ex.getDescription(), "Phidget error " + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
