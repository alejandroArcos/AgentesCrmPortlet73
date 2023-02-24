<%@ include file="./init.jsp"%>

<portlet:actionURL var="datosAgente" name="/crm/action/agentes/datosAgente" />
<portlet:actionURL var="cargaDatosAgente" name="/crm/action/agentes/cargaDatosAgente" />
<portlet:actionURL var="buscarAgente" name="/crm/action/agentes/buscarAgente" />

<jsp:useBean id="current" class="java.util.Date" />

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<%--<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css?v=${version}&t=${current.time}">--%>
<div id="customAlertJS"></div>
<section class="AgentesCrmPortlet upper-case-all">
	<div class="section-heading">
		<div class="container-fluid">
			<h4 class="title text-left">
				<liferay-ui:message key="AGENTESCRMPORTLET73.title" />
			</h4>
			<div class="section-nav-wrapper"></div>
			<div class="form-wrapper">
				<div class="row">
					<div class="col">
						<div class="row">
							<div class="col-3"></div>
							<div class="col-3">
								<div class="md-form form-group">
									<select id="filtroEstatus-select" name="<portlet:namespace/>filtroEstatus" class="mdb-select" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1">TODOS</option>
										<c:forEach items="${listaEstatus}" var="option">
											<c:choose>
												<c:when test="${perfil==3||perfil==11}">
													<option value="${option.catalogoDetalleId}" ${filtroEstatus==option.catalogoDetalleId?'selected':''}>${fn:replace(option.descripcion, 'LEGAL' , 'PROSPECTO LEGAL')}</option>
												</c:when>
												<c:otherwise>
													<option value="${option.catalogoDetalleId}" ${filtroEstatus==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select> 
									<label for="filtroEstatus-select"> <liferay-ui:message key="AGENTESCRMPORTLET73.filter.estatus" />
									</label>
								</div>

							</div>
							<div class="col-3">
								<div class="md-form form-group">
									<input id="filtroClave" type="text"
										name="<portlet:namespace/>filtroClave" value="${filtroAgente}"
										class="form-control"> <label for="filtroClave">
										<liferay-ui:message key="AGENTESCRMPORTLET73.filter.clave" />
									</label>
								</div>
							</div>
							<div class="col-3"></div>
						</div>
						<div style="display: none;">
							<form class="mb-4" action="${buscarAgente}" method="post" id="buscarAgentesForm">
							<input type="hidden" id="filtroEstatus" name="filtroEstatus" value=""/>
							<input type="hidden" id="filtroAgente" name="filtroAgente" value=""/>
							</form>
						</div>
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div id="buscarAgente" class="btn btn-blue pull-center">
						<liferay-ui:message key="AGENTESCRMPORTLET73.filter.buscar" />
					</div>
				</div>
			</div>			 			
			 
			<div class="row">
				<c:if test="${perfil==8||perfil==9}">
					<div class="col-sm-12 text-right">
						<form class="mb-4" action="${datosAgente}" method="post"
							id="altaAgenteForm">
							<div id="altaAgente" class="btn btn-blue float-right">
								<liferay-ui:message key="AGENTESCRMPORTLET73.button.alta" />
							</div>
						</form>
					</div>
				</c:if>
				<div class="col-md-12"></div>
			</div>
			
		</div>
		<c:choose>
			<c:when test="${perfil==3||perfil==11}">
				<div class="table-wrapper">
					<table class="table data-table-custom table-striped table-bordered"
						   style="width: 100%;" id="tableAgentesLegalCRM">
						<!--  tablaAgente -->
						<thead class="btn-blue">
						<tr>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.contrato" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.estatusVentas" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.estatusVentas" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.estatusLC" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.rfc" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.nombre" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.ejecutivo" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.oficina" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.fechaRegistro" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.fechaSeguimiento" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.fechaAlta" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.tipoCedula" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.vencimientoCedula" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.comprobante" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.vencimientoComprobante" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.polizaRC" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.vencimientoPoliza" /></th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${agentesLista}" var="agente">
							<tr>
								<td>${agente.contrato}</td>
								<td>
									<form class="mb-4" action="${cargaDatosAgente}" method="post" id="verAgente${agente.agenteId}}">
										<div>
											<input type="hidden" name="idAgente" value="${agente.agenteId}">
											<a href="#" onclick="clickAgente(this)">${agente.clave}</a>
										</div>
									</form>
								</td>
								<td>${mapaEstatus[agente.estatusVentas]}</td>
								<td>${mapaEstatusLegal[agente.estatusLC]}</td>
								<td>${agente.rfc}</td>
								<td>${agente.nombre}</td>
								<td>${mapaEjecutivos[agente.ejecutivo]}</td>
								<td>${mapaOficinas[agente.oficina]}</td>
								<td>${agente.fechaRegistro}</td>
								<td>${agente.fechaSeguimiento}</td>
								<td>${agente.fechaAlta}</td>
								<td>${mapaTipoCedula[agente.tipoCedula]}</td>
								<td>${agente.vencimientoCedula}</td>
								<td>${agente.comprobante}</td>
								<td>${agente.vencimientoComprobante}</td>
								<td>${agente.polizaRC}</td>
								<td>${agente.vencimientoPoliza}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</c:when>
			<c:otherwise>
				<div class="table-wrapper sin-filtro">
					<table class="table data-table table-striped table-bordered"
						   style="width: 100%;" id="tableAgentesCRM">
						<!--  tablaAgente -->
						<thead class="btn-blue">
						<tr>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.clave" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.rfc" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.nombre" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.estatus" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.ejecutivo" /></th>
							<th><liferay-ui:message key="AGENTESCRMPORTLET73.table.title.oficina" /></th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="${agentesLista}" var="agente">
							<tr>
								<td>
									<form class="mb-4" action="${cargaDatosAgente}" method="post" id="verAgente${agente.agenteId}}">
										<div>
											<input type="hidden" name="idAgente" value="${agente.agenteId}">
											<a href="#" onclick="clickAgente(this)">${agente.clave!=""?agente.clave:agente.preclave}</a>
										</div>
									</form>
								</td>
								<td>${agente.datosRfc}</td>
								<!-- Validar si es PF o PM -->
								<!-- Cargar Catalogo de TIpos de Sociedad-->
								<td>${agente.nombre} ${agente.apellidoP} ${agente.apellidoM}</td>
								<td>${mapaEstatus[agente.estatusAgenteId]}</td>
								<td>${mapaEjecutivos[agente.ejecutivo]}</td>
								<td>${mapaOficinas[agente.oficinaId]}</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</section>
<script>
	const spanishJson = "<%=request.getContextPath()%>" + "/js/dataTables.spanish.json";
	const mapaEstatusLegal = "${mapaEstatusLegal}";
	$(document).ready(function() {

		$('.data-table-custom').DataTable({
			responsive: true,
			destroy: true,
			dom: 'fBrltip',
			ordering:false,
			ordered:false,
			buttons: [{
				extend:    'excelHtml5',
				text:      '<a class="btn-floating btn-sm teal"><i class="far fa-file-excel"></i></a>',
				titleAttr: 'Exportar XLS',
				className:"btn-unstyled",
			}],
			columnDefs: [
                {targets: '_all', className: "py-2" }
            ],
			language: {
				"url": spanishJson,
			},
			lengthChange: true,
			lengthMenu: [[5,10,15], [5,10,15]],
			pageLength: 10
		});

		$("#altaAgente").click(function() {
			showLoader();
			$("#altaAgenteForm").submit();
		});
		$("#buscarAgente").click(function() {			
			$("#filtroEstatus").val($("#filtroEstatus-select").val());
			$("#filtroAgente").val($("#filtroClave").val());
			showLoader();
			$("#buscarAgentesForm").submit();
		});
		var url = window.location.href;
		url = new URL(url);
		if(url.searchParams.get("xp") === "dGVhbW9nYWJp" ){
			showLoader();
			$("#altaAgenteForm").submit();
		}
	});

	var agente = "";
	function clickAgente(agenteX) {
		agente = agenteX;
		console.log(agente.parentElement.parentElement);
		showLoader();
		agente.parentElement.parentElement.submit();
	}
</script>
