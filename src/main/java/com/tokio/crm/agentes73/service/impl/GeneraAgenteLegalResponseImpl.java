package com.tokio.crm.agentes73.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.tokio.crm.agentes73.model.AgenteLegalResponse;
import com.tokio.crm.agentes73.service.GeneraAgenteLegalResponse;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.servicebuilder73.exception.NoSuchCambio_AgenteException;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Legal;
import com.tokio.crm.servicebuilder73.service.Agente_LegalLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Cambio_Agente_LegalLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = GeneraAgenteLegalResponse.class)
public class GeneraAgenteLegalResponseImpl implements GeneraAgenteLegalResponse {
	
	@Reference
	Cambio_AgenteLocalService _Cambio_AgenteLocalService;
	
	@Reference
	Agente_LegalLocalService _Agente_LegalLocalService;
	
	@Reference
	Cambio_Agente_LegalLocalService _Cambio_Agente_LegalLocalService;
	
    @Override
    public List<AgenteLegalResponse> generaAgenteLegalResponse(List<Agente> agentes, Map<Integer, String> mapaSociedad) {
        List<AgenteLegalResponse> agenteLegalResponses = new ArrayList<>();
        AgenteLegalResponse agenteLegalResponse;
        Cambio_Agente cambio_agente;
        Agente_Legal agente_legal = null;
        Cambio_Agente_Legal cambio_agente_legal;
        for (Agente agente : agentes) {
            agenteLegalResponse = new AgenteLegalResponse();
            try {
                cambio_agente = _Cambio_AgenteLocalService.findByAgenteActivoByIdAgente(agente.getAgenteId());
                agenteLegalResponse = new AgenteLegalResponse();
                agenteLegalResponse.setAgenteId(cambio_agente.getAgenteId());
                agenteLegalResponse.setClave(cambio_agente.getClave());
                agenteLegalResponse.setRfc(cambio_agente.getDatosRfc());
                agenteLegalResponse.setEstatusVentas(cambio_agente.getEstatusCambioId());
                agenteLegalResponse.setNombre(generarNombreCambioAgente(cambio_agente,mapaSociedad));
                agenteLegalResponse.setEjecutivo(cambio_agente.getEjecutivo());
                agenteLegalResponse.setOficina(cambio_agente.getOficinaId());
                agenteLegalResponse.setFechaRegistro(agente.getFechaCreacion());
                try {
                    agente_legal = _Agente_LegalLocalService.getAgente_Legal(agente.getAgenteId());
                    cambio_agente_legal = _Cambio_Agente_LegalLocalService.getCambio_Agente_Legal(cambio_agente.getCambioId());
                    agenteLegalResponse.setContrato(cambio_agente_legal.getNumero_contrato());
                    agenteLegalResponse.setEstatusLC(cambio_agente_legal.getEstatus_legal());
                    agenteLegalResponse.setFechaAlta(cambio_agente_legal.getFechaAlta());
                    agenteLegalResponse.setTipoCedula(cambio_agente_legal.getTipoCedula());
                    agenteLegalResponse.setVencimientoCedula(cambio_agente_legal.getVencimientoCedula());
                    agenteLegalResponse.setComprobante(cambioBoleano(cambio_agente_legal.getComprobanteDomicilio()));
                    agenteLegalResponse.setVencimientoComprobante(cambio_agente_legal.getVencimientoCompDomicilio());
                    agenteLegalResponse.setPolizaRC(cambioBoleano(cambio_agente_legal.getPolizaRC()));
                    agenteLegalResponse.setVencimientoPoliza(cambio_agente_legal.getVencimientoPoliza());
                }catch (PortalException ignored){
                    agenteLegalResponse.setContrato(agente_legal.getNumero_contrato());
                    agenteLegalResponse.setEstatusLC(agente_legal.getEstatus_legal());
                    agenteLegalResponse.setFechaSeguimiento(agente_legal.getFecha_creacion());
                    agenteLegalResponse.setFechaAlta(agente_legal.getFechaAlta());
                    agenteLegalResponse.setTipoCedula(agente_legal.getTipoCedula());
                    agenteLegalResponse.setVencimientoCedula(agente_legal.getVencimientoCedula());
                    agenteLegalResponse.setComprobante(cambioBoleano(agente_legal.getComprobanteDomicilio()));
                    agenteLegalResponse.setVencimientoComprobante(agente_legal.getVencimientoCompDomicilio());
                    agenteLegalResponse.setPolizaRC(cambioBoleano(agente_legal.getPolizaRC()));
                    agenteLegalResponse.setVencimientoPoliza(agente_legal.getVencimientoPoliza());
                }
            }catch (NoSuchCambio_AgenteException ignored){
                agenteLegalResponse.setAgenteId(agente.getAgenteId());
                agenteLegalResponse.setClave(!"".equals(agente.getClave())?agente.getClave():agente.getPreclave());
                agenteLegalResponse.setRfc(agente.getDatosRfc());
                agenteLegalResponse.setEstatusVentas(agente.getEstatusAgenteId());
                agenteLegalResponse.setNombre(generarNombre(agente,mapaSociedad));
                agenteLegalResponse.setEjecutivo(agente.getEjecutivo());
                agenteLegalResponse.setOficina(agente.getOficinaId());
                agenteLegalResponse.setFechaRegistro(agente.getFechaCreacion());
                try {
                    agente_legal = _Agente_LegalLocalService.getAgente_Legal(agente.getAgenteId());
                    agenteLegalResponse.setContrato(agente_legal.getNumero_contrato());
                    agenteLegalResponse.setEstatusLC(agente_legal.getEstatus_legal());
                    agenteLegalResponse.setFechaSeguimiento(agente_legal.getFecha_creacion());
                    agenteLegalResponse.setFechaAlta(agente_legal.getFechaAlta());
                    agenteLegalResponse.setTipoCedula(agente_legal.getTipoCedula());
                    agenteLegalResponse.setVencimientoCedula(agente_legal.getVencimientoCedula());
                    agenteLegalResponse.setComprobante(cambioBoleano(agente_legal.getComprobanteDomicilio()));
                    agenteLegalResponse.setVencimientoComprobante(agente_legal.getVencimientoCompDomicilio());
                    agenteLegalResponse.setPolizaRC(cambioBoleano(agente_legal.getPolizaRC()));
                    agenteLegalResponse.setVencimientoPoliza(agente_legal.getVencimientoPoliza());
                }catch (PortalException ignoredLegal){

                }
            }
            agenteLegalResponses.add(agenteLegalResponse);
        }
        return agenteLegalResponses;
    }

    public String generarNombre(Agente agente, Map<Integer, String> mapaSociedades) {
        if (agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA) {
            return agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM();
        } else {
            return agente.getNombre() + " " + (agente.getTipoSociedad() != 45 ? mapaSociedades.get(agente.getTipoSociedad()) : "");
        }
    }

    public String generarNombreCambioAgente(Cambio_Agente cambio_agente, Map<Integer, String> mapaSociedades) {
        if (cambio_agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA) {
            return cambio_agente.getNombre() + " " + cambio_agente.getApellidoP() + " " + cambio_agente.getApellidoM();
        } else {
            return cambio_agente.getNombre() + " " + (cambio_agente.getTipoSociedad() != 45 ? mapaSociedades.get(cambio_agente.getTipoSociedad()) : "");
        }
    }

    public String cambioBoleano (boolean x){
        return x ? "SI" : "NO";
    }

}
