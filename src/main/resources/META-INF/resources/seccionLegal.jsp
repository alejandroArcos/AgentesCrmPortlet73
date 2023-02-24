<%@ include file="./init.jsp"%>
<section class="bodyFields" id="formLegal">
	<div class="accordion md-accordion" id="accordionLeg" role="tablist" aria-multiselectable="true">		
	<!-- Accordion card -->
		<div class="card ">
		<!-- Card header -->
			<div class="card-header btn-blue modificado" role="tab" id="headingDatosLegal">
				<a class="collapsed" data-toggle="collapse" data-parent="#accordionLeg" href="#collapseDatosLegal" aria-expanded="false" aria-controls="collapseDatosLegal">
					<h5 class="mb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_datos_legal" /></h5>
					<i class="fas fa-angle-down rotate-icon"></i>
				</a>
			</div>
			<div id="collapseDatosLegal" class="collapse in" role="tabpanel" aria-labelledby="headingDatosLegal" data-parent="#accordionLeg">
				<div class="card-body">
					<div class="row align-items-center">
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input type="text" pattern="/^\d+/" name="fl_numeroContrato" id="fl_numeroContrato" value=" <fmt:formatNumber type="number" groupingUsed="false" pattern="#########.##" value="${agenteL.numero_contrato}" />" class="form-control numeros"><label for="fl_numeroContrato"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_num_contrato" /></label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_fechaAlta" name="fl_fechaAlta" class="form-control datepicker requerido" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteL.fechaAlta}" />"><label for="fl_fechaAlta"><i class="fa fa-star red-text fa-xs" aria-hidden="true"></i><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_fechaAlta" /></label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_vencimientoContrato" name="fl_vencimientoContrato"class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value="${agenteL.vencimientoContrato}" />"><label for="fl_vencimientoContrato"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_vencimientoContrato" /></label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><select name="fl_tipoCedula" id="fl_tipoCedula" class="mdb-select" searchable='<liferay-ui:message key="AgentesCrmPortlet.buscar"/>'><option value="-1">Seleccionar</option><c:forEach items="${listaCedula}" var="option"><option value="${option.catalogoDetalleId}"  ${agenteL.tipoCedula==option.catalogoDetalleId?'selected':''}>${option.descripcion}</option></c:forEach></select><label for="fl_tipoCedula"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_tipoCedula" /></label></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_vencimientoCedula" name="fl_vencimientoCedula" class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteL.vencimientoCedula}" />"><label for="fl_vencimientoCedula"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_vencimientoCedula" /></label></div></div></div></div>
					</div>
					<div class="row align-items-center">
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_polizaRC" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_polizaRC"  name="fl_polizaRC" type="checkbox" ${agenteL.polizaRC?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_vencimientoPolizaRC" name="fl_vencimientoPolizaRC" class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteL.vencimientoPoliza}" />"><label for="fl_vencimientoPolizaRC"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_vencimientoPolizaRC" /></label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_idApoderado" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_idApoderado"  name="fl_idApoderado" type="checkbox" ${agenteL.idApoderado?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_vencimientoIdApoderado" name="fl_vencimientoIdApoderado" class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteL.vencimientoIdApoderado}" />"><label for="fl_vencimientoIdApoderado"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_vencimientoIdApoderado" /></label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_contrato" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_contrato"  name="fl_contrato" type="checkbox" ${agenteL.contrato?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_compDomicilio" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_compDomicilio"  name="fl_compDomicilio" type="checkbox" ${agenteL.comprobanteDomicilio?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="md-form form-group"><div class="row"><div class="col"><input placeholder="" type="date" id="fl_vencimientoCompDomicilio" name="fl_vencimientoCompDomicilio" class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteL.vencimientoCompDomicilio}" />"><label for="fl_vencimientoCompDomicilio"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_vencimientoCompDomicilio" /></label></div></div></div></div>
					</div>
					<div class="row align-items-center">
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_datosBancarios" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_datosBancarios"  name="fl_datosBancarios" type="checkbox" ${agenteL.datosBancarios?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_autoCNSF" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_autoCNSF"  name="fl_autoCNSF" type="checkbox" ${agenteL.autoCNSF?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_RFC" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_RFC"  name="fl_RFC" type="checkbox" ${agenteL.rfc?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_acta" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_acta"  name="fl_acta" type="checkbox" ${agenteL.acta?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_poder" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_poder"  name="fl_poder" type="checkbox" ${agenteL.poder?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
						<div class="col"><div class="row align-items-end"><div class="label-switch small"><label class="pb-0"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_cartaCompromiso" />:</label></div><div class="col pull-left"><div class="switch"><label>No<input id="fl_cartaCompromiso"  name="fl_cartaCompromiso" type="checkbox" ${agenteL.cartaCompromiso?'checked':''}><span class="lever"></span>Si</label></div></div></div></div>
					</div>
				</div>
			</div>
		</div>
	</div>																																						
</section>
<section class="legal">
	<div class="row">
		<div class="col rechazolegal" style="display:none;"><div class="md-form"><textarea id="fl_observaciones" name ="fl_observaciones" class="md-textarea form-control" rows="2"></textarea><label for="fl_observaciones"><liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.legal.fl_observaciones" /></label></div></div>
	</div>
</section>