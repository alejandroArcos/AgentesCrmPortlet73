package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.CRMClaveResponse;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
	"mvc.command.name=/crm/agentes/generarPreclave" }, service = MVCResourceCommand.class)

public class GenerarPreclaveCommand extends BaseMVCResourceCommand {
	
	@Reference
	AgenteLocalService _AgenteLocalService;

	@Override
    protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
	    throws Exception {

	int tipoPersona = ParamUtil.getInteger(resourceRequest, "tipoPersona");
	String nombre = ParamUtil.getString(resourceRequest, "nombre");
	String apellidoP = ParamUtil.getString(resourceRequest, "apellidoP");
	String apellidoM = ParamUtil.getString(resourceRequest, "apellidoM");
	String oficina = ParamUtil.getString(resourceRequest, "oficina3");
	int tipoNegocio = ParamUtil.getInteger(resourceRequest, "tipoNegocio");
	long agenteId = ParamUtil.getLong(resourceRequest, "agenteId");
	//System.out.println("agenteId:"+agenteId);
	StringBuilder sb = new StringBuilder();
	sb.append("00 ");
	if (tipoPersona == CrmDatabaseKey.PERSONA_FISICA) {
	    sb.append(nombre, 0, Math.min(nombre.length(), 2));
	    sb.append(apellidoP, 0, apellidoP.length() < 2 ? apellidoM.length() : 2);
	    sb.append(apellidoM, 0, apellidoM.length() < 2 ? apellidoM.length() : 1);
	} else {
	    try {
			sb.append(nombre.replace(" ", ""), 0, 5);
	    } catch (IndexOutOfBoundsException ex) {
			sb.append(nombre.replace(" ", ""));
	    }
	}
	while (sb.length() < 8) {
	    sb.append("0");
	}
	if (tipoNegocio == CrmDatabaseKey.NEGOCIO_J) {
	    sb.append("J");
	} else {
	    sb.append("M");
	}
	sb.append(oficina);
	CRMClaveResponse respuesta = new CRMClaveResponse();
	try {
	    StringBuilder dbb = new StringBuilder(sb.toString());
	    dbb.setCharAt(6, '%');
	    dbb.setCharAt(7, '%');
	    List<Agente> agentes = _AgenteLocalService.findByPreclavesAgenteLike(dbb.toString());
	    Map<String, Object> mapa = agentes.stream().filter(agnt->agnt.getAgenteId()!=agenteId).collect(Collectors.toMap(Agente::getPreclave, Agente::getAgenteId));
	    String pc = generarPreclave(sb.toString(), mapa);
	    respuesta.setCode(0);
	    respuesta.setMsg("Preclave Generada con éxito");
	    respuesta.setPreclave(pc.toUpperCase());
	    respuesta.setAgrupador(pc.toUpperCase().replace("00 ", "02 "));
	} catch (Exception e) {
	    e.printStackTrace();
	    respuesta.setCode(1);
	    respuesta.setMsg(e.getMessage());
	}
	Gson gson = new Gson();
	String responseString = gson.toJson(respuesta);
	PrintWriter writer = resourceResponse.getWriter();
	writer.write(responseString);
    }

    public static String generarPreclave(String preclave, Map<String, Object> mapa) {
	if (mapa.containsKey(preclave)) {
	    String posicion7 = String.valueOf(preclave.charAt(7));
	    String posicion6 = String.valueOf(preclave.charAt(6));
	    StringBuilder nPreclave = new StringBuilder();
	    nPreclave.append(preclave);
	    if (isNumeric(posicion7)) {
		int contador;
		try {
		    contador = Integer.parseInt(posicion7);
		    contador++;
		    if (contador > 9) {
			contador = 0;
			if (isNumeric(posicion6)) {
			    int contador2;
			    try {
				contador2 = Integer.parseInt(posicion6);
				contador2++;
				if (contador2 > 9) {
					return "AGOTADAS";
				}
				nPreclave.setCharAt(6, (char) (contador2 + '0'));
			    } catch (Exception ex) {
				ex.printStackTrace();
				// System.out.println("No era un número:6:" + posicion6);
			    }
			} else {
			    nPreclave.setCharAt(6, '1');
			}
		    }
		    nPreclave.setCharAt(7, (char) (contador + '0'));
		} catch (Exception ex) {
		    ex.printStackTrace();
		    // System.out.println("No era un número:7:" + posicion7);
		}
	    } else {
		nPreclave.setCharAt(7, '1');
	    }
	    return generarPreclave(nPreclave.toString(), mapa);
	} else {
	    return preclave;
	}
    }

    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public static boolean isNumeric(String strNum) {
	if (strNum == null) {
	    return false;
	}
	return pattern.matcher(strNum).matches();
    }
}
