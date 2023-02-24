package com.tokio.crm.agentes73.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.agentes73.model.AgenteLegalResponse;
import com.tokio.crm.agentes73.service.GeneraAgenteLegalResponse;
import com.tokio.crm.crmservices73.Bean.Ejecutivo;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.crmservices73.Util.ListUtil;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.AgenteModel;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Catalogo_DetalleModel;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author urielfloresvaldovinos
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=AgentesCrmPortlet73",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"com.liferay.portlet.private-session-attributes=false",
        "com.liferay.portlet.requires-namespaced-parameters=false",
        "com.liferay.portlet.private-request-attributes=false"
	},
	service = Portlet.class
)
public class AgentesCrmPortlet73Portlet extends MVCPortlet {
	
	private static final Log _log = LogFactoryUtil.getLog(AgentesCrmPortlet73Portlet.class);
    @Reference
    CrmGenerico _CrmGenericoService;

    User usuario;

    @Reference
    GeneraAgenteLegalResponse generaAgenteLegalResponse;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
	AgenteLocalService _AgenteLocalService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {
        usuario = (User) renderRequest.getAttribute(WebKeys.USER);
        cargaCatalogos(renderRequest);
        cargaInformacion(renderRequest);
        super.doView(renderRequest, renderResponse);
    }

