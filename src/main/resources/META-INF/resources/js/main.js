var mostrarLog = true;

$("#dc_nombre_p2").on("keyup",function (event){
   var nombre_p2 = $("#dc_nombre_p2").val();
   if(nombre_p2 === ""){
       $("#dc_telefono1_p2").removeClass("requerido");
       $("#dc_email_p2").removeClass("requerido");
       $("#dc_perfil_p2").removeClass("requerido");
   }else{
       $("#dc_telefono1_p2").addClass("requerido");
       $("#dc_email_p2").addClass("requerido");
       $("#dc_perfil_p2").addClass("requerido");
   }
});

function obtenerPreclave() {
    $.post(generarPreclaveURL, {
        /*General*/
        tipoNegocio: $("#tipo_negocio input:checked").val(),
        tipoPersona: $("#tipo_persona input:checked").val(),
        nombre: eliminarEspeciales($("#dp_nombre").val()),
        apellidoP: eliminarEspeciales($("#dp_apellidoP").val()),
        apellidoM: eliminarEspeciales($("#dp_apellidoM").val()),
        agenteId: numeroAgenteId,
        oficina3: $("#dg_oficina").attr("codigo")
    }).done((data) => {
        if (mostrarLog) {
            console.log(data);
        }
        var response = JSON.parse(data);
        llenaCampoText($("#dg_preclave"), response.preclave, true);
        console.log("Es clave espejo?:" + isClaveEspejo)
        if (!isClaveEspejo) {
            llenaCampoText($("#mc_agrupador"), response.agrupador, true);
            limpiarUploads();
        }
    });
}

function buscarRfc() {
    let rfc = $("#dg_rfc").val();
    if ( rfc !== "") {
        $("#claveEspejoRFC").val(rfc);
        $.post(buscarRfcURL, {
            rfc: rfc,
            agenteId: numeroAgenteId
        }).done((data) => {
            if (mostrarLog) {
                console.log(data);
            }
            var response = JSON.parse(data);
            if (response.code !== 0) {
                $("#claveEspejoIdAgente").val(response.agenteId);
                $("#modal-repetido .modal-body .text").html("El agente con RFC <b>\"" + $("#dg_rfc").val() + "\"</b> ya existe<br><br>");
                $('#modal-repetido').modal('show');
            }
        });
    }
}

function crearClaveEspejo() {
    showLoader();
    $('#formClaveEspejo').submit();
}

function guardarAgenteLegal(rechazo) {

    let valida = true;
    valida = vaciosInpText($("#fl_fechaAlta")) ? false : valida;

    if(valida || rechazo){
        $.post(guardarAgentesLegalURL, {
            cambioId:cambioId,
            agenteId: numeroAgenteId,
            numContrato: $("#fl_numeroContrato").val(),
            tipoSociedad: $("#dp_tipoSociedad option:selected").html(),
            fechaAlta: $("#fl_fechaAlta").val(),
            tipoCedula: $("#fl_tipoCedula").val(),
            vencimientoCedula: $("#fl_vencimientoCedula").val(),
            compDomicilio: $("#fl_compDomicilio:checked")[0] === undefined ? false : true,
            vencimientoCompDomicilio: $("#fl_vencimientoCompDomicilio").val(),
            polizaRC: $("#fl_polizaRC:checked")[0] === undefined ? false : true,
            vencimientoPolizaRC: $("#fl_vencimientoPolizaRC").val(),
            idApoderado: $("#fl_idApoderado:checked")[0] == undefined ? false : true,
            vencimientoIdApoderado: $("#fl_vencimientoIdApoderado").val(),
            contrato: $("#fl_contrato:checked")[0] == undefined ? false : true,
            vencimientoContrato: $("#fl_vencimientoContrato").val(),
            datosBancarios: $("#fl_datosBancarios:checked")[0] == undefined ? false : true,
            autoCNSF: $("#fl_autoCNSF:checked")[0] == undefined ? false : true,
            rfc: $("#fl_RFC:checked")[0] == undefined ? false : true,
            acta: $("#fl_acta:checked")[0] == undefined ? false : true,
            poder: $("#fl_poder:checked")[0] == undefined ? false : true,
            cartaCompromiso: $("#fl_cartaCompromiso:checked")[0] == undefined ? false : true,
            observaciones: $("#fl_observaciones").val(),
            rechazo: rechazo,
            colonia: $("#dr_colonia option:selected").html()
        }).done((data) => {
            if (mostrarLog) {
                console.log(data);
            }
            var response = JSON.parse(data);
            if (response.code == 0) {
                if (rechazo) {
                    $('#fin').submit();
                } else {
                    $("#claveDefinitiva").html("<br/><center>" + response.clave + "</center><br/>");
                    hideLoader();
                    $("#modal-clave").modal("show");
                }
            }else if(response.code == 3){
                $('#fin').submit();
            } else {
                hideLoader();
                showMessageError('.navbar', response.msg, 0);
            }
        });
    }
}

