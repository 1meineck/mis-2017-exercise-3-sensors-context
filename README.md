# mis-2017-exercise-3-sensors-context

Explanations for our selected values and thresholds

We chose to calculate the average of our fft array data, to compare to our thresholds because the average is more robust to stray values and the music wont stop playing at times where it should continue. 

When selecting the thresholds we looked at the behavior of our first application to see which values might be suited as thresholds and tried them out. 

When deciding between jogging and cycling, the app looks at the average of our fft data. For jogging the average needs to be higher than for cycling, because the according movement is higher. 

Additionally, we wanted to add a speed constraint for cycling, however, since we could not test it, we decided to leave it out for now.

Without the speed constraint, we decided to make them mutually exclusive. This should be adapted, when speed can be tested and it works.
The thresholds we selected worked fine with our phones, however, they might need to be updated for different devices. 
Our Thresholds are: 
CYCLING_MIN = 2;
CYCLING_MAX = 8;
JOGGING_MIN = 8;
JOGGING_MAX =100; should not be reached, just to avoid vibration alarm setting it off

