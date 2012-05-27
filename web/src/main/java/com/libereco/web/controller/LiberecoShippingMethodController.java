package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.libereco.core.domain.LiberecoShippingMethod;
import com.libereco.core.domain.ShippingLevelType;
import com.libereco.core.service.LiberecoShippingMethodService;
import com.libereco.core.service.MarketplaceService;

@RequestMapping("/liberecoshippingmethods")
@Controller
public class LiberecoShippingMethodController {

    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        LiberecoShippingMethod liberecoShippingMethod = liberecoShippingMethodService.findLiberecoShippingMethod(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (liberecoShippingMethod == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(liberecoShippingMethod.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<LiberecoShippingMethod> result = liberecoShippingMethodService.findAllLiberecoShippingMethods();
        return new ResponseEntity<String>(LiberecoShippingMethod.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        LiberecoShippingMethod liberecoShippingMethod = LiberecoShippingMethod.fromJsonToLiberecoShippingMethod(json);
        liberecoShippingMethodService.saveLiberecoShippingMethod(liberecoShippingMethod);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (LiberecoShippingMethod liberecoShippingMethod : LiberecoShippingMethod.fromJsonArrayToLiberecoShippingMethods(json)) {
            liberecoShippingMethodService.saveLiberecoShippingMethod(liberecoShippingMethod);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        LiberecoShippingMethod liberecoShippingMethod = LiberecoShippingMethod.fromJsonToLiberecoShippingMethod(json);
        if (liberecoShippingMethodService.updateLiberecoShippingMethod(liberecoShippingMethod) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (LiberecoShippingMethod liberecoShippingMethod : LiberecoShippingMethod.fromJsonArrayToLiberecoShippingMethods(json)) {
            if (liberecoShippingMethodService.updateLiberecoShippingMethod(liberecoShippingMethod) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        LiberecoShippingMethod liberecoShippingMethod = liberecoShippingMethodService.findLiberecoShippingMethod(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (liberecoShippingMethod == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        liberecoShippingMethodService.deleteLiberecoShippingMethod(liberecoShippingMethod);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @Autowired
    LiberecoShippingMethodService liberecoShippingMethodService;

    @Autowired
    MarketplaceService marketplaceService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid LiberecoShippingMethod liberecoShippingMethod, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoShippingMethod);
            return "liberecoshippingmethods/create";
        }
        uiModel.asMap().clear();
        liberecoShippingMethodService.saveLiberecoShippingMethod(liberecoShippingMethod);
        return "redirect:/liberecoshippingmethods/" + encodeUrlPathSegment(liberecoShippingMethod.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new LiberecoShippingMethod());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (marketplaceService.countAllMarketplaces() == 0) {
            dependencies.add(new String[] { "marketplace", "marketplaces" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "liberecoshippingmethods/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("liberecoshippingmethod", liberecoShippingMethodService.findLiberecoShippingMethod(id));
        uiModel.addAttribute("itemId", id);
        return "liberecoshippingmethods/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("liberecoshippingmethods", liberecoShippingMethodService.findLiberecoShippingMethodEntries(firstResult, sizeNo));
            float nrOfPages = (float) liberecoShippingMethodService.countAllLiberecoShippingMethods() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("liberecoshippingmethods", liberecoShippingMethodService.findAllLiberecoShippingMethods());
        }
        return "liberecoshippingmethods/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid LiberecoShippingMethod liberecoShippingMethod, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, liberecoShippingMethod);
            return "liberecoshippingmethods/update";
        }
        uiModel.asMap().clear();
        liberecoShippingMethodService.updateLiberecoShippingMethod(liberecoShippingMethod);
        return "redirect:/liberecoshippingmethods/" + encodeUrlPathSegment(liberecoShippingMethod.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, liberecoShippingMethodService.findLiberecoShippingMethod(id));
        return "liberecoshippingmethods/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        LiberecoShippingMethod liberecoShippingMethod = liberecoShippingMethodService.findLiberecoShippingMethod(id);
        liberecoShippingMethodService.deleteLiberecoShippingMethod(liberecoShippingMethod);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/liberecoshippingmethods";
    }

    void populateEditForm(Model uiModel, LiberecoShippingMethod liberecoShippingMethod) {
        uiModel.addAttribute("liberecoShippingMethod", liberecoShippingMethod);
        uiModel.addAttribute("marketplaces", marketplaceService.findAllMarketplaces());
        uiModel.addAttribute("shippingleveltypes", Arrays.asList(ShippingLevelType.values()));
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