function guardarAgenteManager(rechazo) {
    $.post(guardarAgentesManagerURL, {
        agenteId: numeroAgenteId,
        observaciones: $("#fm_observaciones").val(),
        rechazo: rechazo
    }).done((data) => {
        var response = JSON.parse(data);
        if (response.code == 0) {
            $('#fin').submit();
        } else {
            hideLoader();
            showMessageError('.navbar', response.msg, 0);
        }
    });
}

function guardarAgenteContabilidad(rechazo) {
    $.post(guardarAgentesContabilidadURL, {
        agenteId: numeroAgenteId,
        fechaRegistroCuenta: $("#fc_fechaRegistroC").val(),
        registroCuentaBancaria: $("#fc_registroCuentaB:checked")[0] == undefined ? false : true,
        observaciones: $("#fc_observaciones").val(),
        rechazo: rechazo
    }).done((data) => {
        var response = JSON.parse(data);
        if (response.code == 0) {
            $('#fin').submit();
        } else {
            hideLoader();
            showMessageError('.navbar', response.msg, 0);
        }
    });
}

function rechazoL() {
    if (mostrarLog) {
        console.log("Rechazando Legal");
    }
    if ($(".rechazolegal").is(":hidden")) {
        $(".rechazolegal").show();
    } else {
        if ($(".rechazolegal textarea").val() == "") {
            showMessageError('.navbar', "La informaci&oacute;n del rechazo es obligatoria", 0);
        } else {
            showLoader();
            guardarAgenteLegal(true);
        }
    }
}

function rechazoM() {
    if (mostrarLog) {
        console.log("Rechazando Manager");
    }
    if ($(".rechazoManager").is(":hidden")) {
        $(".rechazoManager").show();
    } else {
        if ($(".rechazoManager textarea").val() == "") {
            showMessageError('.navbar', "La informaci&oacute;n del rechazo es obligatoria", 0);
        } else {
            showLoader();
            guardarAgenteManager(true);
        }
    }
}

function rechazoC() {
    if (mostrarLog) {
        console.log("Rechazando Contabilidad");
    }
    if ($(".rechazoContabilidad").is(":hidden")) {
        $(".rechazoContabilidad").show();
    } else {
        if ($(".rechazoContabilidad textarea").val() == "") {
            showMessageError('.navbar', "La informaci&oacute;n del rechazo es obligatoria", 0);
        } else {
            showLoader();
            guardarAgenteContabilidad(true);
        }
    }
}

