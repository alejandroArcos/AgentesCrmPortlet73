package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.agentes73.service.SendMailService;
import com.tokio.crm.agentes73.util.ConvertObjectsUtil;
import com.tokio.crm.crmservices73.Bean.Archivo;
import com.tokio.crm.crmservices73.Bean.CRMClaveResponse;
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.crmservices73.Bean.ListaArchivo;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Bean.Registro;
import com.tokio.crm.crmservices73.Bean.RequestKaizen;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.exception.NoSuchAgente_LegalException;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Agente_Rechazo;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Legal;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_LegalLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_RechazoLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_Agente_LegalLocalService;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/agentes/guardarAgentesLegal"}, service = MVCResourceCommand.class)

public class GuardarAgentesLegalCommand extends BaseMVCResourceCommand {
	
	public static final DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat formatoFechaKaizen = new SimpleDateFormat("dd/MM/yyyy");
	private static final Log _log = LogFactoryUtil.getLog(GuardarAgentesLegalCommand.class);
	
	User usuario;
	
	@Reference
	SendMailService sms;
	
	@Reference
	CrmGenerico _CrmGenericoService;
	
	@Reference
	AgenteLocalService _AgenteLocalService;
	
	@Reference
	Cambio_AgenteLocalService _Cambio_AgenteLocalService;
	
	@Reference
	Agente_RechazoLocalService _Agente_RechazoLocalService;
	
	@Reference
	Agente_LegalLocalService _Agente_LegalLocalService;
	
	@Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;
	
	@Reference
	Cambio_Agente_LegalLocalService _Cambio_Agente_LegalLocalService;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

        long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
        long cambioId = ParamUtil.getLong(resourceRequest, "cambioId");
        long numContrato = ParamUtil.getLong(resourceRequest, "numContrato");
        Date fechaAlta = ParamUtil.getDate(resourceRequest, "fechaAlta", formatoFecha);
        int tipoCedula = ParamUtil.getInteger(resourceRequest, "tipoCedula");
        Date vencimientoCedula = ParamUtil.getDate(resourceRequest, "vencimientoCedula", formatoFecha);
        boolean compDomicilio = ParamUtil.getBoolean(resourceRequest, "compDomicilio");
        Date vencimientoCompDomicilio = ParamUtil.getDate(resourceRequest, "vencimientoCompDomicilio", formatoFecha);
        boolean polizaRC = ParamUtil.getBoolean(resourceRequest, "polizaRC");
        Date vencimientoPolizaRC = ParamUtil.getDate(resourceRequest, "vencimientoPolizaRC", formatoFecha);
        boolean idApoderado = ParamUtil.getBoolean(resourceRequest, "idApoderado");
        Date vencimientoIdApoderado = ParamUtil.getDate(resourceRequest, "vencimientoIdApoderado", formatoFecha);
        boolean contrato = ParamUtil.getBoolean(resourceRequest, "contrato");
        Date vencimientoContrato = ParamUtil.getDate(resourceRequest, "vencimientoContrato", formatoFecha);
        boolean datosBancarios = ParamUtil.getBoolean(resourceRequest, "datosBancarios");
        boolean autoCNSF = ParamUtil.getBoolean(resourceRequest, "autoCNSF");
        boolean rfc = ParamUtil.getBoolean(resourceRequest, "rfc");
        boolean acta = ParamUtil.getBoolean(resourceRequest, "acta");
        boolean poder = ParamUtil.getBoolean(resourceRequest, "poder");
        boolean cartaCompromiso = ParamUtil.getBoolean(resourceRequest, "cartaCompromiso");
        String tipoSociedad = ParamUtil.getString(resourceRequest, "tipoSociedad");

        String coloniaTexto = ParamUtil.getString(resourceRequest, "colonia");
        String observaciones = ParamUtil.getString(resourceRequest, "observaciones");
        boolean esRechazo = ParamUtil.getBoolean(resourceRequest, "rechazo");

        Gson gson = new Gson();
        CRMClaveResponse respuesta = new CRMClaveResponse();

        Agente_Legal agente_Legal;

