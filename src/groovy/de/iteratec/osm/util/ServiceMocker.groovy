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

package de.iteratec.osm.util

import de.iteratec.osm.ConfigService
import de.iteratec.osm.csi.*
import de.iteratec.osm.measurement.environment.Browser
import de.iteratec.osm.measurement.environment.BrowserService
import de.iteratec.osm.measurement.environment.Location
import de.iteratec.osm.measurement.environment.WebPageTestServer
import de.iteratec.osm.measurement.environment.wptserverproxy.ProxyService
import de.iteratec.osm.measurement.schedule.JobGroup
import de.iteratec.osm.report.chart.*
import de.iteratec.osm.result.*
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.joda.time.DateTime

/**
 * <p>
 * Mocks grails-Services. 
 * These services get injected into instance-variables of other services in production by spring .
 * In unit-tests these services has to be mocked. To avoid duplication these mocks are assembled in this class.   
 * </p>
 * @author nkuhn
 *
 */
@TestMixin(GrailsUnitTestMixin)
class ServiceMocker {
	
	private ServiceMocker(){}
	public static ServiceMocker create(){
		return new ServiceMocker()
	}
	
	//TODO: Write one generic method to mock arbitrary methods of arbitrary services:
//	void mockServiceMethod(serviceToMockInjectedServiceIn, Class serviceClassToMock, String nameOfMethodToMock){
//		def serviceMock = mockFor(serviceClassToMock, true)
//		serviceMock.demand.nameOfMethodToMock(
//			1..10000, 
//			getClosureToExecute(serviceClassToMock, nameOfMethodToMock)
//		) 
//		serviceToMockInjectedServiceIn.metaClass.setAttribute(
//			this, serviceToMockInjectedServiceIn, withLowerFirstLetter(serviceClassToMock.getName()),  serviceMock.createMock(), false, false)
//	}
	Closure getClosureToExecute(serviceClassToMock, nameOfMethodToMock){
		//TODO: should deliver the closure to be executed if the method nameOfMethodToMock of service serviceClassToMock is called in unit-tests 
	}
	
	/**
	 * Mocks methods of {@link MeasuredValueUpdateEventDaoService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable. 
	 */
	void mockMeasuredValueUpdateEventDaoService(serviceToMockIn){
		def measuredValueUpdateEventDaoService = mockFor(MeasuredValueUpdateEventDaoService, true)
		measuredValueUpdateEventDaoService.demand.createUpdateEvent(1..10000) {
			Long measuredValueId, MeasuredValueUpdateEvent.UpdateCause cause ->
			
				new MeasuredValueUpdateEvent(
					dateOfUpdate: new Date(),
					measuredValueId: measuredValueId,
					updateCause: cause
				).save(failOnError: true)
				
		}
		serviceToMockIn.measuredValueUpdateEventDaoService = measuredValueUpdateEventDaoService.createMock()
	}
	/**
	 * Mocks methods of {@link CsiConfigCacheService}
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 */
	void mockOsmConfigCacheService(serviceToMockIn){
		def osmConfigCacheService = mockFor(OsmConfigCacheService, true)
		Integer minTimeToExpect = 250
		osmConfigCacheService.demand.getCachedMinDocCompleteTimeInMillisecs(1..10000) {
			Double ageToleranceInHours ->
			return minTimeToExpect
		}
		Integer maxTimeToExpect = 180000
		osmConfigCacheService.demand.getCachedMaxDocCompleteTimeInMillisecs(1..10000) {
			Double ageToleranceInHours ->
			return maxTimeToExpect
		}
		serviceToMockIn.osmConfigCacheService = osmConfigCacheService.createMock()
	}
	/**
	 * Mocks {@link EventResultService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 */
	void mockEventResultService(serviceToMockIn){
		def eventResultService = mockFor(EventResultService, true)
		eventResultService.demand.isCsiRelevant(1..10000) {
			EventResult toProof, Integer minDocTimeInMillisecs, Integer maxDocTimeInMillisecs ->
			
			return toProof.customerSatisfactionInPercent && toProof.docCompleteTimeInMillisecs &&
			(toProof.docCompleteTimeInMillisecs > minDocTimeInMillisecs &&
			toProof.docCompleteTimeInMillisecs < maxDocTimeInMillisecs)
			
		}
		serviceToMockIn.eventResultService = eventResultService.createMock()
	}
	/**
	 * Mocks methods in {@link JobResultService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 */
	void mockJobResultService(serviceToMockIn){
		def jobResultService = mockFor(JobResultService, true)
		jobResultService.demand.findJobResultByEventResult(1..10000) {
			EventResult eventResult ->
			
			JobResult jobResult1 = JobResult.findByTestId(testIdOfJobRunCsiGroup1)
			JobResult jobResult2 = JobResult.findByTestId(testIdOfJobRunCsiGroup2)
			
			return jobResult1.eventResults.contains(eventResult)?
				jobResult1:
				jobResult2
			
		}
		serviceToMockIn.jobResultService = jobResultService.createMock()
	}
	/**
	 * Mocks methods in {@link BrowserService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 */
	void mockBrowserService(serviceToMockIn){
		def browserService = mockFor(BrowserService, true)
		browserService.demand.findByNameOrAlias(1..10000) {
			String browserNameOrAlias ->
			return Browser.findByName(browserName)
			
		}
		serviceToMockIn.browserService = browserService.createMock()
	}
	
