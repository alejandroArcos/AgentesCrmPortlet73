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
import com.tokio.crm.crmservices73.Bean.CRMResponse;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_CarteraLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/agentes/guardarAgentes"}, service = MVCResourceCommand.class)

public class GuardarAgentesCommand extends BaseMVCResourceCommand {
    public static final DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
    private static final Log _log = LogFactoryUtil.getLog(GuardarAgentesCommand.class);
    User usuario;
    @Reference
    SendMailService sms;
    
    @Reference
	AgenteLocalService _AgenteLocalService;
    
    @Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;
    
    @Reference
	Agente_CarteraLocalService _Agente_CarteraLocalService;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
        // Datos general
        long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
        int agentePadreId = ParamUtil.getInteger(resourceRequest, "agentePadreId");
        String preclave = ParamUtil.getString(resourceRequest, "preclave");
        String agrupador = ParamUtil.getString(resourceRequest, "agrupador");
        int tipoPersona = ParamUtil.getInteger(resourceRequest, "tipoPersona");
        String datosRfc = ParamUtil.getString(resourceRequest, "datosRfc");
        String nombre = ParamUtil.getString(resourceRequest, "nombre");
        String apellidoP = ParamUtil.getString(resourceRequest, "apellidoP");
        String apellidoM = ParamUtil.getString(resourceRequest, "apellidoM");
        int tipoSociedad = ParamUtil.getInteger(resourceRequest, "tipoSociedad");
        int tipoNegocio = ParamUtil.getInteger(resourceRequest, "tipoNegocio");
        long ejecutivo = ParamUtil.getLong(resourceRequest, "ejecutivo");
        long regimenId = ParamUtil.getLong(resourceRequest, "regimenId");
        long oficina = ParamUtil.getInteger(resourceRequest, "oficinaId");
        Date fechaNacimiento = ParamUtil.getDate(resourceRequest, "fechaNacimiento", formatoFecha);
        Date fechaConstitucion = ParamUtil.getDate(resourceRequest, "fechaConstitucion", formatoFecha);
        int sexo = ParamUtil.getInteger(resourceRequest, "sexo");

        // Datos de direccion
        int cpId = ParamUtil.getInteger(resourceRequest, "cp");
        String codigo = ParamUtil.getString(resourceRequest, "codigo");
        String calle = ParamUtil.getString(resourceRequest, "calle");
        String numeroint = ParamUtil.getString(resourceRequest, "numeroint");
        String numeroext = ParamUtil.getString(resourceRequest, "numeroext");
        // Contacto
        String c1 = ParamUtil.getString(resourceRequest, "c1");
        String c1tel1 = ParamUtil.getString(resourceRequest, "c1tel1");
        String c1tel2 = ParamUtil.getString(resourceRequest, "c1tel2");
        String c1email = ParamUtil.getString(resourceRequest, "c1email");
        int c1perfil = ParamUtil.getInteger(resourceRequest, "c1perfil");
        String c2 = ParamUtil.getString(resourceRequest, "c2");
        String c2tel1 = ParamUtil.getString(resourceRequest, "c2tel1");
        String c2tel2 = ParamUtil.getString(resourceRequest, "c2tel2");
        String c2email = ParamUtil.getString(resourceRequest, "c2email");
        int c2perfil = ParamUtil.getInteger(resourceRequest, "c2perfil");
        double valor = ParamUtil.getDouble(resourceRequest, "valor");
        float danos = ParamUtil.getFloat(resourceRequest, "danos");
        float vida = ParamUtil.getFloat(resourceRequest, "vida");
        float gmm = ParamUtil.getFloat(resourceRequest, "gmm");
        float autos = ParamUtil.getFloat(resourceRequest, "autos");
        //
        boolean isClaveEspejo = ParamUtil.getBoolean(resourceRequest, "isClaveEspejo");
        Gson gson = new Gson();
        CRMResponse respuesta = new CRMResponse();
        try {
            Agente agente;
            if (agenteId == 0) {
                agente = _AgenteLocalService.createAgente(agenteId);
                agente.setFechaCreacion(new Date());
                agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_REVISION_LEGAL);
            } else {
                agente = _AgenteLocalService.getAgente(agenteId);
            }
            agente.setPreclave(preclave);
            agente.setAgrupador(agrupador);
            agente.setTipoPersona(tipoPersona);
            agente.setDatosRfc(datosRfc);
            agente.setNombre(nombre);
            agente.setOficinaId(oficina);
            if (tipoPersona == CrmDatabaseKey.PERSONA_MORAL) {
                agente.setTipoSociedad(tipoSociedad);
                agente.setFechaNacimientoConstitucion(fechaConstitucion);
            } else {
                agente.setApellidoP(apellidoP);
                agente.setApellidoM(apellidoM);
                agente.setFechaNacimientoConstitucion(fechaNacimiento);
                agente.setSexo(sexo);
                agente.setRegimen_id(regimenId);
            }
            agente.setTipoNegocio(tipoNegocio);
            agente.setEjecutivo(ejecutivo);
            agente.setUserCreacion(usuario.getUserId());

