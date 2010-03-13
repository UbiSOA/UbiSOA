#include <Timer.h>
#include "UBSense.h"

module UBSenseC {
    uses interface Boot;
    uses interface Leds;
    uses interface Timer<TMilli> as Timer0;
    uses interface Packet;
    uses interface AMPacket;
    uses interface AMSend;
    uses interface SplitControl as AMControl;
    uses interface Read<uint16_t> as Voltage;
    uses interface Read<uint16_t> as Light;
    uses interface Read<uint16_t> as LightVisible;
    uses interface Read<uint16_t> as Temperature;
    uses interface Read<uint16_t> as TemperatureInternal;
    uses interface Read<uint16_t> as Humidity;
}
implementation {
    message_t pkt;
    uint16_t counter = 0;
    uint16_t v0, v1, v2, v3, v4, v5;
    bool busy = FALSE;
    
    event void Boot.booted() {
        call AMControl.start();
    }

    event void AMControl.startDone(error_t err) {
        if (err == SUCCESS)
            call Timer0.startPeriodic(TIMER_PERIOD_MILLI);
        else call AMControl.start();
    }
    
    event void AMControl.stopDone(error_t err) {
    }
    
    void sendMessage() {
        if (!busy) {
            UBSenseMsg *m = (UBSenseMsg *)(call Packet.getPayload(&pkt, sizeof(UBSenseMsg)));
            m -> nid = TOS_NODE_ID;
            m -> platform = HW_PLATFORM;
            m -> voltage = v0;
            m -> light = v1;
            m -> light_visible = v2;
            m -> temperature = v3;
            m -> temperature_internal = v4;
            m -> humidity = v5;
            m -> counter = counter++;
            if (call AMSend.send(AM_BROADCAST_ADDR, &pkt, sizeof(UBSenseMsg)) == SUCCESS) {
                busy = TRUE;
                call Leds.led2Toggle();
            }
        }
    }
    
    event void Timer0.fired() {
        call Leds.led1On();
        call Leds.led0Off();
        call Voltage.read();
    }
    
    event void Voltage.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v0 = data;
            call Light.read();
        } else call Leds.led0On();
    }
    
    event void Light.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v1 = data;
            call LightVisible.read();
        } else call Leds.led0On();
    }
    
    event void LightVisible.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v2 = data;
            call Temperature.read();
        } else call Leds.led0On();
    }
    
    event void Temperature.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v3 = data;
            call TemperatureInternal.read();
        } else call Leds.led0On();
    }
    
    event void TemperatureInternal.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v4 = data;
            call Humidity.read();
        } else call Leds.led0On();
    }
    
    event void Humidity.readDone(error_t result, uint16_t data) {
        if (result == SUCCESS) {
            v5 = data;
            sendMessage();
        } else call Leds.led0On();
    }
    
    event void AMSend.sendDone(message_t* msg, error_t error) {
        if (&pkt == msg) {
            call Leds.led1Off();
            busy = FALSE;
        }
    }
}
