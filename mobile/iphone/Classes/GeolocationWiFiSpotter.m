//
//  GeolocationWiFiSpotter.m
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationWiFiSpotter.h"


@implementation GeolocationWiFiSpotter
static GeolocationWiFiSpotter *sharedInstance;


+ (GeolocationWiFiSpotter *)sharedInstance {
	if (!sharedInstance) sharedInstance = [[self alloc] init];
	return sharedInstance;
}

- (id)init {
	self = [super init];
	libHandle = dlopen("/System/Library/SystemConfiguration/WiFiManager.bundle/WiFiManager", RTLD_LAZY);
	open  = dlsym(libHandle, "Apple80211Open");
	bind  = dlsym(libHandle, "Apple80211BindToInterface");
	close = dlsym(libHandle, "Apple80211Close");
	assoc = dlsym(libHandle, "Apple80211Associate");
	scan  = dlsym(libHandle, "Apple80211Scan");
	
	#if !TARGET_IPHONE_SIMULATOR
	open(&libHandle);
	bind(libHandle, @"en0");
	#endif
	return self;
	
	delegates = [[NSMutableDictionary alloc] init];
}

- (void)close {
#if !TARGET_IPHONE_SIMULATOR
	close(libHandle);
	dlclose(libHandle);
#endif
}

- (void)scan {
	[NSThread detachNewThreadSelector:@selector(performScan) toTarget:self withObject:nil];
}

- (void)performScan {
#if !TARGET_IPHONE_SIMULATOR
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	CFDictionaryRef parameters = CFDictionaryCreateMutable(NULL, 0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    scan(libHandle, &networks, parameters);
	[pool release];
#endif
	for (id<GeolocationWiFiSpotterDelegate> object in delegates)
		if (object != nil) [object spotterDidScan:networks];
}

- (void)addDelegate:(id<GeolocationWiFiSpotterDelegate>)object {
	[delegates addObject:object];
}

- (void)dealloc {
	[self close];
	[delegates release];
	[super dealloc];
}

@end
