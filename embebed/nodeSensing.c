/** This code has been modified from the original version (rest-server-example.c) available on Contiki platform.
*
* Class responsible for sensing the humidity and temperature. 
* The data is stored based on the package structure as MESSAGEUbiSOA message in the payload of CoAP package. 
* 
* Code adapted for particular purposes by:
* Franceli Linney Cibrian Roble - linney11@gmail.com
* Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com
**/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "contiki-net.h"
#include "rest.h"

#if defined (PLATFORM_HAS_LIGHT)
#include "dev/light-sensor.h"
#endif
#if defined (PLATFORM_HAS_BATT)
#include "dev/battery-sensor.h"
#endif
#if defined (PLATFORM_HAS_SHT11)
#include "dev/sht11-sensor.h"
#endif
#if defined (PLATFORM_HAS_LEDS)
#include "dev/leds.h"
#endif

#define DEBUG 1
#if DEBUG
#include <stdio.h>
#define PRINTF(...) printf(__VA_ARGS__)
#define PRINT6ADDR(addr) PRINTF(" %02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x ", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], ((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t *)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15])
#define PRINTLLADDR(lladdr) PRINTF(" %02x:%02x:%02x:%02x:%02x:%02x ",(lladdr)->addr[0], (lladdr)->addr[1], (lladdr)->addr[2], (lladdr)->addr[3],(lladdr)->addr[4], (lladdr)->addr[5])
#else
#define PRINTF(...)
#define PRINT6ADDR(addr)
#define PRINTLLADDR(addr)
#endif

char temp[100];
char* nodeId="13";
char* optionSensing;
uint16_t temperature;
uint16_t humidity;

//PAYLOAD MESSAGE UbiSOA
////////////////////////////////////////////////////
///          ///                ///              ///
///  NODEID  ///  OPTIONSENSING ///      DATA	 ///
/// (2 char) ///     (2 char)   ///    (5 char)  ///
///          ///                ///              ///
////////////////////////////////////////////////////


/* Resources are defined by RESOURCE macro, signature: resource name, the http methods it handles and its url*/
RESOURCE(temperature, METHOD_GET, "temperature");

/* For each resource defined, there corresponds an handler method which should be defined too.
 * Name of the handler method should be [resource name]_handler
 * */
void
temperature_handler(REQUEST* request, RESPONSE* response)
{
  optionSensing="01";
  sprintf(temp, "%s%s %u\n", nodeId, optionSensing, temperature);

  rest_set_header_content_type(response, TEXT_PLAIN);
  rest_set_response_payload(response, (uint8_t*)temp, strlen(temp));

}

RESOURCE(humidity, METHOD_GET, "humidity");
void
humidity_handler(REQUEST* request, RESPONSE* response)
{
  optionSensing="02";
  sprintf(temp, "%s%s %u\n", nodeId, optionSensing, humidity);

  rest_set_header_content_type(response, TEXT_PLAIN);
  rest_set_response_payload(response, (uint8_t*)temp, strlen(temp));
}

PROCESS(rest_server_example, "NodeSensing - UbiSOA");
AUTOSTART_PROCESSES(&rest_server_example);


PROCESS_THREAD(rest_server_example, ev, data)
{
  PROCESS_BEGIN();

  SENSORS_ACTIVATE(sht11_sensor);

#ifdef WITH_COAP
  PRINTF("NodeSensing On\n");
#else
  PRINTF("NodeSensing On - (HTTP)\n");
#endif

  rest_init();

  rest_activate_resource(&resource_temperature);
  rest_activate_resource(&resource_humidity);

//////////////////////////////
while(1) {
    static struct etimer et;
    etimer_set(&et, CLOCK_SECOND * 20);
    
    PROCESS_WAIT_EVENT();

    if(etimer_expired(&et)) {
      SENSORS_ACTIVATE(sht11_sensor);

      temperature = sht11_sensor.value(SHT11_SENSOR_TEMP);
      humidity = sht11_sensor.value(SHT11_SENSOR_HUMIDITY);

      //printf("%u %u\n", temperature, humidity);

      SENSORS_DEACTIVATE(sht11_sensor);
    }
  }
/////////////////////
  PROCESS_END();
}
