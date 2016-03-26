pad = (value)-> if String(value).length == 1 then "0#{value}" else value
pad3 = (value)-> 
	switch String(value).length
		when 1 then "00#{value}"
		when 2 then "0#{value}"
		else value

log = (prefix, source, message, e) ->
	n = new Date()
	dateStr = "#{n.getFullYear()}-#{pad(n.getMonth()+1)}-#{pad(n.getDate())} #{pad(n.getHours())}:#{pad(n.getMinutes())}:#{pad(n.getSeconds())}.#{pad3(n.getMilliseconds())}"
	console?.log "#{dateStr} :: #{prefix.toUpperCase()} :: #{source} :: #{message}"
	if e then console?.log e.toString(), e.stack

exports.getLogger = (source,pack = null)->
	source = if source.indexOf "/" > 0 then source.split("/").pop() else source
	new _Logger(source, pack)

_debug = false

class _Logger
# @param source String name of class / file name
	constructor: (@source, @pack)->
	error : (message,e)=>
		log("ERR", @_prefix(), message, e)
	info : (message)=>
		log("INF", @_prefix(), message, null)
	notice : (message)=>
		log("NTC", @_prefix(), message,null)
	debug: (message)=>
		if _debug then log("DBG", @_prefix(), message, null)
	errorCB : (message, callback)=>
		@error(message) ; callback(message)
	_prefix : => if (@pack) then "#{@pack}@#{@source}" else @source

exports.toggleDebug = (isOverride)->
	if isOverride? then _debug = isOverride
	else if _debug then _debug = false else _debug = true
