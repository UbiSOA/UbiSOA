//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 15/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"

@implementation GeolocationViewController
@synthesize scrollView, noServicesView, selectedMap;

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
	[imageTrainDot setAlpha:0.0];
	return imageView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	
	imageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:kMapFile]];
	
	scrollView.contentSize = CGSizeMake(imageView.frame.size.width, imageView.frame.size.height);
	scrollView.maximumZoomScale = 2.0;
	scrollView.minimumZoomScale = (imageView.frame.size.width < imageView.frame.size.height)?
		scrollView.frame.size.width / imageView.frame.size.width:
		scrollView.frame.size.height / imageView.frame.size.height;
	scrollView.clipsToBounds = YES;
	scrollView.delegate = self;
	[scrollView addSubview:imageView];
	[scrollView setZoomScale:scrollView.minimumZoomScale];
	
	imageTrainDot = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ico-traindot.png"]];
	[scrollView addSubview:imageTrainDot];
	[imageTrainDot setAlpha:0.0];
	
	activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
	activityView.hidesWhenStopped = YES;
	[scrollView addSubview:activityView];
	
	spotter = [[GeolocationWiFiSpotter alloc] init];
	[spotter open];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(scanDone) name:kSpotterNotif object:nil];
	
	mode = UBTrainingGeolocationMode;
	
	services = [[NSMutableArray alloc] init];
	searchingServices = NO;
	
	[self lookForServices:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
}

- (void)dealloc {
	[services release];
	[browser release];
	[imageView release];
	[imageTrainDot release];
	[activityView release];
	[scrollView release];
	[spotter close];
	[spotter release];
	[noServicesView release];
	[selectedMap release];
    [super dealloc];
}

- (void)tapIn:(CGPoint)point {	
	if (mode == UBTrainingGeolocationMode) {
		lastPoint = point;
		[scrollView setUserInteractionEnabled:NO];
		[activityView setCenter:point];
		[activityView startAnimating];
		[imageTrainDot setCenter:point];
		[imageTrainDot setAlpha:1.0];
		
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You are about to send training data for the specified location. Do you want to continue?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Yes, Send Data", nil];
		[actionSheet showInView:self.parentViewController.parentViewController.view];
		[actionSheet release];
	}
	
	if (mode == UBTrackingGeolocationMode) {
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
		[spotter scan];
	}
}

- (void)scanDone {
	NSLog(@"LISTO!!!");
	NSLog(@"X:%f Y:%f", lastPoint.x, lastPoint.y);
	NSString *signalData = @"";
	return;
	
	if (spotter.networks != nil) {
		for (int i = 0, n = CFArrayGetCount([spotter networks]); i < n; i++) {
			CFDictionaryRef network = CFArrayGetValueAtIndex([spotter networks], i);
			NSLog(@"%@\t%@", CFDictionaryGetValue(network, @"SSID_STR"), CFDictionaryGetValue(network, @"RSSI"));

			signalData = [NSString stringWithFormat:@"%@%@=%@,", signalData, CFDictionaryGetValue(network, @"BSSID"), CFDictionaryGetValue(network, @"RSSI")];
		}
	}
	
	if (mode == UBTrainingGeolocationMode) {	
		NSURL *url = [NSURL URLWithString:@"http://158.97.88.156:8310/"];
		ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:url];
		[request setPostValue:[NSString stringWithFormat:@"%f", lastPoint.x / imageView.frame.size.width] forKey:@"latitude"];
		[request setPostValue:[NSString stringWithFormat:@"%f", lastPoint.y / imageView.frame.size.height] forKey:@"longitude"];
		[request setPostValue:[signalData substringToIndex:([signalData length] - 1)] forKey:@"signalData"];
		[request setPostValue:[[UIDevice currentDevice] model] forKey:@"platform"];
		[request start];
	
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
		NSLog(@"%@", [request responseString]);
	}
	
	if (mode == UBTrackingGeolocationMode && [signalData compare:@""] != 0) {
		NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@%@%@", @"http://158.97.88.156:8310/", [signalData substringToIndex:([signalData length] - 1)], @"/", [[UIDevice currentDevice] model]]];
		ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
		[request start];
		
		CFShow(url);
		CFShow(request);
		
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
		NSLog(@"%@", [request responseString]);
		
		if ([request responseStatusCode] != 200) {
			UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Communication Problem" message:@"The geolocation service cannot be located or the location cannot be estimated." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
			[alert show];
			[alert release];
		} else {
			SBJsonParser *parser = [SBJsonParser new];
			NSDictionary *obj = [parser objectWithString:[request responseString]];
			
			CGPoint point = CGPointMake(0.0, 0.0);
			point.x = [[obj valueForKey:@"latitude"] floatValue] * imageView.frame.size.width;
			point.y = [[obj valueForKey:@"longitude"] floatValue] * imageView.frame.size.height;
			CGPoint offset = CGPointMake(point.x - scrollView.frame.size.width / 2.0, point.y - scrollView.frame.size.height / 2.0);
			
			[UIView beginAnimations:@"Move" context:nil];
			[UIView setAnimationDelay:0.5];
			[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
			[scrollView setContentOffset:offset];
			[imageTrainDot setCenter:point];
			[imageTrainDot setAlpha:1.0];
			[UIView commitAnimations];
			
			[parser release];
		}
	}
	
	[scrollView setUserInteractionEnabled:YES];
	[activityView stopAnimating];
}

