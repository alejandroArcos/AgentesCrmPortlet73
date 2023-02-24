package com.tokio.crm.agentes73.util;

import com.tokio.crm.crmservices73.Bean.RequestKaizen;
import com.tokio.crm.crmservices73.Constants.CrmDatabaseKey;
import com.tokio.crm.servicebuilder73.model.Agente;
import com.tokio.crm.servicebuilder73.model.Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Agente_Contabilidad;
import com.tokio.crm.servicebuilder73.model.Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Agente_Legal;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Cartera;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Contabilidad;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Domicilio;
import com.tokio.crm.servicebuilder73.model.Cambio_Agente_Legal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ConvertObjectsUtil {
    public static final DateFormat formatoFechaKaizen = new SimpleDateFormat("dd/MM/yyyy");

    public static Agente llenarAgentefromCambio(Agente agente, Cambio_Agente cambio_agente) {
        agente.setDatosRfc(cambio_agente.getDatosRfc());
        agente.setClave(cambio_agente.getClave());
        agente.setNombre(cambio_agente.getNombre());
        agente.setApellidoP(cambio_agente.getApellidoP());
        agente.setApellidoM(cambio_agente.getApellidoM());
        agente.setEjecutivo(cambio_agente.getEjecutivo());
        agente.setOficinaId(cambio_agente.getOficinaId());
        agente.setFechaNacimientoConstitucion(cambio_agente.getFechaNacimientoConstitucion());
        agente.setSexo(cambio_agente.getSexo());
        agente.setTipoSociedad(cambio_agente.getTipoSociedad());
        return agente;
    }

    public static Agente_Cartera llenarAgente_CarterafromCambio(Agente_Cartera agente_cartera, Cambio_Agente_Cartera cambio_agente_cartera) {
        agente_cartera.setValorCartera(cambio_agente_cartera.getValor_cartera());
        agente_cartera.setDanos(cambio_agente_cartera.getDanos());
        agente_cartera.setVida(cambio_agente_cartera.getVida());
        agente_cartera.setGmm(cambio_agente_cartera.getGmm());
        agente_cartera.setAutos(cambio_agente_cartera.getAutos());
        return agente_cartera;
    }

    public static Agente_Domicilio llenarAgente_DomiciliofromCambio(Agente_Domicilio agente_domicilio, Cambio_Agente_Domicilio cambio_agente_domicilio) {
        agente_domicilio.setCalle(cambio_agente_domicilio.getCalle());
        agente_domicilio.setCodigo(cambio_agente_domicilio.getCodigo());
        agente_domicilio.setCpId(cambio_agente_domicilio.getCpId());
        agente_domicilio.setNoInt(cambio_agente_domicilio.getNoInt());
        agente_domicilio.setNoExt(cambio_agente_domicilio.getNoExt());
        agente_domicilio.setNombreContacto1(cambio_agente_domicilio.getNombreContacto1());
        agente_domicilio.setTel1Contacto1(cambio_agente_domicilio.getTel1Contacto1());
        agente_domicilio.setTel2Contacto1(cambio_agente_domicilio.getTel2Contacto1());
        agente_domicilio.setEmailContacto1(cambio_agente_domicilio.getEmailContacto1());
        agente_domicilio.setPerfil1(cambio_agente_domicilio.getPerfil1());
        agente_domicilio.setNombreContacto2(cambio_agente_domicilio.getNombreContacto2());
        agente_domicilio.setTel1Contacto2(cambio_agente_domicilio.getTel1Contacto2());
        agente_domicilio.setTel2Contacto2(cambio_agente_domicilio.getTel2Contacto2());
        agente_domicilio.setEmailContacto2(cambio_agente_domicilio.getEmailContacto2());
        agente_domicilio.setPerfil2(cambio_agente_domicilio.getPerfil2());
        return agente_domicilio;
    }

    public static Agente_Legal llenarAgente_LegalfromCambio(Agente_Legal agente_legal, Cambio_Agente_Legal cambio_agente_legal) {
        agente_legal.setNumero_contrato(cambio_agente_legal.getNumero_contrato());
        agente_legal.setTipoCedula(cambio_agente_legal.getTipoCedula());
        agente_legal.setVencimientoCedula(cambio_agente_legal.getVencimientoCedula());
        agente_legal.setPolizaRC(cambio_agente_legal.getPolizaRC());
        agente_legal.setVencimientoPoliza(cambio_agente_legal.getVencimientoPoliza());
        agente_legal.setIdApoderado(cambio_agente_legal.getIdApoderado());
        agente_legal.setVencimientoIdApoderado(cambio_agente_legal.getVencimientoIdApoderado());
        agente_legal.setContrato(cambio_agente_legal.getContrato());
        agente_legal.setVencimientoContrato(cambio_agente_legal.getVencimientoContrato());
        agente_legal.setDatosBancarios(cambio_agente_legal.getDatosBancarios());
        agente_legal.setAutoCNSF(cambio_agente_legal.getAutoCNSF());
        agente_legal.setRfc(cambio_agente_legal.getRfc());
        agente_legal.setActa(cambio_agente_legal.getActa());
        agente_legal.setCartaCompromiso(cambio_agente_legal.getCartaCompromiso());
        return agente_legal;
    }

    public static Agente_Contabilidad llenarAgente_ContabilidadfromCambio(Agente_Contabilidad agente_contabilidad, Cambio_Agente_Contabilidad cambio_agente_contabilidad) {
        agente_contabilidad.setRegistroCuentaBancaria(cambio_agente_contabilidad.getRegistroCuentaBancaria());
        agente_contabilidad.setFechaRegistroCuenta(cambio_agente_contabilidad.getFechaRegistroCuenta());
        return agente_contabilidad;
    }

    public static RequestKaizen convertirObjetosAKaizen(Agente agente, Agente_Domicilio agente_Domicilio, String coloniaTexto) {
        RequestKaizen rk = new RequestKaizen();
        rk.setP_codigo(agente.getClave());
        rk.setP_rfc(agente.getDatosRfc());
        rk.setP_nombre(agente.getNombre());
        rk.setP_paterno(agente.getApellidoP());
        rk.setP_materno(agente.getApellidoM());
        rk.setP_genero((agente.getSexo() == -1 || agente.getSexo() == 0) ? "" : (agente.getSexo() == 50 ? "2" : "1"));
        rk.setP_fecha(formatoFechaKaizen.format(agente.getFechaNacimientoConstitucion()));
        rk.setP_tipper(agente.getTipoPersona() == CrmDatabaseKey.PERSONA_FISICA ? "1" : "2");
        rk.setP_regimen(agente.getRegimen_id() + "");
        rk.setP_calle(agente_Domicilio.getCalle());
        rk.setP_numero(agente_Domicilio.getNoExt());
        rk.setP_colonia(coloniaTexto);
        rk.setP_cp(agente_Domicilio.getCodigo());
        rk.setP_email(agente_Domicilio.getEmailContacto1());
        return rk;
    }
}