        _log.debug(cambioId);
        try {
            if(cambioId != 0){
                Cambio_Agente cambio_agente = _Cambio_AgenteLocalService.getCambio_Agente(cambioId);
                Agente agente = _AgenteLocalService.getAgente(agenteId);
                switch ((int)cambio_agente.getEstatusCambioId()){
                    case CrmDatabaseKey.ESTATUS_AUTORIZACION_RFC_LEGAL:
                        if(esRechazo){
                            //agente_Legal.setEstatus_legal(CrmDatabaseKey.INACTIVO);
                            Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                            agente_rechazo.setFechaCreacion(new Date());
                            agente_rechazo.setMotivo(observaciones);
                            agente_rechazo.setAgenteId(agenteId);
                            agente_rechazo.setUserCreacion(usuario.getUserId());
                            agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ACTUALIZACION);
                            _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                            cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL);
                            sms.enviarRechazoLegalActualizacion(agente.getUserCreacion(),
                                    agente.getClave(), agente.getDatosRfc(),
                                    agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),
                                    formatoFecha.format(agente.getFechaCreacion()), agente.getEjecutivo(), observaciones,"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                        }else {
                            cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_ACTUALIZACION_MANAGER);
                            sms.enviarPendienteVentasActualizar(agente.getEjecutivo(),agente.getClave(),agente.getDatosRfc(),
                                    agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),
                                    formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),cambio_agente.getComentario(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                            Agente_Legal agente_legal = _Agente_LegalLocalService.getAgente_Legal(agenteId);
                            agente_legal.setEstatus_legal(CrmDatabaseKey.ACTUALIZACION);
                            _Agente_LegalLocalService.updateAgente_Legal(agente_legal);
                        }
                        _Cambio_AgenteLocalService.updateCambio_Agente(cambio_agente);
                        break;
                    case CrmDatabaseKey.ESTATUS_AUTORIZACION_LEGAL:
                        if(esRechazo){
                            Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                            agente_rechazo.setFechaCreacion(new Date());
                            agente_rechazo.setMotivo(observaciones);
                            agente_rechazo.setAgenteId(agenteId);
                            agente_rechazo.setUserCreacion(usuario.getUserId());
                            agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ACTUALIZACION);
                            _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                            cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL);
                            sms.enviarRechazoLegalActualizacion(agente.getUserCreacion(),
                                    agente.getClave(), agente.getDatosRfc(),
                                    agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),
                                    formatoFecha.format(agente.getFechaCreacion()), agente.getEjecutivo(), observaciones,"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                        }else {
                            Agente_Domicilio agente_domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agenteId);
                            ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                            CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_domicilio.getCpId()).findAny().get();
                            sms.enviarAutorizadoArea(agenteId,selected,"Legal","Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                            cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_AUTORIZACION_ACTUALIZACION_VENTAS);
                            /*try {
                                RequestKaizen rk = ConvertObjectsUtil.convertirObjetosAKaizen(agente, agente_domicilio, coloniaTexto);
                                _CrmGenericoService.actualizaAgenteKaizen(rk, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            _AgenteLocalService.updateAgente(ConvertObjectsUtil.llenarAgentefromCambio(agente,cambio_agente));*/
                        }
                        _Cambio_AgenteLocalService.updateCambio_Agente(cambio_agente);
                        break;
                    case CrmDatabaseKey.ESTATUS_ACTUALIZACION_LEGAL:
                        Cambio_Agente_Legal cambio_agente_legal = _Cambio_Agente_LegalLocalService.createCambio_Agente_Legal(0);
                        cambio_agente_legal.setNumero_contrato(numContrato);
                        cambio_agente_legal.setFechaAlta(fechaAlta);
                        cambio_agente_legal.setTipoCedula(tipoCedula);
                        cambio_agente_legal.setVencimientoCedula(vencimientoCedula);
                        cambio_agente_legal.setComprobanteDomicilio(compDomicilio);
                        cambio_agente_legal.setVencimientoCompDomicilio(vencimientoCompDomicilio);
                        cambio_agente_legal.setPolizaRC(polizaRC);
                        cambio_agente_legal.setVencimientoPoliza(vencimientoPolizaRC);
                        cambio_agente_legal.setIdApoderado(idApoderado);
                        cambio_agente_legal.setVencimientoIdApoderado(vencimientoIdApoderado);
                        cambio_agente_legal.setContrato(contrato);
                        cambio_agente_legal.setVencimientoContrato(vencimientoContrato);
                        cambio_agente_legal.setDatosBancarios(datosBancarios);
                        cambio_agente_legal.setAutoCNSF(autoCNSF);
                        cambio_agente_legal.setRfc(rfc);
                        cambio_agente_legal.setActa(acta);
                        cambio_agente_legal.setPoder(poder);
                        cambio_agente_legal.setCartaCompromiso(cartaCompromiso);
                        if(esRechazo){
                            Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                            agente_rechazo.setFechaCreacion(new Date());
                            agente_rechazo.setMotivo(observaciones);
                            agente_rechazo.setAgenteId(agenteId);
                            agente_rechazo.setUserCreacion(usuario.getUserId());
                            agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ACTUALIZACION);
                            _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                            cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL);
                            cambio_agente_legal.setEstatus_legal(CrmDatabaseKey.INACTIVO);
                            sms.enviarRechazoLegalActualizacion(agente.getUserCreacion(),
                                    agente.getClave(), agente.getDatosRfc(),
                                    agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),
                                    formatoFecha.format(agente.getFechaCreacion()), agente.getEjecutivo(), observaciones,"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                        }else{
                            cambio_agente_legal.setEstatus_legal(CrmDatabaseKey.ACTIVO);
                            Agente_Legal agente_legal = _Agente_LegalLocalService.getAgente_Legal(agenteId);
                            _Agente_LegalLocalService.updateAgente_Legal(ConvertObjectsUtil.llenarAgente_LegalfromCambio(agente_legal,cambio_agente_legal));
                            Agente_Domicilio agente_domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agenteId);
                            ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                            CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_domicilio.getCpId()).findAny().get();
                            sms.enviarAutorizadoArea(agenteId,selected,"Legal","Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0] );
                            ListaArchivo listaDocumentosExistentes = null;
                            ListaRegistro listaDocumentos;
                            Map<String,String> mapaDocumentos = new HashMap<>();
                            try{
                                listaDocumentosExistentes = _CrmGenericoService.listaDocumentos(agente.getAgrupador(), usuario.getScreenName(),AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                                listaDocumentos = _CrmGenericoService.getCatalogo(
                                        CrmServiceKey.TMX_CTE_ROW_TODOS,
                                        CrmServiceKey.TMX_CTE_TRANSACCION_GET,
                                        CrmServiceKey.LIST_CAT_DOCUMENTOS_AGENTES,
                                        CrmServiceKey.TMX_CTE_CAT_ACTIVOS,
                                        usuario.getScreenName(),
                                        AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73
                                );
                                mapaDocumentos = listaDocumentos.getLista().stream().collect(Collectors.toMap(Registro::getCodigo, Registro::getTipo));
                            }catch (Exception ignored){
                            }
                            if (cambio_agente.getSolCambiosConta().contains("true")) {
                                cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_ACTUALIZACION_CONTABILIDAD);
                                sms.enviarPendienteContabilidad(agente.getClave(), agente.getUserCreacion(), formatoFecha.format(agente.getFechaCreacion()), agente.getDatosRfc(), agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), agente.getEjecutivo(),"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                            }else if(Objects.nonNull(listaDocumentosExistentes) && "0".equals(listaDocumentosExistentes.getCode())){
                                int index = 0;
                                for(Archivo archivo: listaDocumentosExistentes.getArchivo()){
                                    try{
                                        if("TEMPORAL".equals(mapaDocumentos.get(archivo.getId())))
                                            index++;
                                    }catch (Exception ignored){
                                    }
                                }
                                if(index > 0){
                                    cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_ACTUALIZACION_CONTABILIDAD);
                                    sms.enviarPendienteContabilidad(agente.getClave(), agente.getUserCreacion(), formatoFecha.format(agente.getFechaCreacion()), agente.getDatosRfc(), agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), agente.getEjecutivo(),"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                                }
                            }else{
                                cambio_agente.setEstatusCambioId(CrmDatabaseKey.ESTATUS_AUTORIZADO);
                                try {
                                    RequestKaizen rk = ConvertObjectsUtil.convertirObjetosAKaizen(agente, agente_domicilio, coloniaTexto);
                                    _CrmGenericoService.actualizaAgenteKaizen(rk, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            _AgenteLocalService.updateAgente(ConvertObjectsUtil.llenarAgentefromCambio(agente,cambio_agente));
                        }
                        _Cambio_Agente_LegalLocalService.addCambio_Agente_Legal(cambio_agente_legal);
                        break;
                }
                respuesta.setCode(3);
                respuesta.setMsg("Actualizacion de información Legal realizada con éxito");
            }else {
                Agente agente = _AgenteLocalService.getAgente(agenteId);
                Agente_Domicilio agente_Domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agenteId);
                agente.setFechaModificacion(new Date());
                try {
                    agente_Legal = _Agente_LegalLocalService.getAgente_Legal(agenteId);
                } catch (NoSuchAgente_LegalException ignored) {
                    agente_Legal = _Agente_LegalLocalService.createAgente_Legal(agenteId);
                }
                agente_Legal.setNumero_contrato(numContrato);
                agente_Legal.setFechaAlta(fechaAlta);
                agente_Legal.setTipoCedula(tipoCedula);
                agente_Legal.setVencimientoCedula(vencimientoCedula);
                agente_Legal.setComprobanteDomicilio(compDomicilio);
                agente_Legal.setVencimientoCompDomicilio(vencimientoCompDomicilio);
                agente_Legal.setPolizaRC(polizaRC);
                agente_Legal.setVencimientoPoliza(vencimientoPolizaRC);
                agente_Legal.setIdApoderado(idApoderado);
                agente_Legal.setVencimientoIdApoderado(vencimientoIdApoderado);
                agente_Legal.setContrato(contrato);
                agente_Legal.setVencimientoContrato(vencimientoContrato);
                agente_Legal.setDatosBancarios(datosBancarios);
                agente_Legal.setAutoCNSF(autoCNSF);
                agente_Legal.setRfc(rfc);
                agente_Legal.setActa(acta);
                agente_Legal.setPoder(poder);
                agente_Legal.setCartaCompromiso(cartaCompromiso);
                agente_Legal.setFecha_creacion(new Timestamp((new Date()).getTime()));
                _log.debug(agente_Legal);
                if (esRechazo) {
                    agente_Legal.setEstatus_legal(CrmDatabaseKey.INACTIVO);
                    Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                    agente_rechazo.setFechaCreacion(new Date());
                    agente_rechazo.setMotivo(observaciones);
                    agente_rechazo.setAgenteId(agenteId);
                    agente_rechazo.setUserCreacion(usuario.getUserId());
                    agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ALTA);
                    _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                    agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL);
                    _AgenteLocalService.updateAgente(agente);
                    sms.enviarRechazoLegal(agente.getUserCreacion(),
                            agente.getPreclave(), agente.getDatosRfc(),
                            agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),
                            formatoFecha.format(agente.getFechaCreacion()), agente.getEjecutivo(), observaciones,"Alta",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                } else {
                    agente_Legal.setEstatus_legal(CrmDatabaseKey.ACTIVO);
                    agente.setClave(agente.getPreclave().replace("00", "01"));
                    respuesta.setClave(agente.getPreclave().replace("00", "01").toUpperCase());
                    respuesta.setAgrupador(agente.getAgrupador().toUpperCase());
                    respuesta.setPreclave(agente.getPreclave().toUpperCase());
                    agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD);
                    ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_Domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                    CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_Domicilio.getCpId()).findAny().get();
                    _AgenteLocalService.updateAgente(agente);
                    _log.debug(agente.getClave());
                    sms.enviarAutorizadoArea(agenteId, selected, "Legal", "Alta",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    sms.enviarAutorizadoLegal(agente.getClave(),agente.getUserCreacion(),formatoFecha.format(agente.getFechaCreacion()), agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), agente.getDatosRfc(),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    sms.enviarPendienteContabilidad(agente.getClave(), agente.getUserCreacion(), formatoFecha.format(agente.getFechaCreacion()), agente.getDatosRfc(), agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), agente.getEjecutivo(),"Actualizaci&oacute;n",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    try {
                        RequestKaizen rk = convertirObjeto(agente, agente_Domicilio, coloniaTexto, tipoSociedad);
                        _CrmGenericoService.insertaAgenteKaizen(rk, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                _Agente_LegalLocalService.updateAgente_Legal(agente_Legal);
                respuesta.setCode(0);
                respuesta.setMsg("Alta información Legal realizada con éxito");
            }
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.setCode(2);
            respuesta.setMsg(e.getMessage());
        }
        _log.debug("Guardado Legal: " + respuesta);
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);
    }

    private RequestKaizen convertirObjeto(Agente agente, Agente_Domicilio agente_Domicilio, String coloniaTexto, String tipoSociedad) {
        RequestKaizen rk = new RequestKaizen();
        rk.setP_codigo(agente.getClave());
        rk.setP_rfc(agente.getDatosRfc());
        rk.setP_nombre(agente.getNombre());
        rk.setP_paterno(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA ? agente.getApellidoP() : tipoSociedad);
        rk.setP_materno(agente.getApellidoM());
        rk.setP_genero((agente.getSexo() == -1 || agente.getSexo() == 0) ? "" : (agente.getSexo() == 50 ? "2" : "1"));
        rk.setP_fecha(formatoFechaKaizen.format(agente.getFechaNacimientoConstitucion()));
        rk.setP_tipper(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA ? "1" : "2");
        rk.setP_regimen(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA ? agente.getRegimen_id() + "" : "");
        rk.setP_calle(agente_Domicilio.getCalle());
        rk.setP_numero(agente_Domicilio.getNoExt());
        rk.setP_colonia(coloniaTexto);
        rk.setP_cp(agente_Domicilio.getCodigo());
        rk.setP_email(agente_Domicilio.getEmailContacto1());
        return rk;
    }

}
