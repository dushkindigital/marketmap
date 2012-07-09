package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.libereco.core.domain.LiberecoShippingInformation;
import com.libereco.core.domain.ShippingService;
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

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        LiberecoShippingInformation shippingInformation = LiberecoShippingInformation.fromJsonToLiberecoShippingInformation(json);
        liberecoShippingInformationService.saveShippingInformation(shippingInformation);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("shippingInformation", liberecoShippingInformationService.findShippingInformation(id));
        uiModel.addAttribute("itemId", id);
        return "liberecolisting/shippinginformations/show";
    }

    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        LiberecoShippingInformation shippingInformation = liberecoShippingInformationService.findShippingInformation(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (shippingInformation == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(shippingInformation.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(produces = "text/html")
    public String list(Model uiModel) {
        uiModel.addAttribute("shippingInformations", liberecoShippingInformationService.findAllShippingInformations());
        return "liberecolisting/shippinginformations/list";
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<LiberecoShippingInformation> result = liberecoShippingInformationService.findAllShippingInformations();
        return new ResponseEntity<String>(LiberecoShippingInformation.toJsonArray(result), headers, HttpStatus.OK);
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

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        LiberecoShippingInformation shippingInformation = LiberecoShippingInformation.fromJsonToLiberecoShippingInformation(json);
        if (liberecoShippingInformationService.updateShippingInformation(shippingInformation) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiberecoShippingInformation shippingInformation = liberecoShippingInformationService.findShippingInformation(id);
        liberecoShippingInformationService.deleteShippingInformation(shippingInformation);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/liberecolisting/shippinginformations";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        LiberecoShippingInformation shippingInformation = liberecoShippingInformationService.findShippingInformation(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (shippingInformation == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        liberecoShippingInformationService.deleteShippingInformation(shippingInformation);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    void populateEditForm(Model uiModel, LiberecoShippingInformation shippingInformation) {
        uiModel.addAttribute("shippingInformation", shippingInformation);
        uiModel.addAttribute("shippingTypes", Arrays.asList(ShippingType.values()));
        uiModel.addAttribute("shippingservices", Arrays.asList(ShippingService.values()));
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
