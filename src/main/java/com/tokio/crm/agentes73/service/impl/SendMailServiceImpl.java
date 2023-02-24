package com.tokio.crm.agentes73.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.tokio.crm.agentes73.service.SendMailService;
import com.tokio.crm.crmservices73.Bean.Archivo;
import com.tokio.crm.crmservices73.Bean.CpData;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.crmservices73.Util.SendMailJavaMail;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Catalogo_Detalle;
import com.tokio.crm.servicebuilder73.model.User_Crm;
import com.tokio.crm.servicebuilder73.service.AgenteLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_CarteraLocalService;
import com.tokio.crm.servicebuilder73.service.Agente_DomicilioLocalService;
import com.tokio.crm.servicebuilder73.service.Catalogo_DetalleLocalService;
import com.tokio.crm.servicebuilder73.service.User_CrmLocalService;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = SendMailService.class)
public class SendMailServiceImpl implements SendMailService {
    private static final Log _log = LogFactoryUtil.getLog(SendMailServiceImpl.class);
    
    @Reference
	User_CrmLocalService _User_CrmLocalService;
    
    @Reference
	Agente_DomicilioLocalService _Agente_DomicilioLocalService;
    
    @Reference
	AgenteLocalService _AgenteLocalService;
    
    @Reference
	Catalogo_DetalleLocalService _Catalogo_DetalleLocalService;
    
    @Reference
    Agente_CarteraLocalService _Agente_CarteraLocalService;

    @Override
    public void enviarPendienteLegal(String clave, long solicitanteId, String fechaSolicitud, String nombreAgente, String rfcAgente, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Solicitud Pendiente de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>${cambioPalabra} una solicitud de Alta de Agentes <b>Pendiente de Autorizar</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                //+ "		<p><a href=\"http://crmqa.tokiomarine.corp:8080/group/guest/agentes\">crmqa.tokiomarine.corp</a></p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL);
        List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL);
        String finalbody = body
                .replace("${claveAgente}",clave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${nombreUsuario}",getUsersNames(analistas))
                .replace("${cambioPalabra}",analistas.size() > 1? "Tienen":"Tienes")
                .replace("${nombreSolicitante}",getUser(solicitanteId).getFullName())
                .replace("${url}",url);
        sendOneMailCCbyUser(getEmailsUsers(analistas),subject,finalbody,getEmailsUsers(managers));
        //sendMailCCbyUsers(analistas,subject,finalbody,getEmailsUsers(managers));
        //List<String> aux = Arrays.asList("erick851@hotmail.es","erick.alvarez@globalquark.com.mx");
        //sendMailCCbyUsers(analistas,subject,finalbody,aux);
    }

