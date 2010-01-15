//
//  GeolocationWiFiSpotter.h
//  UbiSOA
//
//  Created by Edgardo on 18/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Constants.h"
#import <dlfcn.h>


@interface GeolocationWiFiSpotter : NSObject {
	void *libHandle;
	int (*open)(void *);
	int (*bind)(void *, NSString *);
	int (*close)(void *);
	int (*associate)(void *, NSDictionary*, NSString*);
	int (*scan)(void *, NSArray **, void *);
    CFArrayRef networks;
}

@property (nonatomic) CFArrayRef networks;

- (void)open;
- (void)close;
- (void)scan;
- (void)performScan;

@end