- (void)segmentAction:(id)sender {
	mode = [(UISegmentedControl *)sender selectedSegmentIndex];
}

- (void)locate:(id)sender {
	NSLog(@"%@", sender);
}

#pragma mark -
#pragma mark Services related methods

- (void)lookForServices:(id)sender {
	if (browser == nil) {
		browser = [[NSNetServiceBrowser alloc] init];
		[browser setDelegate:self];
	}
	if (!searchingServices) [browser searchForServicesOfType:@"_ubisoa._tcp" inDomain:@"local."];
}

- (void)chooseService {
	if ([services count] == 1) [self willUseService:0];
	if ([services count] > 1) {
		servicesMenu = [[[UIActionSheet alloc] initWithTitle:@"Choose a Geolocation service:" delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil] autorelease];
		for (NSNetService *service in services)
			[servicesMenu addButtonWithTitle:[service name]];
		[servicesMenu addButtonWithTitle:@"Cancel"];
		[servicesMenu setCancelButtonIndex:[services count]];
		[servicesMenu showInView:self.parentViewController.parentViewController.view];
	}
}

- (void)willUseService:(int)serviceIndex {
	activeServiceIndex = serviceIndex;
	NSLog(@"WILL USE SERVICE: %@", [services objectAtIndex:activeServiceIndex]);
	[self viewDidAppear:NO];
}

#pragma mark -
#pragma mark Maps related methods

- (void)showNewMap:(id)sender {
	GeolocationMapViewController *map = [[GeolocationMapViewController alloc] initWithNibName:@"GeolocationMapViewController" bundle:[NSBundle mainBundle]];
	[map setTitle:@"New Map"];
	[[self navigationController] pushViewController:map animated:YES];
	[map release];
}

- (void)showMaps:(id)sender {
	[self showMaps:sender withAnimation:YES];
}

- (void)showMaps:(id)sender withAnimation:(BOOL)animated {
	[[Database sharedInstance] loadMaps];
	
	GeolocationMapsViewController *maps = [[GeolocationMapsViewController alloc] initWithNibName:@"GeolocationMapsViewController" bundle:[NSBundle mainBundle]];
	[maps setTitle:@"Maps"];
	[[self navigationController] pushViewController:maps animated:animated];
	[maps release];
}

#pragma mark -
#pragma mark NSNetServiceBrowser delegate methods for service browsing