function guardarAgente(isClaveEspejo) {
    if(validaRequeridos()) {
        if (isClaveEspejo === 0) {
            if ($(".suma input").toArray().reduce((sum, element) => {
                if (isNaN(sum)) sum = 0;
                return sum + Number(element.value)
            }, 0) !== 100) {
                hideLoader();
                showMessageError('.navbar', "La suma de la cartera debe ser igual a 100%", 0);
                return false;
            }
        }
        $.post(guardarAgentesURL, {
            /*General*/
            agenteId: numeroAgenteId,
            agentePadreId: numeroAgentePadreId,
            tipoNegocio: $("#tipo_negocio input:checked").val(),
            ejecutivo: $("#dg_ejecutivo").val(),
            oficinaId: $("#dg_oficina").attr("idoficina"),
            preclave: $("#dg_preclave").val(),
            agrupador: $("#mc_agrupador").val(),
            tipoPersona: $("#tipo_persona input:checked").val(),
            datosRfc: $("#dg_rfc").val(),
            nombre: $("#dp_nombre").val(),
            apellidoP: $("#tipo_persona input:checked").val() == 2 ? $("#dp_tipoSociedad option:selected").html() : $("#dp_apellidoP").val(),
            apellidoM: $("#dp_apellidoM").val(),
            tipoSociedad: $("#dp_tipoSociedad").val() == -1 ? null : $("#dp_tipoSociedad").val(),
            fechaNacimiento: $("#dp_fechaNacimiento").val(),
            fechaConstitucion: $("#dp_fechaConstitucion").val(),
            regimenId:$("#dg_regimenFiscal option:selected").val(),
            sexo: $("#dp_sexo").val(),
            /*Direccion*/
            cp: $("#dr_colonia").val(),
            codigo: $("#dr_cp").val(),
            calle: $("#dr_calle").val(),
            numeroint: $("#dr_numeroi").val(),
            numeroext: $("#dr_numero").val(),
            /*Contacto*/
            c1: $("#dc_nombre_p1").val(),
            c1tel1: $("#dc_telefono1_p1").val() + "|" + $("#dc_ext1_p1").val(),
            c1tel2: $("#dc_telefono2_p1").val() + "|" + $("#dc_ext2_p1").val(),
            c1email: $("#dc_email_p1").val(),
            c1perfil: $("#dc_perfil_p1").val(),
            c2: $("#dc_nombre_p2").val(),
            c2tel1: $("#dc_telefono1_p2").val() + "|" + $("#dc_ext1_p2").val(),
            c2tel2: $("#dc_telefono2_p2").val() + +"|" + $("#dc_ext2_p2").val(),
            c2email: $("#dc_email_p2").val(),
            c2perfil: $("#dc_perfil_p2").val(),
            valor: $("#pf_valor").val(),
            danos: $("#pf_danos").val(),
            vida: $("#pf_vida").val(),
            gmm: $("#pf_gmm").val(),
            autos: $("#pf_autos").val(),
            isClaveEspejo: isClaveEspejo,
        }).done((data) => {
            var response = JSON.parse(data);
            if (response.code == 0) {
                $('#fin').submit();
            } else {
                showMessageError('.navbar', response.msg, 0);
                hideLoader();
            }
        });
    }
}

function nombreCompleto() {
    var nombreFinal = "";
    if ($("#dp_nombre").val() !== "") {
        if ($("#tipo_persona input:checked").val() === '1') {
            nombreFinal = $("#dp_nombre").val() + " " + $("#dp_apellidoP").val() + " " + $("#dp_apellidoM").val();
        } else {
            nombreFinal = $("#dp_nombre").val() + " " + ($("#dp_tipoSociedad option:selected").val() === '-1' ? "" : $("#dp_tipoSociedad option:selected").text());
        }
        llenaCampoText($("#dp_nombreCompleto"), nombreFinal, true);
    }
    validarPreclave();
}

function nombreCompletoV2() {
    var nombreFinal = "";
    if ($("#dp_nombre").val() !== "") {
        if ($("#tipo_persona input:checked").val() === '1') {
            nombreFinal = $("#dp_nombre").val() + " " + $("#dp_apellidoP").val() + " " + $("#dp_apellidoM").val();
        } else {
            nombreFinal = $("#dp_nombre").val() + " " + ($("#dp_tipoSociedad option:selected").val() === '-1' ? "" : $("#dp_tipoSociedad option:selected").text());
        }
        llenaCampoText($("#dp_nombreCompleto"), nombreFinal, true);
    }
}

function validarPreclave() {
    llenaCampoText($("#dg_preclave"), "", true);
    if (!isClaveEspejo) {
        llenaCampoText($("#mc_agrupador"), "", true);
    }
    limpiarUploads();

    if ($("#tipo_persona input:checked").val() == 1) {
        if ($("#dp_nombre").val() != "" && $("#dp_apellidoP").val() != "" && $("#dp_apellidoM").val() != "" && $("#dg_oficina").attr("codigo") != "") {
            obtenerPreclave();
        }
    } else {
        if ($("#dp_nombre").val() != "" && $("#dg_oficina").attr("codigo") != "") {
            obtenerPreclave();
        }
    }
}

function mostrarPM() {
    $(".seccion-moral").show();
    $(".seccion-fisica").hide();
}

function mostrarPF() {
    $(".seccion-moral").hide();
    $(".seccion-fisica").show();
}

