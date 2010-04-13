#include <Timer.h>
#include "UBSense.h"

configuration UBSenseAppC {
}
implementation {
    components MainC,
        LedsC, UBSenseC as App, new TimerMilliC() as Timer0,
        ActiveMessageC, new AMSenderC(AM_UBSENSEMSG),
        new VoltageC() as Voltage,
        new PhotoC() as Light,
        new TempC() as Temperature,
        new MicC() as Microphone;
    
    App.Packet -> AMSenderC;
    App.AMPacket -> AMSenderC;
    App.AMSend -> AMSenderC;
    App.AMControl -> ActiveMessageC;
    App.Boot -> MainC;
    App.Leds -> LedsC;
    App.Timer0 -> Timer0;
    
    App.Voltage -> Voltage;
    App.Light -> Light;
    App.Temperature -> Temperature;
    App.Microphone -> Microphone;
}
