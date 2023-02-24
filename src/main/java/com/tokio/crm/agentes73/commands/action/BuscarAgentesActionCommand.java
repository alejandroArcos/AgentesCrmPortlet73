package com.tokio.crm.agentes73.commands.action;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.init-param.copy-request-parameters=true",
        "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/action/agentes/buscarAgente"}, service = MVCActionCommand.class)

public class BuscarAgentesActionCommand extends BaseMVCActionCommand {
    private static final Log _log = LogFactoryUtil.getLog(BuscarAgentesActionCommand.class);

    User usuario;

    @Reference
    CrmGenerico _CrmGenericoService;

    @Reference
    GeneraAgenteLegalResponse generaAgenteLegalResponse;
    
    @Reference
	AgenteLocalService _AgenteLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) {

        HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
        usuario = (User) originalRequest.getAttribute(WebKeys.USER);
        cargaCatalogos(actionRequest);
        cargaInformacion(actionRequest, Integer.valueOf(originalRequest.getParameter("filtroEstatus")), originalRequest.getParameter("filtroAgente"));
        actionRequest.setAttribute("filtroEstatus", originalRequest.getParameter("filtroEstatus"));
        actionRequest.setAttribute("filtroAgente", originalRequest.getParameter("filtroAgente"));
        actionResponse.setRenderParameter("jspPage", "/view.jsp");
    }

    public void cargaInformacion(ActionRequest actionRequest, Integer estatusAgente, String filtroAgente) {
        List<Agente> agentes;//_AgenteLocalService.findByEstatusAgente(estatusAgente);
        List<Catalogo_Detalle> estatus = _Catalogo_DetalleLocalService.findByCodigo("CATESTATUS");
        List<AgenteLegalResponse> agenteLegalResponses = new ArrayList<>();
        actionRequest.setAttribute("mapaEstatus", estatus.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Catalogo_Detalle::getDescripcion)));
        long companyId = CompanyThreadLocal.getCompanyId();
        User user;
        try {
            List<Agente> agentesEjecutivo = new ArrayList<>();
            List<Agente> agentesAux;
            User_Crm user_Crm = _User_CrmLocalService.getUser_Crm(new Long(usuario.getUserId()).intValue());
            List<User> usuariosAux = UserLocalServiceUtil.getCompanyUsers(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
            actionRequest.setAttribute("perfil",user_Crm.getPerfilId());
            switch (user_Crm.getPerfilId()) {
                // Perfiles Manager
                case CrmDatabaseKey.ID_PERFIL_MANAGER_CONTABILIDAD:
                case CrmDatabaseKey.ID_PERFIL_ANALISTA_CONTABILIDAD:
                    agentes = _AgenteLocalService.findByEstatusFiltroUsuario(CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD + "", "%", "%" + filtroAgente + "%");
                    actionRequest.setAttribute("listaEstatus", estatus.stream().filter(e -> e.getCatalogoDetalleId() == CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD).collect(Collectors.toList()));
                    for(User u : usuariosAux){
                        if(u.getFullName().toLowerCase().contains(filtroAgente.toLowerCase())){
                            try{
                                User_Crm user_crm = _User_CrmLocalService.getUser_Crm((int) u.getUserId());
                                agentesAux = _AgenteLocalService.findByEjecutivoIdAndEstatusAgente(user_crm.getUserId() + "",CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD + "");
                                if(!agentesAux.isEmpty()){
                                    agentesEjecutivo.addAll(agentesAux);
                                }
                            }catch (Exception ignored){

                            }
                        }
                    }
                    for(Agente agente: agentesEjecutivo){
                        if(!agentes.contains(agente)){
                            agentes.add(agente);
                        }
                    }
                    break;
                case CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL:
                case CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL:
                    Map<Integer, String> mapaSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC").stream().collect(Collectors.toMap(e -> (int) e.getCatalogoDetalleId(), Catalogo_Detalle::getDescripcion));
                    List<Integer> idUsuarios = new ArrayList<>();
                    if(!"".equals(filtroAgente)) {
                        for (User u : usuariosAux) {
                            if (u.getFullName().toLowerCase().contains(filtroAgente.toLowerCase())) {
                                try {
                                    User_Crm user_crm = _User_CrmLocalService.getUser_Crm((int) u.getUserId());
                                    idUsuarios.add(user_crm.getUserId());
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    _log.debug(idUsuarios);
                    _log.debug(estatusAgente);
                    _log.debug(filtroAgente);
                    //agentes = _AgenteLocalService.findByEstatusFiltroUsuarioLegal(estatusAgente == -1 ? "%" : (estatusAgente + ""), "%" + filtroAgente + "%",idUsuarios);
                    if(estatusAgente == -1){
                        agentes = _AgenteLocalService.findByEstatusFiltroUsuarioLegal( "%", "%" + filtroAgente + "%", idUsuarios);
                    }else if(estatusAgente == CrmDatabaseKey.ACTUALIZACION){
                        agentes = new ArrayList<>();
                        agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal( CrmDatabaseKey.ESTATUS_ACTUALIZACION_LEGAL + "", "%" + filtroAgente + "%", idUsuarios));
                        agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal( CrmDatabaseKey.ESTATUS_AUTORIZACION_RFC_LEGAL + "", "%" + filtroAgente + "%", idUsuarios));
                        agentes.addAll(_AgenteLocalService.findByEstatusFiltroUsuarioLegal( CrmDatabaseKey.ESTATUS_AUTORIZACION_LEGAL + "", "%" + filtroAgente + "%", idUsuarios));
                    }else{
                        agentes = _AgenteLocalService.findByEstatusFiltroUsuarioLegal( estatusAgente + "", "%" + filtroAgente + "%", idUsuarios);
                    }
                    _log.debug(agentes);
                    agenteLegalResponses.addAll(generaAgenteLegalResponse.generaAgenteLegalResponse(agentes,mapaSociedad));
                    List<Catalogo_Detalle> registros = _Catalogo_DetalleLocalService.findByCodigo("CATTIPCED");
                    actionRequest.setAttribute("mapaTipoCedula",registros.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_Detalle::getDescripcion)));
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
                    actionRequest.setAttribute("mapaEstatusLegal",registros.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId,Catalogo_DetalleModel::getDescripcion)));
                    actionRequest.setAttribute("listaEstatus", estatus);
                    break;
                case CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS:
                    actionRequest.setAttribute("listaEstatus", estatus);
                    agentes = new ArrayList<>();
                    agentesAux = _AgenteLocalService.findByEstatusFiltroManager(estatusAgente == -1 ? "%" : (estatusAgente + ""), usuario.getUserId() + "", "%" + filtroAgente + "%",null);
                    if(agentesAux.isEmpty()){
                        agentesAux = _AgenteLocalService.findByEstatusFiltroManager(estatusAgente == -1 ? "%" : (estatusAgente + ""), usuario.getUserId() + "", "%",null);
                        for(Agente agente: agentesAux){
                            try{
                                user = UserLocalServiceUtil.getUser(agente.getEjecutivo());
                                if(user.getScreenName().contains(filtroAgente.toLowerCase())){
                                    agentes.add(agente);
                                }
                            }catch (Exception ignored){
                            }
                        }
                    }else{
                        agentes.addAll(agentesAux);
                    }
                    break;
                // Perfil Master
                case 100:
                    agentes = _AgenteLocalService.findByEstatusFiltroMaster(estatusAgente == -1 ? "%" : (estatusAgente + ""), "%" + filtroAgente + "%");
                    actionRequest.setAttribute("listaEstatus", estatus);
                    for(User u : usuariosAux){
                        if(u.getFullName().toLowerCase().contains(filtroAgente.toLowerCase())){
                            try{
                                User_Crm user_crm = _User_CrmLocalService.getUser_Crm((int) u.getUserId());
                                agentesAux = _AgenteLocalService.findByEjecutivoIdAndEstatusAgente(user_crm.getUserId() + "",estatusAgente == -1 ? "%" : (estatusAgente + ""));
                                if(!agentesAux.isEmpty()){
                                    agentesEjecutivo.addAll(agentesAux);
                                }
                            }catch (Exception ignored){
                            }
                        }
                    }
                    for(Agente agente: agentesEjecutivo){
                        if(!agentes.contains(agente)){
                            agentes.add(agente);
                        }
                    }
                    break;
                // Perfiles Otros
                default:
                    agentes = new ArrayList<>();
                    agentesAux = _AgenteLocalService.findByEstatusFiltroUsuario(estatusAgente == -1 ? "%" : (estatusAgente + ""), usuario.getUserId() + "", "%" + filtroAgente + "%");
                    actionRequest.setAttribute("listaEstatus", estatus);
                    if(agentesAux.isEmpty()){
                        agentesAux = _AgenteLocalService.findByEstatusFiltroUsuario(estatusAgente == -1 ? "%" : (estatusAgente + ""), usuario.getUserId() + "", "%");
                        for(Agente agente: agentesAux){
                            try{
                                user = UserLocalServiceUtil.getUser(agente.getEjecutivo());
                                if(user.getScreenName().contains(filtroAgente.toLowerCase())){
                                    agentes.add(agente);
                                }
                            }catch (Exception ignored){
                            }
                        }
                    }else{
                        agentes.addAll(agentesAux);
                    }
                    break;
            }
            if(user_Crm.getPerfilId() == CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL || user_Crm.getPerfilId() == CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL){
                Map<Long, String> mapaEjecutivos = agenteLegalResponses.stream().filter(ListUtil.distinctByKey(AgenteLegalResponse::getEjecutivo))
                        .map(a ->{
                            try {
                                return new Ejecutivo(a.getEjecutivo(), UserLocalServiceUtil.getUserById(a.getEjecutivo()).getFullName().toUpperCase());
                            }catch (PortalException ignored){
                                return null;
                            }
                        }).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo,Ejecutivo::getNombreEjecutivo));
                _log.debug(agenteLegalResponses);
                _log.debug(mapaEjecutivos);
                actionRequest.setAttribute("mapaEjecutivos", mapaEjecutivos);
                actionRequest.setAttribute("agentesLista",agenteLegalResponses);
            }else{
                Map<Long, String> mapaEjecutivos = agentes.stream().filter(ListUtil.distinctByKey(AgenteModel::getEjecutivo))
                        .map(a -> {
                            try {
                                return new Ejecutivo(a.getEjecutivo(), UserLocalServiceUtil.getUserById(a.getEjecutivo()).getFullName().toUpperCase());
                            } catch (PortalException ignored) {
                                return null;
                            }
                        }).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo, Ejecutivo::getNombreEjecutivo));
                _log.debug(mapaEjecutivos);
                actionRequest.setAttribute("mapaEjecutivos", mapaEjecutivos);
                actionRequest.setAttribute("agentesLista", agentes);
            }
        } catch (PortalException e1) {
            e1.printStackTrace();
        }
    }

    public void cargaCatalogos(ActionRequest actionRequest) {
        // Llamamos a los servicios del Portlet
        try {
            List<Catalogo_Detalle> oficinas = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
            actionRequest.setAttribute("listaOficina", oficinas);
            actionRequest.setAttribute("mapaOficinas", oficinas.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Catalogo_Detalle::getDescripcion)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
