<g:each var="wptServer" in="${servers}">
	<h3>${wptServer.key}</h3>
	
	<g:if test="${wptServer.value instanceof String}">
		<p>Error: ${wptServer.value}</p>
	</g:if>
	<g:else>
		<table class="table-striped">
		<thead>
			<tr>
				<th rowspan="2"><g:message code="queue.id.label" /></th>
				<th rowspan="2"><g:message code="queue.numberOfAgents.label" /></th>
				<th rowspan="2"><g:message code="queue.wptServerJobs.label" /></th>
				<th colspan="2"><g:message code="queue.ism.label" /></th>
				
				<th colspan="3" class="new-col-sec"><g:message code="queue.lastHour.label" /></th>
				<th colspan="2" class="new-col-sec"><g:message code="queue.nextHour.label" /></th>
			</tr>
			<tr>
				<th><g:message code="queue.pendingJobs.label" /></th>
				<th><g:message code="queue.runningJobs.label" /></th>
				
				<th class="new-col-sec"><g:message code="queue.errorsLastHour.label" /></th>
				<th><g:message code="queue.jobResultsLastHour.label" /></th>
				<th><g:message code="queue.eventResultsLastHour.label" /></th>
				
				<th class="new-col-sec"><g:message code="queue.jobsNextHour.label" /></th>
				<th><g:message code="queue.eventsNextHour.label" /></th>
			</tr>
		</thead>
		<tbody class="datarows">
			<g:each var="location" in="${wptServer.value}">
				<tr class="queueRow">
					<td>
						<abbr title="${message(code: "queue.label.label")}: ${location.label}">${location.id}</abbr>
					</td>
					<td>
						${location.agents}
					</td>
					<td>
						${location.jobs}
					</td>
					<td>
						<g:if test="${location.pendingJobs > 0}">
								<a href="#" class="jobDetail" data-status="100">
									<span class="arrow icon-chevron-up"></span>
						</g:if>
						${location.pendingJobs}
						<g:if test="${location.pendingJobs > 0}">
								</a>
						</g:if>
					</td>
					<td>
						<g:if test="${location.runningJobs > 0}">
								<a href="#" class="jobDetail" data-status="101">
									<span class="arrow icon-chevron-up"></span>
						</g:if>
						${location.runningJobs}
						<g:if test="${location.runningJobs > 0}">
								</a>
						</g:if>
					</td>
					<td class="new-col-sec">
						${location.errorsLastHour}
					</td>
					<td>
						${location.jobResultsLastHour}
					</td>
					<td>
						${location.eventResultsLastHour}
					</td>
					<td class="new-col-sec">
						${location.jobsNextHour}
					</td>
					<td>
						${location.eventsNextHour}
					</td>
				</tr>
				<g:if test="${location.executingJobs.size() > 0}">
					<tr class="jobsRow" data-queue="${location.id}">
						<td colspan="10">
						<g:each var="job" in="${location.executingJobs}">
							<strong>${job.key.label}</strong>
							<table>
							<thead>
							 <tr>
							  <th><g:message code="job.status.label" /></th>
							  <th><g:message code="job.whenLaunched.label" /></th>
							 </tr>
							</thead>
							<tbody>
							<g:each var="jobResult" in="${job.value}">
								<tr data-statuscode="${jobResult.httpStatusCode}">
								<td>
									<a href="${jobResult.wptStatus}">${jobResult.getSatusCodeMessage()}</a>
								 </td>
								 <td>
									<g:render template="../job/timeago" model="${[date: jobResult.date]}" />
								</td>
								</tr>
							</g:each>
							</tbody>
							</table>
						</g:each>
						</td>
					</tr>
				</g:if>
			</g:each>
		</tbody>
	</table>
	</g:else>
</g:each>