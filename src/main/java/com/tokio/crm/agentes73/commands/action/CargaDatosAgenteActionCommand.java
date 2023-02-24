package com.tokio.crm.agentes73.commands.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.agentes73.util.ConvertObjectsUtil;
import com.tokio.crm.crmservices73.Bean.Archivo;
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.crmservices73.Bean.Ejecutivo;
import com.tokio.crm.crmservices73.Bean.ListaArchivo;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Bean.Registro;
import com.tokio.crm.crmservices73.Bean.UsuarioCrm;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Exeption.CrmServicesException;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.crmservices73.Util.ListUtil;
import com.tokio.crm.servicebuilder73.exception.NoSuchAgente_ContabilidadException;
import com.tokio.crm.servicebuilder73.exception.NoSuchAgente_LegalException;
import com.tokio.crm.servicebuilder73.exception.NoSuchCambio_Agente_LegalException;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Contabilidad;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Agente_Rechazo;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Cambio_Bitacora;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_CarteraLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_ContabilidadLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_LegalLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_RechazoLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_Agente_CarteraLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_Agente_LegalLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_BitacoraLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.init-param.copy-request-parameters=true",
        "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/action/agentes/cargaDatosAgente"}, service = MVCActionCommand.class)

public class CargaDatosAgenteActionCommand extends BaseMVCActionCommand {

    private static final Log _log = LogFactoryUtil.getLog(CargaDatosAgenteActionCommand.class);
    User usuario;
    
    @Reference
    CrmGenerico _CrmGenericoService;
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;
    
    @Reference
	AgenteLocalService _AgenteLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;
    
    @Reference
	Agente_CarteraLocalService _Agente_CarteraLocalService;
    
    @Reference
	Agente_LegalLocalService _Agente_LegalLocalService;
    
    @Reference
	Cambio_Agente_LegalLocalService _Cambio_Agente_LegalLocalService;
    
    @Reference
	Agente_ContabilidadLocalService _Agente_ContabilidadLocalService;
    
    @Reference
	Agente_RechazoLocalService _Agente_RechazoLocalService;
    
    @Reference
    Cambio_AgenteLocalService _Cambio_AgenteLocalService;
    
    @Reference
    Cambio_Agente_DomicilioLocalService _Cambio_Agente_DomicilioLocalService;
    
    @Reference
    Cambio_Agente_CarteraLocalService _Cambio_Agente_CarteraLocalService;
    
    @Reference
    Cambio_BitacoraLocalService _Cambio_BitacoraLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {

        HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
        usuario = (User) originalRequest.getAttribute(WebKeys.USER);
        User_Crm user_Crm = _User_CrmLocalService.getUser_Crm(new Long(usuario.getUserId()).intValue());
        actionRequest.setAttribute("perfilUsuario", user_Crm.getPerfilId());
        Agente agente = cargaInformacionAgente(actionRequest);
        cargaCatalogos(actionRequest, agente);
        switch (user_Crm.getPerfilId()) {
            // Perfiles de Legal
            case 3:
            case 11:
                cargaInformacionAgenteLegal(actionRequest, agente);
                actionRequest.setAttribute("esEjecutivo", 0);
                actionRequest.setAttribute("readOnly", 1);
                break;
            // Perfiles de Contabilidad
            case 1:
            case 10:
                cargaInformacionAgenteContabilidad(actionRequest, agente);
                actionRequest.setAttribute("esEjecutivo", 0);
                actionRequest.setAttribute("readOnly", 1);
                break;
            case 16:
                actionRequest.setAttribute("esEjecutivo", 2);
                actionRequest.setAttribute("readOnly", 1);
            break;
            default:
                actionRequest.setAttribute("esEjecutivo", 1);
                actionRequest.setAttribute("readOnly", 0);
                break;
        }
        actionRequest.setAttribute("nuevo", 0);
        if (agente.getIsClaveEspejo()) {
            Agente agentePadre = _AgenteLocalService.getAgente(agente.getAgentePadreId());
            actionRequest.setAttribute("clavePrimaria", agentePadre.getClave());
            String nombre;
            if (agentePadre.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA) {
                nombre = agentePadre.getNombre() + " " + agentePadre.getApellidoP() + " " + agentePadre.getApellidoM();
            } else {
                List<Catalogo_Detalle> tiposSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC");
                final long tss = agentePadre.getTipoSociedad();
                nombre = agentePadre.getNombre() + " " + (tiposSociedad.stream().filter(ts -> ts.getCatalogoDetalleId() == tss).map(ts -> ts.getDescripcion()).findFirst().get());
            }
            actionRequest.setAttribute("nombreClavePrimaria", nombre);
            actionResponse.setRenderParameter("jspPage", "/claveEspejo.jsp");
        } else {
            actionResponse.setRenderParameter("jspPage", "/datosAgente.jsp");
        }
    }