    @Override
    public void enviarAutorizadoLegal(String clave, long solicitanteId, String fechaSolicitud, String nombreAgente, String rfcAgente, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Solicitud Pendiente de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>La una solicitud de Alta de Agentes fue <b>Autorizada</b> por Legal y Cumplimiento</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_ANALISTA_LEGAL);
        List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_MANAGER_LEGAL);
        String finalbody = body
                .replace("${claveAgente}",clave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${nombreUsuario}",getUsersNames(analistas))
                .replace("${nombreSolicitante}",getUser(solicitanteId).getFullName())
                .replace("${url}",url);
        sendOneMailCCbyUser(getEmailsUsers(analistas),subject,finalbody,getEmailsUsers(managers));
    }

    @Override
    public void enviarPendienteContabilidad(String clave, long solicitanteId, String fechaSolicitud, String rfcAgente, String nombreAgente, long ejecutivoAsginado, String accion, String url) {
        String subject = "Solicitud de Alta Datos Bancarios";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>${cambioPalabra} una solicitud de ${accion} de Agentes <b>Pendiente de Autorizar</b></p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_ANALISTA_CONTABILIDAD);
        String finalbody = body
                .replace("${claveAgente}",clave)
                .replace("${accion}",accion)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${nombreUsuario}",getUsersNames(analistas))
                .replace("${cambioPalabra}",analistas.size() > 1? "Tienen":"Tienes")
                .replace("${nombreSolicitante}",getUser(solicitanteId).getFullName())
                .replace("${url}",url);
        sendOneMailContabilidad(subject,finalbody, new ArrayList<>());
    }

    @Override
    public void enviarPendienteVentas(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Solicitud Pendiente de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Alta de Agentes <b>Pendiente de Autorizar</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${claveAgente}",preClave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${nombreSolicitante}",getUser(userId).getFullName())
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            sendMailByUsers(managers,subject,finalbody);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarRechazoContabilidad( Agente agente, String fechaSolicitud, String comentario, String url) {
        String subject = "Notificación de Solicitud de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Alta de Agentes <b>Rechazada</b> por Contabilidad</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Oficina:</b> ${oficina}</p>"
                + "		<p><b>Agrupador:</b> ${agrupador}</p>"
                + "		<p><b>Nombre Contacto:</b> ${nombreContacto}</p>"
                + "		<p><b>Tel&eacute;fono:</b> ${telefonoContacto}</p>"
                + "		<p><b>Correo electr&oacute;nico:</b> ${emailContacto}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><b>Comentarios:</b> ${comentarios}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        try{
            Agente_Domicilio agente_domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(agente.getAgenteId());
            String finalbody = body
                    .replace("${claveAgente}",agente.getClave())
                    .replace("${rfcAgente}",agente.getDatosRfc())
                    .replace("${nombreAgente}",generarNombre(agente))
                    .replace("${nombreAsignado}",getUser(agente.getEjecutivo()).getFullName())
                    .replace("${oficina}",obtenerDescripcion(agente.getOficinaId()))
                    .replace("${agrupador}",agente.getAgrupador())
                    .replace("${nombreContacto}",agente_domicilio.getNombreContacto1())
                    .replace("${telefonoContacto}",agente_domicilio.getTel1Contacto1())
                    .replace("${emailContacto}",agente_domicilio.getEmailContacto1())
                    .replace("${comentarios}",comentario)
                    .replace("${fechaSolicitud}",fechaSolicitud)
                    .replace("${url}",url);
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) agente.getEjecutivo());
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            List<String> correosCC = new ArrayList<>(getEmailsUsers(managers));
            correosCC.add("pago_comisiones@tokiomarine.com.mx");
            sendMailCCbyUser((int) agente.getEjecutivo(), subject, finalbody, correosCC);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarRechazoLegal(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String accion, String url) {
        String subject = "Notificación de Solicitud de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de ${accion} de Agentes <b>Rechazada</b> por Legal</p>"
                + "		<p><b>Pre Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><b>Comentarios:</b> ${comentarios}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${accion}",accion)
                .replace("${claveAgente}",preClave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${comentarios}",comentario)
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            sendMailCCbyUser(userId,subject,finalbody,getEmailsUsers(managers));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarRechazoLegalActualizacion(long userId, String clave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String accion, String url){
        String subject = "Notificación de Solicitud de Actualización de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de ${accion} de Agentes <b>Rechazada</b> por Legal</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><b>Comentarios:</b> ${comentarios}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${accion}",accion)
                .replace("${claveAgente}",clave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${comentarios}",comentario)
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            sendMailCCbyUser(userId,subject,finalbody,getEmailsUsers(managers));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarRechazoVentas(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Solicitud de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Alta de Agentes <b>Rechazada</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${claveAgente}",preClave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            sendMailCCbyUser(userId,subject,finalbody,getEmailsUsers(managers));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarAutorizadoUsuario(long userId, String preClave, String clave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Solicitud de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Alta de Agentes <b>Autorizada</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${preClaveAgente}</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${preClaveAgente}",preClave)
                .replace("${claveAgente}",clave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            //List<String> aux = Arrays.asList("erick.alvarez@globalquark.com.mx","erick851@hotmail.es");
            sendMailCCbyUser(userId,subject,finalbody,getEmailsUsers(managers));
            //sendMailCCbyUser(usuario.getUserId(),subject,finalbody,aux);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarAutorizadoClaveEspejo(long userId, String preClave, String clave,String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String url) {
        String subject = "Notificación de Clave Espejo Generada";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Alta de Agentes <b>Generada</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${preClaveAgente}</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${preClaveAgente}",preClave)
                .replace("${claveAgente}",clave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            List<User_Crm> legal = _User_CrmLocalService.getUsers_CrmByArea(CrmDatabaseKey.AREA_LEGAL);
            List<User_Crm> contabilidad = _User_CrmLocalService.getUsers_CrmByArea(CrmDatabaseKey.AREA_CONTABILIDAD);
            List<User_Crm> siniestros = _User_CrmLocalService.getUsers_CrmByArea(CrmDatabaseKey.AREA_SINIESTROS);
            List<User_Crm> emision = _User_CrmLocalService.getUsers_CrmByArea(CrmDatabaseKey.AREA_EMISION);
            List<User_Crm> cc = new ArrayList<>();
            cc.addAll(legal);
            cc.addAll(contabilidad);
            cc.addAll(siniestros);
            cc.addAll(emision);
            cc.addAll(managers);
            //List<String> aux = Arrays.asList("erick.alvarez@globalquark.com.mx","erick851@hotmail.es");
            sendMailCCbyUser(userId,subject,finalbody,getEmailsUsers(cc));
            //sendMailCCbyUser(userId,subject,finalbody,aux);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void enviarAutorizadoArea(long idAgente, CpData selected, String area, String accion, String url) {
        String subject = "Notificación de Solicitud de Alta de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de ${accion} de Agentes <b>Autorizada por ${area}</b></p>"
                + "		<p><b>Canal de Negocio:</b> ${canal}</p>"
                + "		<p><b>Oficina:</b> ${oficina}</p>"
                + "		<p><b>Pre Clave de Agente:</b> ${preClaveAgente}</p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Agrupador:</b> ${agrupador}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Delegaci&oacute;n o Municipio:</b> ${delegacionMunicipio}</p>"
                + "		<p><b>Estado:</b> ${estado}</p>"
                + "		<p><b>Nombre Contacto:</b> ${nombreContacto}</p>"
                + "		<p><b>Tel&eacute;fono:</b> ${telefonoContacto}</p>"
                + "		<p><b>Correo electr&oacute;nico:</b> ${emailContacto}</p>"
                + "		<p><b>Perfil:</b> ${perfilContacto}</p>"
                + "		<p><b>Valor portafolio:</b> ${valorPortafolio}</p>"
                + "		<p><b>% Da&ntilde;os:</b> ${danios}</p>"
                + "		<p><b>% Vida:</b> ${vida}</p>"
                + "		<p><b>% GMM:</b> ${gmm}</p>"
                + "		<p><b>% Autos:</b> ${autos}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        try{
            NumberFormat numberFormat = new DecimalFormat("#############.##");
            Agente agente = _AgenteLocalService.getAgente(idAgente);
            Agente_Domicilio agente_domicilio = _Agente_DomicilioLocalService.getAgente_Domicilio(idAgente);
            Agente_Cartera agente_cartera = _Agente_CarteraLocalService.getAgente_Cartera(idAgente);
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) agente.getEjecutivo());
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            String finalbody = body
                    .replace("${accion}",accion)
                    .replace("${area}",area)
                    .replace("${canal}",agente.getTipoNegocio()==CrmDatabaseKey.NEGOCIO_M?"MD":"J")
                    .replace("${oficina}",obtenerDescripcion(agente.getOficinaId()))
                    .replace("${preClaveAgente}",agente.getPreclave())
                    .replace("${claveAgente}",agente.getPreclave().replace("00", "01"))
                    .replace("${rfcAgente}",agente.getDatosRfc())
                    .replace("${nombreAgente}",generarNombre(agente))
                    .replace("${agrupador}",agente.getAgrupador())
                    .replace("${nombreAsignado}",getUser(agente.getEjecutivo()).getFullName())
                    .replace("${delegacionMunicipio}",selected.getDelegacion())
                    .replace("${estado}",selected.getEstado())
                    .replace("${nombreContacto}",agente_domicilio.getNombreContacto1())
                    .replace("${telefonoContacto}",agente_domicilio.getTel1Contacto1())
                    .replace("${emailContacto}",agente_domicilio.getEmailContacto1())
                    .replace("${perfilContacto}",obtenerDescripcion(agente_domicilio.getPerfil1()))
                    .replace("${valorPortafolio}",numberFormat.format(agente_cartera.getValorCartera()))
                    .replace("${danios}",agente_cartera.getDanos() + "")
                    .replace("${vida}",agente_cartera.getVida() + "")
                    .replace("${gmm}",agente_cartera.getGmm() + "")
                    .replace("${autos}",agente_cartera.getAutos() + "")
                    .replace("${fechaSolicitud}",agente.getFechaCreacion().toString())
                    .replace("${url}",url);
            sendMailCCbyUser(agente.getEjecutivo(),subject,finalbody,getEmailsUsers(managers));
        }catch (Exception e){
            _log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void enviarAutorizadoContabilidad(Agente agente, List<Archivo> archivos, String url) {
        String subject = "Alta de Datos Bancarios Autorizado";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>La solicitud de Alta Datos Bancarios del Agentes fue <b>Autorizado</b></p>"
                + "		<p><b>Pre Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        List<User_Crm> analistas = _User_CrmLocalService.getUsers_CrmByPerfil(CrmDatabaseKey.ID_PERFIL_ANALISTA_CONTABILIDAD);
        String finalbody = body
                .replace("${claveAgente}",agente.getClave())
                .replace("${rfcAgente}",agente.getDatosRfc())
                .replace("${nombreAgente}",generarNombre(agente))
                .replace("${fechaSolicitud}",agente.getFechaCreacion().toString())
                .replace("${nombreAsignado}",getUser(agente.getEjecutivo()).getFullName())
                .replace("${nombreUsuario}",getUsersNames(analistas))
                .replace("${cambioPalabra}",analistas.size() > 1? "Tienen":"Tienes")
                .replace("${nombreSolicitante}",getUser(agente.getUserCreacion()).getFullName())
                .replace("${url}",url);
        sendOneMailContabilidad(subject,finalbody,archivos);
    }

    @Override
    public void enviarPendienteVentasActualizar(long userId, String preClave, String rfcAgente, String nombreAgente, String fechaSolicitud, long ejecutivoAsginado, String comentario, String url) {
        String subject = "Notificación de Solicitud Pendiente de Actualización de Agente";
        String body = "<!DOCTYPE html> \r\n"
                + "<html>   \r\n"
                + "<head>  \r\n"
                + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />\r\n"
                + "</head>\r\n"
                + "<body>\r\n"
                + "	<section>\r\n"
                + "		<p>Hola ${nombreUsuario}</p>\r\n"
                + "		<p>Tienes una solicitud de Actualizaci&oacute;n de RFC <b>Pendiente de Autorizar</b></p>"
                + "		<p><b>Clave de Agente:</b> ${claveAgente}</p>"
                + "		<p><b>RFC de Agente:</b> ${rfcAgente}</p>"
                + "		<p><b>Nombre de Agente:</b> ${nombreAgente}</p>"
                + "		<p><b>Ejecutivo Asignado:</b> ${nombreAsignado}</p>"
                + "		<p><b>Nombre del solicitante:</b> ${nombreSolicitante}</p>"
                + "		<p><b>Fecha de solicitud:</b> ${fechaSolicitud}</p>"
                + "		<p><b>Solicitud:</b> ${comentario}</p>"
                + "		<p><br/><br/></p>"
                +"      <p>ingresa al CRM para m&aacute;s informaci&oacute;n</p>\r\n"
                + "		<p><a href=\"${url}\">https://crm.tokiomarine.corp/</a></p>\r\n"
                + "		<p>Cualquier duda o comentario, por favor ac&eacute;rcate al equipo de Ventas.</p>\r\n"
                + "	</section>\r\n"
                + "</body>\r\n"
                + "</html>";
        String finalbody = body
                .replace("${claveAgente}",preClave)
                .replace("${rfcAgente}",rfcAgente)
                .replace("${nombreAgente}",nombreAgente)
                .replace("${fechaSolicitud}",fechaSolicitud)
                .replace("${nombreAsignado}",getUser(ejecutivoAsginado).getFullName())
                .replace("${comentario}",comentario)
                .replace("${nombreSolicitante}",getUser(userId).getFullName())
                .replace("${url}",url);
        try{
            User_Crm usuario = _User_CrmLocalService.getUser_Crm((int) userId);
            List<User_Crm> managers = _User_CrmLocalService.getUsers_CrmByAreaPerfil(usuario.getArea(),CrmDatabaseKey.ID_PERFIL_MANAGER_VENTAS);
            sendMailByUsers(managers,subject,finalbody);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public User getUser(long userId) {
        try {
            return UserLocalServiceUtil.getUserById(userId);
        } catch (PortalException e) {
            return null;
        }
    }

    public User getUser(User_Crm usr) {
        try {
            return UserLocalServiceUtil.getUserById(usr.getUserId());
        } catch (PortalException e) {
            return null;
        }
    }

    public String getEmailAddress(long userId) {
        try {
            return UserLocalServiceUtil.getUserById(userId).getEmailAddress();
        } catch (PortalException e) {
            return "";
        }
    }

    public String getEmailAddress(User_Crm usr) {
        try {
            return UserLocalServiceUtil.getUserById(new Long(usr.getUserId())).getEmailAddress();
        } catch (PortalException e) {
            return "";
        }
    }

    public void sendMailByUsers(List<User_Crm> usuarios, String subject, String body){
        for(User_Crm user_crm: usuarios){
            if(user_crm.isAccesoCRM()){
                sendMailByUser(user_crm,subject,body);
            }
        }
    }

    public void sendMailByUser( User_Crm usuario, String subject, String body){
        SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{getEmailAddress(usuario)});
        //SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{"erick851a@gmail.com"});
        List<String> emails = new ArrayList<>();
        //List<String> emails = Arrays.asList("erick851@hotmail.es","erick.alvarez@globalquark.com.mx");
        try {
            sendMailJavaMail.setMailCC(emails);
            sendMailJavaMail.setSubject(subject);
            sendMailJavaMail.setBody(body.replace("${nombreUsuario}", getUser(usuario).getFullName()));
            sendMailJavaMail.addBody();
            sendMailJavaMail.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendMailCCbyUsers(List<User_Crm> usuarios, String subject, String body, List<String> mails){
        for(User_Crm user_crm: usuarios){
            if(user_crm.isAccesoCRM()){
                _log.debug(user_crm);
                sendMailCCbyUser(user_crm.getUserId(),subject,body,mails);
            }
        }
    }

    public void sendMailCCbyUser(long usuario, String subject, String body, List<String> mails){
        SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{getEmailAddress(usuario)});
       //SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{"erick851a@gmail.com"});
        try {
            sendMailJavaMail.setMailCC(mails);
            sendMailJavaMail.setSubject(subject);
            sendMailJavaMail.setBody(body.replace("${nombreUsuario}", getUser(usuario).getFullName()));
            sendMailJavaMail.addBody();
            sendMailJavaMail.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getEmailsUsers(List<User_Crm> usuarios){
        List<String> emailsCC = new ArrayList<>();
        usuarios.forEach(f -> {
            String correo = getEmailAddress(f.getUserId());
            if(!"".equals(correo) && f.isAccesoCRM()){
                emailsCC.add(correo);
            }
        });
        //emailsCC.add("erick.alvarez@globalquark.com.mx");
        return emailsCC;
    }

    public String getUsersNames (List<User_Crm> usuarios){
        String saludo = "";
        for(User_Crm user_crm: usuarios){
            String nombre = getUser(user_crm.getUserId()).getFullName();
            if(!"".equals(nombre) && user_crm.isAccesoCRM()){
                if("".equals(saludo)){
                    saludo = nombre;
                }else{
                    saludo += ", " + nombre;
                }
            }
        }
        return saludo;
    }

    public void sendOneMailCCbyUser(List<String> analistas, String subject, String body, List<String> mails){
        SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(analistas.toArray(new String[0]));
        try {
            sendMailJavaMail.setMailCC(mails);
            sendMailJavaMail.setSubject(subject);
            sendMailJavaMail.setBody(body);
            sendMailJavaMail.addBody();
            sendMailJavaMail.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendOneMailContabilidad(String subject, String body, List<Archivo> archivos){
        SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{"pago_comisiones@tokiomarine.com.mx"});
        //SendMailJavaMail sendMailJavaMail = new SendMailJavaMail(new String[]{"rebekro@hotmail.com"});
        if(!archivos.isEmpty()){
            File file;
            byte[] decodedBytes;
            Base64.Decoder decoder = Base64.getMimeDecoder();
            for(Archivo archivo: archivos){
                file = new File(archivo.getNombre() + "." + archivo.getExtension());
                decodedBytes = decoder.decode(archivo.getArchivo());
                try {
                    FileUtils.writeByteArrayToFile(file, decodedBytes);
                    sendMailJavaMail.addFile(file,file.getName());
                }catch (Exception ignored){
                }
            }
        }
        try {
            sendMailJavaMail.setSubject(subject);
            sendMailJavaMail.setBody(body);
            sendMailJavaMail.addBody();
            sendMailJavaMail.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String obtenerDescripcion(long idCatalogoDetalle){
        try {
            return _Catalogo_DetalleLocalService.getCatalogo_Detalle(idCatalogoDetalle).getDescripcion();
        }catch (Exception e){
            return "";
        }
    }

    public String generarNombre(Agente agente) {
        Map<Integer,String> mapaSociedades = _Catalogo_DetalleLocalService.findByCodigo("CATTIPSOC").stream().collect(Collectors.toMap(e -> (int) e.getCatalogoDetalleId(), Catalogo_Detalle::getDescripcion));
        if (agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA) {
            return agente.getNombre() + " " + agente.getApellidoP() + " " + agente.getApellidoM();
        } else {
            return agente.getNombre() + " " + (agente.getTipoSociedad() != 45 ? mapaSociedades.get(agente.getTipoSociedad()) : "");
        }
    }

}
