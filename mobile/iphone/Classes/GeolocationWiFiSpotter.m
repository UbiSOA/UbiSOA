//
//  GeolocationWiFiSpotter.m
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationWiFiSpotter.h"


@implementation GeolocationWiFiSpotter
@synthesize delegate;
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
	NSAutoreleasePool *pool = [[NSAutoreleasePool alloc] init];
#if !TARGET_IPHONE_SIMULATOR
	CFDictionaryRef parameters = CFDictionaryCreateMutable(NULL, 0, &kCFTypeDictionaryKeyCallBacks, &kCFTypeDictionaryValueCallBacks);
    scan(libHandle, &networks, parameters);
#else
	[NSThread sleepForTimeInterval:kSimulatedWiFiSpotterDelay];
#endif
	if (self.delegate != nil && [self.delegate respondsToSelector:@selector(spotterDidScan)])
[self.delegate spotterDidScan];
	else NSLog(@"SPOTTER TRIED TO UPDATE RELEASED OBJECT!");
	[pool release];
}

- (NSString *)signalData {
#if TARGET_IPHONE_SIMULATOR
	return kSimulatedSignalData;
#endif
	NSString *signalData = @"";
	if (networks != nil)
		for (int i = 0, n = CFArrayGetCount(networks); i < n; i++) {
			CFDictionaryRef network = CFArrayGetValueAtIndex(networks, i);
			signalData = [signalData stringByAppendingFormat:@"%@=%@,", CFDictionaryGetValue(network, @"BSSID"), CFDictionaryGetValue(network, @"RSSI")];
		}
	if ([signalData length] > 0)
		signalData = [signalData substringToIndex:[signalData length] - 1];
	return signalData;
}

- (void)dealloc {
	[self close];
	[delegate release];
	[super dealloc];
}

@end