- (void)netServiceBrowserWillSearch:(NSNetServiceBrowser *)browser {
	searchingServices = YES;
	
	searchingAlert = [[[UIAlertView alloc] initWithTitle:@"Looking for Services" message:@"\n\n" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:nil] autorelease];
	[searchingAlert show];
	
	UIActivityIndicatorView *aiv = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
	aiv.center = CGPointMake(searchingAlert.bounds.size.width / 2.0f, searchingAlert.bounds.size.height * 0.43f);
	[aiv startAnimating];
	[searchingAlert addSubview:aiv];
	[aiv release];
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didNotSearch:(NSDictionary *)errorDict {
	if (searchingServices) {
		searchingServices = NO;
		[searchingAlert dismissWithClickedButtonIndex:0 animated:YES];
	}
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)aBrowser didFindService:(NSNetService *)aNetService moreComing:(BOOL)moreComing {
	if (![services containsObject:aNetService]) [services addObject:aNetService];
	if (!moreComing) {
		searchingServices = NO;
		[searchingAlert dismissWithClickedButtonIndex:1 animated:NO];
		[browser stop];
	}
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)aBrowser didRemoveService:(NSNetService *)aNetService moreComing:(BOOL)moreComing {
	[services removeObject:aNetService];
	if (!moreComing) {
		UIAlertView *av = [[[UIAlertView alloc] initWithTitle:@"Services Lost" message:@"Some of the services are no longer available." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[av show];
		[browser stop];
	}
}

#pragma mark -
#pragma mark UIActionSheet delegate methods

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
	if (actionSheet == servicesMenu)
		if (buttonIndex != actionSheet.cancelButtonIndex)
			[self willUseService:buttonIndex];
	
	
	/*	if (buttonIndex != [actionSheet cancelButtonIndex]) {
	 [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	 [spotter scan];
	 return;
	 }
	 
	 [scrollView setUserInteractionEnabled:YES];
	 [activityView stopAnimating];
	 [imageTrainDot setAlpha:0.0];*/
}

#pragma mark -
#pragma mark UIAlertView delegate methods

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (alertView == searchingAlert && buttonIndex == 0) {
		[browser stop];
		searchingServices = NO;
		[searchingAlert dismissWithClickedButtonIndex:0 animated:YES];
	}
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
	if (alertView == searchingAlert && buttonIndex == 1) [self chooseService];
}

#pragma mark -
#pragma mark UIView delegate methods

- (void)viewDidAppear:(BOOL)animated {
	BOOL noServicesOnTop = [self.view.subviews lastObject] == [self.view viewWithTag:101];
	BOOL noMapsOnTop = [self.view.subviews lastObject] == [self.view viewWithTag:102];
	int mapCount = [[Database sharedInstance] countRowsOfTable:@"maps"];
	int servicesCount = [services count];

//	NSLog(@"---PRE----");
//	NSLog(@"SELECTED MAP: %@", self.selectedMap);
//	NSLog(@"SERVICES ON TOP: %@", (noServicesOnTop)? @"YES": @"NO");
//	NSLog(@"NO MAPS ON TOP: %@", (noMapsOnTop)? @"YES": @"NO");
//	NSLog(@"MAPS ON FILE: %d", mapCount);
//	NSLog(@"SERVICES COUNT: %d", servicesCount);
//	NSLog(@"-------");
	
	if (servicesCount > 0 && noServicesOnTop) {
		[UIView beginAnimations:@"NoServicesViewHide" context:nil];
		[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
		[UIView setAnimationDuration:0.75];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:self.view cache:YES];
		[self.view sendSubviewToBack:[self.view viewWithTag:101]];
		[UIView commitAnimations];
		noServicesOnTop = NO;
		noMapsOnTop = [self.view.subviews lastObject] == [self.view viewWithTag:102];
	}
	
	if (mapCount == 1 && self.selectedMap == nil) {
		[[Database sharedInstance] loadMaps];
		self.selectedMap = [[[Database sharedInstance] data] objectAtIndex:0];
	}
	
	if (mapCount == 0 && self.selectedMap != nil)
		self.selectedMap = nil;
	
	if (mapCount > 0 && self.selectedMap != nil && noMapsOnTop) {
		[UIView beginAnimations:@"NoMapsViewHide" context:nil];
		[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
		[UIView setAnimationDuration:0.75];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:self.view cache:YES];
		[self.view sendSubviewToBack:[self.view viewWithTag:102]];
		[UIView commitAnimations];
		noMapsOnTop = NO;
	}
	
	if (mapCount == 0 && !noMapsOnTop && !noServicesOnTop) {
		[UIView beginAnimations:@"NoMapsViewShow" context:nil];
		[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
		[UIView setAnimationDuration:0.75];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:self.view cache:YES];
		[self.view bringSubviewToFront:[self.view viewWithTag:102]];
		[UIView commitAnimations];
		noMapsOnTop = YES;
	}
	
	if (mapCount > 1 && !noServicesOnTop && self.selectedMap == nil)
		[self showMaps:nil withAnimation:YES];
	
	noServicesOnTop = [self.view.subviews lastObject] == [self.view viewWithTag:101];
	noMapsOnTop = [self.view.subviews lastObject] == [self.view viewWithTag:102];
	mapCount = [[Database sharedInstance] countRowsOfTable:@"maps"];
	servicesCount = [services count];
	
//	NSLog(@"---AFTER----");
//	NSLog(@"SELECTED MAP: %@", self.selectedMap);
//	NSLog(@"SERVICES ON TOP: %@", (noServicesOnTop)? @"YES": @"NO");
//	NSLog(@"NO MAPS ON TOP: %@", (noMapsOnTop)? @"YES": @"NO");
//	NSLog(@"MAPS ON FILE: %d", mapCount);
//	NSLog(@"SERVICES COUNT: %d", servicesCount);
//	NSLog(@"-------");	
	
}

@end
