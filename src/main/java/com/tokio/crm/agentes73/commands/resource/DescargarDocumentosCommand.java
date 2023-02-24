package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.ListaArchivo;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/agentes/descargarDocumentos" }, service = MVCResourceCommand.class)

public class DescargarDocumentosCommand extends BaseMVCResourceCommand {

//    private static final Log _log = LogFactoryUtil.getLog(DescargarDocumentosCommand.class);
    @Reference
    CrmGenerico _CrmGenericoService;

    User usuario;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
	    throws Exception {

	usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
	String agrupador = ParamUtil.getString(resourceRequest, "agrupador");
	String tipoArchivo = ParamUtil.getString(resourceRequest, "tipoArchivo");
	ListaArchivo listaArchivos = _CrmGenericoService.descargarDocumento(agrupador, tipoArchivo,
		usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
	Gson gson = new Gson();
	String responseString = gson.toJson(listaArchivos);
	PrintWriter writer = resourceResponse.getWriter();
	writer.write(responseString);
    }

}