    public Agente cargaInformacionAgente(ActionRequest actionRequest) {
        String id = actionRequest.getParameter("idAgente");
        Agente agente = null;
        Agente_Domicilio agente_Domicilio;
        Agente_Cartera agente_Cartera;

        try {
            agente = _AgenteLocalService.getAgente(Long.valueOf(id));
            actionRequest.setAttribute("agente", agente);
            switch ((int) agente.getEstatusAgenteId()){
                case CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL:
                case CrmDatabaseKey.ESTATUS_RECHAZADO_CONTABILIDAD:
                case CrmDatabaseKey.ESTATUS_REVISION_LEGAL:
                case CrmDatabaseKey.ESTATUS_RECHAZADO_MANAGER:
                    obtineRechazos(actionRequest);
                    break;
            }
        } catch (NumberFormatException | PortalException e) {
            e.printStackTrace();
        }
        try {
            agente_Domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(Long.valueOf(id));
            agente_Domicilio.setTel1Contacto1(agente_Domicilio.getTel1Contacto1().contains("|") ? agente_Domicilio.getTel1Contacto1() : (agente_Domicilio.getTel1Contacto1() + "|"));
            agente_Domicilio.setTel2Contacto1(agente_Domicilio.getTel2Contacto1().contains("|") ? agente_Domicilio.getTel2Contacto1() : (agente_Domicilio.getTel2Contacto1() + "|"));
            agente_Domicilio.setTel1Contacto2(agente_Domicilio.getTel1Contacto2().contains("|") ? agente_Domicilio.getTel1Contacto2() : (agente_Domicilio.getTel1Contacto2() + "|"));
            agente_Domicilio.setTel2Contacto2(agente_Domicilio.getTel2Contacto2().contains("|") ? agente_Domicilio.getTel2Contacto2() : (agente_Domicilio.getTel2Contacto2() + "|"));
            actionRequest.setAttribute("agenteD", agente_Domicilio);
            _log.debug(agente_Domicilio);
            if (Objects.nonNull(agente_Domicilio.getCodigo())) {
                ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_Domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_Domicilio.getCpId()).findAny().get();
                actionRequest.setAttribute("dEstado", selected.getEstado());
                actionRequest.setAttribute("dMunicipio", selected.getDelegacion());
                actionRequest.setAttribute("dColonia", selected.getColonia());
                actionRequest.setAttribute("cpData", cpData.getCpData());
                _log.debug(cpData);
            }
        } catch (NumberFormatException | PortalException | CrmServicesException e) {
            e.printStackTrace();
        }

        try {
            agente_Cartera = _Agente_CarteraLocalService.getAgente_Cartera(Long.valueOf(id));
            actionRequest.setAttribute("agenteC", agente_Cartera);
            _log.debug(agente_Cartera);
        } catch (NumberFormatException | PortalException e) {
            e.printStackTrace();
        }