function inicializarSecciones() {
    if ($("#tipo_persona input:checked").val() == 1) {
        mostrarPF();
    } else {
        mostrarPM();
    }
    nombreCompletoV2();
}

function cambiarTipoPersona() {
    $("#dg_preclave").val("");
    $("#mc_agrupador").val("");
    $("#dp_apellidoP").val("");
    $("#dp_apellidoM").val("");
    $("#dp_tipoSociedad").val(-1);
    if ($("#tipo_persona input:checked").val() == 1) {
        mostrarPF();
    } else {
        mostrarPM();
    }
    nombreCompleto();
}

function cargarEjecutivos() {
    if (mostrarLog) {
        console.log("CargarEjecutivos");
    }
    $("#mc_agrupador").val("");
    $("#dg_preclave").val("");
    $.post(buscarEjecutivosURL, {
        tipoNegocio: $("#tipo_negocio input:checked").val()
    }).done((data) => {
        if (mostrarLog) {
            console.log(data);
        }
        var response = JSON.parse(data);
        if (response.code == 0) {
            cargarInfoEjecutivos(response.ejecutivos);
        } else {
            showMessageError('.navbar', response.msg, 0);
        }
        hideLoader();
    });

}

function limpiarUploads() {
    $(".upload-item").each((element, form) => {
        form.reset()
    });
    $(".upload-item .uploaded").addClass("not-uploaded");
    $(".upload-item .uploaded").removeClass("uploaded");
}

function cargarInfoEjecutivos(ejecutivos) {
    $("#dg_oficina").attr("codigo", "");
    $("#dg_oficina").attr("idoficina", "");
    $("#dg_oficina").val("");

    $("#dg_ejecutivo option:not(:first)").remove();
    selectDestroy($("#dg_ejecutivo"), false);
    $.each(ejecutivos, function (key, value) {
        var opt = document.createElement('option');
        opt.value = value.id;
        $(opt).attr('oficina', value.oficina);
        opt.innerHTML = value.nombre;
        $('#dg_ejecutivo').append(opt);
        selectDestroy($('#dg_ejecutivo'), false);
    });
}

function cambiaOficina() {
    var idOficina = $("#dg_ejecutivo option:selected").attr("oficina");
    var codigoOficina = $("#dg_oficina-select option[value='" + idOficina + "']").attr("codigo");
    var nombreOficina = $("#dg_oficina-select option[value='" + idOficina + "']").html();
    $("#dg_oficina").attr("codigo", codigoOficina);
    $("#dg_oficina").attr("idoficina", idOficina);
    $("#dg_oficina").val(nombreOficina);
    $("#dg_oficina").siblings("label").addClass("active");
    validarPreclave();
}

function limpiaCamposCp(ubicacionPadre) {
    $("#dr_calle").val("");
    $("#dr_numero").val("");
    $("#dr_municipio").val("");
    $("#dr_estado").val("");
    $("#dr_colonia option:not(:first)").remove();
    selectDestroy($("#dr_colonia"), false);
}

var aux;

function llenaInfoByCP(ubicacionPadre, cp) {
    $.post(obtenerCPURL, {
        cp: cp
    }).done((data) => {
        var response = JSON.parse(data);
        if (response.code == 0) {
            aux = ubicacionPadre;
            if (mostrarLog) {
                console.log(ubicacionPadre);
            }
            llenaInfoCp(ubicacionPadre, response.cpData);
        } else {
            $(ubicacionPadre).find("#dr_cp").val("");
            showMessageError('.navbar', response.msg, 0);
        }
        hideLoader();
    });
}

function llenaInfoCp(ubicacionPadre, cpData) {
    if (mostrarLog) {
        console.log(cpData);
    }
    $.each(cpData, function (key, value) {
        $("#dr_municipio").val(value.delegacion);
        $("#dr_municipio").siblings("label").addClass("active");
        $("#dr_estado").val(value.estado);
        $("#dr_estado").siblings("label").addClass("active");
        var opt = document.createElement('option');
        opt.value = value.id;
        opt.innerHTML = value.colonia;
        $('#dr_colonia').append(opt);
        selectDestroy($('#dr_colonia'), false);
    });
}

