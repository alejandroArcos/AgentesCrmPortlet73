package com.tokio.crm.agentes73.commands.resource;

import com.google.gson.Gson;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.tokio.crm.agentes73.constants.AgentesCrmPortlet73PortletKeys;
import com.tokio.crm.crmservices73.Bean.CRMResponse;
import com.tokio.crm.crmservices73.Interface.CrmGenerico;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = { "javax.portlet.name=" + AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,
		"mvc.command.name=/crm/agentes/subirDocumentos" }, service = MVCResourceCommand.class)

public class SubirDocumentosCommand extends BaseMVCResourceCommand {

	private static final Log _log = LogFactoryUtil.getLog(SubirDocumentosCommand.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	CrmGenerico _CrmGenericoService;
	
	User usuario;
	
	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {
		Gson gson = new Gson();
		String responseString = gson.toJson(enviaArchivos(resourceRequest));
		PrintWriter writer = resourceResponse.getWriter();
		writer.write(responseString);
	}

	CRMResponse enviaArchivos(ResourceRequest resourceRequest) {
		CRMResponse response;
		String auxiliarDoc = HtmlUtil.unescape(ParamUtil.getString(resourceRequest, "auxiliarDoc"));
		String tipoDoc= ParamUtil.getString(resourceRequest, "tipoDoc");
		String agente= ParamUtil.getString(resourceRequest, "agente");
		usuario = (User) resourceRequest.getAttribute(WebKeys.USER);
		//String p_usuario = usuario.getScreenName();
		String nombre = "file";
		try {
			JSONObject jsonObj;
			jsonObj = JSONFactoryUtil.createJSONObject(auxiliarDoc);
			UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(resourceRequest);
			File file = uploadRequest.getFile(nombre);
			String mimeType = uploadRequest.getContentType(nombre);
			float n = file.length() / 1024 / 1024;
			float n64 = 4 * (n / 3) + (n % 3 != 0 ? 4 : 0);
			JSONObject jsonObj2;
			jsonObj2 = JSONFactoryUtil.createJSONObject(jsonObj.getString(nombre));
			String nom = jsonObj2.getString("nom").replace(" ", "_");
			Map<String, Object> info;
			if (n64 > 1.49) {
				_log.debug(jsonObj2);
				info = guardaDocumentos(resourceRequest, file, nom, mimeType, jsonObj2.getString("ext"));
				response=_CrmGenericoService.subirDocumento(agente, tipoDoc, nom, jsonObj2.getString("ext"), "", usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,"1",info.get("url").toString());
				if (response.getMsg().toLowerCase().trim().equals("ok")) {
					elimianArchivo((long) info.get("idDoc"));
				}
				//_log.debug(info);
			} else {
				String dataArchivo = Base64.encode(Files.readAllBytes(file.toPath()));
				response=_CrmGenericoService.subirDocumento(agente, tipoDoc, nom, jsonObj2.getString("ext"), dataArchivo, usuario.getScreenName(), AgentesCrmPortlet73PortletKeys.AGENTESCRMPORTLET73,"0","");
			}
		} catch (Exception e) {
			_log.error(e.getMessage());
			e.printStackTrace();
			response = new CRMResponse();
			response.setCode(1);
			response.setMsg("Error al subir el archivo, favor de volver a intentarlo");
		}
		return response;
	}

	Map<String, Object> guardaDocumentos(ResourceRequest resourceRequest, File file, String nombre, String mimeType, String ext){
		try {

			Map<String, Object> respuesta = new HashMap<>();
			String tipoDoc= ParamUtil.getString(resourceRequest, "tipoDoc");
			String agente= ParamUtil.getString(resourceRequest, "agente");
			ThemeDisplay td  = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
			User user = (User) resourceRequest.getAttribute(WebKeys.USER);
			String aux2 = user.getScreenName() + "-" + nombre + "-A_" + agente + "-T_" + tipoDoc;

			long idGroup = PortalUtil.getScopeGroupId(resourceRequest);
			ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFolder.class.getName(), resourceRequest);

			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			DLFolder fCotizadores = DLFolderLocalServiceUtil.getFolder(idGroup, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					"Documentos_Agentes");

			FileEntry fileEntry = _dlAppService.addFileEntry(idGroup, fCotizadores.getFolderId(), nombre + "." + ext,
					mimeType, nombre + "." + ext, aux2, "hi", file, serviceContext);

			String urlDoc = td.getPortalURL() + "/documents/" + idGroup + "/" + fileEntry.getFolderId() + "/" + fileEntry.getFileName()
					+ "/" + fileEntry.getUuid();

			respuesta.put("url", urlDoc);
			respuesta.put("idDoc", fileEntry.getFileEntryId());
			return respuesta;
		} catch (PortalException e) {
			_log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	void elimianArchivo(long idDoc){
		try {
			_dlAppService.deleteFileEntry(idDoc);
		} catch (PortalException e) {
			_log.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
