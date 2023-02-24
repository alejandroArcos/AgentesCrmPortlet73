package com.tokio.crm.agentes73.commands.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Bean.Registro;
import com.tokio.crm.crmservices73.Bean.UsuarioCrm;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.init-param.copy-request-parameters=true",
	"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/action/agentes/datosAgente" }, service = MVCActionCommand.class)

public class DatosAgenteActionCommand extends BaseMVCActionCommand {

    @Reference
    CrmGenerico _CrmGenericoService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;

    User usuario;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

	HttpServletRequest originalRequest = PortalUtil
		.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
	// String id = originalRequest.getParameter("idAgente");
	usuario = (User) originalRequest.getAttribute(WebKeys.USER);
	User_Crm user_Crm = _User_CrmLocalService.getUser_Crm(new Long(usuario.getUserId()).intValue());
	actionRequest.setAttribute("perfilUsuario", user_Crm.getPerfilId());
	
	switch (user_Crm.getPerfilId()) {
	// Perfiles de Legal
	case 3:
	case 11:
	    actionRequest.setAttribute("esEjecutivo",0);
	    actionRequest.setAttribute("readOnly",1);
	    break;
	// Perfiles de Contabilidad
	case 1:
	case 10:
	    actionRequest.setAttribute("esEjecutivo",0);
	    actionRequest.setAttribute("readOnly",1);
	    break;
	default:
	    actionRequest.setAttribute("esEjecutivo",1);
	    actionRequest.setAttribute("readOnly",0);
	    break;
	}
	
	cargaCatalogos(actionRequest);
	cargaInformacion(actionRequest);
	actionRequest.setAttribute("nuevo", 1);
	actionResponse.setRenderParameter("jspPage", "/datosAgente.jsp");
    }

    public void cargaInformacion(ActionRequest actionRequest) {
	/*
	 * List<Agente> agentes = _AgenteLocalService.findByEstatusAgente(1);
	 * actionRequest.setAttribute("agentesLista", agentes);
	 */
    }

    public void cargaCatalogos(ActionRequest actionRequest) {
	try {
	    ListaRegistro listaDocumentos = _CrmGenericoService.getCatalogo(
			CrmServiceKey.TMX_CTE_ROW_TODOS,
			CrmServiceKey.TMX_CTE_TRANSACCION_GET,
			CrmServiceKey.LIST_CAT_DOCUMENTOS_AGENTES,
			CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
			usuario.getScreenName(),
			AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73
		);
		actionRequest.setAttribute("listaDocumentos", listaDocumentos.getLista().stream().filter(r -> CrmDatabaseKey.DOC_FINAL.equals(r.getTipo())).sorted(Comparator.comparing(Registro::getCodigo)).collect(Collectors.toList()));
		ListaRegistro listaRegistro = _CrmGenericoService.getCatalogo(CrmServiceKey.TMX_CTE_ROW_TODOS,CrmServiceKey.TMX_CTE_TRANSACCION_GET,"CATREGIMEN",CrmServiceKey.TMX_CTE_CAT_ACTIVOS,usuario.getScreenName(),AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
		actionRequest.setAttribute("listaRegimen",listaRegistro.getLista());
	}catch (Exception e) {
	    e.printStackTrace();
	}	
	// Llamamos a los servicios del Portlet
	try {
	    List<Catalogo_Detalle> perfilesContacto = _Catalogo_DetalleLocalService.findByCodigo("CATPERCON");
	    actionRequest.setAttribute("listaPerfilesContacto", perfilesContacto);
	    List<Catalogo_Detalle> tiposSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC");
	    actionRequest.setAttribute("listaTipoSociedad", tiposSociedad);
	    List<Catalogo_Detalle> sexos = _Catalogo_DetalleLocalService.findByCodigo("CATSEX");
	    actionRequest.setAttribute("listaSexo", sexos);
	    List<Catalogo_Detalle> oficinas = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
	    actionRequest.setAttribute("listaOficina",oficinas);
	    actionRequest.setAttribute("mapaOficinas", oficinas.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Catalogo_Detalle::getDescripcion)));
	    List<User_Crm> ejecutivos = _User_CrmLocalService.getUsers_CrmByAreaPerfil(CrmDatabaseKey.AREA_NEGOCIO_M,CrmDatabaseKey.ID_PERFIL_EJECUTIVO_VENTAS);
	    List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByAreaPerfil(CrmDatabaseKey.AREA_NEGOCIO_M,CrmDatabaseKey.ID_PERFIL_ANALISTA_VENTAS);
	    List<UsuarioCrm> listadoUsuarios = Stream.concat(ejecutivos.stream(), analistas.stream()).map(usr -> {
		try {
		    return new UsuarioCrm(UserLocalServiceUtil.getUserById(new Long(usr.getUserId())).getFullName().toUpperCase(), usr.getUserId(), usr.getOficina());
		} catch (PortalException e) {
		    return null;
		}
	    }).collect(Collectors.toList());	    
	    ejecutivos = null;
	    analistas = null;
	    actionRequest.setAttribute("listaUsuarios", listadoUsuarios);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
