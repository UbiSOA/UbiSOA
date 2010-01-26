//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"


@implementation GeolocationViewController
@synthesize service, map, scrollView;


- (void)viewDidLoad {
    [super viewDidLoad];
	
	// Load map image to the scroll.
	imageView = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@", map.file]]];
	scrollView.contentSize = CGSizeMake(imageView.frame.size.width, imageView.frame.size.height);
	scrollView.maximumZoomScale = 2.0;
	scrollView.minimumZoomScale = (imageView.frame.size.width < imageView.frame.size.height)?
	scrollView.frame.size.width / imageView.frame.size.width:
	scrollView.frame.size.height / imageView.frame.size.height;
	scrollView.clipsToBounds = YES;
	scrollView.delegate = self;
	[scrollView addSubview:imageView];
	[scrollView setZoomScale:scrollView.minimumZoomScale];
	
	// Configuring WiFi spotter
	[[GeolocationWiFiSpotter sharedInstance] setDelegate:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc {
	[[GeolocationWiFiSpotter sharedInstance] setDelegate:nil];
	[service release];
	[map release];
	[scrollView release];
	[imageView release];
    [super dealloc];
}

#pragma mark -
#pragma mark UIScrollView delegate methods

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
	return imageView;
}

#pragma mark -
#pragma mark GeolocationWiFiSpotter delegate methods

- (void)spotterDidScan {
	switch (action) {
		case UBLocateGeolocationActionType:
			[self performEstimationOfCurrentLocation];
			break;
	}
}

#pragma mark -
#pragma mark Estimate current location

- (void)animateEstimationButton:(BOOL)animate andDisableIt:(BOOL)disable {
	UIBarButtonItem *button = [[(UIToolbar *)[self.view viewWithTag:1] items] objectAtIndex:0];
	[button setEnabled:!disable];
	[button setImage:(animate)? nil: [UIImage imageNamed:@"BarIconLocation.png"]];
	if (animate) [button setWidth:37];
	[button setStyle:(animate)? UIBarButtonItemStyleDone: UIBarButtonItemStyleBordered];
	UIActivityIndicatorView *act = (UIActivityIndicatorView *)[self.view viewWithTag:2];
	if (animate) [act startAnimating]; else [act stopAnimating];
}

- (IBAction)estimateCurrentLocation:(id)sender {
	if (action == UBLocateGeolocationActionType) {
		[self animateEstimationButton:NO andDisableIt:NO];
		action = 0;
		return;
	}
	
	if (action > 0) return; action = UBLocateGeolocationActionType;
	
	[[GeolocationWiFiSpotter sharedInstance] scan];
	[self animateEstimationButton:YES andDisableIt:NO];
}

- (void)performEstimationOfCurrentLocation {
	if (action != UBLocateGeolocationActionType) return;
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	
 	NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"http://%@:%d/%@/%@", [self.service hostName], [self.service port], [[GeolocationWiFiSpotter sharedInstance] signalData], [[UIDevice currentDevice] model]] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
	ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
	[request start];

	NSLog(@"%@", [request responseHeaders]);
	NSLog(@"Status Code: %d, Status Message: %@", [request responseStatusCode], [request responseStatusMessage]);
	NSLog(@"%@", [request responseString]);
	
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
	
	if ([request responseStatusCode] != 200) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Request Error" message:[request responseString] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
	} else {
		// TO DO: Show current location in GUI
	}
	
	[self animateEstimationButton:NO andDisableIt:NO];	
	action = 0;
}

@end