        return agente;
    }

    public void cargaInformacionAgenteLegal(ActionRequest actionRequest, Agente agente) {
        try {
            Cambio_Agente cambio_agente = compruebaCambios(actionRequest);
            Agente_Legal agente_Legal = _Agente_LegalLocalService.getAgente_Legal(agente.getAgenteId());
            if(Objects.nonNull(cambio_agente)) {
                try {
                    Cambio_Agente_Legal cambio_agente_legal = _Cambio_Agente_LegalLocalService.getCambio_Agente_Legal(cambio_agente.getAgenteId());
                    agente_Legal = ConvertObjectsUtil.llenarAgente_LegalfromCambio(agente_Legal,cambio_agente_legal);
                }catch (NoSuchCambio_Agente_LegalException e){
                    _log.error("Controlado: " + e.getMessage());
                }
            }
            actionRequest.setAttribute("agenteL", agente_Legal);
            _log.debug(agente_Legal);
            obtineRechazos(actionRequest);
        } catch (NoSuchAgente_LegalException e) {
            _log.error("Controlado: " + e.getMessage());
        } catch (NumberFormatException | PortalException e) {
            e.printStackTrace();
        }
    }

    public void cargaInformacionAgenteContabilidad(ActionRequest actionRequest, Agente agente) {
        try {
            Agente_Contabilidad agente_Contabilidad = _Agente_ContabilidadLocalService.getAgente_Contabilidad(agente.getAgenteId());
            actionRequest.setAttribute("agenteCc", agente_Contabilidad);
            _log.debug(agente_Contabilidad);
        } catch (NoSuchAgente_ContabilidadException e) {
            _log.error("Controlado:" + e.getMessage());
        } catch (NumberFormatException | PortalException e) {
            e.printStackTrace();
        }
    }

    public void cargaCatalogos(ActionRequest actionRequest, Agente agente) {
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
            ListaArchivo listaDocumentosExistentes = _CrmGenericoService.listaDocumentos(agente.getAgrupador(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
            actionRequest.setAttribute("listaDocumentosExistentes", listaDocumentosExistentes.getArchivo().stream().map(Archivo::getId).collect(Collectors.toList()));
            ListaRegistro listaRegistro = _CrmGenericoService.getCatalogo(CrmServiceKey.TMX_CTE_ROW_TODOS,CrmServiceKey.TMX_CTE_TRANSACCION_GET,"CATREGIMEN",CrmServiceKey.TMX_CTE_CAT_ACTIVOS,usuario.getScreenName(),AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
            actionRequest.setAttribute("listaRegimen",listaRegistro.getLista());
        } catch (Exception e) {
            _log.error(e.getMessage());
            e.printStackTrace();
        }

        try {
            List<Catalogo_Detalle> perfilesContacto = _Catalogo_DetalleLocalService.findByCodigo("CATPERCON");
            actionRequest.setAttribute("listaPerfilesContacto", perfilesContacto);
            List<Catalogo_Detalle> tiposSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC");
            actionRequest.setAttribute("listaTipoSociedad", tiposSociedad);
            List<Catalogo_Detalle> sexos = _Catalogo_DetalleLocalService.findByCodigo("CATSEX");
            actionRequest.setAttribute("listaSexo", sexos);
            List<Catalogo_Detalle> oficinas = _Catalogo_DetalleLocalService.findByCodigo("CATOFICINA");
            actionRequest.setAttribute("listaOficina", oficinas);
            actionRequest.setAttribute("mapaOficinas", oficinas.stream().collect(Collectors.toMap(Catalogo_Detalle::getCatalogoDetalleId, Function.identity())));
            List<Catalogo_Detalle> cedulas = _Catalogo_DetalleLocalService.findByCodigo("CATTIPCED");
            actionRequest.setAttribute("listaCedula", cedulas);
            List<User_Crm> ejecutivos = _User_CrmLocalService.getUsers_CrmByAreaPerfil(Objects.isNull(agente) ? CrmDatabaseKey.AREA_NEGOCIO_M : (agente.getTipoNegocio() == CrmDatabaseKey.NEGOCIO_J ? CrmDatabaseKey.AREA_NEGOCIO_J : CrmDatabaseKey.AREA_NEGOCIO_M), CrmDatabaseKey.ID_PERFIL_EJECUTIVO_VENTAS);
            List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByAreaPerfil(Objects.isNull(agente) ? CrmDatabaseKey.AREA_NEGOCIO_M : (agente.getTipoNegocio() == CrmDatabaseKey.NEGOCIO_J ? CrmDatabaseKey.AREA_NEGOCIO_J : CrmDatabaseKey.AREA_NEGOCIO_M), CrmDatabaseKey.ID_PERFIL_ANALISTA_VENTAS);
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

    public void obtineRechazos(ActionRequest actionRequest){
        String id = actionRequest.getParameter("idAgente");
        List<Agente_Rechazo> agente_Rechazo;
        agente_Rechazo = _Agente_RechazoLocalService.findByIdAgenteAndTipoRechazo(Long.parseLong(id),CrmDatabaseKey.RECHAZO_ALTA);
        if(!agente_Rechazo.isEmpty()) {
            actionRequest.setAttribute("rechazos", agente_Rechazo);
            _log.debug(agente_Rechazo);
            Map<Long, String> mapaUsuariosRechazo = agente_Rechazo.stream().filter(ListUtil.distinctByKey(p -> p.getUserCreacion())).map(a ->
                    {
                        try {
                            return new Ejecutivo(a.getUserCreacion(), UserLocalServiceUtil.getUserById(a.getUserCreacion()).getFullName().toUpperCase());
                        } catch (PortalException e) {
                            return null;
                        }
                    }
            ).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo, Ejecutivo::getNombreEjecutivo));
            actionRequest.setAttribute("mapaUsuariosRechazo", mapaUsuariosRechazo);
        }
    }

    public Cambio_Agente compruebaCambios(ActionRequest actionRequest){
        String id = actionRequest.getParameter("idAgente");
        try {
            Cambio_Agente cambio_agente = _Cambio_AgenteLocalService.findByAgenteActivoByIdAgente(Long.parseLong(id));
            _log.debug(cambio_agente);
            Agente agente = (Agente) actionRequest.getAttribute("agente");
            agente = ConvertObjectsUtil.llenarAgentefromCambio(agente,cambio_agente);
            actionRequest.removeAttribute("agente");
            actionRequest.setAttribute("agente",agente);
            Cambio_Agente_Domicilio cambio_agente_domicilio = _Cambio_Agente_DomicilioLocalService.getCambio_Agente_Domicilio(cambio_agente.getCambioId());
            actionRequest.setAttribute("solCambioVentas",cambio_agente.getSolCambiosVentas());
            Agente_Domicilio agente_domicilio = (Agente_Domicilio) actionRequest.getAttribute("agenteD");
            agente_domicilio = ConvertObjectsUtil.llenarAgente_DomiciliofromCambio(agente_domicilio,cambio_agente_domicilio);
            agente_domicilio.setTel1Contacto1(agente_domicilio.getTel1Contacto1().contains("|") ? agente_domicilio.getTel1Contacto1() : (agente_domicilio.getTel1Contacto1() + "|"));
            agente_domicilio.setTel2Contacto1(agente_domicilio.getTel2Contacto1().contains("|") ? agente_domicilio.getTel2Contacto1() : (agente_domicilio.getTel2Contacto1() + "|"));
            agente_domicilio.setTel1Contacto2(agente_domicilio.getTel1Contacto2().contains("|") ? agente_domicilio.getTel1Contacto2() : (agente_domicilio.getTel1Contacto2() + "|"));
            agente_domicilio.setTel2Contacto2(agente_domicilio.getTel2Contacto2().contains("|") ? agente_domicilio.getTel2Contacto2() : (agente_domicilio.getTel2Contacto2() + "|"));
            actionRequest.removeAttribute("agenteD");
            actionRequest.setAttribute("agenteD",agente_domicilio);
            if (Objects.nonNull(agente_domicilio.getCodigo())) {
                try{
                    ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                    Agente_Domicilio finalAgente_domicilio = agente_domicilio;
                    CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == finalAgente_domicilio.getCpId()).findAny().get();
                    actionRequest.removeAttribute("dEstado");
                    actionRequest.setAttribute("dEstado", selected.getEstado());
                    actionRequest.removeAttribute("dMunicipio");
                    actionRequest.setAttribute("dMunicipio", selected.getDelegacion());
                    actionRequest.removeAttribute("dColonia");
                    actionRequest.setAttribute("dColonia", selected.getColonia());
                    actionRequest.removeAttribute("cpData");
                    actionRequest.setAttribute("cpData", cpData.getCpData());
                    _log.debug(cpData);
                }catch (CrmServicesException e){
                    _log.error(e.getMessage());
                }
            }
            Cambio_Agente_Cartera cambioAgenteCartera = _Cambio_Agente_CarteraLocalService.getCambio_Agente_Cartera(cambio_agente.getCambioId());
            Agente_Cartera agente_cartera = _Agente_CarteraLocalService.getAgente_Cartera(cambio_agente.getAgenteId());
            agente_cartera = ConvertObjectsUtil.llenarAgente_CarterafromCambio(agente_cartera,cambioAgenteCartera);
            actionRequest.removeAttribute("agenteC");
            actionRequest.setAttribute("agenteC",agente_cartera);
            try {
                List<Cambio_Bitacora> cambios_bitacora = _Cambio_BitacoraLocalService.findByIdAgente(Long.parseLong(id));
                Map<Long, String> mapaUsuariosCambio = cambios_bitacora.stream().filter(com.tokio.crm.crmservices73.Util.ListUtil.distinctByKey(p -> p.getUserCreacion())).map(a ->
                        {
                            try {
                                return new Ejecutivo(a.getUserCreacion(), UserLocalServiceUtil.getUserById(a.getUserCreacion()).getFullName().toUpperCase());
                            } catch (PortalException e) {
                                return null;
                            }
                        }
                ).collect(Collectors.toMap(Ejecutivo::getIdEjecutivo, Ejecutivo::getNombreEjecutivo));
                actionRequest.setAttribute("cambios", cambios_bitacora);
                actionRequest.setAttribute("mapaUsuariosCambio", mapaUsuariosCambio);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            ListaRegistro listaDocumentos = _CrmGenericoService.getCatalogo(
                    CrmServiceKey.TMX_CTE_ROW_TODOS,
                    CrmServiceKey.TMX_CTE_TRANSACCION_GET,
                    CrmServiceKey.LIST_CAT_DOCUMENTOS_AGENTES,
                    CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
                    usuario.getScreenName(),
                    AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73
            );
            actionRequest.removeAttribute("listaDocumentos");
            actionRequest.setAttribute("listaDocumentos", listaDocumentos.getLista().stream().filter(r -> CrmDatabaseKey.DOC_TEMPORAL.equals(r.getTipo()) && !CrmDatabaseKey.DOC_RFC.equalsIgnoreCase(r.getCodigo())).sorted(Comparator.comparing(Registro::getCodigo)).collect(Collectors.toList()));
            _log.debug(listaDocumentos);
            _log.debug(listaDocumentos.getLista().stream().filter(r -> CrmDatabaseKey.DOC_TEMPORAL.equals(r.getTipo()) && !CrmDatabaseKey.DOC_RFC.equalsIgnoreCase(r.getCodigo())).sorted(Comparator.comparing(Registro::getCodigo)).collect(Collectors.toList()));
            ListaArchivo listaDocumentosExistentes = _CrmGenericoService.listaDocumentos(agente.getAgrupador(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
            actionRequest.removeAttribute("listaDocumentosExistentes");
            actionRequest.setAttribute("listaDocumentosExistentes", listaDocumentosExistentes.getArchivo().stream().map(Archivo::getId).collect(Collectors.toList()));
            _log.debug(listaDocumentosExistentes);
            actionRequest.setAttribute("cambioId",cambio_agente.getCambioId());
            actionRequest.setAttribute("cambio",1);
            //actionRequest.setAttribute("comentario",cambio_agente.getComentario().replaceAll("\n","").replaceAll("\r",""));
            actionRequest.setAttribute("comentario",cambio_agente.getComentario());
            _log.debug(cambio_agente.getComentario());
            actionRequest.setAttribute("estatusAgente",cambio_agente.getEstatusCambioId());

            return cambio_agente;
        }catch (Exception e){
            _log.error(e.getMessage());
            return null;
        }
    }
}
