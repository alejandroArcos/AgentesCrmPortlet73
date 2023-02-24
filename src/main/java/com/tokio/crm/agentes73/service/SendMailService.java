package com.tokio.crm.agentes73.service;

import com.tokio.crm.crmservices73.Bean.Archivo;
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.servicebuilder73.model.Agente;

import java.util.List;

public interface SendMailService {

    void enviarPendienteLegal(String clave, long solicitanteId, String fechaSolicitud, String nombreAgente, String rfcAgente, long ejecutivoAsginado, String url);

    void enviarAutorizadoLegal(String clave, long solicitanteId, String fechaSolicitud, String nombreAgente, String rfcAgente, long ejecutivoAsginado, String url);

    void enviarPendienteContabilidad(String clave, long solicitanteId, String fechaSolicitud, String rfcAgente, String nombreAgente, long ejecutivoAsginado, String accion, String url);

    void enviarPendienteVentas(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url);

    void enviarRechazoContabilidad( Agente agente, String fechaSolicitud, String comentario, String url);

    void enviarRechazoLegal(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String accion, String url);

    void enviarRechazoLegalActualizacion(long userId, String clave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String accion, String url);

    void enviarRechazoVentas(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url);

    void enviarAutorizadoUsuario(long userId, String preClave, String clave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url);

    void enviarAutorizadoClaveEspejo(long userId, String preClave, String clave,String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url);

    void enviarAutorizadoArea(long idAgente, CpData selected, String area, String accion, String url);

    void enviarAutorizadoContabilidad(Agente agente, List<Archivo> archivos, String url);

    void enviarPendienteVentasActualizar(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String url);
}
