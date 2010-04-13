//
//  UbiSOAAppDelegate.h
//  UbiSOA
//
//  Created by Edgardo on 15/12/09.
//  Copyright CICESE 2009. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UbiSOAAppDelegate : NSObject <UIApplicationDelegate, UITabBarControllerDelegate> {
    UIWindow *window;
    UITabBarController *tabBarController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UITabBarController *tabBarController;

@end
