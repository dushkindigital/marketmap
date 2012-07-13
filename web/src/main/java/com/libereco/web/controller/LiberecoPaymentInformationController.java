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

import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.service.LiberecoPaymentInformationService;

@RequestMapping("/liberecolisting/paymentinformations")
@Controller
public class LiberecoPaymentInformationController {

    @Autowired
    LiberecoPaymentInformationService liberecoPaymentInformationService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid LiberecoPaymentInformation liberecoPaymentInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoPaymentInformation);
            return "liberecolisting/paymentinformations/create";
        }
        uiModel.asMap().clear();
        liberecoPaymentInformationService.saveLiberecoPaymentInformation(liberecoPaymentInformation);
        return "redirect:/liberecolisting/paymentinformations/"
                + encodeUrlPathSegment(liberecoPaymentInformation.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new LiberecoPaymentInformation());
        return "liberecolisting/paymentinformations/create";
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        LiberecoPaymentInformation liberecoPaymentInformation = LiberecoPaymentInformation.fromJsonToLiberecoPaymentInformation(json);
        liberecoPaymentInformationService.saveLiberecoPaymentInformation(liberecoPaymentInformation);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(liberecoPaymentInformation.toJson(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("liberecopaymentinformation", liberecoPaymentInformationService.findLiberecoPaymentInformation(id));
        uiModel.addAttribute("itemId", id);
        return "liberecolisting/paymentinformations/show";
    }

    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        LiberecoPaymentInformation liberecoPaymentInformation = liberecoPaymentInformationService.findLiberecoPaymentInformation(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (liberecoPaymentInformation == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(liberecoPaymentInformation.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("liberecopaymentinformations",
                    liberecoPaymentInformationService.findLiberecoPaymentInformationEntries(firstResult, sizeNo));
            float nrOfPages = (float) liberecoPaymentInformationService.countAllLiberecoPaymentInformations() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("liberecopaymentinformations", liberecoPaymentInformationService.findAllLiberecoPaymentInformations());
        }
        return "liberecolisting/paymentinformations/list";
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<LiberecoPaymentInformation> result = liberecoPaymentInformationService.findAllLiberecoPaymentInformations();
        return new ResponseEntity<String>(LiberecoPaymentInformation.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid LiberecoPaymentInformation liberecoPaymentInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoPaymentInformation);
            return "liberecolisting/paymentinformations/update";
        }
        uiModel.asMap().clear();
        liberecoPaymentInformationService.updateLiberecoPaymentInformation(liberecoPaymentInformation);
        return "redirect:/liberecolisting/paymentinformations/"
                + encodeUrlPathSegment(liberecoPaymentInformation.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, liberecoPaymentInformationService.findLiberecoPaymentInformation(id));
        return "liberecolisting/paymentinformations/update";
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        LiberecoPaymentInformation liberecoPaymentInformation = LiberecoPaymentInformation.fromJsonToLiberecoPaymentInformation(json);
        if (liberecoPaymentInformationService.updateLiberecoPaymentInformation(liberecoPaymentInformation) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiberecoPaymentInformation liberecoPaymentInformation = liberecoPaymentInformationService.findLiberecoPaymentInformation(id);
        liberecoPaymentInformationService.deleteLiberecoPaymentInformation(liberecoPaymentInformation);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/liberecolisting/paymentinformations";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        LiberecoPaymentInformation liberecoPaymentInformation = liberecoPaymentInformationService.findLiberecoPaymentInformation(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (liberecoPaymentInformation == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        liberecoPaymentInformationService.deleteLiberecoPaymentInformation(liberecoPaymentInformation);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    void populateEditForm(Model uiModel, LiberecoPaymentInformation liberecoPaymentInformation) {
        uiModel.addAttribute("liberecoPaymentInformation", liberecoPaymentInformation);
        uiModel.addAttribute("paymentmethods", Arrays.asList(PaymentMethod.values()));
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
