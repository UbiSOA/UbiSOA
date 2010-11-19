/*- ServoPositionListener - 
 * Display the details of the position change event.
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package net.ubisoa.servo;

import com.phidgets.PhidgetException;
import com.phidgets.ServoPhidget;
import com.phidgets.event.ServoPositionChangeListener;
import com.phidgets.event.ServoPositionChangeEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ServoPositionListener implements ServoPositionChangeListener{
    
    private JFrame appFrame;
    private JTextField positionTxt;
    private JComboBox servoCmb;
    private JCheckBox engagedChk;
    /**
     * Creates a new instance of ServoPositionListener
     */
    public ServoPositionListener(JFrame appFrame, JTextField positionTxt, 
            JComboBox servoCmb, JCheckBox engagedChk)
    {
        this.appFrame = appFrame;
        this.positionTxt = positionTxt;
        this.servoCmb = servoCmb;
        this.engagedChk = engagedChk;
    }

    public void servoPositionChanged(ServoPositionChangeEvent servoPositionChangeEvent)
    {
        if(this.servoCmb.getSelectedIndex() == servoPositionChangeEvent.getIndex())
        {
            try
            {
                positionTxt.setText(Double.toString(servoPositionChangeEvent.getValue()));
                engagedChk.setSelected(((ServoPhidget)servoPositionChangeEvent.getSource()).getEngaged(servoPositionChangeEvent.getIndex()));
            }
            catch(PhidgetException ex)
            {
                JOptionPane.showMessageDialog(appFrame, ex.getDescription(), "Phidget error " + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
}
