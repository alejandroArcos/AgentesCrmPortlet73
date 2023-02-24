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
import com.tokio.crm.crmservices73.Bean.Archivo;
import com.tokio.crm.crmservices73.Bean.CRMClaveResponse;
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.crmservices73.Bean.ListaArchivo;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.exception.NoSuchAgente_ContabilidadException;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Contabilidad;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Rechazo;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_ContabilidadLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_RechazoLocalService;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/agentes/guardarAgentesContabilidad"}, service = MVCResourceCommand.class)

public class GuardarAgentesContabilidadCommand extends BaseMVCResourceCommand {
	
	public static final DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
	private static final Log _log = LogFactoryUtil.getLog(GuardarAgentesContabilidadCommand.class);
	
	User usuario;
	
	@Reference
	SendMailService sms;
	
	@Reference
	CrmGenerico _CrmGenericoService;
	
	@Reference
	AgenteLocalService _AgenteLocalService;
	
	@Reference
    Agente_ContabilidadLocalService _Agente_ContabilidadLocalService;
	
	@Reference
	Agente_RechazoLocalService _Agente_RechazoLocalService;
	
	@Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
        Date fechaRegistroCuenta = ParamUtil.getDate(resourceRequest, "fechaRegistroCuenta", formatoFecha);
        boolean registroCuentaBancaria = ParamUtil.getBoolean(resourceRequest, "registroCuentaBancaria");

        String observaciones = ParamUtil.getString(resourceRequest, "observaciones");
        boolean esRechazo = ParamUtil.getBoolean(resourceRequest, "rechazo");

        Gson gson = new Gson();
        CRMClaveResponse respuesta = new CRMClaveResponse();
        Agente_Contabilidad agente_Contabilidad;
        try {
            Agente agente = _AgenteLocalService.getAgente(agenteId);
            agente.setFechaModificacion(new Date());
            try {
                agente_Contabilidad = _Agente_ContabilidadLocalService.getAgente_Contabilidad(agenteId);
            } catch (NoSuchAgente_ContabilidadException ex) {
                agente_Contabilidad = _Agente_ContabilidadLocalService.createAgente_Contabilidad(agenteId);
            }
            agente_Contabilidad.setFechaRegistroCuenta(fechaRegistroCuenta);
            agente_Contabilidad.setRegistroCuentaBancaria(registroCuentaBancaria);
            if (esRechazo) {
                Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                agente_rechazo.setFechaCreacion(new Date());
                agente_rechazo.setMotivo(observaciones);
                agente_rechazo.setAgenteId(agenteId);
                agente_rechazo.setUserCreacion(usuario.getUserId());
                agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ALTA);
                _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_RECHAZADO_CONTABILIDAD);
                sms.enviarRechazoContabilidad(agente,formatoFecha.format(agente.getFechaCreacion()),observaciones,themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
            } else {
                Agente_Domicilio agente_Domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agenteId);
                respuesta.setClave(agente.getClave().toUpperCase());
                respuesta.setAgrupador(agente.getAgrupador().toUpperCase());
                respuesta.setPreclave(agente.getPreclave().toUpperCase());
                ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_Domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_Domicilio.getCpId()).findAny().get();
                ListaArchivo listaDocumentosExistentes = _CrmGenericoService.listaDocumentos(agente.getAgrupador(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                List<Archivo> archivos = new ArrayList<>();
                for(Archivo archivo :listaDocumentosExistentes.getArchivo()){
                    archivos.addAll(_CrmGenericoService.descargarDocumento(agente.getAgrupador(), archivo.getId(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73).getArchivo());
                }
                sms.enviarAutorizadoArea(agenteId,selected,"Contabilidad","Alta",themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                sms.enviarAutorizadoContabilidad(agente,archivos,themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_AUTORIZADO);
            }
            agente_Contabilidad = _Agente_ContabilidadLocalService.updateAgente_Contabilidad(agente_Contabilidad);
            agente = _AgenteLocalService.updateAgente(agente);
            _log.debug(agente_Contabilidad);
            _log.debug(agente);
            respuesta.setCode(0);
            respuesta.setMsg("Alta información Contabilidad realizada con éxito");
        } catch (Exception e) {
            respuesta.setCode(2);
            respuesta.setMsg(e.getMessage());
        }
        _log.debug("Guardado Contabilidad:" + respuesta);
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);
    }

}