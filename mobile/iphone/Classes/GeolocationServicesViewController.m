//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 21/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationServicesViewController.h"


@implementation GeolocationServicesViewController


- (void)viewDidLoad {
    [super viewDidLoad];
}

- (void)dealloc {
	[browser release];
	[services release];
	[hiddenHeader release];
    [super dealloc];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
	
	if (services == nil) {
		services = [[NSMutableArray alloc] init];
		[self lookForServices:self];
	}

	if ([services count] > 0 && self.tableView.tableHeaderView != nil) {
		hiddenHeader = self.tableView.tableHeaderView;
		[hiddenHeader retain];
		
		[UIView beginAnimations:@"TableHeaderHide" context:nil];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:self.view cache:YES];
		[UIView setAnimationDuration:0.5];
		[UIView setAnimationDelegate:self];
		[UIView setAnimationDidStopSelector:@selector(animationDidStop:finished:context:)];
		self.tableView.tableHeaderView = nil;
		[self.tableView setScrollEnabled:YES];
		[self.tableView reloadData];
		[UIView commitAnimations];
	}
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void) animationDidStop:(NSString *)animationID finished:(NSNumber *)finished context:(void *)context
{
	if ([services count] == 1) [self willUseService:0 animated:YES];
}

#pragma mark -
#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return (services != nil)? [services count]: 0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
	cell.textLabel.text = [[services objectAtIndex:indexPath.row] name];
	cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
	
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self willUseService:indexPath.row animated:YES];
}

#pragma mark -
#pragma mark Services related methods

- (IBAction)lookForServices:(id)sender {
	if (browser == nil) {
		browser = [[NSNetServiceBrowser alloc] init];
		[browser setDelegate:self];
	}
	if (!searchingServices) [browser searchForServicesOfType:@"_ubisoa._tcp" inDomain:@"local."];	
}

- (void)willUseService:(int)serviceIndex animated:(BOOL)animate {
	GeolocationMapsViewController *maps = [[GeolocationMapsViewController alloc] initWithNibName:@"GeolocationMapsViewController" bundle:nil];
	maps.title = @"Maps";
	maps.service = [services objectAtIndex:serviceIndex];
	[self.navigationController pushViewController:maps animated:animate];
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
		[searchingAlert dismissWithClickedButtonIndex:0 animated:YES];
		[browser stop];
	}
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)aBrowser didRemoveService:(NSNetService *)aNetService moreComing:(BOOL)moreComing {
	[services removeObject:aNetService];
	UIAlertView *av = [[[UIAlertView alloc] initWithTitle:@"Service Lost" message:[NSString stringWithFormat:@"%@", aNetService] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
	[av show];	
	if (!moreComing) {
		[browser stop];
	}
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
	[self viewDidAppear:YES];
}

@end

