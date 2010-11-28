/*
 * Copyright (c) 2010, Edgardo Avilés-López
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * – Redistributions of source code must retain the above copyright notice, this list of
 *   conditions and the following disclaimer.
 * – Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * – Neither the name of the CICESE Research Center nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ubisoa.servo;

import com.phidgets.ServoPhidget;
import com.phidgets.PhidgetException;

/**
 * @author V. Soto <valeria@ubisoa.net>
 */
public class Servo {
	/*
	 * servo info:
	 * 		attached: true/false
	 * 		name:
	 * 		serial number:
	 * 		version:
	 * 		servos:
	 * servo position:
	 * 		servo no.:
	 * 		type:
	 * 		position: 0 a 180
	 * 		engaged: true/false
	 */
	
    private ServoPhidget phiServo;
	private double positionPost;
	//private int positionAnt;

	public Servo(double finP) {
		this.positionPost = finP;
		inicializaServo();		
		//this.positionAnt = 0;
	}
	
	/**
     * initialize the servo object and hook the event listeners
     **/
    private void inicializaServo() {
        
        try
        {
            this.phiServo = new ServoPhidget();
            //System.out.println("0 " + phiServo.toString());
            this.phiServo.openAny();
            int cont = 0;
            while (!this.phiServo.isAttached()) {
            	this.phiServo.waitForAttachment();
            	System.out.println(cont + " " + phiServo.toString());
            	cont++;
            }
            this.phiServo.setPosition(0, this.positionPost);
            this.phiServo.close();
        }
        catch(PhidgetException ex) {
        	System.out.println("private void inicializaServo()");
        	System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
        }
    }
	
	public int getDeviceClass() {
		try
        {
			return this.phiServo.getDeviceClass();
        }
		catch (PhidgetException ex)
        {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
            return -1;
        }
	}
	
	public int getDeviceID() {
		try
        {
			return this.phiServo.getDeviceID();
        }
		catch (PhidgetException ex)
        {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
            return -1;
        }
	}	

	public String getDeviceLabel() {
		try
        {
			return this.phiServo.getDeviceLabel();
		}
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return "Not Attached";
	    }	
	}
	
	public String getDeviceName() {
		try
        {
			return this.phiServo.getDeviceName();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return "Not Attached";
	    }
	}
	
	public String getDeviceType() {
		try
        {
			return this.phiServo.getDeviceType();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return "Not Attached";
	    }
	}
	
	public int getDeviceVersion() {
		try
        {
			return this.phiServo.getDeviceVersion();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return -1;
	    }
	}
	
	public boolean getEngaged(int arg) {
		try
        {
			return this.phiServo.getEngaged(arg);
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return false;
	    }
	}
	
	public int getMotorCount() {
		try
        {
			return this.phiServo.getMotorCount();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return -1;
	    }
	}
	
	public double getPosition(int arg) {
		try
        {
			return this.phiServo.getPosition(arg);
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());	        return -1;
	    }
	}
	
	public double getPositionMax(int arg) {
		try
        {
			return this.phiServo.getPositionMax(arg);
		}
		catch (PhidgetException ex)
	    {
	        System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return -1;
	    }
	}
	
	public double getPositionMin(int arg) {
		try
        {
			return this.phiServo.getPositionMin(arg);
		}
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return -1;
	    }
	}
	
	public int getSerialNumber() {
		try
        {
			return this.phiServo.getSerialNumber();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return -1;
	    }
	}
	
	public String getServerAddress() {
		try
        {
			return this.phiServo.getServerAddress();
        }
		catch (PhidgetException ex)
	    {
			System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
	        return "Not Attached";
	    }
	}
	
	public void setPositionPost(double positionPost) {
		this.positionPost = positionPost;
	}

	public double getPositionPost() {
		return positionPost;
	}
	
	/**
     * Modify the servo position
     **/
    public void mover(double position) {
        //A PhidgetException will be thrown if you try to set the position 
        //to any value NOT between -23 and 232 and if a PhidgetServo
        //is not connected
        try
        {
            this.phiServo.setPosition(this.phiServo.getDeviceID(), position);
            //servo.setPosition(((Integer)servoCmb.getSelectedItem()).intValue(), positionScrl.getValue());
        }
        catch (PhidgetException ex)
        {
        	System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
        }
    }

  
}


//import listeners.*;

