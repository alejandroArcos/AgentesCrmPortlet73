<%@ include file="./init.jsp"%>
<hr class="divisor"/>
<section>
	<div class="row">		
		<div class="col">
			<h5 class="title text-center">
				<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.cambios" />
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
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.cambios.title.fecha" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.cambios.title.motivo" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.cambios.title.usuario" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${cambios}" var="cambio">
							<tr>
								<td><fmt:formatDate pattern="yyyy-MM-dd" value="${cambio.fechaCreacion}" /></td>
								<td>${cambio.motivo}</td>
								<td>${mapaUsuariosCambio[cambio.userCreacion]}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="col-3"></div>
	</div>
</section>
<hr class="divisor"/>