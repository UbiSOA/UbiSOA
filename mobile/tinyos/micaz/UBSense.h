#ifndef UBSENSE_H
#define UBSENSE_H

enum {
    HW_TELOSB = 0xA,
    HW_MICAZ = 0xB,
    AM_UBSENSEMSG = 0xE1,
    TIMER_PERIOD_MILLI = 2000
};

#define HW_PLATFORM HW_MICAZ

typedef nx_struct UBSenseMsg {
    nx_uint16_t nid;
    nx_uint16_t platform;
    nx_uint16_t voltage;
    nx_uint16_t light;
    nx_uint16_t light_visible;
    nx_uint16_t temperature;
    nx_uint16_t temperature_internal;
    nx_uint16_t humidity;
    nx_uint16_t microphone;
    nx_uint16_t counter;
} UBSenseMsg;

#endif
