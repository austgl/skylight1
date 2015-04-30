# Test Data Capture Application (TDCA) Use Case #

**Description:** The user captures test data to play back later for automated testing.

**Primary Actor:** An Android developer.

**Primary Flow:**
|1|The user launches the Skylight TDCA.|
|:|:-----------------------------------|
|2 |The system provides a list of available sensors, recording rates, and start and quit buttons.|
|3 |The user selects the sensors they wish to capture data for (accelerometer, light, magnetic, orientation, raw orientation, proximity, temperature) - to which is added the camera preview, which is not truly a sensor - and a recording rate (fastest, game, normal, UI), and selects the start button.|
|4 |The system presents feedback to indicate the recording is in progress.  In the case of camera preview, a preview window.|
|5 |When the user is satisfied with the recording, the user presses the stop button.|
|6 |The system stores the recording on the SD card and then returns to step 2.|