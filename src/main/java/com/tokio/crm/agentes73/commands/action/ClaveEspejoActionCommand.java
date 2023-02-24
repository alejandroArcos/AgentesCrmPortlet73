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
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.crmservices73.Bean.ListaCpData;
import com.tokio.crm.crmservices73.Bean.ListaRegistro;
import com.tokio.crm.crmservices73.Bean.UsuarioCrm;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Constants.CrmServiceKey;
import com.tokio.crm.crmservices73.Exeption.CrmServicesException;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Rechazo;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.Catalogo_DetalleModel;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_RechazoLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.init-param.copy-request-parameters=true",
	"javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/action/agentes/claveEspejo" }, service = MVCActionCommand.class)

public class ClaveEspejoActionCommand extends BaseMVCActionCommand {

    private static final Log _log = LogFactoryUtil.getLog(ClaveEspejoActionCommand.class);
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
	Agente_RechazoLocalService _Agente_RechazoLocalService;

    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		HttpServletRequest originalRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(actionRequest));
		usuario = (User) originalRequest.getAttribute(WebKeys.USER);
		User_Crm user_Crm = _User_CrmLocalService.getUser_Crm(Long.valueOf(usuario.getUserId()).intValue());
		actionRequest.setAttribute("perfilUsuario", user_Crm.getPerfilId());
		Agente agente = cargaInformacionAgente(actionRequest);
		cargaCatalogos(actionRequest, agente);
		actionRequest.setAttribute("nuevo", 1);
		actionResponse.setRenderParameter("jspPage", "/claveEspejo.jsp");
    }

    public Agente cargaInformacionAgente(ActionRequest actionRequest) {
		String id = actionRequest.getParameter("claveEspejoIdAgente");
		Agente agente = null;
		Agente_Domicilio agente_Domicilio;
		List<Agente_Rechazo> agente_Rechazo;
		try {
			agente = _AgenteLocalService.getAgente(Long.parseLong(id));
			actionRequest.setAttribute("agente", agente);
			agente.setAgentePadreId(Long.valueOf(agente.getAgenteId()).intValue());
			agente.setIsClaveEspejo(true);
			agente.setAgenteId(0);
			agente.setOficinaId(0);
			agente.setPreclave("");
			_log.debug(agente);
			actionRequest.setAttribute("clavePrimaria",agente.getClave());
			String nombre;
			if(agente.getTipoPersona()==CrmDatabaseKey.PERSONA_FISICA){
				nombre=agente.getNombre()+" "+agente.getApellidoP()+" "+agente.getApellidoM();
				ListaRegistro listaRegistro = _CrmGenericoService.getCatalogo(CrmServiceKey.TMX_CTE_ROW_TODOS,CrmServiceKey.TMX_CTE_TRANSACCION_GET,"CATREGIMEN",CrmServiceKey.TMX_CTE_CAT_ACTIVOS,usuario.getScreenName(),AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
				actionRequest.setAttribute("listaRegimen",listaRegistro.getLista());
			}else {
				List<Catalogo_Detalle> tiposSociedad = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC");
			final long tss=agente.getTipoSociedad();
				nombre=agente.getNombre()+" "+(tiposSociedad.stream().filter(ts->ts.getCatalogoDetalleId()==tss).map(Catalogo_DetalleModel::getDescripcion).findFirst().get());
			}
			actionRequest.setAttribute("nombreClavePrimaria",nombre);
			if (agente.getEstatusAgenteId() == CrmDatabaseKey.ESTATUS_RECHAZADO_LEGAL || agente.getEstatusAgenteId() == CrmDatabaseKey.ESTATUS_RECHAZADO_CONTABILIDAD) {
				//agente_Rechazo = _Agente_RechazoLocalService.findByIdAgente(Long.valueOf(id));
				agente_Rechazo = _Agente_RechazoLocalService.findByIdAgenteAndTipoRechazo(Long.parseLong(id),CrmDatabaseKey.RECHAZO_ALTA);
				actionRequest.setAttribute("rechazos", agente_Rechazo);
				_log.debug(agente_Rechazo);
			}
		} catch (NumberFormatException | PortalException | CrmServicesException e ) {
			e.printStackTrace();
		}
		try {
			agente_Domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(Long.parseLong(id));
			actionRequest.setAttribute("agenteD", agente_Domicilio);
			_log.debug(agente_Domicilio);
			if (Objects.nonNull(agente_Domicilio.getCodigo())) {
				ListaCpData cpData = _CrmGenericoService.getCatalogoCP(agente_Domicilio.getCodigo(),usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73);
				CpData selected = cpData.getCpData().stream().filter(e -> e.getId() == agente_Domicilio.getCpId()).findAny().get();
				actionRequest.setAttribute("dEstado", selected.getEstado());
				actionRequest.setAttribute("dMunicipio", selected.getDelegacion());
				actionRequest.setAttribute("dColonia", selected.getColonia());
				actionRequest.setAttribute("cpData", cpData.getCpData());
			}
			agente_Domicilio.setTel1Contacto1(agente_Domicilio.getTel1Contacto1().contains("|")?agente_Domicilio.getTel1Contacto1():(agente_Domicilio.getTel1Contacto1()+"|"));
			agente_Domicilio.setTel2Contacto1(agente_Domicilio.getTel2Contacto1().contains("|")?agente_Domicilio.getTel2Contacto1():(agente_Domicilio.getTel2Contacto1()+"|"));
			agente_Domicilio.setTel1Contacto2(agente_Domicilio.getTel1Contacto2().contains("|")?agente_Domicilio.getTel1Contacto2():(agente_Domicilio.getTel1Contacto2()+"|"));
			agente_Domicilio.setTel2Contacto2(agente_Domicilio.getTel2Contacto2().contains("|")?agente_Domicilio.getTel2Contacto2():(agente_Domicilio.getTel2Contacto2()+"|"));
		} catch (NumberFormatException | PortalException | CrmServicesException e) {
			e.printStackTrace();
		}
		return agente;
    }

    public void cargaCatalogos(ActionRequest actionRequest, Agente agente) {
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
			_log.debug(agente.getClave());
			List<User_Crm> ejecutivos = _User_CrmLocalService.getUsers_CrmByAreaPerfil(Objects.isNull(agente) ? CrmDatabaseKey.AREA_NEGOCIO_M: (agente.getTipoNegocio() == CrmDatabaseKey.NEGOCIO_J? CrmDatabaseKey.AREA_NEGOCIO_J: CrmDatabaseKey.AREA_NEGOCIO_M),CrmDatabaseKey.ID_PERFIL_EJECUTIVO_VENTAS);
			List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByAreaPerfil(Objects.isNull(agente) ? CrmDatabaseKey.AREA_NEGOCIO_M : (agente.getTipoNegocio() == CrmDatabaseKey.NEGOCIO_J ? CrmDatabaseKey.AREA_NEGOCIO_J: CrmDatabaseKey.AREA_NEGOCIO_M),CrmDatabaseKey.ID_PERFIL_ANALISTA_VENTAS);
			List<UsuarioCrm> listadoUsuarios = Stream.concat(ejecutivos.stream(), analistas.stream()).map(usr -> {
				try {
					return new UsuarioCrm(UserLocalServiceUtil.getUserById(usr.getUserId()).getFullName().toUpperCase(), usr.getUserId(), usr.getOficina());
				} catch (PortalException e) {
					return null;
				}
			}).collect(Collectors.toList());
			actionRequest.setAttribute("listaUsuarios", listadoUsuarios);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
