#include <Timer.h>
#include "UBSense.h"

configuration UBSenseAppC {
}
implementation {
    components MainC, LedsC, UBSenseC as App, new TimerMilliC() as Timer0;
    components ActiveMessageC, new AMSenderC(AM_UBSENSEMSG);
    components new VoltageC() as Voltage, new HamamatsuS10871TsrC() as Light, new HamamatsuS1087ParC() as LightVisible, new SensirionSht11C() as Sensirion, new Msp430InternalTemperatureC() as TemperatureInternal;
    
    App.Packet -> AMSenderC;
    App.AMPacket -> AMSenderC;
    App.AMSend -> AMSenderC;
    App.AMControl -> ActiveMessageC;
    App.Boot -> MainC;
    App.Leds -> LedsC;
    App.Timer0 -> Timer0;
    App.Voltage -> Voltage;
    App.Light -> Light;
    App.LightVisible -> LightVisible;
    App.Temperature -> Sensirion.Temperature;
    App.TemperatureInternal -> TemperatureInternal;
    App.Humidity -> Sensirion.Humidity;
}
