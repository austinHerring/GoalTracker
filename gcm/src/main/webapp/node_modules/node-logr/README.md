logr
====

Its such a simple logger, we had to shorten its name

## Installation

    npm install node-logr
    
## Usage

when requiring "logr" you can pass the "__filename" magic parameter and it will magically use the file name, you can pass it any other string that does not contain a forward slash

    var logr = require('node-logr').getLogger(__filename)
    
print some useful things

    logr.info("this is a normal thing that happened")
    test.js :: 2012-09-03 16:0:18.476 :: INFO :: this is a normal thing that happened
    logr.notice("this should not normally happen")
    test.js :: 2012-09-03 16:0:51.909 :: NOTICE :: this should not normally happen
    
print error messages with exceptions

    try {
        iDontExist()
    } catch (e){
        logr.error("was expecting this error", e);
    }
    
    test.js :: 2012-9-3 16:23:51.463 :: ERROR :: was expecting this error
    ReferenceError: iDontExist is not defined

add debug prints which are enable with a global flag

    logr.debug("this is a ghost debug message");
    require('node-logr').toggleDebug();
    logr.debug("this is a debug message");
    test.js :: 2012-09-03 16:23:51.465 :: DEBUG :: this is a debug message

