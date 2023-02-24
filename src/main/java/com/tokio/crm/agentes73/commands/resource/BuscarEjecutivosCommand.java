package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.CRMUsuarioCrmResponse;
import com.tokio.crm.crmservices73.Bean.UsuarioCrm;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/agentes/buscarEjecutivos" }, service = MVCResourceCommand.class)

public class BuscarEjecutivosCommand extends BaseMVCResourceCommand {

    private static final Log _log = LogFactoryUtil.getLog(BuscarEjecutivosCommand.class);
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;

	@Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
	    throws Exception {

	int tipoNegocio = ParamUtil.getInteger(resourceRequest, "tipoNegocio");	
	Gson gson = new Gson();
	CRMUsuarioCrmResponse respuesta = new CRMUsuarioCrmResponse();
	try {
	    List<User_Crm> ejecutivos = _User_CrmLocalService.getUsers_CrmByAreaPerfil(tipoNegocio==CrmDatabaseKey.NEGOCIO_M?CrmDatabaseKey.AREA_NEGOCIO_M:CrmDatabaseKey.AREA_NEGOCIO_J,
		    CrmDatabaseKey.ID_PERFIL_EJECUTIVO_VENTAS);
	    List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByAreaPerfil(tipoNegocio==CrmDatabaseKey.NEGOCIO_M?CrmDatabaseKey.AREA_NEGOCIO_M:CrmDatabaseKey.AREA_NEGOCIO_J,
		    CrmDatabaseKey.ID_PERFIL_ANALISTA_VENTAS);
	    List<UsuarioCrm> listadoUsuarios = Stream.concat(ejecutivos.stream(), analistas.stream())
		    .map(usr -> {
			try {
			    return new UsuarioCrm(UserLocalServiceUtil.getUserById(usr.getUserId()).getFullName().toUpperCase(), usr.getUserId(), usr.getOficina());
			} catch (PortalException e) {
			    return null;
			}
		    })
		    .collect(Collectors.toList());
		respuesta.setCode(0);
	    respuesta.setMsg("Consulta de Ejecutivos realizada con éxito");
	    respuesta.setEjecutivos(listadoUsuarios);	    
	} catch (Exception ex) {
	    respuesta.setCode(1);
	    respuesta.setMsg(ex.getMessage());
	    ex.printStackTrace();
	}
	String responseString = gson.toJson(respuesta);
	_log.debug(responseString);
	PrintWriter writer = resourceResponse.getWriter();
	writer.write(responseString);
    }

}
