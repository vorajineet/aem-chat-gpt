/*******************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2018 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 ******************************************************************************/
;(function (document, $, ns, channel, window, undefined) {
    "use strict";

    var SELECTORS = {
        useAiCheckbox: '.js-use-ai-checkbox',
        descriptionBox: '.js-description-checkbox'
    };

    // Initialized on foundation-contentloaded
    var fields = {
        useAiCheckbox: undefined,
        descriptionBox: undefined
    };

    function disableFields() {
        fields.descriptionBox.setAttribute("disabled", "");
    }

    function enableFields() {
        fields.descriptionBox.removeAttribute("disabled");
    }

    /**
     * Called when content is loaded for the first time.
     */
	channel.one("foundation-contentloaded", function () {
        fields.useAiCheckbox = document.querySelector(SELECTORS.useAiCheckbox);
        if(fields.useAiCheckbox.checked) {
            alert("checked");
        } else {
            alert("unchecked");
        }
        fields.descriptionBox = document.querySelector(SELECTORS.descriptionBox);
    });

    /**
     * Called when the "inherited from" checkbox is ticked.
     */
    channel.on("change", SELECTORS.useAiCheckbox, function () {
        if (this.checked) {
            $(SELECTORS.descriptionBox).val("AI Generated Text");
            disableFields();
        } else {
            enableFields();
        }
    });

}(document, jQuery, Granite.author, jQuery(document), this));