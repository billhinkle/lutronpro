metadata {
	definition (name: "Lutron Virtual Dimmer", namespace: "njschwartz", author: "Nate Schwartz") {	// rev 20180105 wjh
		capability "Switch"
		capability "Refresh"
		capability "Switch Level"
		command "rampLevel"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.Lighting.light21", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', action: "switch.off", icon: "st.Lighting.light21", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}        
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, backgroundColor:"#ffe71e") {
			state "level", action:"switch level.setLevel"
		}
		valueTile("lValue", "device.level", inactiveLabel: true, height:1, width:1, decoration: "flat") {
			state "levelValue", label:'${currentValue}%', unit:"", backgroundColor: "#53a7c0"
		}

		main(["button"])
		details(["button", "refresh","levelSliderControl","lValue"])
	}
}

def parse(description) {
	sendEvent(name: description.name, value: description.value)
}

def on() {
	log.debug getDataValue("zone")
	if (device.currentValue("level") > 0) {
		parent.setLevel(this, device.currentValue("level"))
	} else {
		parent.setLevel(this, 100)
	}
	sendEvent(name: "switch", value: "on")
	log.info "Dimmer On"
}

def off() {
	parent.off(this)
	sendEvent(name: "switch", value: "off")
	log.info "Dimmer Off"
}

def setLevel(level) {
	level = limitedLevel(level)

	parent.setLevel(this, level)
	sendEvent(name: "level", value: level)
	log.info "setLevel $level"
}

def rampLevel(level, userRampSeconds) {
	level = limitedLevel(level)

	parent.setLevel(this, level, userRampSeconds)
	sendEvent(name: "level", value: level)
	sendEvent(name: "switch", value: "on")
}

def refresh() {
	parent.refresh(this)
	log.info "refresh"
}

def limitedLevel(level) {
	if (level < 0) return 0
	else if (level > 100) return 100
	return level	
}
