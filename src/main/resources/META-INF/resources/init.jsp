<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri ="http://java.sun.com/jsp/jstl/functions" prefix ="fn"%>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="version" scope="session" value="DEV.26052022.16:27" />
<liferay-theme:defineObjects />

<portlet:defineObjects />
<style>
	.AgentesCrmPortlet .switch label input[type=checkbox]:checked + .lever:after {
  background-color: #4285f4 !important; }

.AgentesCrmPortlet .switch label input[type=checkbox]:checked + .lever {
  background-color: #c7e0f1 !important; }

.AgentesCrmPortlet .file-field.big .file-path-wrapper {
  height: 3.2rem; }

.AgentesCrmPortlet .file-field.big .file-path-wrapper .file-path {
  height: 3rem; }

.AgentesCrmPortlet #documentosAltaAgente .md-form {
  min-height: 8em;
  padding-top: 2em;
  border: solid silver 1px;
  border-radius: 1em; }

.AgentesCrmPortlet .sin-filtro .dt-buttons {
  visibility: hidden;
  display: none; }

.AgentesCrmPortlet .sin-filtro .dataTables_filter {
  visibility: hidden;
  display: none; }

.AgentesCrmPortlet .sin-registros .dataTables_length {
  visibility: hidden;
  display: none; }

.AgentesCrmPortlet .check-bottom {
  position: absolute;
  bottom: 1em; }

.AgentesCrmPortlet div.switch {
  min-width: 105px; }

.AgentesCrmPortlet .label-switch.small {
  font-size: 0.8rem; }

.AgentesCrmPortlet table.table a {
  margin: 0;
  color: #0275d8;
  text-decoration: underline; }

.AgentesCrmPortlet .soloLectura {
  background: silver !important; }

.AgentesCrmPortlet .uploaded::before {
  content: "\f00c";
  position: absolute;
  left: 5em;
  font-weight: 900;
  font-family: 'Font Awesome 5 Free';
  color: green; }

.AgentesCrmPortlet .not-uploaded::before {
  content: "\f00d";
  position: absolute;
  left: 5em;
  font-weight: 900;
  font-family: 'Font Awesome 5 Free';
  color: pink; }

.AgentesCrmPortlet .upload-item .file-path.validate, .AgentesCrmPortlet .download-item .file-path.validate {
  font-size: 10.5px; }

    .AgentesCrmPortlet .rounded-silver {
        border: solid silver 1px;
        border-radius: 1em;
        margin-bottom: 1em;
        padding-bottom: 1em;
        padding-right: 1em;
        background: #9999990d; }

</style>

