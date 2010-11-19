/* - ServoErrorListener -
 * Display the details of the error in a message box
 *
 * Copyright 2007 Phidgets Inc.  
 * This work is licensed under the Creative Commons Attribution 2.5 Canada License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/2.5/ca/
 */

package net.ubisoa.servo;

import com.phidgets.event.ErrorListener;
import com.phidgets.event.ErrorEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServoErrorListener implements ErrorListener{
    
    private JFrame appFrame;
    
    /** Creates a new instance of ServoErrorListener */
    public ServoErrorListener(JFrame appFrame)
    {
        this.appFrame = appFrame;
    }

    public void error(ErrorEvent errorEvent)
    {
        JOptionPane.showMessageDialog(appFrame, errorEvent.toString(), "Servo Error Event", JOptionPane.ERROR_MESSAGE);
    }
    
}
