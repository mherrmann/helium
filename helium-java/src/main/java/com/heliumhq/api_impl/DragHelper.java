package com.heliumhq.api_impl;

import com.heliumhq.selenium_wrappers.WebElementWrapper;

class DragHelper {

	private final APIImpl apiImpl;
	private boolean isHtml5Drag;

	DragHelper(APIImpl apiImpl) {
		this.apiImpl = apiImpl;
		this.isHtml5Drag = false;
	}

	void begin() {
		executeScript(
			"window.helium = {};" +
			"window.helium.dragHelper = {" +
			"    createEvent: function(type) {" +
			"        var event = document.createEvent('CustomEvent');" +
			"        event.initCustomEvent(type, true, true, null);" +
			"        event.dataTransfer = {" +
			"            data: {}," +
			"            setData: function(type, val) {" +
			"                this.data[type] = val;" +
			"            }," +
			"            getData: function(type) {" +
			"                return this.data[type];" +
			"            }" +
			"        };" +
			"        return event;" +
			"    }" +
			"};"
		);
	}

	void startDragging(WebElementWrapper element) {
		if (attemptHtml5Drag(element))
			isHtml5Drag = true;
		else
			apiImpl.pressMouseOn(element);
	}

	void dropOnTarget(WebElementWrapper target) {
		if (isHtml5Drag)
			completeHtml5Drag(target);
		else
			apiImpl.releaseMouseOver(target);
	}

	void end() {
		executeScript("delete window.helium;");
	}

	private boolean attemptHtml5Drag(WebElementWrapper elementToDrag) {
		return (Boolean) executeScript(
			"var source = arguments[0];" +
			"function getDraggableParent(element) {" +
			"    var previousParent = null;" +
			"    while (element != null && element != previousParent) {" +
			"        previousParent = element;" +
			"        if ('draggable' in element) {" +
			"            var draggable = element.draggable;" +
			"            if (draggable === true)" +
			"                return element;" +
			"            if (typeof draggable == 'string' " +
			"                    || draggable instanceof String)" +
			"                if (draggable.toLowerCase() == 'true')" +
			"                    return element;" +
			"        }" +
			"        element = element.parentNode;" +
			"    }" +
			"    return null;" +
			"}" +
			"var draggableParent = getDraggableParent(source);" +
			"if (draggableParent == null)" +
			"    return false;" +
			"window.helium.dragHelper.draggedElement = draggableParent;" +
			"var dragStart = " +
				"window.helium.dragHelper.createEvent('dragstart');" +
			"source.dispatchEvent(dragStart);" +
			"window.helium.dragHelper.dataTransfer = dragStart.dataTransfer;" +
			"return true;",
			elementToDrag.unwrap()
		);
	}

	private void completeHtml5Drag(WebElementWrapper on) {
		executeScript(
			"var target = arguments[0];" +
			"var drop = window.helium.dragHelper.createEvent('drop');" +
			"drop.dataTransfer = window.helium.dragHelper.dataTransfer;" +
			"target.dispatchEvent(drop);" +
			"var dragEnd = window.helium.dragHelper.createEvent('dragend');" +
			"dragEnd.dataTransfer = window.helium.dragHelper.dataTransfer;" +
			"window.helium.dragHelper.draggedElement.dispatchEvent(dragEnd);",
			on.unwrap()
		);
	}

	private Object executeScript(String script, Object... args) {
		return apiImpl.requireDriver().executeScript(script, args);
	}

}
