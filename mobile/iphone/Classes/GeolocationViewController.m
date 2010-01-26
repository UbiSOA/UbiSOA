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
	[imageView release];
	
	indicatorCenter = CGPointMake(0.5, 0.5);
	
	shadow = [[UIImageView alloc] initWithImage:[self create:50]];
	shadow.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	[scrollView addSubview:shadow];
	[shadow release];
	
	// Configuring the location indicator
	indicator = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"IndicatorLocation.png"]];

	indicator.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	[scrollView addSubview:indicator];
	[indicator release];
	
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
    [super dealloc];
}

#pragma mark -
#pragma mark UIScrollView delegate methods

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
	[indicator setAlpha:0.0];
	[shadow setAlpha:0.0];
	return imageView;
}



- (void)scrollViewDidEndZooming:(UIScrollView *)scrollView withView:(UIView *)view atScale:(float)scale {
	indicator.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	shadow.center = indicator.center;

	[UIView beginAnimations:@"show" context:nil];
	[indicator setAlpha:1.0];
	[shadow setAlpha:1.0];
	[UIView commitAnimations];
}

- (void)tapIn:(CGPoint)point {
	NSLog(@"TAP IN %f,%f", point.x, point.y);
}

#pragma mark -
#pragma mark UISearchBar delegate methods

- (void)searchBar:(UISearchBar *)searchBar selectedScopeButtonIndexDidChange:(NSInteger)selectedScope {
	mode = selectedScope;
	[self setStatusText:@"Double tap on the map"];
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
#pragma mark Common methods

- (void)animateEstimationButton:(BOOL)animate andDisableIt:(BOOL)disable {
	UIBarButtonItem *button = [[(UIToolbar *)[self.view viewWithTag:1] items] objectAtIndex:0];
	[button setEnabled:!disable];
	[button setImage:(animate)? nil: [UIImage imageNamed:@"BarIconLocation.png"]];
	if (animate) [button setWidth:37];
	[button setStyle:(animate)? UIBarButtonItemStyleDone: UIBarButtonItemStyleBordered];
	UIActivityIndicatorView *act = (UIActivityIndicatorView *)[self.view viewWithTag:2];
	if (animate) [act startAnimating]; else [act stopAnimating];
}

- (void)setStatusText:(NSString *)newStatus {
	UILabel *label = (UILabel *)[self.view viewWithTag:3];
	[label setText:newStatus];
}

#pragma mark -
#pragma mark Estimate current location

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
		[self setStatusText:[request responseString]];
	} else {
		// TO DO: Show current location in GUI
	}
	
	[UIView beginAnimations:@"im" context:nil];
	[shadow setImage:[self create:100]];
	[shadow setFrame:CGRectMake(indicatorCenter.x * imageView.frame.size.width - 50, indicatorCenter.y * imageView.frame.size.height - 50, 100, 100)];
	[UIView commitAnimations];
	
	[self animateEstimationButton:NO andDisableIt:NO];	
	action = 0;
}

- (UIImage *)create:(float)width {
	UIGraphicsBeginImageContext(CGSizeMake(width + 2, width + 2));
	CGContextRef context = UIGraphicsGetCurrentContext();
	
	CGContextSetFillColorWithColor(context, [[kShadowColor colorWithAlphaComponent:0.2] CGColor]);
	CGContextAddEllipseInRect(context, CGRectMake(1, 1, width, width));
	CGContextFillPath(context);
	
	CGContextSetLineWidth(context, 2);
	CGContextSetStrokeColorWithColor(context, [[kShadowColor colorWithAlphaComponent:0.5] CGColor]);
	CGContextAddEllipseInRect(context, CGRectMake(2, 2, width - 2, width - 2));
	CGContextStrokePath(context);
	
	UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	return theImage;
}

@end
