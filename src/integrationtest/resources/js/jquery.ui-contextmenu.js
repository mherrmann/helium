/*******************************************************************************
 * jquery.ui-contextmenu.js plugin.
 *
 * jQuery plugin that provides a context menu (based on the jQueryUI menu widget).
 *
 * @see https://github.com/mar10/jquery-ui-contextmenu
 *
 * Copyright (c) 2013, Martin Wendt (http://wwWendt.de). Licensed MIT.
 */
;(function($, window, document, undefined) {
	"use strict";
	var supportSelectstart = "onselectstart" in document.createElement("div");

	/** Return command without leading '#' (default to ""). */
	function normCommand(cmd){
		return (cmd && cmd.match(/^#/)) ? cmd.substring(1) : (cmd || "");
	}


	$.widget("moogle.contextmenu", {
		version: "1.2.2",
		options: {
			delegate: null,       // selector
			hide: { effect: "fadeOut", duration: "fast"},
			ignoreParentSelect: true, // Don't trigger 'select' for sub-menu parents
			menu: null,           // selector or jQuery pointing to <UL>, or a definition hash
			position: null,       // popup positon
			preventSelect: false, // disable text selection of target
			show: { effect: "slideDown", duration: "fast"},
			taphold: false,       // open menu on taphold events (requires external plugins)
			// Events:
			beforeOpen: $.noop,   // menu about to open; return `false` to prevent opening
			blur: $.noop,         // menu option lost focus
			close: $.noop,        // menu was closed
			create: $.noop,       // menu was initialized
			createMenu: $.noop,   // menu was initialized (original UI Menu)
			focus: $.noop,        // menu option got focus
			open: $.noop,         // menu was opened
			select: $.noop        // menu option was selected; return `false` to prevent closing
		},
		/** Constructor */
		_create: function () {
			var eventNames, targetId,
				opts = this.options;

			this.$headStyle = null;
			this.$menu = null;
			this.menuIsTemp = false;
			this.currentTarget = null;

			if(opts.preventSelect){
				// Create a global style for all potential menu targets
				// If the contextmenu was bound to `document`, we apply the
				// selector relative to the <body> tag instead
				targetId = ($(this.element).is(document) ? $("body") : this.element).uniqueId().attr("id");
				this.$headStyle = $("<style class='moogle-contextmenu-style'>")
					.prop("type", "text/css")
					.html("#" + targetId + " " + opts.delegate + " { " +
						"-webkit-user-select: none; " +
						"-khtml-user-select: none; " +
						"-moz-user-select: none; " +
						"-ms-user-select: none; " +
						"user-select: none; " +
						"}")
					.appendTo("head");
				// TODO: the selectstart is not supported by FF?
				if(supportSelectstart){
					this.element.delegate(opts.delegate, "selectstart" + this.eventNamespace, function(event){
						event.preventDefault();
					});
				}
			}
			this._createUiMenu(opts.menu);

			eventNames = "contextmenu" + this.eventNamespace;
			if(opts.taphold){
				eventNames += " taphold" + this.eventNamespace;
			}
			this.element.delegate(opts.delegate, eventNames, $.proxy(this._openMenu, this));
		},
		/** Destructor, called on $().contextmenu("destroy"). */
		_destroy: function(){
			this.element.undelegate(this.eventNamespace);

			this._createUiMenu(null);

			if(this.$headStyle){
				this.$headStyle.remove();
				this.$headStyle = null;
			}
		},
		/** (Re)Create jQuery UI Menu. */
		_createUiMenu: function(menuDef){
			// Remove temporary <ul> if any
			if(this.isOpen()){
				// close without animation, to force async mode
				this._closeMenu(true);
			}

			if(this.menuIsTemp){
				this.$menu.remove(); // this will also destroy ui.menu
			} else if(this.$menu){
				this.$menu.menu("destroy").hide();
			}
			this.$menu = null;
			this.menuIsTemp = false;
			// If a menu definition array was passed, create a hidden <ul>
			// and generate the structure now
			if( ! menuDef ){
				return;
			} else if($.isArray(menuDef)){
				this.$menu = $.moogle.contextmenu.createMenuMarkup(menuDef);
				this.menuIsTemp = true;
			}else if ( typeof menuDef === "string" ){
				this.$menu = $(menuDef);
			}else{
				this.$menu = menuDef;
			}
			// Create - but hide - the jQuery UI Menu widget
			this.$menu
				.hide()
//				.addClass("moogle-contextmenu")
				// Create a menu instance that delegates events to our widget
				.menu({
					blur: $.proxy(this.options.blur, this),
					create: $.proxy(this.options.createMenu, this),
					focus: $.proxy(this.options.focus, this),
					select: $.proxy(function(event, ui){
						// User selected a menu entry
						var retval,
							isParent = (ui.item.has(">a[aria-haspopup='true']").length > 0),
							$a = ui.item.find(">a"),
							actionHandler = $a.data("actionHandler");
						ui.cmd = normCommand($a.attr("href"));
						ui.target = $(this.currentTarget);
						// ignore clicks, if they only open a sub-menu
						if( !isParent || !this.options.ignoreParentSelect){
							retval = this._trigger.call(this, "select", event, ui);
							if( actionHandler ){
								retval = actionHandler.call(this, event, ui);
							}
							if( retval !== false ){
								this._closeMenu.call(this);
							}
							event.preventDefault();
						}
					}, this)
				});
		},
		/** Open popup (called on 'contextmenu' event). */
		_openMenu: function(event){
			var opts = this.options,
				posOption = opts.position,
				self = this,
				ui = {menu: this.$menu, target: $(event.target)};
			this.currentTarget = event.target;
			// Prevent browser from opening the system context menu
			event.preventDefault();

			if( this._trigger("beforeOpen", event, ui) === false ){
				this.currentTarget = null;
				return false;
			}
			ui.menu = this.$menu; // Might have changed in beforeOpen
			// Register global event handlers that close the dropdown-menu
			$(document).bind("keydown" + this.eventNamespace, function(event){
				if( event.which === $.ui.keyCode.ESCAPE ){
					self._closeMenu();
				}
			}).bind("mousedown" + this.eventNamespace + " touchstart" + this.eventNamespace, function(event){
				// Close menu when clicked outside menu
				if( !$(event.target).closest(".ui-menu-item").length ){
					self._closeMenu();
				}
			});

			// required for custom positioning (issue #18 and #13).
			if ($.isFunction(posOption)) {
				posOption = posOption(event, ui);
			}
			posOption = $.extend({
				my: "left top",
				at: "left bottom",
				// if called by 'open' method, event does not have pageX/Y
				of: (event.pageX === undefined) ? event.target : event,
				collision: "fit"
			}, posOption);

			// Finally display the popup
			this.$menu
				.show() // required to fix positioning error
				.css({
					position: "absolute",
					left: 0,
					top: 0
				}).position(posOption)
				.hide(); // hide again, so we can apply nice effects

			this._show(this.$menu, this.options.show, function(){
				self._trigger.call(self, "open", event, ui);
			});
		},
		/** Close popup. */
		_closeMenu: function(immediately){
			var self = this,
				hideOpts = immediately ? false : this.options.hide;

			// Note: we don't want to unbind the 'contextmenu' event
			$(document)
				.unbind("mousedown" + this.eventNamespace)
				.unbind("touchstart" + this.eventNamespace)
				.unbind("keydown" + this.eventNamespace);

			this._hide(this.$menu, hideOpts, function() {
				self._trigger("close");
				self.currentTarget = null;
			});
		},
		/** Handle $().contextmenu("option", key, value) calls. */
		_setOption: function(key, value){
			switch(key){
			case "menu":
				this.replaceMenu(value);
				break;
			}
			$.Widget.prototype._setOption.apply(this, arguments);
		},
		/** Return ui-menu entry (<A> or <LI> tag). */
		_getMenuEntry: function(cmd, wantLi){
			var $entry = this.$menu.find("li a[href=#" + normCommand(cmd) + "]");
			return wantLi ? $entry.closest("li") : $entry;
		},
		/** Close context menu. */
		close: function(){
			if(this.isOpen()){
				this._closeMenu();
			}
		},
		/** Enable or disable the menu command. */
		enableEntry: function(cmd, flag){
			this._getMenuEntry(cmd, true).toggleClass("ui-state-disabled", (flag === false));
		},
		/** Redefine the whole menu. */
		/** Return Menu element (UL). */
		getMenu: function(){
			return this.$menu;
		},
		/** Return true if menu is open. */
		isOpen: function(){
//            return this.$menu && this.$menu.is(":visible");
			return !!this.$menu && !!this.currentTarget;
		},
		/** Open context menu on a specific target (must match options.delegate) */
		open: function(target){
			// Fake a 'contextmenu' event
			var e = jQuery.Event("contextmenu", {target: target.get(0)});
			return this.element.trigger(e);
		},
		replaceMenu: function(data){
			this._createUiMenu(data);
		},
		/** Redefine menu entry (title or all of it). */
		setEntry: function(cmd, titleOrData){
			var $parent,
				$entry = this._getMenuEntry(cmd, false);

			if(typeof titleOrData === "string"){
				// Replace <a> text without removing <span> child
				$entry
					.contents()
					.filter(function(){ return this.nodeType === 3; })
					.first()
					.replaceWith(titleOrData);
			}else{
				$parent = $entry.closest("li").empty();
				$.moogle.contextmenu.createEntryMarkup(titleOrData, $parent);
			}
		},
		/** Show or hide the menu command. */
		showEntry: function(cmd, flag){
			this._getMenuEntry(cmd, true).toggle(flag !== false);
		}
	});

/*
 * Global functions
 */
$.extend($.moogle.contextmenu, {
	/** Convert a menu description into a into a <li> content. */
	createEntryMarkup: function(entry, $parentLi){
		var $a = null;

		if(entry.title.match(/^---/)){
			$parentLi.text(entry.title);
		}else{
			$a = $("<a>", {
				text: "" + entry.title,
				href: "#" + normCommand(entry.cmd)
			}).appendTo($parentLi);
			if( $.isFunction(entry.action) ){
				$a.data("actionHandler", entry.action);
			}
			if(entry.uiIcon){
				$a.append($("<span class='ui-icon'>").addClass(entry.uiIcon));
			}
			if(entry.disabled){
				$parentLi.addClass("ui-state-disabled");
			}
			if($.isPlainObject(entry.data)){
				$a.data(entry.data);
			}
		}
		return $a;
	},
	/** Convert a nested array of command objects into a <ul> structure. */
	createMenuMarkup: function(options, $parentUl){
		var i, menu, $ul, $li;
		if( $parentUl == null ){
			$parentUl = $("<ul class='ui-helper-hidden'>").appendTo("body");
		}
		for(i = 0; i < options.length; i++){
			menu = options[i];
			$li = $("<li>").appendTo($parentUl);

			$.moogle.contextmenu.createEntryMarkup(menu, $li);

			if( $.isArray(menu.children) ){
				$ul = $("<ul>").appendTo($li);
				$.moogle.contextmenu.createMenuMarkup(menu.children, $ul);
			}
		}
		return $parentUl;
	}
});

}(jQuery, window, document));
