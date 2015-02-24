/** This code has been modified from the original version (rest-client-example.c) available on Contiki platform.
*
* Class responsible for requesting data from constant  sensed by the nodes. 
* This particular case has been configured to get connection to a specified node specified on variable SERVER_NODE.
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
#include "buffer.h"

#define DEBUG 1
#if DEBUG
#include <stdio.h>
#define PRINTF(...) printf(__VA_ARGS__)
#define PRINT6ADDR(addr) PRINTF("%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x:%02x%02x", ((uint8_t *)addr)[0], ((uint8_t *)addr)[1], ((uint8_t *)addr)[2], ((uint8_t *)addr)[3], ((uint8_t *)addr)[4], ((uint8_t *)addr)[5], ((uint8_t *)addr)[6], ((uint8_t *)addr)[7], ((uint8_t *)addr)[8], ((uint8_t *)addr)[9], ((uint8_t *)addr)[10], ((uint8_t *)addr)[11], ((uint8_t *)addr)[12], ((uint8_t *)addr)[13], ((uint8_t *)addr)[14], ((uint8_t *)addr)[15])
#define PRINTLLADDR(lladdr) PRINTF(" %02x:%02x:%02x:%02x:%02x:%02x ",(lladdr)->addr[0], (lladdr)->addr[1], (lladdr)->addr[2], (lladdr)->addr[3],(lladdr)->addr[4], (lladdr)->addr[5])
#else
#define PRINTF(...)
#define PRINT6ADDR(addr)
#define PRINTLLADDR(addr)
#endif

/*IPv6 address from nodeSon node*/
#define SERVER_NODE(ipaddr)   uip_ip6addr(ipaddr, 0xfe80, 0, 0, 0, 0x0212, 0x7400, 0x1024, 0x1b1f)

#define LOCAL_PORT 61617
#define REMOTE_PORT 61616

char temp[100];
int xact_id;
static uip_ipaddr_t server_ipaddr;
static struct uip_udp_conn *client_conn;
static struct etimer et;
#define MAX_PAYLOAD_LEN   100

#define NUMBER_OF_URLS 2
char* service_urls[NUMBER_OF_URLS] = {"temperature", "humidity"};

static void
response_handler(coap_packet_t* response)
{
  uint16_t payload_len = 0;
  uint8_t* payload = NULL;
  payload_len = coap_get_payload(response, &payload);

  //PRINTF("Response transaction id: %u", response->tid);
  if (payload) {
    memcpy(temp, payload, payload_len);
    temp[payload_len] = 0;
    //UbiSOA package
    PRINTF("##%s\n", temp);
  }
}

static void
send_data(void)
{
  char buf[MAX_PAYLOAD_LEN];

  if (init_buffer(COAP_DATA_BUFF_SIZE)) {
    int data_size = 0;
    int service_id = random_rand() % NUMBER_OF_URLS;
    coap_packet_t* request = (coap_packet_t*)allocate_buffer(sizeof(coap_packet_t));
    init_packet(request);

    coap_set_method(request, COAP_GET);
    request->tid = xact_id++;
    request->type = MESSAGE_TYPE_CON;
    coap_set_header_uri(request, service_urls[service_id]);

    data_size = serialize_packet(request, buf);
    
    //PRINTF("Sending request to NodeSensing");
    //PRINTF("/%s\n", service_urls[service_id]);
    uip_udp_packet_send(client_conn, buf, data_size);

    delete_buffer();
  }
}

static void
handle_incoming_data()
{
  //PRINTF("Incoming packet size: %u \n", (uint16_t)uip_datalen());
  if (init_buffer(COAP_DATA_BUFF_SIZE)) {
    if (uip_newdata()) {
      coap_packet_t* response = (coap_packet_t*)allocate_buffer(sizeof(coap_packet_t));
      if (response) {
        parse_message(response, uip_appdata, uip_datalen());
        response_handler(response);
      }
    }
    delete_buffer();
  }
}

PROCESS(coap_client_example, "NodeBorder - UbiSOA");
AUTOSTART_PROCESSES(&coap_client_example);

PROCESS_THREAD(coap_client_example, ev, data)
{
  PROCESS_BEGIN();

  SERVER_NODE(&server_ipaddr);

  /* new connection with nodeSon */
  client_conn = udp_new(&server_ipaddr, UIP_HTONS(REMOTE_PORT), NULL);
  udp_bind(client_conn, UIP_HTONS(LOCAL_PORT));

  PRINTF("Created a connection with NodeSensing ");
  PRINT6ADDR(&client_conn->ripaddr);
  PRINTF(" local/remote port %u/%u\n",
  UIP_HTONS(client_conn->lport), UIP_HTONS(client_conn->rport));

  etimer_set(&et, 5 * CLOCK_SECOND);
  while(1) {
    PROCESS_YIELD();
    if (etimer_expired(&et)) {
      send_data();
      etimer_reset(&et);
    } else if (ev == tcpip_event) {
      handle_incoming_data();
    }
  }

  PROCESS_END();
}
