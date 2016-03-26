{exec} = require 'child_process'

handleExecErrors = (err, stdout, stderr)->
        console.log err if err
        console.log 'Messages: ' + stdout + stderr if err || stdout

task 'build',->
	exec "coffee -c index.coffee", handleExecErrors