function uploadFile() {
    var aux = event.target;
    if ($("#mc_agrupador").val() != "") {
        console.log("UploadFile:" + aux);
        adjuntaArchivos(aux.id);
    } else {
        showMessageError('.navbar', "Se requiere generar Preclave y Agrupador para subir un documento", 0);
        var id = aux.id.split("-")[1];
        $("#form-" + id)[0].reset();
        $("#div-" + id).removeClass("uploaded");
        $("#div-" + id).addClass("not-uploaded");
        console.log(id);
    }
}

function soloLectura() {
    $("#formEjecutivo .accordion .card-header").addClass("soloLectura");
    $("#formEjecutivo input").attr("disabled", true);
}

function downloadFile() {
    showLoader();
    var aux = event.target;
    var id = aux.id.split("-")[1];
    $.post(descargarDocumentosURL, {
        agrupador: $("#mc_agrupador").val(),
        tipoArchivo: id
    }).done((data) => {
        var response = JSON.parse(data);
        if (response.code == 0) {
            if ((response.archivo != "")) {
                downloadDocument(response.archivo[0].archivo, (response.archivo[0].nombre + "." + response.archivo[0].extension));
            } else {
                showMessageError('.navbar', "No es posible descargar el archivo", 0);
            }
        } else {
            showMessageError('.navbar', response.msg, 0);
        }
        hideLoader();
    });
}

async function adjuntaArchivos(elementId) {
    showLoader();
    var data = new FormData();
    var auxiliarDoc = '{';
    data.append('file', $("#" + elementId)[0].files[0]);
    var nomAux = $("#" + elementId)[0].files[0].name.split('.');
    auxiliarDoc += '\"file\" : {';
    auxiliarDoc += '\"nom\" : \"' + nomAux[0] + '\",';
    auxiliarDoc += '\"ext\" : \"' + nomAux[1] + '\"}';
    auxiliarDoc += '}';
    data.append('auxiliarDoc', auxiliarDoc);
    data.append('tipoDoc', $("#" + elementId)[0].id.split("-")[1]);
    data.append('agente', $("#mc_agrupador").val());
    var id = $("#" + elementId)[0].id.split("-")[1];
    $.ajax({
        url: subirDocumentosURL,
        data: data,
        processData: false,
        contentType: false,
        type: 'POST',
        success: function (data) {
            if (data != "") {
                var response = jQuery.parseJSON(data);
                console.log(response);
                if (response.code > 0) {
                    hideLoader();
                    showMessageError('.navbar', response.msg, 0);
                    $("#form-" + id)[0].reset();
                    $("#div-" + id).removeClass("uploaded");
                    $("#div-" + id).addClass("not-uploaded");
                }
                if (response.code == 0) {
                    hideLoader();
                    $("#div-" + id).removeClass("not-uploaded");
                    $("#div-" + id).addClass("uploaded");
                }
            } else {
                showMessageError('.navbar', "Error al enviar la información", 0);
                hideLoader();
                $("#form-" + id)[0].reset();
                $("#div-" + id).removeClass("uploaded");
                $("#div-" + id).addClass("not-uploaded");
            }
        }
    });
}

function downloadBlob(blob, filename) {
    if (window.navigator.msSaveOrOpenBlob) {
        window.navigator.msSaveBlob(blob, filename);
    } else {
        var elem = window.document.createElement('a');
        elem.href = window.URL.createObjectURL(blob);
        elem.download = filename;
        document.body.appendChild(elem);
        elem.click();
        document.body.removeChild(elem);
    }
}


function validaFlotantes() {
    var aux = $(event.target).val().split('.');
    $(event.target).val(aux[0]);
    $(event.target).val(function (index, value) {
        if (aux.length > 1) {
            return Number(value.replace(/\D/g, "").slice(0, $(event.target)[0].size)) + '.' + aux[1].replace(/\D/g, "").slice(0, 2);
        } else {
            return Number(value.replace(/\D/g, "").slice(0, $(event.target)[0].size));
        }
    });
}

function validaPorcentajeDecimales() {
    var aux = $(event.target).val().split('.');
    $(event.target).val(aux[0]);
    $(event.target).val(function (index, value) {
        if (aux.length > 1) {
            if(aux[0]>99){
                return "100";
            }
            return Number(aux[0].replace(/\D/g, "").slice(0, $(event.target)[0].size)) + '.' + aux[1].replace(/\D/g, "").slice(0, 2);
        } else {
            if(value>99){
                return "100";
            }
            return Number(value.replace(/\D/g, "").slice(0, $(event.target)[0].size));
        }
    });
}

