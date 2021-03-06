/* 
* OpenSpeedMonitor (OSM)
* Copyright 2014 iteratec GmbH
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* 	http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

package de.iteratec.osm.measurement.environment.wptserverproxy

import groovy.util.slurpersupport.GPathResult;
import groovyx.net.http.ContentType;
import groovyx.net.http.RESTClient
import de.iteratec.osm.measurement.environment.WebPageTestServer;

class HttpRequestService {
	
	private Map<String, RESTClient> clients = new HashMap()

	Object getWptServerHttpGetResponse(WebPageTestServer wptserver, String path, Map query, ContentType contentType, Map headers){
		getClient(wptserver).get(
			path: path,
			query: query,
			contentType: contentType,
			headers: headers
			)
	}
    GPathResult getWptServerHttpGetResponseAsGPathResult(WebPageTestServer wptserver, String path, Map query, ContentType contentType, Map headers){
		return parseXml(getWptServerHttpGetResponse(wptserver, path, query, contentType, headers))
	}
	GPathResult parseXml(Object resp) {
		assert resp != null
		
		def myresponse = new XmlSlurper().parseText(resp.data.text)
		assert myresponse != null

		return myresponse
	}
	
	RESTClient getClient(WebPageTestServer wptserver) {
		assert wptserver != null
		return new RESTClient(wptserver.baseUrl)
	}
	RESTClient getClientCached(WebPageTestServer wptserver) {
		assert wptserver != null

		if (!this.clients.containsKey(wptserver.baseUrl)) {
			this.clients[wptserver.baseUrl] = new RESTClient(wptserver.baseUrl)
		}

		assert this.clients[wptserver.baseUrl] != null

		return this.clients[wptserver.baseUrl]
	}
}
