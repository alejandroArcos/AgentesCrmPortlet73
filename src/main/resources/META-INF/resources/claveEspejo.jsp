<%@ include file="./init.jsp"%>
<jsp:useBean id="current" class="java.util.Date" />


<portlet:resourceURL id="/crm/agentes/obtenerCP" var="obtenerCPURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/agentes/guardarAgentes" var="guardarAgentesURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/agentes/guardarAgentesManager" var="guardarAgentesManagerURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/agentes/generarPreclave" var="generarPreclaveURL" cacheability="FULL" />
<portlet:resourceURL id="/crm/agentes/buscarEjecutivos" var="buscarEjecutivosURL" cacheability="FULL" />


<script src="<%=request.getContextPath()%>/js/jquery-ui.min.js?v=${version}&t=${current.time}"></script>
<script src="<%=request.getContextPath()%>/js/main.js?v=${version}&t=${current.time}"></script>
<script src="<%=request.getContextPath()%>/js/funcionesGenericas.js?v=${version}&t=${current.time}"></script>
<!--<link rel="stylesheet" href="<%=request.getContextPath()%>/css/main.css?v=${version}&t=${current.time}">-->

<div id="customAlertJS"></div>
<div class="AgentesCrmPortlet upper-case-all">
	<section class="">
		<!-- "upper-case-all">-->
		<div class="section-heading">
			<div class="container-fluid">
				<h4 class="title text-left">
					<c:choose>
						<c:when test="${nuevo==1}">
							<liferay-ui:message key="AGENTESCRMPORTLET73.alta.espejo.title" />
						</c:when>
						<c:when test="${nuevo==0&&esEjecutivo==2}">
							<liferay-ui:message key="AGENTESCRMPORTLET73.autorizacion.espejo.title" />
						</c:when>
						<c:when test="${nuevo==0&&esEjecutivo==1}">
							<liferay-ui:message key="AGENTESCRMPORTLET73.actualizacion.espejo.title" />
						</c:when>
						<c:when test="${nuevo==0&&esEjecutivo==0}">
							<liferay-ui:message key="AGENTESCRMPORTLET73.alta.espejo.title" />
						</c:when>
					</c:choose>
				</h4>
				<div class="section-nav-wrapper"></div>
				<div class="form-wrapper">
					<section id="formEjecutivo" class="bodyFields">												
					<div class="row">
						<div class="col-3"><div class="md-form form-group"><input type="text" name="pd_clave" id="pd_clave" value="${clavePrimaria}" class="form-control" readonly disabled> <label for="pd_clave"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.espejo.pd_clave" /></label></div></div>
						<div class="col-3"><div class="md-form form-group"><input type="text" name="pd_nombre" id="pd_nombre" value="${nombreClavePrimaria}" class="form-control" readonly disabled> <label for="pd_nombre"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.espejo.pd_nombre" /></label></div></div>
						<div class="col-md-3 cn_tipo_persona"><div class="form-inline check-bottom" id="tipo_persona">
								<div class="form-check"><input class="form-check-input form-control" name="dp_tipoPersona" type="radio" id="cn_personamoral" value="2" ${agente.tipoPersona==2?'checked':''} readonly="readonly" disabled="disabled"><label class="form-check-label" for="cn_personamoral"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_tipoPersona.opcion.moral"/></label></div>
								<div class="form-check"><input class="form-check-input form-control" name="dp_tipoPersona" type="radio" id="cn_personafisica" value="1" ${agente.tipoPersona==1?'checked':''} readonly="readonly" disabled="disabled"> <label class="form-check-label" for="cn_personafisica"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_tipoPersona.opcion.fisica"/></label></div>
								</div>
						</div>
						<div class="col-3">
							<div class="md-form form-group seccion-fisica" id="selectRegimen">
								<select name="dg_regimenFiscal" id="dg_regimenFiscal" class="mdb-select" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>' disabled="disabled">
									<option value="-1">Seleccionar</option>
									<c:forEach items="${listaRegimen}" var="option">
										<option value="${option.id}"  ${agente.regimen_id==option.id?'selected':''}>${option.descripcion}</option>
									</c:forEach>
								</select>
								<label for="dg_regimenFiscal">
									<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_regimenFiscal" />
								</label>
							</div>
						</div>
					</div>										
					<div class="accordion md-accordion" id="accordionEx" role="tablist" aria-multiselectable="true">							
					<!-- Accordion card -->
					<div class="card ">			
						<!-- Card header -->
						<div class="card-header btn-blue modificado" role="tab" id="headingDatosAgente">
							<a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="#collapseDatosAgente" aria-expanded="false" aria-controls="collapseDatosAgente">
							<h5 class="mb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.espejo.accordion.ejecutivo.general" /></h5>
							<i class="fas fa-angle-down rotate-icon"></i>
							</a>
						</div>				
					<div id="collapseDatosAgente" class="collapse in" role="tabpanel" aria-labelledby="headingDatosAgente" data-parent="#accordionEx">
					<div class="card-body">										
						<div class="row">
							<div class="col-3" style="display:none;"><div class="md-form form-group"><input type="hidden" maxlength="13" name="dg_rfc" id="dg_rfc" value="${agente.datosRfc}" class="form-control"> <label for="dg_rfc"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_rfc" /></label></div></div>
							<div class="col-md-3 cn_tipo_negocio"><div class="form-inline check-bottom" id="tipo_negocio">
								<div class="form-check"><input class="form-check-input form-control" name="dg_tipoNegocio" type="radio" id="cn_negocioMD" value="46" ${agente.tipoNegocio==46?'checked':''} ${nuevo==1?'checked':''} readonly ${nuevo==1?'':'disabled'}> <label class="form-check-label" for="cn_negocioMD"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_tipoNegocio.opcion.m"/></label></div>
								<div class="form-check"><input class="form-check-input form-control" name="dg_tipoNegocio" type="radio" id="cn_negocioJ" value="47" ${agente.tipoNegocio==47?'checked':''} readonly ${nuevo==1?'':'disabled'}><label class="form-check-label" for="cn_negocioJ"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_tipoNegocio.opcion.j"/></label></div>
								</div>
							</div>
							<div class="col-3">
								<div class="md-form form-group">
									<select name="dg_ejecutivo" id="dg_ejecutivo" class="mdb-select requerido" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1">Seleccionar</option>
										<c:forEach items="${listaUsuarios}" var="option">
											<option oficina="${option.oficina}" value="${option.id}"  ${agente.ejecutivo==option.id?'selected':''}>${option.nombre}</option>
										</c:forEach>
									</select>
									<label for="dg_ejecutivo">
										<i class="fa fa-star red-text fa-xs" aria-hidden="true"></i>
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_ejecutivo" />
									</label>
								</div>
							</div>
							<div class="col-3">
								<div class="md-form form-group">
									<input type="text" name="dg_oficina" id="dg_oficina"  codigo="${mapaOficinas[agente.oficinaId].valorS}" idoficina="${mapaOficinas[agente.oficinaId].catalogoDetalleId}" value="${mapaOficinas[agente.oficinaId].descripcion}" class="form-control-plaintext requerido" readonly>
								<label for="dg_oficina">
									<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_oficina" />
								</label>
							</div></div>
							<div class="col-3" style="display:none;"><div class="md-form form-group">
								<select name="dg_oficina-select" id="dg_oficina-select" class="mdb-select" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
									<c:forEach items="${listaOficina}" var="option">
										<option codigo="${option.valorS}" value="${option.catalogoDetalleId}">${option.descripcion}</option>
									</c:forEach>
								</select>
								<label for="dg_oficina-select"></label>
							</div>
							</div>
							<div class="col-3">
								<div class="md-form form-group">
									<input type="text" maxlength="12" name="dg_preclave" id="dg_preclave" value="${agente.preclave}" class="form-control requerido" readonly disabled>
									<label for="dg_preclave">
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dg_preclave" />
									</label>
								</div>
							</div>
							<div class="col-3" style="display:none;"><div class="md-form form-group"><input type="text" name="mc_agrupador" id="mc_agrupador" value="${agente.agrupador}" class="form-control" readonly disabled> <label for="mc_agrupador"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.mc_agrupador" /></label></div></div>							
						</div>
						<div class="row">
							<div class="col-6"><div class="md-form form-group"><input type="text" name="dp_nombreCompleto" id="dp_nombreCompleto" value="" class="form-control" readonly disabled> <label for="dp_nombreCompleto"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_nombreCompleto" /></label></div></div>
						</div>									
						<div class="row">
							<div class="col-3"><div class="md-form form-group"><input type="text" name="dp_nombre" id="dp_nombre" value="${agente.nombre}" class="form-control" readonly disabled> <label for="dp_nombre"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_nombre" /></label></div></div>
							<div class="col seccion-fisica"><div class="md-form form-group"><input type="text" name="dp_apellidoP" id="dp_apellidoP" value="${agente.apellidoP}" class="form-control" readonly disabled> <label for="dp_apellidoP"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_apellidoP" /></label></div></div>
							<div class="col seccion-fisica"><div class="md-form form-group"><input type="text" name="dp_apellidoM" id="dp_apellidoM" value="${agente.apellidoM}" class="form-control" readonly disabled> <label for="dp_apellidoM"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_apellidoM" /></label></div></div>
							<div class="col-2 seccion-moral" style="display:none;">
								<div class="md-form form-group">
									<select name="dp_tipoSociedad" id="dp_tipoSociedad" class="mdb-select" readonly disabled searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1"  ${nuevo==1?'selected':''}>Seleccionar</option>
										<c:forEach items="${listaTipoSociedad}" var="option">
											<option value="${option.catalogoDetalleId}"  ${agente.tipoSociedad==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option>
										</c:forEach>
									</select>
									<label for="dp_tipoSociedad"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_tipoSociedad" /></label>
								</div>
							</div>
							<div class="col seccion-fisica"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="dp_fechaNacimiento"name="dp_fechaNacimiento"class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agente.fechaNacimientoConstitucion}" />" readonly disabled><label for="dp_fechaNacimiento"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_fechaNacimiento" /></label></div></div></div></div>
							<div class="col-2 seccion-moral" style="display:none;"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="dp_fechaConstitucion"name="dp_fechaConstitucion"class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agente.fechaNacimientoConstitucion}" />" readonly disabled><label for="dp_fechaConstitucion"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_fechaConstitucion" /></label></div></div></div></div>
							<div class="col seccion-fisica">
								<div class="md-form form-group">
									<select name="dp_sexo" id="dp_sexo" class="mdb-select" readonly disabled searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1"  ${nuevo==1?'selected':''}>Seleccionar</option>
										<c:forEach items="${listaSexo}" var="option">
											<option value="${option.catalogoDetalleId}" ${agente.sexo==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option>
										</c:forEach>
									</select>
									<label for="dp_sexo"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dp_sexo" /></label>
								</div>
							</div>
						</div>										
					</div>
					</div>
					</div>
					<div class="card ">			
						<!-- Card header -->
						<div class="card-header btn-blue modificado" role="tab" id="headingDatosDireccion">
							<a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="#collapseDatosDireccion" aria-expanded="false" aria-controls="collapseDatosDireccion">
							<h5 class="mb-0">Direcci&oacute;n</h5>
							<i class="fas fa-angle-down rotate-icon"></i>
							</a>
						</div>				
					<div id="collapseDatosDireccion" class="collapse in" role="tabpanel" aria-labelledby="headingDatosDireccion" data-parent="#accordionEx">
					<div class="card-body">							
						<div class="row ubicacion">							
							<div class="col-3"><div class="md-form form-group"><input readonly disabled type="text" name="dr_cp" id="dr_cp" value="${agenteD.codigo}" maxlength="5" class="form-control cpValido"> <label for="dr_cp"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_cp" /></label></div></div>
							<div class="col-6"><div class="md-form form-group"><input readonly disabled type="text" name="dr_calle" id="dr_calle" value="${agenteD.calle}" class="form-control"> <label for="dr_calle"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_calle" /></label></div></div>
							<div class="col"><div class="md-form form-group"><input readonly disabled type="text" name="dr_numero" id="dr_numero" value="${agenteD.noExt}" class="form-control"> <label for="dr_numero"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_numero" /></label></div></div>
							<div class="col"><div class="md-form form-group"><input readonly disabled type="text" name="dr_numeroi" id="dr_numeroi" value="${agenteD.noInt}" class="form-control"> <label for="dr_numeroi"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_numeroi" /></label></div></div>
						</div>
						<div class="row">
							<div class="col">
								<div class="md-form form-group">
									<select name="dr_colonia" id="dr_colonia" class="mdb-select" readonly disabled searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1">Seleccionar</option>
										<c:forEach items="${cpData}" var="cpItem">
											<option value="${cpItem.id}" ${cpItem.id==agenteD.cpId?'selected':''}>${cpItem.colonia}</option>
										</c:forEach>
									</select>
									<label for="dr_colonia"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_colonia" /></label>
								</div>
							</div>
							<div class="col"><div class="md-form form-group"><input type="text" name="dr_municipio" id="dr_municipio" value="${dMunicipio}" class="form-control" readonly> <label for="dr_municipio"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_municipio" /></label></div></div>
							<div class="col"><div class="md-form form-group"><input type="text" name="dr_estado" id="dr_estado" value="${dEstado}" class="form-control" readonly> <label for="dr_estado"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dr_estado" /></label></div></div>
						</div>
					</div>
					</div>
					</div>
					<div class="card ">			
						<!-- Card header -->
						<div class="card-header btn-blue modificado" role="tab" id="headingDatosContacto">
							<a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="#collapseDatosContacto" aria-expanded="false" aria-controls="collapseDatosContacto">
							<h5 class="mb-0">Contacto</h5>
							<i class="fas fa-angle-down rotate-icon"></i>
							</a>
						</div>				
					<div id="collapseDatosContacto" class="collapse in" role="tabpanel" aria-labelledby="headingDatosContacto" data-parent="#accordionEx">
					<div class="card-body">
						<div class="row">
							<div class="col">
								<div class="md-form form-group">
									<input type="text" name="dc_nombre_p1" id="dc_nombre_p1" value="${agenteD.nombreContacto1}" class="form-control requerido" >
									<label for="dc_nombre_p1">
										<i class="fa fa-star red-text fa-xs" aria-hidden="true"></i>
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_nombre_p1" />
									</label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<input type="text" maxlength="10" name="dc_telefono1_p1" id="dc_telefono1_p1" class="numeros form-control requerido" value="${agenteD.tel1Contacto1.substring(0,fn:indexOf(agenteD.tel1Contacto1,"|"))}">
									<label for="dc_telefono1_p1">
										<i class="fa fa-star red-text fa-xs" aria-hidden="true"></i>
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_telefono1_p1" />
									</label>
								</div>
							</div>
							<div class="col-1">
								<div class="md-form form-group">
									<input type="text" maxlength="6" name="dc_ext1_p1" id="dc_ext1_p1" value="${agenteD.tel1Contacto1.substring(fn:indexOf(agenteD.tel1Contacto1,"|") + 1)}" class="numeros form-control" >
									<label for="dc_ext1_p1"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_ext1_p1" /></label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<input type="text" maxlength="10" name="dc_telefono2_p1" id="dc_telefono2_p1" value="${agenteD.tel2Contacto1.substring(0,fn:indexOf(agenteD.tel2Contacto1,"|"))}" class="numeros form-control" >
									<label for="dc_telefono2_p1"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_telefono2_p1" /></label>
								</div>
							</div>
							<div class="col-1">
								<div class="md-form form-group">
									<input type="text" maxlength="6" name="dc_ext2_p1" id="dc_ext2_p1" value="${agenteD.tel2Contacto1.substring(fn:indexOf(agenteD.tel2Contacto1,"|") + 1)}" class="numeros form-control" >
									<label for="dc_ext2_p1"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_ext2_p1" /></label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<input type="email" name="dc_email_p1" id="dc_email_p1" value="${agenteD.emailContacto1}" class="form-control requerido">
									<label for="dc_email_p1" >
										<i class="fa fa-star red-text fa-xs" aria-hidden="true"></i>
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_email_p1" />
									</label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<select name="dc_perfil_p1" id="dc_perfil_p1" class="mdb-select requerido" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1">Seleccionar</option>
										<c:forEach items="${listaPerfilesContacto}" var="option">
											<option value="${option.catalogoDetalleId}" ${agenteD.perfil1==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option>
										</c:forEach>
									</select>
									<label for="dc_perfil_p1">
										<i class="fa fa-star red-text fa-xs" aria-hidden="true"></i>
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_perfil_p1" />
									</label>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col"><div class="md-form form-group"><input type="text" name="dc_nombre_p2" id="dc_nombre_p2" value="${agenteD.nombreContacto2}" class="form-control" > <label for="dc_nombre_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_nombre_p2" /></label></div></div>
							<div class="col">
								<div class="md-form form-group">
									<input type="text" maxlength="10"  name="dc_telefono1_p2" id="dc_telefono1_p2" value="${agenteD.tel1Contacto2.substring(0,fn:indexOf(agenteD.tel1Contacto2,"|"))}" class="numeros form-control" >
									<label for="dc_telefono1_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_telefono1_p2" /></label>
								</div>
							</div>
							<div class="col-1">
								<div class="md-form form-group">
									<input type="text" maxlength="6" name="dc_ext1_p2" id="dc_ext1_p2" value="${agenteD.tel1Contacto2.substring(fn:indexOf(agenteD.tel1Contacto2,"|") + 1)}" class="numeros form-control" >
									<label for="dc_ext1_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_ext1_p2" /></label>
								</div>
							</div>
							<div class="col"><div class="md-form form-group"><input type="text" maxlength="10" name="dc_telefono2_p2" id="dc_telefono2_p2" value="${agenteD.tel2Contacto2.substring(0,fn:indexOf(agenteD.tel2Contacto2,"|"))}" class="numeros form-control" > <label for="dc_telefono2_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_telefono2_p2" /></label></div></div>
							<div class="col-1">
								<div class="md-form form-group">
									<input type="text" maxlength="6" name="dc_ext2_p2" id="dc_ext2_p2" value="${agenteD.tel2Contacto2.substring(fn:indexOf(agenteD.tel2Contacto2,"|") + 1)}" class="numeros form-control" >
									<label for="dc_ext2_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_ext2_p2" /></label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<input type="email" name="dc_email_p2" id="dc_email_p2" value="${agenteD.emailContacto2}" class="form-control">
									<label for="dc_email_p2" ><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_email_p2" /></label>
								</div>
							</div>
							<div class="col">
								<div class="md-form form-group">
									<select name="dc_perfil_p2" id="dc_perfil_p2" class="mdb-select" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'>
										<option value="-1">Seleccionar</option>
										<c:forEach items="${listaPerfilesContacto}" var="option">
											<option value="${option.catalogoDetalleId}" ${agenteD.perfil2==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option>
										</c:forEach>
									</select>
									<label for="dc_perfil_p2"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.dc_perfil_p2" /></label>
								</div>
							</div>
						</div>
					</div>
					</div>
					</div>
					
					</div>
					</section>
					<section class="manager">
						<div class="row">
							<div class="col rechazoManager" style="display:none;">
								<div class="md-form">
									<textarea id="fm_observaciones" name ="fm_observaciones" class="md-textarea form-control" rows="2"></textarea>
									<label for="fm_observaciones"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.ejecutivo.fm_observaciones" /></label>
								</div>
							</div>
						</div>
					</section>
					<c:if test="${!empty rechazos}">
						<%@ include file="./tablaRechazos.jsp"%>
					</c:if>														
					<c:choose>								
						<c:when test="${perfilUsuario==8||perfilUsuario==9}">					
							<section class="ejecutivo">						
									<div class="row text-center">
										<div class="col-md"></div>
										<div class="col-md-1.5">
											<button class="btn btn-blue" id="enviarEjecutivo">Enviar</button>
										</div>
										<div class="col-md-1.5">
											<button class="btn btn-pink cancelar" >Cancelar</button>
										</div>
										<div class="col-md"></div>
									</div>						
							</section>					
						</c:when>
						<c:when test="${perfilUsuario==16}">
							<section class="manager">						
									<div class="row text-center">
										<div class="col-md"></div>
										<div class="col-md-1.5">
											<button class="btn btn-blue" id="autorizarManager">Autorizar</button>
										</div>
										<div class="col-md-1.5">
											<button class="btn btn-blue" id="rechazarManager">Rechazar</button>
										</div>
										<div class="col-md-1.5">
											<button class="btn btn-pink cancelar">Cancelar</button>
										</div>
										<div class="col-md"></div>
									</div>						
							</section>
						</c:when>
					</c:choose>					
					<form action="" id="fin"></form>
				</div>
			</div>
		</div>		
	</section>
	<c:if test="${perfilUsuario==17}">	 
		<div class="modal" id="modal-clave" tabindex="-1" role="dialog"
		aria-labelledby="" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header  blue-gradient text-white">
					<h5 class="modal-title"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.modal.clave.title" /></h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="form-wrapper">
						<div class="row">
							<div class="col">
								<liferay-ui:message key="AGENTESCRMPORTLET73.alta.modal.clave.title" />
								<div style="font-weight: bold;" id="claveDefinitiva"></div>
							</div>
						</div>
						<div class="row">
							<div class="col text-center">
								<button class="btn btn-blue pull-center" data-dismiss="modal"
									onclick="regresar()"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.modal.clave.button" /></button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		</div>
	</c:if>
</div>

<script>
	const perfil = "${perfilUsuario}";
	var obtenerCPURL = "${obtenerCPURL}";
	var guardarAgentesURL = "${guardarAgentesURL}";
	var guardarAgentesManagerURL= "${guardarAgentesManagerURL}";
	var generarPreclaveURL="${generarPreclaveURL}";	
	var buscarEjecutivosURL="${buscarEjecutivosURL}";
	var isClaveEspejo=true;
	var esSoloLectura="${readOnly}";
	var numeroAgenteId="${agente.agenteId}";
	var numeroAgentePadreId="${agente.agentePadreId}";
	var nuevo="${nuevo}";	
	var estatusAutorizado="${agente.estatusAgenteId==7}"
	var estatusRevisionLegal="${agente.estatusAgenteId==8}"
	var estatusRevisionConta="${agente.estatusAgenteId==10}"
	var estatusAutorizar="${agente.estatusAgenteId==58}"
	
	$(document).ready(()=>{
		window.scrollTo(0, 0);
		$('.datepicker').pickadate({
			format : 'yyyy-mm-dd', /* 'dd/mm/yyyy', */
			formatSubmit : 'yyyy-mm-dd'
		});
		$("#dg_ejecutivo").change(()=>{cambiaOficina();});
		$('#tipo_negocio input').change(()=>{cargarEjecutivos();});
		$("#dp_nombre").blur(()=>{nombreCompleto();});
		$("#dp_apellidoP").blur(()=>{nombreCompleto();});
		$("#dp_apellidoM").blur(()=>{nombreCompleto();});
		$("#dp_tipoSociedad").blur(()=>{nombreCompleto();});		
		$("#enviarEjecutivo").click(()=>{showLoader();guardarAgente(true);});
		$("#autorizarManager").click(()=>{showLoader();guardarAgenteManager(false);});		
		$("#rechazarManager").click(()=>rechazoM());		
		$("#modal-clave button.accept").click(()=>{showLoader();$('#fin').submit();});
		$(".cancelar").click(()=>{$('#fin').submit();});
		asignaCP();
		validarNumeros();
		inicializarSecciones();
		if(nuevo==0){
			inicializarSecciones();
			if(esSoloLectura==1){
				$("#dg_ejecutivo").attr("disabled","disabled");
				$("#dg_oficina-select").attr("disabled","disabled");
				$("#dp_tipoSociedad").attr("disabled","disabled");
				$("#dp_sexo").attr("disabled","disabled");
				$("#dr_colonia").attr("disabled","disabled");
				$("#dc_perfil_p1").attr("disabled","disabled");
				$("#dc_perfil_p2").attr("disabled","disabled");
				soloLectura();
			}
			if(estatusAutorizado=="true"){
				soloLectura();
				$("#enviarEjecutivo").hide();
				$("#autorizarLegal").hide();
				$("#autorizarContabilidad").hide();
				$("#rechazarLegal").hide();
				$("#rechazarContabilidad").hide();
			}
			if(estatusRevisionLegal=="true"||estatusRevisionConta=="true"||estatusAutorizar=="true"){
				$("#enviarEjecutivo").hide();
			}
		}
		$("#dg_ejecutivo").trigger("change");
	});
	
</script>