	/**
	 * Mocks methods in {@link MeasuredValueUtilService}.
	 * @param serviceToMockIn
	 * 		Grails-Service with the service to mock as instance-variable.
	 */
	void mockMeasuredValueUtilService(serviceToMockIn, DateTime toReturnFromGetNowInUtc){
		def measuredValueUtilService = mockFor(MeasuredValueUtilService, true)
		measuredValueUtilService.demand.getNowInUtc(1..10000) {
			->
			return toReturnFromGetNowInUtc
		}
		serviceToMockIn.measuredValueUtilService = measuredValueUtilService.createMock()
	}
	
	/**
	 * Mocks methods of {@link EventMeasuredValueService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param toReturnFromGetOrCalculateHourlyMeasuredValues
	 * 		To return from mocked method {@link EventMeasuredValueService#getOrCalculateHourylMeasuredValues}.
	 */
	void mockEventMeasuredValueService(serviceToMockIn, List<MeasuredValue> toReturnFromGetOrCalculateHourlyMeasuredValues){
		def eventMeasuredValueServiceMocked = mockFor(EventMeasuredValueService, true)
		eventMeasuredValueServiceMocked.demand.getHourylMeasuredValues(0..10000) { Date from, Date to, MvQueryParams mvQueryParams ->
			return 	toReturnFromGetOrCalculateHourlyMeasuredValues
		}
		serviceToMockIn.eventMeasuredValueService = eventMeasuredValueServiceMocked.createMock()
	}
	/**
	 * Mocks methods of {@link PageMeasuredValueService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param toReturnFromGetOrCalculateWeeklyPageMeasuredValues
	 * 		List of {@link MeasuredValue}s, the method {@link PageMeasuredValueService#getOrCalculateWeeklyPageMeasuredValues(java.util.Date, java.util.Date)} should return.
	 */
	void mockPageMeasuredValueService(serviceToMockIn, List<MeasuredValue> toReturnFromGetOrCalculateWeeklyPageMeasuredValues){
		def pageMeasuredValueServiceMocked = mockFor(PageMeasuredValueService)
		// new Version:
		pageMeasuredValueServiceMocked.demand.getOrCalculatePageMeasuredValues(0..10000) { 
			Date from, Date to, MeasuredValueInterval mvInterval, List<JobGroup> csiGroups, List<Page> pages ->
			return toReturnFromGetOrCalculateWeeklyPageMeasuredValues
		}
		serviceToMockIn.pageMeasuredValueService = pageMeasuredValueServiceMocked.createMock()
	}
	/**
	 * Mocks {@link ShopMeasuredValueService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param toReturnFromGetOrCalculateWeeklyShopMeasuredValues
	 * 		List of {@link MeasuredValue}s, the method {@link ShopMeasuredValueService#getOrCalculateWeeklyShopMeasuredValues(java.util.Date, java.util.Date)} should return.
	 */
	void mockShopMeasuredValueService(serviceToMockIn, List<MeasuredValue> toReturnFromGetOrCalculateWeeklyShopMeasuredValues){
		def shopMeasuredValueServiceMocked = mockFor(ShopMeasuredValueService, true)
		shopMeasuredValueServiceMocked.demand.getOrCalculateWeeklyShopMeasuredValues(0..10000) { Date from, Date to ->
			return toReturnFromGetOrCalculateWeeklyShopMeasuredValues
		}
		shopMeasuredValueServiceMocked.demand.getOrCalculateShopMeasuredValues(0..10000) { Date from, Date to, MeasuredValueInterval interval, List csiGroups ->
			return toReturnFromGetOrCalculateWeeklyShopMeasuredValues
		}
		serviceToMockIn.shopMeasuredValueService = shopMeasuredValueServiceMocked.createMock()
	}
	/**
	 * Mocks methods of {@link MeasuredValueTagService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param idAsStringToJobGroupMap
	 * 		A map with id's as keys and respective JobGroups as values.  
	 * @param idAsStringToMeasuredEventMap
	 * 		A map with id's as keys and respective MeasuredEvents as values.
	 *	@param idAsStringToPageMap
	 *			A map with id's as keys and respective Pages as values.
	 *	@param idAsStringToBrowserMap
	 *			A map with id's as keys and respective Browsers as values.
	 *	@param idAsStringToLocationMap
	 *			A map with id's as keys and respective Locations as values.
	 * 
	 */
	void mockMeasuredValueTagService(
		serviceToMockIn,
		Map idAsStringToJobGroupMap,
		Map idAsStringToMeasuredEventMap,
		Map idAsStringToPageMap,
		Map idAsStringToBrowserMap,
		Map idAsStringToLocationMap){

		def measuredValueTagServiceMocked = mockFor(MeasuredValueTagService, true)
		
		measuredValueTagServiceMocked.demand.createHourlyEventTag(1..10000) {
			JobGroup jobGroupParam,
			MeasuredEvent measuredEventParam,
			Page pageParam,
			Browser browserParam,
			Location locationParam ->
			
			return new MeasuredValueTagService().createHourlyEventTag(jobGroupParam,
				measuredEventParam,
				pageParam,
				browserParam,
				locationParam)
		}
		measuredValueTagServiceMocked.demand.findJobGroupOfHourlyEventTag(0..10000) {String mvTag ->
			String idJobGroup = mvTag.split(";")[0]
			return idAsStringToJobGroupMap[idJobGroup]
		}
		measuredValueTagServiceMocked.demand.findMeasuredEventOfHourlyEventTag(0..10000) {String mvTag ->
			String measuredEventId = mvTag.split(";")[1]
			return idAsStringToMeasuredEventMap[measuredEventId]
		}
		measuredValueTagServiceMocked.demand.findPageOfHourlyEventTag(0..10000) {String mvTag ->
			String pageId = mvTag.split(";")[2]
			return idAsStringToPageMap[pageId]
		}
		measuredValueTagServiceMocked.demand.findBrowserOfHourlyEventTag(0..10000) {String mvTag ->
			String browserId = mvTag.split(";")[3]
			return idAsStringToBrowserMap[browserId]
		}
		measuredValueTagServiceMocked.demand.findLocationOfHourlyEventTag(0..10000) {String mvTag ->
			String locationId = mvTag.split(";")[4]
			return idAsStringToLocationMap[locationId]
		}
		measuredValueTagServiceMocked.demand.findJobGroupOfWeeklyPageTag(0..10000) {String mvTag ->
			String idJobGroup = mvTag.split(";")[0]
			return idAsStringToJobGroupMap[idJobGroup]
		}
		measuredValueTagServiceMocked.demand.findPageOfWeeklyPageTag(0..10000) {String mvTag ->
			String pageId = mvTag.split(";")[1]
			return idAsStringToPageMap[pageId]
		}
		measuredValueTagServiceMocked.demand.findJobGroupOfWeeklyShopTag(0..10000) {String mvTag ->
			return idAsStringToJobGroupMap[mvTag]
		}
		measuredValueTagServiceMocked.demand.isValidHourlyEventTag(1..10000) {String tagToProof ->
			return true // not the concern of the tests
		}

		serviceToMockIn.measuredValueTagService = measuredValueTagServiceMocked.createMock()
	}
	/**
	 * Mocks methods in {@link CsTargetGraphDaoService}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param labelOfActualCsTargetGraph
	 * 		The label of the {@link CsTargetGraph} to be returned from {@link CsTargetGraphDaoService#getActualCsTargetGraph()}. 
	 */
	void mockCsTargetGraphDaoService(serviceToMockIn, String labelOfActualCsTargetGraph){
		def csTargetGraphDaoService = mockFor(CsTargetGraphDaoService, true)
		csTargetGraphDaoService.demand.getActualCsTargetGraph(0..10000) { ->
			return CsTargetGraph.findByLabel(labelOfActualCsTargetGraph)
		}
		serviceToMockIn.csTargetGraphDaoService = csTargetGraphDaoService.createMock()
	}
	/**
	 * Mocks methods of {@link LinkGenerator}.
	 * @param serviceToMockIn 
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param toReturnFromLink
	 * 		To be returned from method {@link LinkGenerator#link()}. 
	 */
	void mockLinkGenerator(serviceToMockIn, String toReturnFromLink){
		def grailsLinkGeneratorMocked = mockFor(LinkGenerator, true)
		grailsLinkGeneratorMocked.demand.link(0..10000) { Map params ->
			return 	toReturnFromLink
		}
		serviceToMockIn.grailsLinkGenerator = grailsLinkGeneratorMocked.createMock()
	}

