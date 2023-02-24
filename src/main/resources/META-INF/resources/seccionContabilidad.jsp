<%@ include file="./init.jsp"%>
<section class="bodyFields" id="formContabilidad">
	<div class="accordion md-accordion" id="accordionCont" role="tablist" aria-multiselectable="true">		
		<!-- Accordion card -->
		<div class="card ">
			<!-- Card header -->
			<div class="card-header btn-blue modificado" role="tab" id="headingDatosCont">
				<a class="collapsed" data-toggle="collapse" data-parent="#accordionCont" href="#collapseDatosCont" aria-expanded="false" aria-controls="collapseDatosCont">
					<h5 class="mb-0">&nbsp;</h5>
					<i class="fas fa-angle-down rotate-icon"></i>
				</a>
			</div>
			<div id="collapseDatosCont" class="collapse in" role="tabpanel" aria-labelledby="headingDatosCont" data-parent="#accordionCont">
				<div class="card-body">
					<div class="row align-items-center">
						<div class="col-2"></div>
						<div class="col-.5"></div>
						<div class="col">
							<div class="row align-items-end">
								<div class="label-switch">
									<label class="pb-0">
										<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.contabilidad.fc_registroCuentaB" />:
									</label>
								</div>
								<div class="col pull-left">
									<div class="switch">
										<label>
											No
											<input id="fc_registroCuentaB"  name="fc_registroCuentaB" type="checkbox" ${agenteCc.registroCuentaBancaria?'checked':''} onchange="fechaActual(this);">
											<span class="lever"></span>
											Si
										</label>
									</div>
								</div>
							</div>
						</div>
						<div class="col">
							<div class="md-form form-group">
								<div class="row">
									<div class="col">
										<input placeholder="" type="date" id="fc_fechaRegistroC" name="fc_fechaRegistroC" class="form-control datepicker" value="<fmt:formatDate pattern = "yyyy-MM-dd" value = "${agenteCc.fechaRegistroCuenta}" />">
										<label for="fc_fechaRegistroC">
											<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.contabilidad.fc_fechaRegistroC" />
										</label>
									</div>
								</div>
							</div>
						</div>
						<div class="col-.5"></div>
						<div class="col-2"></div>
					</div>
				</div>
			</div>
		</div>
	</div>																																						
</section>
<section class="contabilidad">
	<div class="row">
		<div class="col rechazoContabilidad" style="display:none;">
			<div class="md-form">
				<textarea id="fc_observaciones" name ="fc_observaciones" class="md-textarea form-control" rows="2"></textarea>
				<label for="fc_observaciones">
					<liferay-ui:message key="AGENTESCRMPORTLET73.alta.form.contabilidad.fc_observaciones" />
				</label>
			</div>
		</div>
	</div>
</section>