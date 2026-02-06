<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ attribute name="text" required="true" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="placeholder" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="errors" type="java.util.List" %>

<div class="form-group">
    <label for="${id}" class="form-label">${text}</label>
    <input
            type="text"
            id="${id}"
            name="${name}"
            class="form-input"
            placeholder="${placeholder}"
            value="<c:out value='${value}'/>"
            required
    >
    <t:error-list errors="${errors}"/>
</div>