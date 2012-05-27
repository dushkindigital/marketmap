package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
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

import com.libereco.core.domain.Marketplace;
import com.libereco.core.service.LiberecoShippingMethodService;
import com.libereco.core.service.MarketplaceService;

@RequestMapping("/marketplaces")
@Controller
public class MarketplaceController {

    @RequestMapping(value = "/{id}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("id") Long id) {
        Marketplace marketplace = marketplaceService.findMarketplace(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (marketplace == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(marketplace.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Marketplace> result = marketplaceService.findAllMarketplaces();
        return new ResponseEntity<String>(Marketplace.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        Marketplace marketplace = Marketplace.fromJsonToMarketplace(json);
        marketplaceService.saveMarketplace(marketplace);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (Marketplace marketplace : Marketplace.fromJsonArrayToMarketplaces(json)) {
            marketplaceService.saveMarketplace(marketplace);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Marketplace marketplace = Marketplace.fromJsonToMarketplace(json);
        if (marketplaceService.updateMarketplace(marketplace) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (Marketplace marketplace : Marketplace.fromJsonArrayToMarketplaces(json)) {
            if (marketplaceService.updateMarketplace(marketplace) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
        Marketplace marketplace = marketplaceService.findMarketplace(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (marketplace == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        marketplaceService.deleteMarketplace(marketplace);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @Autowired
    MarketplaceService marketplaceService;

    @Autowired
    LiberecoShippingMethodService liberecoShippingMethodService;

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Marketplace marketplace, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, marketplace);
            return "marketplaces/create";
        }
        uiModel.asMap().clear();
        marketplaceService.saveMarketplace(marketplace);
        return "redirect:/marketplaces/" + encodeUrlPathSegment(marketplace.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Marketplace());
        return "marketplaces/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("marketplace", marketplaceService.findMarketplace(id));
        uiModel.addAttribute("itemId", id);
        return "marketplaces/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("marketplaces", marketplaceService.findMarketplaceEntries(firstResult, sizeNo));
            float nrOfPages = (float) marketplaceService.countAllMarketplaces() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("marketplaces", marketplaceService.findAllMarketplaces());
        }
        return "marketplaces/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Marketplace marketplace, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, marketplace);
            return "marketplaces/update";
        }
        uiModel.asMap().clear();
        marketplaceService.updateMarketplace(marketplace);
        return "redirect:/marketplaces/" + encodeUrlPathSegment(marketplace.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, marketplaceService.findMarketplace(id));
        return "marketplaces/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Marketplace marketplace = marketplaceService.findMarketplace(id);
        marketplaceService.deleteMarketplace(marketplace);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/marketplaces";
    }

    void populateEditForm(Model uiModel, Marketplace marketplace) {
        uiModel.addAttribute("marketplace", marketplace);
        uiModel.addAttribute("liberecoshippingmethods", liberecoShippingMethodService.findAllLiberecoShippingMethods());
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
