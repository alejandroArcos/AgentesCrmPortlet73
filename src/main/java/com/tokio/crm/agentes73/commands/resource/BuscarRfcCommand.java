package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.CRMRfcResponse;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/agentes/buscarRfc" }, service = MVCResourceCommand.class)

public class BuscarRfcCommand extends BaseMVCResourceCommand {
	
	@Reference
	AgenteLocalService _AgenteLocalService;

    //private static final Log _log = LogFactoryUtil.getLog(BuscarRfcCommand.class);
     @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
	    throws Exception {

	String rfc = ParamUtil.getString(resourceRequest, "rfc");
	long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
	List<Agente> agentes = _AgenteLocalService.findByRfcAgente(rfc);
	Gson gson = new Gson();
	CRMRfcResponse respuesta = new CRMRfcResponse();
	Optional<Agente> res = agentes.stream().filter(agnt->agnt.getAgenteId()!=agenteId).findAny();
	if (res.isPresent()) {
	    respuesta.setAgenteId(res.get().getAgenteId());
	    respuesta.setRfc(res.get().getDatosRfc());
	    respuesta.setCode(1);
	    respuesta.setMsg("Ya hay un agente preexistente con ese RFC");
	} else {
	    respuesta.setCode(0);
	    respuesta.setMsg("No hay agentes preexistentes con ese RFC");
	}
	String responseString = gson.toJson(respuesta);
	PrintWriter writer = resourceResponse.getWriter();
	writer.write(responseString);
    }

}