    public void cargaInformacion(RenderRequest renderRequest) {
    	
    	_log.info("Estoy cargando informacion");
    	
        List<Agente> agentes;
        List<AgenteLegalResponse> agenteLegalResponses = new ArrayList<>();
        List<Catalogo_Detalle> estatus = _Catalogo_DetalleLocalService.findByCodigo("CATESTATUS");
        Map<Long, String> mapaEstatus = estatus.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Catalogo_Detalle::getDescripcion));
        try {
            User_Crm user_Crm = _User_CrmLocalService.getUser_Crm(new Long(usuario.getUserId()).intValue());
            renderRequest.setAttribute("perfil",user_Crm.getPerfilId());

            switch (user_Crm.getPerfilId()) {
                // Perfiles Manager
                case CrmDatabaseKey.ID_PERFIL_MANAGER_CONTABILIDAD:
                case CrmDatabaseKey.ID_PERFIL_ANALISTA_CONTABILIDAD:
                    agentes = _AgenteLocalService.findByEstatusFiltroUsuario(CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD + "", "%", "%");
                    renderRequest.setAttribute("listaEstatus", estatus.stream().filter(e -> e.getCatalogoDetalleId() == CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD).collect(Collectors.toList()));
                    break;
                case CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL:
                case CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL:
                    Map<Integer, String> mapaSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC").stream().collect(Collectors.toMap(e -> (int) e.getCatalogoDetalleId(), Catalogo_Detalle::getDescripcion));
                    agentes = _AgenteLocalService.findByEstatusFiltroUsuarioLegal(CrmDatabaseKey.ESTATUS_REVISION_LEGAL + "", "%",new ArrayList<>());
                    agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal(CrmDatabaseKey.ESTATUS_ACTUALIZACION_LEGAL + "", "%",new ArrayList<>()));
                    agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal( CrmDatabaseKey.ESTATUS_AUTORIZACION_RFC_LEGAL + "", "%", new ArrayList<>()));
                    agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal( CrmDatabaseKey.ESTATUS_AUTORIZACION_LEGAL + "",  "%", new ArrayList<>()));
                    agenteLegalResponses.addAll(generaAgenteLegalResponse.generaAgenteLegalResponse(agentes,mapaSociedad));
                    List<Catalogo_Detalle> registros = _Catalogo_DetalleLocalService.findByCodigo("CATTIPCED");
                    renderRequest.setAttribute("mapaTipoCedula",registros.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion)));
                    registros = _Catalogo_DetalleLocalService.findByCodigo("CATESTATUSLEGAL");
                    estatus.addAll(registros);
                    estatus = estatus.stream().filter(f -> {
                        switch ((int) f.getCatalogoDetalleId()) {
                            case CrmDatabaseKey.ESTATUS_REVISION_LEGAL:
                            //case CrmDatabaseKey.ESTATUS_ACTUALIZACION_LEGAL:
                            case CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL:
                            case CrmDatabaseKey.ACTIVO:
                            case CrmDatabaseKey.INACTIVO:
                            case CrmDatabaseKey.BAJA:
                            case CrmDatabaseKey.ACTUALIZACION:
                                return true;
                            default:
                                return false;
                        }
                    }).collect(Collectors.toList());
                    _log.debug(registros.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_DetalleModel::getDescripcion)));
                    renderRequest.setAttribute("mapaEstatusLegal",registros.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_DetalleModel::getDescripcion)));
                    renderRequest.setAttribute("listaEstatus", estatus);
                    break;
                case CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS:
                    agentes = _AgenteLocalService.findByEstatusFiltroManager("" + CrmDatabaseKey.ESTATUS_PENDIENTE_AUTORIZAR, usuario.getUserId() + "", "%", null);
                    renderRequest.setAttribute("listaEstatus", estatus);
                    break;
                // Perfil Master
                case 100:
                    agentes = _AgenteLocalService.findByEstatusFiltroMaster("%", "%");
                    renderRequest.setAttribute("listaEstatus", estatus);
                    break;
                // Perfiles Otros
                default:
                    agentes = _AgenteLocalService.findByEstatusFiltroUsuario(CrmDatabaseKey.ESTATUS_PENDIENTE_AUTORIZAR + "", usuario.getUserId() + "", "%");
                    renderRequest.setAttribute("listaEstatus", estatus);
                    break;
            }
            _log.debug(agentes);
            if(user_Crm.getPerfilId() == CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL || user_Crm.getPerfilId() == CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL){
                Map<Long, String> mapaEjecutivos = agenteLegalResponses.stream().filter(ListUtil.distinctByKey(AgenteLegalResponse::getEjecutivo))
                                .map(a ->{
                                  try {
                                      return new Ejecutivo(a.getEjecutivo(), UserLocalServiceUtil.getUserById(a.getEjecutivo()).getFullName().toUpperCase());
                                  }catch (PortalException ignored){
                                	  
                                	  _log.error(ignored.getMessage(), ignored);
                                	  
                                      return null;
                                  }
                                }).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo,Ejecutivo::getNombreEjecutivo));
                _log.debug(mapaEjecutivos);
                renderRequest.setAttribute("mapaEjecutivos", mapaEjecutivos);
                renderRequest.setAttribute("agentesLista",agenteLegalResponses);
            }else{
                Map<Long, String> mapaEjecutivos = agentes.stream().filter(ListUtil.distinctByKey(AgenteModel::getEjecutivo))
                        .map(a -> {
                            try {
                                return new Ejecutivo(a.getEjecutivo(), UserLocalServiceUtil.getUserById(a.getEjecutivo()).getFullName().toUpperCase());
                            } catch (PortalException ignored) {
                            	
                            	_log.error(ignored.getMessage(), ignored);
                            	
                                return null;
                            }
                        }).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo, Ejecutivo::getNombreEjecutivo));
                _log.debug(mapaEjecutivos);
                renderRequest.setAttribute("mapaEjecutivos", mapaEjecutivos);
                renderRequest.setAttribute("agentesLista", agentes);
            }
            renderRequest.setAttribute("mapaEstatus", mapaEstatus);
        } catch (Exception e1) {
        	
        	_log.error(e1.getMessage(), e1);
        }
    }

    public void cargaCatalogos(RenderRequest renderRequest) {
    	
    	_log.info("Estoy cargando catalogos");
    	
        try {
            List<Catalogo_Detalle> oficinas = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
            renderRequest.setAttribute("listaOficina", oficinas);
            renderRequest.setAttribute("mapaOficinas", oficinas.stream().collect(
                    Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Catalogo_Detalle::getDescripcion)));
        } catch (Exception e) {
        	
        	_log.error(e.getMessage(), e);
        }
    }
}