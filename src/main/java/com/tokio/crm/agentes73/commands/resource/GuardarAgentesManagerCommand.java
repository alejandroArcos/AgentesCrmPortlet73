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
import com.tokio.crm.crmservices73.Bean.CRMResponse;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Bean.RequestKaizen;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Rechazo;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_RechazoLocalService;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
        "mvc.command.name=/crm/agentes/guardarAgentesManager"}, service = MVCResourceCommand.class)

public class GuardarAgentesManagerCommand extends BaseMVCResourceCommand {
	
	public static final DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
	private static final Log _log = LogFactoryUtil.getLog(GuardarAgentesManagerCommand.class);
	
	User usuario;
	
	@Reference
	SendMailService sms;
	
	@Reference
	CrmGenerico _CrmGenericoService;
	
	@Reference
	Agente_RechazoLocalService _Agente_RechazoLocalService;
	
	@Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;
	
	@Reference
	AgenteLocalService _AgenteLocalService;

    @Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
            throws Exception {

        usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
        ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
        _log.debug(themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);

        long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
        String observaciones = ParamUtil.getString(resourceRequest, "observaciones");
        boolean esRechazo = ParamUtil.getBoolean(resourceRequest, "rechazo");

        Gson gson = new Gson();
        CRMResponse respuesta = new CRMResponse();

        try {
            Agente agente = _AgenteLocalService.getAgente(agenteId);
            agente.setFechaModificacion(new Date());
            if (esRechazo) {
                Agente_Rechazo agente_rechazo = _Agente_RechazoLocalService.createAgente_Rechazo(0);
                agente_rechazo.setFechaCreacion(new Date());
                agente_rechazo.setMotivo(observaciones);
                agente_rechazo.setAgenteId(agenteId);
                agente_rechazo.setUserCreacion(usuario.getUserId());
                agente_rechazo.setTipoRechazo(CrmDatabaseKey.RECHAZO_ALTA);
                _Agente_RechazoLocalService.updateAgente_Rechazo(agente_rechazo);
                agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_RECHAZADO_MANAGER);
                //sms.enviarRechazoVentas(agente.getUserCreacion(),agente.getPreclave(),agente.getDatosRfc(),agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
            } else {
                agente.setEstatusAgenteId(CrmDatabaseKey.ESTATUS_AUTORIZADO);
                agente.setClave(agente.getPreclave().toUpperCase().replace("00", "01"));
                //sms.enviarAutorizadoClaveEspejo(agente.getUserCreacion(),agente.getPreclave(),agente.getClave(),agente.getDatosRfc(),agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM(), formatoFecha.format(agente.getFechaCreacion()),agente.getEjecutivo(),themeDisplay.getURLPortal() + themeDisplay.getURLCurrent().split("\\?")[0]);
                try{
                    Agente_Domicilio agente_domicilio= _Agente_DomicilioLocalService.getAgente_Domicilio(agenteId);
                    ListaCpData listaCpData=_CrmGenericoService.getCatalogoCP(agente_domicilio.getCodigo(), usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                    String coloniaTexto = listaCpData.getCpData().stream().filter(f-> f.getId()==agente_domicilio.getCpId()).collect(Collectors.toList()).get(0).getColonia();
                    //String coloniaTexto = listaCpData.getCpData().stream().filter(f-> f.getId()==agente_domicilio.getCpId()).findFirst().get().getColonia();
                    RequestKaizen rk = ConvertObjectsUtil.convertirObjetosAKaizen(agente, agente_domicilio, coloniaTexto);
                    _CrmGenericoService.insertaAgenteKaizen(rk, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
                }catch (Exception e){
                    _log.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            _AgenteLocalService.updateAgente(agente);
            respuesta.setCode(0);
            respuesta.setMsg("Alta Manager realizada con éxito");
        } catch (Exception e) {
            respuesta.setCode(2);
            respuesta.setMsg(e.getMessage());
        }
        _log.debug("Guardado Manager...");
        String responseString = gson.toJson(respuesta);
        PrintWriter writer = resourceResponse.getWriter();
        writer.write(responseString);
    }

}