            switch (new Long(agente.getEstatusAgenteId()).intValue()) {
                case CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL:
                    agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_REVISION_LEGAL);
                    sms.enviarPendienteLegal(preclave,agente.getUserCreacion(),formatoFecha.format(agente.getFechaCreacion()),nombre + " " + apellidoP + " " + apellidoM, agente.getDatosRfc(),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    break;
                case CrmDatabaseKey.ESTATUS_RECHAZADO_CONTABILIDAD:
                    agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD);
                    sms.enviarPendienteContabilidad(agente.getClave(),agente.getUserCreacion(),formatoFecha.format(agente.getFechaCreacion()),agente.getDatosRfc(),nombre + " " + apellidoP + " " + apellidoM, agente.getEjecutivo(),"Alta",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    break;
                case CrmDatabaseKey.ESTATUS_RECHAZADO_MANAGER:
                    agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_PENDIENTE_AUTORIZAR);
                    sms.enviarPendienteVentas(agente.getUserCreacion(),agente.getPreclave(),agente.getDatosRfc(),agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    break;
                case CrmDatabaseKey.ESTATUS_REVISION_LEGAL:
                    if (isClaveEspejo) {
                        agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_PENDIENTE_AUTORIZAR);
                        //sms.enviarPendienteVentas(agente.getUserCreacion(),agente.getPreclave(),agente.getDatosRfc(),agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    } else {
                        sms.enviarPendienteLegal(preclave,agente.getUserCreacion(),formatoFecha.format(agente.getFechaCreacion()),nombre + " " + apellidoP + " " + apellidoM, agente.getDatosRfc(),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    }
                    break;
                default:
                    if (isClaveEspejo) {
                        agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_PENDIENTE_AUTORIZAR);
                        sms.enviarPendienteVentas(agente.getUserCreacion(),agente.getPreclave(),agente.getDatosRfc(),agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(),formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    } else {
                        agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_REVISION_CONTABILIDAD);
                        sms.enviarPendienteContabilidad(agente.getClave(),agente.getUserCreacion(),formatoFecha.format(agente.getFechaCreacion()),agente.getDatosRfc(),nombre + " " + apellidoP + " " + apellidoM, agente.getEjecutivo(),"Alta",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                    }
            }
            agente.setIsClaveEspejo(isClaveEspejo);
            agente.setAgentePadreId(agentePadreId);
            agente.setFechaModificacion(new Date());
            agente = _AgenteLocalService.updateAgente(agente);
            Agente_Domicilio agente_Domicilio;
            Agente_Cartera agente_Cartera;
            if (agenteId == 0) {
                agente_Domicilio = _Agente_DomicilioLocalService.createAgente_Domicilio(agente.getAgenteId());
                agente_Cartera = _Agente_CarteraLocalService.createAgente_Cartera(agente.getAgenteId());
            } else {
                agente_Domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agente.getAgenteId());
                agente_Cartera = _Agente_CarteraLocalService.getAgente_Cartera(agente.getAgenteId());
            }
            agente_Domicilio.setCpId(cpId);
            agente_Domicilio.setCodigo(codigo);
            agente_Domicilio.setCalle(calle);
            agente_Domicilio.setNoExt(numeroext);
            agente_Domicilio.setNoInt(numeroint);
            agente_Domicilio.setNombreContacto1(c1);
            agente_Domicilio.setTel1Contacto1(c1tel1);
            agente_Domicilio.setTel2Contacto1(c1tel2);
            agente_Domicilio.setEmailContacto1(c1email);
            agente_Domicilio.setPerfil1(c1perfil);
            if (Objects.nonNull(c2) && !"".equals(c2)) {
                agente_Domicilio.setNombreContacto2(c2);
                agente_Domicilio.setTel1Contacto2(c2tel1);
                agente_Domicilio.setTel2Contacto2(c2tel2);
                agente_Domicilio.setEmailContacto2(c2email);
                agente_Domicilio.setPerfil2(c2perfil);
            }
            agente_Cartera.setValorCartera(valor);
            agente_Cartera.setVida(vida);
            agente_Cartera.setDanos(danos);
            agente_Cartera.setGmm(gmm);
            agente_Cartera.setAutos(autos);
            // Guardado Final
            //agente_Domicilio =
            _Agente_DomicilioLocalService.updateAgente_Domicilio(agente_Domicilio);
            //agente_Cartera =
            _Agente_CarteraLocalService.updateAgente_Cartera(agente_Cartera);
            // RESPUESTA
            respuesta.setCode(0);
            respuesta.setMsg("Alta de agente realizada con éxito");
        } catch (Exception e) {
            e.printStackTrace();
            respuesta.setCode(1);
            respuesta.setMsg(e.getMessage());
        }
        _log.debug("Guardado Ejecutivo:" + respuesta);
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);

    }

}
