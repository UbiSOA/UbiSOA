# This code shows a modification to the original version written by available in Contiki platform
#
# Configuring the Makefile to compile correctly and nodeSensing nodeBorder files
#
# Code adapted for particular purposes by:
# Franceli Linney Cibrian Roble - linney11@gmail.com
# Netzahualcoyotl Hernandez Cruz - netzahdzc@gmail.com

all: rest-server-example coap-client-example nodeBorder nodeSensing


ifndef TARGET
TARGET=sky
endif

CONTIKI=../..

WITH_UIP6=1
UIP_CONF_IPV6=1

WITH_COAP = 1

CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"

ifeq ($(WITH_COAP), 1)
CFLAGS += -DWITH_COAP
APPS += rest-coap
else
CFLAGS += -DWITH_HTTP
APPS += rest-http
endif

include $(CONTIKI)/Makefile.include

$(CONTIKI)/tools/tunslip6:	$(CONTIKI)/tools/tunslip6.c
	(cd $(CONTIKI)/tools && $(MAKE) tunslip6)

connect-router:	$(CONTIKI)/tools/tunslip6
	sudo $(CONTIKI)/tools/tunslip6 aaaa::1/64

connect-router-cooja:	$(CONTIKI)/tools/tunslip6
	sudo $(CONTIKI)/tools/tunslip6 -a 127.0.0.1 aaaa::1/64


