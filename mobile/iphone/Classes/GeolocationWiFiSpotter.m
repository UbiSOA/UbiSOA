//
//  GeolocationWiFiSpotter.m
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationWiFiSpotter.h"


@implementation GeolocationWiFiSpotter
@synthesize networks;

- (void)open {
	libHandle = dlopen("/System/Library/SystemConfiguration/WiFiManager.bundle/WiFiManager", RTLD_LAZY);
	open  = dlsym(libHandle, "Apple80211Open");
	bind  = dlsym(libHandle, "Apple80211BindToInterface");
	close = dlsym(libHandle, "Apple80211Close");
	associate = dlsym(libHandle, "Apple80211Associate");
	scan  = dlsym(libHandle, "Apple80211Scan");
	
	open(&libHandle);
	bind(libHandle, @"en0");
}

- (void)close {
	close(libHandle);
	dlclose(libHandle);
}

- (void)scan {
	[NSThread detachNewThreadSelector:@selector(performScan) toTarget:self withObject:nil];
}

- (void)performScan {
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
	
	CFDictionaryRef parameters = CFDictionaryCreateMutable(NULL, 0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    scan(libHandle, &networks, parameters);
	
	[[NSNotificationCenter defaultCenter] postNotificationName:kSpotterNotif object:self];
	
	[pool release];
}

@end
