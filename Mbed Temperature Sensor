/**
 *  Mbed Temperature Sensor
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
	definition (name: "Mbed Temperature Sensor", namespace: "CR76", author: "Cameron Reid") {
		capability "Polling"
		capability "Refresh"
		capability "Temperature Measurement"
        
        fingerprint endpointId: "76", profileId: "0104",inClusters: "0000,0402"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		multiAttributeTile(name:"thermostatFull", type:"thermostat", width:6, height:4) {
    		tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
        	attributeState("default", label:'${currentValue}°', unit:"dC",
            backgroundColors:[
				[value: 0, color: "#153591"],
				[value: 7, color: "#1e9cbb"],
				[value: 15, color: "#90d2a7"],
				[value: 23, color: "#44b621"],
                [value: 29, color: "#f1d801"],
				[value: 35, color: "#d04e00"],
				[value: 36, color: "#bc2323"]
			]
            )
    		}
    	}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width:2, height:2 ) {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		main (["thermostatFull"])
		details (["thermostatFull", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing '${zigbee.parse(description)}'"
    def name = null
    def value = null
    Map map = [:]
    if (description?.startsWith("catchall: 0104 0402 76")) {
    	log.debug "get temperature"
		map = parseCatchAllMessage(description)
        log.debug "Parse returned $map"
        map ? createEvent(map) : null
    }
    else {
        log.warn "DID NOT PARSE MESSAGE for description : $description"
        log.debug zigbee.parseDescriptionAsMap(description)
    }
}

private Map parseCatchAllMessage(String description) {
    def cluster = zigbee.parse(description)

        // temp is last 2 data values. reverse to swap endian
        String temp = cluster.data[-2..-1].reverse().collect { cluster.hex1(it) }.join()
        log.debug "temp = $temp"
        def value = Integer.parseInt(temp, 16)
        return [
		name: 'temperature',
		value: value,
        ]
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	refresh()
}

def refresh() {
	log.debug "Executing 'refresh'"
    def cmd = []
	cmd << "st rattr 0x${device.deviceNetworkId} 0x76 0x0402 0x0000"    // Read probe 1 Temperature 
}
