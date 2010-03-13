#ifndef UBSENSE_H
#define UBSENSE_H

enum {
    HW_TELOSB = 100,
    HW_MICAZ = 101,
    AM_UBSENSEMSG = 237,
    TIMER_PERIOD_MILLI = 1000
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
