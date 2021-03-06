/*
 * Copyright (c) 2010, Edgardo Avilés-López <edgardo@ubisoa.net>
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

/*
 * This class has not been modified from its original version written by 
 * Edgardo Avilés-López <edgardo@ubisoa.net>, however, it is used 
 * to collect and read the messages received by the Motes.
 * 
 * Code adapted for particular purposes by:
 * Franceli Linney Cibrian Roble - linney11@gmail.com
 * Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
 *****************************************************************************/

package src.net.ubisoa.coap.sensing;

/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'UBSenseMsg'
 * message type.
 */

public class SensingMessage extends net.tinyos.message.Message {

	/** The default size of this message type in bytes. */
	public static final int DEFAULT_MESSAGE_SIZE = 20;

	/** The Active Message type associated with this message. */
	public static final int AM_TYPE = 225;

	/** Create a new SensingMessage of size 20. */
	public SensingMessage() {
		super(DEFAULT_MESSAGE_SIZE);
		amTypeSet(AM_TYPE);
	}

	/** Create a new SensingMessage of the given data_length. */
	public SensingMessage(int data_length) {
		super(data_length);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage with the given data_length
	 * and base offset.
	 */
	public SensingMessage(int data_length, int base_offset) {
		super(data_length, base_offset);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage using the given byte array
	 * as backing store.
	 */
	public SensingMessage(byte[] data) {
		super(data);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage using the given byte array
	 * as backing store, with the given base offset.
	 */
	public SensingMessage(byte[] data, int base_offset) {
		super(data, base_offset);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage using the given byte array
	 * as backing store, with the given base offset and data length.
	 */
	public SensingMessage(byte[] data, int base_offset, int data_length) {
		super(data, base_offset, data_length);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage embedded in the given message
	 * at the given base offset.
	 */
	public SensingMessage(net.tinyos.message.Message msg, int base_offset) {
		super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
		amTypeSet(AM_TYPE);
	}

	/**
	 * Create a new SensingMessage embedded in the given message
	 * at the given base offset and length.
	 */
	public SensingMessage(net.tinyos.message.Message msg, int base_offset, int data_length) {
		super(msg, base_offset, data_length);
		amTypeSet(AM_TYPE);
	}

	/**
	    /* Return a String representation of this message. Includes the
	 * message type name and the non-indexed field values.
	 */
	public String toString() {
		String s = "Message <SensingMessage> \n";
		try {
			s += "  [nid=0x"+Long.toHexString(get_nid())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [platform=0x"+Long.toHexString(get_platform())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [voltage=0x"+Long.toHexString(get_voltage())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [light=0x"+Long.toHexString(get_light())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [light_visible=0x"+Long.toHexString(get_light_visible())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [temperature=0x"+Long.toHexString(get_temperature())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [temperature_internal=0x"+Long.toHexString(get_temperature_internal())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [humidity=0x"+Long.toHexString(get_humidity())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [microphone=0x"+Long.toHexString(get_microphone())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		try {
			s += "  [counter=0x"+Long.toHexString(get_counter())+"]\n";
		} catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
		return s;
	}

	// Message-type-specific access methods appear below.

	/////////////////////////////////////////////////////////
	// Accessor methods for field: nid
	//   Field type: int, unsigned
	//   Offset (bits): 0
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'nid' is signed (false).
	 */
	public static boolean isSigned_nid() {
		return false;
	}

	/**
	 * Return whether the field 'nid' is an array (false).
	 */
	public static boolean isArray_nid() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'nid'
	 */
	public static int offset_nid() {
		return (0 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'nid'
	 */
	public static int offsetBits_nid() {
		return 0;
	}

	/**
	 * Return the value (as a int) of the field 'nid'
	 */
	public int get_nid() {
		return (int)getUIntBEElement(offsetBits_nid(), 16);
	}

	/**
	 * Set the value of the field 'nid'
	 */
	public void set_nid(int value) {
		setUIntBEElement(offsetBits_nid(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'nid'
	 */
	public static int size_nid() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'nid'
	 */
	public static int sizeBits_nid() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: platform
	//   Field type: int, unsigned
	//   Offset (bits): 16
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'platform' is signed (false).
	 */
	public static boolean isSigned_platform() {
		return false;
	}

	/**
	 * Return whether the field 'platform' is an array (false).
	 */
	public static boolean isArray_platform() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'platform'
	 */
	public static int offset_platform() {
		return (16 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'platform'
	 */
	public static int offsetBits_platform() {
		return 16;
	}

	/**
	 * Return the value (as a int) of the field 'platform'
	 */
	public int get_platform() {
		return (int)getUIntBEElement(offsetBits_platform(), 16);
	}

	/**
	 * Set the value of the field 'platform'
	 */
	public void set_platform(int value) {
		setUIntBEElement(offsetBits_platform(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'platform'
	 */
	public static int size_platform() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'platform'
	 */
	public static int sizeBits_platform() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: voltage
	//   Field type: int, unsigned
	//   Offset (bits): 32
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'voltage' is signed (false).
	 */
	public static boolean isSigned_voltage() {
		return false;
	}

	/**
	 * Return whether the field 'voltage' is an array (false).
	 */
	public static boolean isArray_voltage() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'voltage'
	 */
	public static int offset_voltage() {
		return (32 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'voltage'
	 */
	public static int offsetBits_voltage() {
		return 32;
	}

	/**
	 * Return the value (as a int) of the field 'voltage'
	 */
	public int get_voltage() {
		return (int)getUIntBEElement(offsetBits_voltage(), 16);
	}

	/**
	 * Set the value of the field 'voltage'
	 */
	public void set_voltage(int value) {
		setUIntBEElement(offsetBits_voltage(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'voltage'
	 */
	public static int size_voltage() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'voltage'
	 */
	public static int sizeBits_voltage() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: light
	//   Field type: int, unsigned
	//   Offset (bits): 48
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'light' is signed (false).
	 */
	public static boolean isSigned_light() {
		return false;
	}

	/**
	 * Return whether the field 'light' is an array (false).
	 */
	public static boolean isArray_light() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'light'
	 */
	public static int offset_light() {
		return (48 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'light'
	 */
	public static int offsetBits_light() {
		return 48;
	}

	/**
	 * Return the value (as a int) of the field 'light'
	 */
	public int get_light() {
		return (int)getUIntBEElement(offsetBits_light(), 16);
	}

	/**
	 * Set the value of the field 'light'
	 */
	public void set_light(int value) {
		setUIntBEElement(offsetBits_light(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'light'
	 */
	public static int size_light() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'light'
	 */
	public static int sizeBits_light() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: light_visible
	//   Field type: int, unsigned
	//   Offset (bits): 64
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'light_visible' is signed (false).
	 */
	public static boolean isSigned_light_visible() {
		return false;
	}

	/**
	 * Return whether the field 'light_visible' is an array (false).
	 */
	public static boolean isArray_light_visible() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'light_visible'
	 */
	public static int offset_light_visible() {
		return (64 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'light_visible'
	 */
	public static int offsetBits_light_visible() {
		return 64;
	}

	/**
	 * Return the value (as a int) of the field 'light_visible'
	 */
	public int get_light_visible() {
		return (int)getUIntBEElement(offsetBits_light_visible(), 16);
	}

	/**
	 * Set the value of the field 'light_visible'
	 */
	public void set_light_visible(int value) {
		setUIntBEElement(offsetBits_light_visible(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'light_visible'
	 */
	public static int size_light_visible() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'light_visible'
	 */
	public static int sizeBits_light_visible() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: temperature
	//   Field type: int, unsigned
	//   Offset (bits): 80
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'temperature' is signed (false).
	 */
	public static boolean isSigned_temperature() {
		return false;
	}

	/**
	 * Return whether the field 'temperature' is an array (false).
	 */
	public static boolean isArray_temperature() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'temperature'
	 */
	public static int offset_temperature() {
		return (80 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'temperature'
	 */
	public static int offsetBits_temperature() {
		return 80;
	}

	/**
	 * Return the value (as a int) of the field 'temperature'
	 */
	public int get_temperature() {
		return (int)getUIntBEElement(offsetBits_temperature(), 16);
	}

	/**
	 * Set the value of the field 'temperature'
	 */
	public void set_temperature(int value) {
		setUIntBEElement(offsetBits_temperature(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'temperature'
	 */
	public static int size_temperature() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'temperature'
	 */
	public static int sizeBits_temperature() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: temperature_internal
	//   Field type: int, unsigned
	//   Offset (bits): 96
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'temperature_internal' is signed (false).
	 */
	public static boolean isSigned_temperature_internal() {
		return false;
	}

	/**
	 * Return whether the field 'temperature_internal' is an array (false).
	 */
	public static boolean isArray_temperature_internal() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'temperature_internal'
	 */
	public static int offset_temperature_internal() {
		return (96 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'temperature_internal'
	 */
	public static int offsetBits_temperature_internal() {
		return 96;
	}

	/**
	 * Return the value (as a int) of the field 'temperature_internal'
	 */
	public int get_temperature_internal() {
		return (int)getUIntBEElement(offsetBits_temperature_internal(), 16);
	}

	/**
	 * Set the value of the field 'temperature_internal'
	 */
	public void set_temperature_internal(int value) {
		setUIntBEElement(offsetBits_temperature_internal(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'temperature_internal'
	 */
	public static int size_temperature_internal() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'temperature_internal'
	 */
	public static int sizeBits_temperature_internal() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: humidity
	//   Field type: int, unsigned
	//   Offset (bits): 112
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'humidity' is signed (false).
	 */
	public static boolean isSigned_humidity() {
		return false;
	}

	/**
	 * Return whether the field 'humidity' is an array (false).
	 */
	public static boolean isArray_humidity() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'humidity'
	 */
	public static int offset_humidity() {
		return (112 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'humidity'
	 */
	public static int offsetBits_humidity() {
		return 112;
	}

	/**
	 * Return the value (as a int) of the field 'humidity'
	 */
	public int get_humidity() {
		return (int)getUIntBEElement(offsetBits_humidity(), 16);
	}

	/**
	 * Set the value of the field 'humidity'
	 */
	public void set_humidity(int value) {
		setUIntBEElement(offsetBits_humidity(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'humidity'
	 */
	public static int size_humidity() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'humidity'
	 */
	public static int sizeBits_humidity() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: microphone
	//   Field type: int, unsigned
	//   Offset (bits): 128
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'microphone' is signed (false).
	 */
	public static boolean isSigned_microphone() {
		return false;
	}

	/**
	 * Return whether the field 'microphone' is an array (false).
	 */
	public static boolean isArray_microphone() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'microphone'
	 */
	public static int offset_microphone() {
		return (128 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'microphone'
	 */
	public static int offsetBits_microphone() {
		return 128;
	}

	/**
	 * Return the value (as a int) of the field 'microphone'
	 */
	public int get_microphone() {
		return (int)getUIntBEElement(offsetBits_microphone(), 16);
	}

	/**
	 * Set the value of the field 'microphone'
	 */
	public void set_microphone(int value) {
		setUIntBEElement(offsetBits_microphone(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'microphone'
	 */
	public static int size_microphone() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'microphone'
	 */
	public static int sizeBits_microphone() {
		return 16;
	}

	/////////////////////////////////////////////////////////
	// Accessor methods for field: counter
	//   Field type: int, unsigned
	//   Offset (bits): 144
	//   Size (bits): 16
	/////////////////////////////////////////////////////////

	/**
	 * Return whether the field 'counter' is signed (false).
	 */
	public static boolean isSigned_counter() {
		return false;
	}

	/**
	 * Return whether the field 'counter' is an array (false).
	 */
	public static boolean isArray_counter() {
		return false;
	}

	/**
	 * Return the offset (in bytes) of the field 'counter'
	 */
	public static int offset_counter() {
		return (144 / 8);
	}

	/**
	 * Return the offset (in bits) of the field 'counter'
	 */
	public static int offsetBits_counter() {
		return 144;
	}

	/**
	 * Return the value (as a int) of the field 'counter'
	 */
	public int get_counter() {
		return (int)getUIntBEElement(offsetBits_counter(), 16);
	}

	/**
	 * Set the value of the field 'counter'
	 */
	public void set_counter(int value) {
		setUIntBEElement(offsetBits_counter(), 16, value);
	}

	/**
	 * Return the size, in bytes, of the field 'counter'
	 */
	public static int size_counter() {
		return (16 / 8);
	}

	/**
	 * Return the size, in bits, of the field 'counter'
	 */
	public static int sizeBits_counter() {
		return 16;
	}

}