/*public class Servo extends javax.swing.JFrame {
    
    private static String runArgs[];
    private Hashtable servoTypes;
    private ServoPhidget servo;
    private ServoAttachListener attach_listener;
    private ServoDetachListener detach_listener;
    private ServoErrorListener error_listener;
    private ServoPositionListener change_listener;
    
    /** Creates new form Servo */
	/*public Servo() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    /*private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        attachedTxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        nameTxt = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        serialTxt = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        versionTxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        numServoTxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        servoCmb = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        positionTxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        positionScrl = new javax.swing.JSlider();
        engagedChk = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        servoTypeCmb = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servo - full");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Servo Info"));

        attachedTxt.setEditable(false);

        jLabel1.setText("Attached:");

        nameTxt.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.disabledBackground"));
        nameTxt.setColumns(20);
        nameTxt.setEditable(false);
        nameTxt.setLineWrap(true);
        nameTxt.setRows(3);
        nameTxt.setTabSize(2);
        nameTxt.setWrapStyleWord(true);
        jScrollPane1.setViewportView(nameTxt);

        jLabel2.setText("Name:");

        serialTxt.setEditable(false);

        jLabel3.setText("Serial No.:");

        versionTxt.setEditable(false);

        jLabel4.setText("Version:");

        numServoTxt.setEditable(false);

        jLabel5.setText("Servos:");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(35, 35, 35)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel4)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, numServoTxt)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, versionTxt)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, serialTxt)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, attachedTxt)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attachedTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .add(16, 16, 16)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(serialTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .add(16, 16, 16)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(versionTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(numServoTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Servo Position"));

        servoCmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                servoCmbActionPerformed(evt);
            }
        });

        jLabel6.setText("Servo No.:");

        positionTxt.setEditable(false);

        jLabel7.setText("Position:");

        positionScrl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                positionScrlStateChanged(evt);
            }
        });

        engagedChk.setText("Engaged");
        engagedChk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engagedChkActionPerformed(evt);
            }
        });

        jLabel8.setText("Type:");

        servoTypeCmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                servoTypeCmbActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(91, 91, 91)
                        .add(engagedChk))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(57, 57, 57)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(servoCmb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel7)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(positionTxt, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .add(servoTypeCmb, 0, 200, Short.MAX_VALUE)
                            .add(positionScrl, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(servoCmb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(servoTypeCmb, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(positionTxt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(positionScrl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                .add(engagedChk)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * initialize the servo object and hook the event listeners
     **/
    /*private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
        servoTypes = new Hashtable();
        servoTypes.put("DEFAULT", ServoPhidget.PHIDGET_SERVO_DEFAULT);
        servoTypes.put("RAW_us_MODE", ServoPhidget.PHIDGET_SERVO_RAW_us_MODE);
        servoTypes.put("HITEC_HS322HD", ServoPhidget.PHIDGET_SERVO_HITEC_HS322HD);
        servoTypes.put("HITEC_HS5245MG", ServoPhidget.PHIDGET_SERVO_HITEC_HS5245MG);
        servoTypes.put("HITEC_805BB", ServoPhidget.PHIDGET_SERVO_HITEC_805BB);
        servoTypes.put("HITEC_HS422", ServoPhidget.PHIDGET_SERVO_HITEC_HS422);
        servoTypes.put("TOWERPRO_MG90", ServoPhidget.PHIDGET_SERVO_TOWERPRO_MG90);
        servoTypes.put("HITEC_HSR1425CR", ServoPhidget.PHIDGET_SERVO_HITEC_HSR1425CR);
        servoTypes.put("HITEC_HS785HB", ServoPhidget.PHIDGET_SERVO_HITEC_HS785HB);
        servoTypes.put("HITEC_HS485HB", ServoPhidget.PHIDGET_SERVO_HITEC_HS485HB);
        servoTypes.put("HITEC_HS645MG", ServoPhidget.PHIDGET_SERVO_HITEC_HS645MG);
        servoTypes.put("HITEC_815BB", ServoPhidget.PHIDGET_SERVO_HITEC_815BB);
        
        servoCmb.setEnabled(false);
        servoTypeCmb.setEnabled(false);
        positionScrl.setEnabled(false);
        
        engagedChk.setEnabled(false);
        
        try
        {
            servo = new ServoPhidget();
            
            attach_listener = new ServoAttachListener(this, this.attachedTxt,
                    this.nameTxt, this.serialTxt, this.versionTxt,
                    this.numServoTxt, this.servoCmb, this.servoTypeCmb, 
                    this.positionTxt, this.positionScrl, this.engagedChk, this.servoTypes);
            
            detach_listener = new ServoDetachListener(this, this.attachedTxt,
                    this.nameTxt, this.serialTxt, this.versionTxt,
                    this.numServoTxt, this.servoCmb, this.servoTypeCmb, 
                    this.positionTxt, this.positionScrl, this.engagedChk);
            
            error_listener = new ServoErrorListener(this);
            
            change_listener = new ServoPositionListener(this, this.positionTxt, 
                    this.servoCmb, this.engagedChk);
            
            servo.addAttachListener(attach_listener);
            servo.addDetachListener(detach_listener);
            servo.addErrorListener(error_listener);
            servo.addServoPositionChangeListener(change_listener);
            
            //This assumes that if there is a command line argument, it is a serial number
            //and we try to open that specific device. Otherwise, open any device.
            if((runArgs.length > 1) && (runArgs[1].equals("remote")))
            {
                servo.open(Integer.parseInt(runArgs[0]), null);
            }
            else if(runArgs.length > 0)
            {
                servo.open(Integer.parseInt(runArgs[0]));
            }
            else
            {
                servo.openAny();
            }
        }
        catch(PhidgetException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getDescription(), "Phidget Error" + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * If using a 4-motor Phidget Servo, this code will switch between the 
     * selected servos and display their position data.
     **/
    /*private void servoCmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_servoCmbActionPerformed
        
        if(servoCmb.isEnabled())
        {
            initAllValues();
        }
    }//GEN-LAST:event_servoCmbActionPerformed

    /**
     * Modify the servo position based on the value returned by the slider
     **/
    /*private void positionScrlStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_positionScrlStateChanged
        if(positionScrl.isEnabled())
        {
            //A PhidgetException will be thrown if you try to set the position 
            //to any value NOT between -23 and 232 and if a PhidgetServo
            //is not connected
            try
            {
                servo.setPosition(((Integer)servoCmb.getSelectedItem()).intValue(), positionScrl.getValue());
            }
            catch (PhidgetException ex)
            {
                JOptionPane.showMessageDialog(this, ex.getDescription(), "Phidget Error" + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_positionScrlStateChanged

    /**
     * When the application is terminating, close the Phidget.
     **/
    /*private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try
        {
            //unhook the event listeners
            servo.removeServoPositionChangeListener(change_listener);
            servo.removeErrorListener(error_listener);
            servo.removeDetachListener(detach_listener);
            servo.removeAttachListener(attach_listener);
            
            //close the servo
            servo.close();
            
            servo = null;
            
            dispose();
            System.exit(0);
        }
        catch(PhidgetException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getDescription(), "Phidget Error" + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
            dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosed

    private void engagedChkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engagedChkActionPerformed
        try
        {
            servo.setEngaged(((Integer)servoCmb.getSelectedItem()).intValue(), engagedChk.isSelected());
        }
        catch(PhidgetException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getDescription(), "Phidget Error" + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_engagedChkActionPerformed

	private void servoTypeCmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_servoTypeCmbActionPerformed
	    if(servoTypeCmb.isEnabled())
	    {
	        try
	        {
	            int index = servoCmb.getSelectedIndex();
	            if(servo.getServoType(index) != servoTypes.get(servoTypeCmb.getSelectedItem()))
	            {
	                servo.setServoType(index, (Integer)servoTypes.get(servoTypeCmb.getSelectedItem()));
	                initAllValues();
	            }
	        }
	        catch(PhidgetException ex)
	        {
	            JOptionPane.showMessageDialog(this, ex.getDescription(), "Phidget Error" + ex.getErrorNumber(), JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}*///GEN-LAST:event_servoTypeCmbActionPerformed
    

	/*private void initAllValues()
	{
	    //Use a try-catch block around code where you are getting and displaying the servo position data
	    //if the current position state has yet to be set, it will throw a PhidgetException for value not set
	    //you can use this to test this and to display that the value is unknown
	    int index = this.servoCmb.getSelectedIndex();
	    
	    
	    try
	    {
	        positionScrl.setMinimum((int)servo.getPositionMin(index));
	        positionScrl.setMaximum((int)servo.getPositionMax(index));
	        positionTxt.setText(Double.toString(servo.getPosition(index)));
	        positionScrl.setValue((int)servo.getPosition(index));
	        engagedChk.setSelected(servo.getEngaged(index));
	        
	        String selectedType;
	        for(Enumeration e = servoTypes.keys(); e.hasMoreElements();)
	        {
	            selectedType = (String)e.nextElement();
	            if((Integer)servoTypes.get(selectedType) == servo.getServoType(index))
	                servoTypeCmb.setSelectedItem(selectedType);
	        }
	    }
	    catch(PhidgetException ex)
	    {
	        positionTxt.setText("Unknown");
	        engagedChk.setSelected(false);
	    }
	}
}*/
