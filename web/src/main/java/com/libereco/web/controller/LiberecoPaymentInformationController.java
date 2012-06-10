package com.libereco.web.controller;

import com.libereco.core.domain.LiberecoPaymentInformation;
import com.libereco.core.domain.PaymentMethod;
import com.libereco.core.service.LiberecoPaymentInformationService;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

@RequestMapping("/liberecopaymentinformations")
@Controller
@RooWebScaffold(path = "liberecopaymentinformations", formBackingObject = LiberecoPaymentInformation.class)
public class LiberecoPaymentInformationController {

    @Autowired
    LiberecoPaymentInformationService liberecoPaymentInformationService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid LiberecoPaymentInformation liberecoPaymentInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoPaymentInformation);
            return "liberecopaymentinformations/create";
        }
        uiModel.asMap().clear();
        liberecoPaymentInformationService.saveLiberecoPaymentInformation(liberecoPaymentInformation);
        return "redirect:/liberecopaymentinformations/" + encodeUrlPathSegment(liberecoPaymentInformation.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new LiberecoPaymentInformation());
        return "liberecopaymentinformations/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("liberecopaymentinformation", liberecoPaymentInformationService.findLiberecoPaymentInformation(id));
        uiModel.addAttribute("itemId", id);
        return "liberecopaymentinformations/show";
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
        return "liberecopaymentinformations/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid LiberecoPaymentInformation liberecoPaymentInformation, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoPaymentInformation);
            return "liberecopaymentinformations/update";
        }
        uiModel.asMap().clear();
        liberecoPaymentInformationService.updateLiberecoPaymentInformation(liberecoPaymentInformation);
        return "redirect:/liberecopaymentinformations/" + encodeUrlPathSegment(liberecoPaymentInformation.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, liberecoPaymentInformationService.findLiberecoPaymentInformation(id));
        return "liberecopaymentinformations/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiberecoPaymentInformation liberecoPaymentInformation = liberecoPaymentInformationService.findLiberecoPaymentInformation(id);
        liberecoPaymentInformationService.deleteLiberecoPaymentInformation(liberecoPaymentInformation);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/liberecopaymentinformations";
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
