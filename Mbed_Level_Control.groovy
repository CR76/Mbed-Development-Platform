/**
 *  Mbed Level Control
 *
 *  Copyright 2016 Cameron Reid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Mbed Level Control", namespace: "CR76", author: "Cameron Reid") {
    capability "Refresh"
    capability "Polling"
    capability "Switch"
    capability "Switch level"
    
    
   	fingerprint endpointId: "76", profileId: "0104",inClusters: "0000,0006,0008"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"Switch", type:"generic", width:6, height:4) {
    		tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        	attributeState "on", label:'${name}', action: "Switch.off", backgroundColor:"#79b821"
        	attributeState "off", label:'${name}', action: "Switch.on", backgroundColor:"#ffffff"
            }
        tileAttribute("device.level", key: "SLIDER_CONTROL") {
        	attributeState "level", action:"switch level.setLevel"
    	}
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width:2, height:2 ) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main (["Switch"])
		details (["Switch", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
    log.debug "Parse description $description"
    Map map = [:]
    def name = null
    def value = null
    def event = zigbee.getEvent(description)
    
    if (event){
    	if (event.name == "switch"){
        sendEvent(event)
        log.debug "Event '${event}'"
        }
        else if (event.name == "level"){
        def descMap = parseDescriptionAsMap(description)
        value = zigbee.convertHexToInt(descMap.value)
        name = "level"
        log.debug " Value $value"
        createEvent(name: name, value: value) 
        }
    }
    else {
        log.warn "DID NOT PARSE MESSAGE for description : $description"
        log.debug zigbee.parseDescriptionAsMap(description)
    }
}

def parseDescriptionAsMap(description) {
    (description - "read attr - ").split(",").inject([:]) { map, param ->
        def nameAndValue = param.split(":")
        map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    }
}

def on() {
	log.debug "LED 1 on()"
    zigbee.on()
}

def off() {
	log.debug "LED 1 off()"
    zigbee.off()
}

def setLevel(value) {
	log.debug "Set Level $value"
    int lev = value
    value = zigbee.convertToHexString(lev)
    "st cmd 0x${device.deviceNetworkId} 0x76 8 4 {$value}"
}

def poll(){
	log.debug "Poll is calling refresh"
	refresh()
}

def refresh() {
	log.debug "sending refresh command"
    def cmd = []	
    cmd << "st rattr 0x${device.deviceNetworkId} 0x76 0x0006 0x0000"	// Read on / off attribute at End point 0x76
    cmd << "delay 250"
    cmd << "st rattr 0x${device.deviceNetworkId} 0x76 0x0008 0x0000"	// Read Level attribute at End point 0x76
}