	/**
	 * Mocks methods of {@link de.iteratec.osm.csi.TimeToCsMappingCacheService}.
	 * @param serviceToMockIn
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param timeToCsMappings
	 * 		To be returned from method {@link de.iteratec.osm.csi.TimeToCsMappingCacheService#getTimeToCsMappings()}.
	 * @param frustrations
	 * 		To be returned from method {@link de.iteratec.osm.csi.TimeToCsMappingCacheService#getCustomerFrustrations(de.iteratec.osm.csi.Page)}
	 */
	void mockTimeToCsMappingService(serviceToMockIn, timeToCsMappings, frustrations){
		def timeToCsMappingCacheService = mockFor(TimeToCsMappingCacheService)
		
		timeToCsMappingCacheService.demand.getTimeToCsMappings(0..25) { ->
			return timeToCsMappings
		}
		timeToCsMappingCacheService.demand.getCustomerFrustrations(0..25) {Page page ->
			return frustrations
		}
		
		serviceToMockIn.timeToCsMappingCacheService = timeToCsMappingCacheService.createMock()
	}
	/**
	 * Mocks methods of {@link de.iteratec.osm.ConfigService}.
	 * @param serviceToMockIn
	 * 		Grails-Service with the service to mock as instance-variable.
	 * @param toReturnFromGetDatabaseDriverClassName
	 * 		To be returned from method {@link de.iteratec.osm.ConfigService#getDatabaseDriverClassName()}.
	 */
	void mockConfigService(serviceToMockIn, String toReturnFromGetDatabaseDriverClassName){
		def configServiceMock = mockFor(ConfigService, true)
		configServiceMock.demand.getDatabaseDriverClassName(0..100){ ->
			return toReturnFromGetDatabaseDriverClassName
		}
		serviceToMockIn.configService = configServiceMock.createMock()
	}
	void mockProxyService(serviceToMockIn){
		def proxyServiceMock = mockFor(ProxyService, true)
		proxyServiceMock.demand.fetchLocations(0..100){WebPageTestServer wptserver ->
			//do nothing, using tests will have to create necessary locations on their own
		}
		serviceToMockIn.proxyService = proxyServiceMock.createMock()
	}
}
