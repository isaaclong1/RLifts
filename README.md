# RLifts

The objective of our project is to create an Android application to help students
carpool in our CS180 software engineering class. The application allows users to post and look for rides in the area around them.
If a ride overlaps with their travel plans, they can request a pickup from the driver. The
driver will see the request and has the option to accept or decline to pick up the rider.
After the driver drops off the rider at their destination, payment will be forwarded to the driver.

The purpose of this service is not to be a taxi service, like Uber and Lyft, although it is similar in execution. 
This is geared towards normal people and students carpooling to a common area, and ensures
that the driver is compensated with enough money for gas. While this is targeting
college students, there is potential for it to be adapted for people in dense urban areas.

# Technologies and Frameworks

### Back end

Server with LAMP stack (Linux, Apache, MySQL, Python for scripting instead of PHP)

### Front end

Java, Android SDK/Android Studio, Facebook and Google+ Login APIs, Paypal API

# Basic Usage

### Login

Upon opening the app, the login page appears. Users have the option to log in with an existing account, their Google account, or their Facebook account.
New users can choose 'create profile' to create a new account.

<img src="http://i.imgur.com/nhzZbxg.png" width="200">
<img src="http://i.imgur.com/Fs4gDfm.png" width="200">
<img src="http://i.imgur.com/9EbCXZC.png" width="200">
<img src="http://i.imgur.com/JDoRalL.png" width="200">

### Home

Upon logging in the user is greeted with a small tutorial page explaining the 
structure and functionality of the side menu.

<img src="http://i.imgur.com/JPZ3IHT.png" width="200">
<img src="http://i.imgur.com/qoYfuNe.png" width="200">

### Rider

Riders can see posted rides that are near their current geographical location. They 
may also filter rides by adjusting a radius around their current location. Upon selection of a ride they 
can see ride details and choose whether to take the ride or not.

<img src="http://i.imgur.com/xB4K5Ca.png" width="200">
<img src="http://i.imgur.com/mlLl26L.png" width="200">

### Driver

Drivers post rides with a starting location, destination, and departure time. If the driver has not registered as a driver,
they must input their license and vehicle information in a registration form. Their ride will appear to riders who
are searching for rides via the ride search map. 

<img src="http://i.imgur.com/qthu6a3.png" width="200">

### Tokens

In lieu of fully fleshed out payment system, we added in ride tokens as a place holder to simulate payment 
between the rider and the driver upon ride completion. For now, users can purchase tokens to pay for rides.
Drivers who recieve token payment can in turn use their tokens for rides for themselves.

<img src="http://i.imgur.com/aJGjZyy.png" width="200">
<img src="http://i.imgur.com/HDisMUj.png" width="200">
<img src="http://i.imgur.com/ej0zlal.png" width="200">

# Future Work

Version 2 of our app is currently under development. Version 1 exists as a proof of concept as well as
our team's first foray into Android development. Due to time constraints during the quarter, we prioritized getting basic functionality 
working and fulfilling our list of specifications for our class. Below are the core features that we would like to implement in the next version.
* Updated system for ride requests and communication between driver and rider
* Updated system for detection of ride completion
* Updated ride karma/payment system that accurately accounts for gas and the amount of people in the car
* Improve code quality, error checking, and documentation


