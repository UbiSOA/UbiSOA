#include <Timer.h>
#include "UBSense.h"

configuration UBSenseAppC {
}
implementation {
    components MainC, LedsC, UBSenseC as App, new TimerMilliC() as Timer0;
    components ActiveMessageC, new AMSenderC(AM_UBSENSEMSG);
    components new VoltageC() as Voltage;
    
    App.Packet -> AMSenderC;
    App.AMPacket -> AMSenderC;
    App.AMSend -> AMSenderC;
    App.AMControl -> ActiveMessageC;
    App.Boot -> MainC;
    App.Leds -> LedsC;
    App.Timer0 -> Timer0;
    App.Voltage -> Voltage;
}
