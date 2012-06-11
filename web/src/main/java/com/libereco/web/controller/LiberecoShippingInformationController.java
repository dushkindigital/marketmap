package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ShippingType;
import com.libereco.core.service.LiberecoShippingInformationService;

@RequestMapping("/liberecolisting/shippinginformations")
@Controller
public class LiberecoShippingInformationController {

    @Autowired
    LiberecoShippingInformationService liberecoShippingInformationService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid LiberecoShippingInformation shippingInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, shippingInformation);
            return "liberecolisting/shippinginformations/create";
        }
        uiModel.asMap().clear();
        liberecoShippingInformationService.saveShippingInformation(shippingInformation);
        return "redirect:/liberecolisting/shippinginformations/" + encodeUrlPathSegment(shippingInformation.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new LiberecoShippingInformation());
        return "liberecolisting/shippinginformations/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("shippingInformation", liberecoShippingInformationService.findShippingInformation(id));
        uiModel.addAttribute("itemId", id);
        return "liberecolisting/shippinginformations/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(Model uiModel) {
        uiModel.addAttribute("shippingInformations", liberecoShippingInformationService.findAllShippingInformations());
        return "liberecolisting/shippinginformations/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid LiberecoShippingInformation shippingInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, shippingInformation);
            return "liberecolisting/shippinginformations/update";
        }
        uiModel.asMap().clear();
        liberecoShippingInformationService.updateShippingInformation(shippingInformation);
        return "redirect:/liberecolisting/shippinginformations/" + encodeUrlPathSegment(shippingInformation.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, liberecoShippingInformationService.findShippingInformation(id));
        return "liberecolisting/shippinginformations/update";
    }

    void populateEditForm(Model uiModel, LiberecoShippingInformation shippingInformation) {
        uiModel.addAttribute("shippingInformation", shippingInformation);
        uiModel.addAttribute("shippingTypes", Arrays.asList(ShippingType.values()));
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }
}
