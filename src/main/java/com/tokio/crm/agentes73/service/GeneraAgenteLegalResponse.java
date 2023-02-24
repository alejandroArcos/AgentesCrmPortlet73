package com.tokio.crm.agentes73.service;

import com.tokio.crm.agentes73.model.AgenteLegalResponse;
import com.tokio.crm.servicebuilder73.model.Agente;

import java.util.List;
import java.util.Map;

public interface GeneraAgenteLegalResponse {

    List<AgenteLegalResponse> generaAgenteLegalResponse(List<Agente> agentes, Map<Integer, String> mapaSociedad);
}
