//
//  GeolocationMapsViewController.m
//  UbiSOA
//
//  Created by Edgardo on 19/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationMapsViewController.h"


@implementation GeolocationMapsViewController

/*
- (id)initWithStyle:(UITableViewStyle)style {
    // Override initWithStyle: if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
    if (self = [super initWithStyle:style]) {
    }
    return self;
}
*/

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.leftBarButtonItem = self.editButtonItem;
	self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMap:)];
}

- (void)addMap:(id)sender {
	GeolocationMapViewController *map = [[GeolocationMapViewController alloc] initWithNibName:@"GeolocationMapViewController" bundle:[NSBundle mainBundle]];
	[map setTitle:@"New Map"];
	[[self navigationController] pushViewController:map animated:YES];
	[map release];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
	[self.tableView reloadData];
}

/*
- (void)viewWillDisappear:(BOOL)animated {
	[super viewWillDisappear:animated];
}
*/
/*
- (void)viewDidDisappear:(BOOL)animated {
	[super viewDidDisappear:animated];
}
*/

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [[[Database sharedInstance] data] count];
}


// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";

    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    
	GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
    cell.textLabel.text = map.name;
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	GeolocationViewController *controller = [self.navigationController.viewControllers objectAtIndex:0];
	[controller setSelectedMap:[[[Database sharedInstance] data] objectAtIndex:indexPath.row]];
	[self.navigationController popViewControllerAnimated:YES];
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
	GeolocationMapViewController *controller = [[GeolocationMapViewController alloc] initWithNibName:@"GeolocationMapViewController" bundle:[NSBundle mainBundle]];
	GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
	[controller setEditing:YES];
	[controller setMap:map];
	[controller setTitle:map.name];
	[self.navigationController pushViewController:controller animated:YES];
	[controller release];
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/


// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {    
    if (editingStyle == UITableViewCellEditingStyleDelete) {
		GeolocationMap *map = [[[Database sharedInstance] data] objectAtIndex:indexPath.row];
		NSString *path = [NSString stringWithFormat:@"%@/Documents/%@", NSHomeDirectory(), map.file];
		[[NSFileManager defaultManager] removeItemAtPath:path error:nil];	

		[[Database sharedInstance] removeRow:map.tag ofTable:@"maps"];
		[[Database sharedInstance] loadMaps];
		
        [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];

		if ([[[Database sharedInstance] data] count] == 0)
			[self.navigationController popViewControllerAnimated:YES];
    }
}


/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/


/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/


- (void)dealloc {
    [super dealloc];
}

- (void)viewDidAppear:(BOOL)animated {
	/*if ([[Database sharedInstance] countRows:@"maps"] == 0) {
		[UIView beginAnimations:@"HideNoServicesView" context:nil];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:self.parentViewController.view cache:YES];
		[UIView setAnimationDuration:0.5];
		[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
		[(UINavigationController *)[self parentViewController] popViewControllerAnimated:NO];
		[UIView commitAnimations];
	}*/
}

@end

