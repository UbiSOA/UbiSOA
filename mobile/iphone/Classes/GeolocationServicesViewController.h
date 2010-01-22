//
//  GeolocationViewController.h
//  UbiSOA
//
//  Created by Edgardo on 21/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Constants.h"
#import "GeolocationMapsViewController.h"


@interface GeolocationServicesViewController : UITableViewController {
	NSNetServiceBrowser *browser;
	NSMutableArray *services;
	UIAlertView *searchingAlert;
	bool searchingServices;
	UIView *hiddenHeader;
}

// Services related methods
- (IBAction)lookForServices:(id)sender;
- (void)willUseService:(int)serviceIndex animated:(BOOL)animate;

// NSNetServiceBrowser delegate methods for service browsing
- (void)netServiceBrowserWillSearch:(NSNetServiceBrowser *)browser;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didNotSearch:(NSDictionary *)errorDict;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didFindService:(NSNetService *)aNetService moreComing:(BOOL)moreComing;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didRemoveService:(NSNetService *)aNetService moreComing:(BOOL)moreComing;

// UIAlertView delegate methods
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;

@end
