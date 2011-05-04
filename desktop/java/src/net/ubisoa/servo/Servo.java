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

	public Servo() {
		this.positionPost = 0;
		inicializaServo();		
	}
	
	public void position(double finP) {
		this.positionPost = finP;
		initServo();		
	}
	
	/**
     * initialize the servo object and hook the event listeners
     **/
    private void inicializaServo() {        
        try
        {
            this.phiServo = new ServoPhidget();
            System.out.println("0 " + phiServo.toString());
            /*this.phiServo.openAny();
            int cont = 0;
            while (!this.phiServo.isAttached()) {
            	this.phiServo.waitForAttachment();
            	System.out.println(cont + " " + phiServo.toString());
            	cont++;
            }
            this.phiServo.setPosition(0, this.positionPost);
            this.phiServo.close();*/
        }
        catch(PhidgetException ex) {
        	System.out.println("private void inicializaServo()");
        	System.out.println("Phidget Error " + ex.getErrorNumber() + ": " + ex.getDescription());
        }
    }
    
    public void initServo() {        
        try
        {
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
        	System.out.println("private void initServo()");
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