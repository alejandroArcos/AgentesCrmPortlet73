package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;

import java.io.PrintWriter;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
		"mvc.command.name=/crm/agentes/obtenerCP" }, service = MVCResourceCommand.class)

public class ObtenerCPCommand extends BaseMVCResourceCommand {


	@Reference
	CrmGenerico _CrmGenericoService;
	
	User usuario;
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
															
		usuario=(User) resourceRequest.getAttribute(WebKeys.USER);
		String numeroCP= ParamUtil.getString(resourceRequest, "cp");		
		ListaCpData listaCpData=_CrmGenericoService.getCatalogoCP(numeroCP, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
		Gson gson = new Gson();				
		String responseString = gson.toJson(listaCpData);
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);		
	}

}
