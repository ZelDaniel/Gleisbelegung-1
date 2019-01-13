# Plugin-Tester
## Features

* interactive testing of plugin
* preset of trains, schedules that will be simulated
* simulated time starts at 05:00:00

Only one plugin may be connected to the tester at same time.

## Interaction
The tester is controlled by the console. Supported commands are

command | description
---|---
help | Prints the help message
quit | Closes the socket and terminates the tester
delay \<train id\> | Adjust the delay of a train
trainlist | Sends the trainlist (\<zugliste\>)
remove train \<train id\> | Removes a train
close | Closes the socket and resets the tester - the tester will be ready for a new connection 
 
##Simulated schedule
Will be described later.