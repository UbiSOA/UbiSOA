//
//  GeolocationLogViewController.m
//  UbiSOA
//
//  Created by Edgardo on 28/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationLogViewController.h"


@implementation GeolocationLogViewController
@synthesize textView, mapName;


- (void)dealloc {
	[textView release];
	[mapName release];
	textView.tran
	[super dealloc];
}

- (IBAction)clear:(id)sender {
	self.textView.text = @"";
	NSLog(@"%@", mapName);
}

- (IBAction)export:(id)sender {
	MFMailComposeViewController *mcvc = [[[MFMailComposeViewController alloc] init] autorelease];
	mcvc.mailComposeDelegate = self;
	[mcvc setSubject:@"Geolocation Log"];
	[mcvc setMessageBody:[NSString stringWithFormat:@"Attached you will find the log of the geolocation service using the map \"%@\".", self.mapName] isHTML:NO];
	[mcvc addAttachmentData:[self.textView.text dataUsingEncoding:NSUTF8StringEncoding] mimeType:@"plain/text" fileName:@"ub-geolocation.log"];
	[self presentModalViewController:mcvc animated:YES];
}

- (void)mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError *)error {
	[self dismissModalViewControllerAnimated:YES];
}

@end