function downloadDocument(strBase64, filename) {
    var url = "data:application/octet-stream;base64," + strBase64;
    var documento = null;
    fetch(url)
        .then(function (res) {
            return res.blob()
        })
        .then(function (blob) {
            downloadBlob(blob, filename);
        });
}

function validarNumeros() {
    $('input.floatNumber').on('input', function () {
        this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
    });
}

function asignaCP() {
    $("#dr_cp").blur((e) => {
        showLoader();
        cp = $(e.target).val();
        var ubicacionPadre = $(".ubicacion")
        if ((cp.length < 5) && (cp.length > 0)) {
            $(e.target).focus();
            showMessageError('.navbar', 'Por favor verifique el código postal a 5 dígitos', 0);
            hideLoader();
        } else if (cp.length == 5) {
            limpiaCamposCp(ubicacionPadre);
            llenaInfoByCP(ubicacionPadre, cp);
        } else {
            limpiaCamposCp(ubicacionPadre);
            showMessageSuccess('.navbar', ' Código postal vacio', 0);
            hideLoader();
        }
    });
}

function validarNumerosSolo() {
    $('input.numeros').on('input', function () {
        this.value = this.value.replace(/[^\d]/g, "");
    });
}
function validarNumerosyLetras() {
    $('input.numerosLetras').on('input', function () {
        this.value = this.value.replace(/[^a-zA-Z\d]/g, "");
    });
}

function eliminarEspeciales(especiales) {
    return especiales.replace(/[^a-zA-Z\d]/g, "");
}

function validaRequeridos(){
    var campos = $(".requerido:visible");
    var completos = true;
    $(campos).removeClass('invalid');
    $(".alert.alert-danger").remove();
    $.each(campos, function(key, campo) {
        if($(campo).hasClass("select-wrapper")){
            let select = $(campo).children('select');
            completos = noSelect($(select)) ? false : completos;
        }else{
            completos = vaciosInpText($(campo)) ? false : completos;
        }
    });
    if(perfil == 9 || perfil == 8 ){
        let documentos = $(".fa-upload").length;
        let subidos = $(".uploaded").length;
        if(subidos !== documentos){
            showMessageError(".navbar","Error debes proporcionar todos los documentos. ",0);
            hideLoader();
            completos = false;
        }
    }
    if(!completos){
        showMessageError( '.modal-header', "Falta información requerida", 0 );
        hideLoader();
    }
    return completos;
}

function noSelect(campo) {
    var errores = false;
    if ($(campo).val() == "-1") {
        errores = true;
        $(campo).siblings("input").addClass('invalid');
        $(campo).parent().append(
            "<div class=\"alert alert-danger\"> <span class=\"glyphicon glyphicon-ban-circle\"></span> " + " Campo Requerido" +
            "</div>");
    }

    return errores;
}

function vaciosInpText(value) {
    var errores = false;
    if($(value).is(":visible")){
        if (valIsNullOrEmpty($(value).val())) {
            errores = true;
            $(value).addClass('invalid');
            $(value).parent().append(
                "<div class=\"alert alert-danger\" role=\"alert\"> <span class=\"glyphicon glyphicon-ban-circle\"></span>" + " Campo Requerido"
                + "</div>");
        }
    }
    return errores;
}

function perfilamiento(){
    switch (perfil) {
        case '3':
        case '11':
            if(cambio == 1){
                if(estatus == 338){
                    pintarCamposParaEditarEjecutivoVentas(solCambiosVentas);
                }
            }
            break;
        case '1':
        case '10':
            if(estatusAgente != 10 && estatusAgente != 73){
                $("#autorizarContabilidad").hide();
                $("#rechazarContabilidad").hide();
            }
            break;
    }
}

function fechaActual(sw){
    if($(sw).is(':checked')){
        let hoy = new Date();
        let fecha = hoy.toISOString().split("T");
        $("#fc_fechaRegistroC").val(fecha[0]).siblings("label").addClass("active");
    }
}

function pintarCamposParaEditarEjecutivoVentas(campos) {
    let claseCampos = campos.split(";");
    if (claseCampos.length > 1) {
        $(".edicionEjecutivo.corregir").each((e, f) => {
            $(f).addClass(claseCampos[e] == "true" ? "click" : "");
        });
    }
}