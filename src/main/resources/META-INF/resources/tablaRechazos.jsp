<%@ include file="./init.jsp"%>
<section>
	<div class="row">		
		<div class="col">
			<h5 class="title text-center">
				<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_observaciones" />
			</h5>
		</div>		
	</div>
	<div class="row">
		<div class="col-3"></div>
		<div class="col-6">
			<div class="table-wrapper sin-filtro sin-registros">
				<table class="table data-table table-striped table-bordered"
					style="width: 100%;" id="tableAgentesCRM">
					<!--  tablaAgente -->
					<thead class="btn-blue">
						<tr>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.rechazos.title.motivo" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.rechazos.title.fecha" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.rechazos.title.usuario" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${rechazos}" var="rechazo">
							<tr>
								<td>${rechazo.motivo}</td>
								<td><fmt:formatDate pattern="yyyy-MM-dd" value="${rechazo.fechaCreacion}" /></td>
								<td>${mapaUsuariosRechazo[rechazo.userCreacion]}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="col-3"></div>
	</div>
</section>