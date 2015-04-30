## Details ##
The initial work flow "playflow?" of the skylight1 game was to pass the g-phone around to another person, a set distance in a limited amount of time. Initial development was done with captured accelerometer data and some time was spent refining the algorithms for identifying a fixed distance of 1 meter. With the availability of a second phone the data captured from the accelerometer, it was observed that the accelerometer data at rest had some variance between the two phones. To remove the variance between the data sets/phones required a 'noise margin'. Unfortunately this noise margin prevents accurate reading of the accelerometers for distance measurement.

Further research pointed to this blog post:
http://ajnaware.wordpress.com/2008/09/05/accelerating-iphones/

As this is also a problem with iphones (and probably all IC based accelerometers right now) the work flow/play flow for the game was changed to steadiness.