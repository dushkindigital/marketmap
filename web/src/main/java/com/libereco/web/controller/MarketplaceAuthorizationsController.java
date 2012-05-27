package com.libereco.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
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

import com.libereco.core.domain.MarketplaceAuthorizations;
import com.libereco.core.domain.MarketplaceAuthorizationsCompositeKey;
import com.libereco.core.service.LiberecoUserService;
import com.libereco.core.service.MarketplaceAuthorizationsService;

@RequestMapping("/marketplaceauthorizationses")
@Controller
public class MarketplaceAuthorizationsController {

    @RequestMapping(value = "/{key}", headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> showJson(@PathVariable("key") MarketplaceAuthorizationsCompositeKey key) {
        MarketplaceAuthorizations marketplaceAuthorizations = marketplaceAuthorizationsService.findMarketplaceAuthorizations(key);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        if (marketplaceAuthorizations == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(marketplaceAuthorizations.toJson(), headers, HttpStatus.OK);
    }

    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<MarketplaceAuthorizations> result = marketplaceAuthorizationsService.findAllMarketplaceAuthorizationses();
        return new ResponseEntity<String>(MarketplaceAuthorizations.toJsonArray(result), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
        MarketplaceAuthorizations marketplaceAuthorizations = MarketplaceAuthorizations.fromJsonToMarketplaceAuthorizations(json);
        marketplaceAuthorizationsService.saveMarketplaceAuthorizations(marketplaceAuthorizations);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJsonArray(@RequestBody String json) {
        for (MarketplaceAuthorizations marketplaceAuthorizations : MarketplaceAuthorizations.fromJsonArrayToMarketplaceAuthorizationses(json)) {
            marketplaceAuthorizationsService.saveMarketplaceAuthorizations(marketplaceAuthorizations);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        MarketplaceAuthorizations marketplaceAuthorizations = MarketplaceAuthorizations.fromJsonToMarketplaceAuthorizations(json);
        if (marketplaceAuthorizationsService.updateMarketplaceAuthorizations(marketplaceAuthorizations) == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/jsonArray", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJsonArray(@RequestBody String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        for (MarketplaceAuthorizations marketplaceAuthorizations : MarketplaceAuthorizations.fromJsonArrayToMarketplaceAuthorizationses(json)) {
            if (marketplaceAuthorizationsService.updateMarketplaceAuthorizations(marketplaceAuthorizations) == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("key") MarketplaceAuthorizationsCompositeKey key) {
        MarketplaceAuthorizations marketplaceAuthorizations = marketplaceAuthorizationsService.findMarketplaceAuthorizations(key);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        if (marketplaceAuthorizations == null) {
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        }
        marketplaceAuthorizationsService.deleteMarketplaceAuthorizations(marketplaceAuthorizations);
        return new ResponseEntity<String>(headers, HttpStatus.OK);
    }

    private ConversionService conversionService;

    @Autowired
    MarketplaceAuthorizationsService marketplaceAuthorizationsService;

    @Autowired
    LiberecoUserService liberecoUserService;

    @Autowired
    public MarketplaceAuthorizationsController(ConversionService conversionService) {
        super();
        this.conversionService = conversionService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid MarketplaceAuthorizations marketplaceAuthorizations, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, marketplaceAuthorizations);
            return "marketplaceauthorizationses/create";
        }
        uiModel.asMap().clear();
        marketplaceAuthorizationsService.saveMarketplaceAuthorizations(marketplaceAuthorizations);
        return "redirect:/marketplaceauthorizationses/"
                + encodeUrlPathSegment(conversionService.convert(marketplaceAuthorizations.getKey(), String.class), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new MarketplaceAuthorizations());
        return "marketplaceauthorizationses/create";
    }

    @RequestMapping(value = "/{key}", produces = "text/html")
    public String show(@PathVariable("key") MarketplaceAuthorizationsCompositeKey key, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("marketplaceauthorizations", marketplaceAuthorizationsService.findMarketplaceAuthorizations(key));
        uiModel.addAttribute("itemId", conversionService.convert(key, String.class));
        return "marketplaceauthorizationses/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("marketplaceauthorizationses",
                    marketplaceAuthorizationsService.findMarketplaceAuthorizationsEntries(firstResult, sizeNo));
            float nrOfPages = (float) marketplaceAuthorizationsService.countAllMarketplaceAuthorizationses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("marketplaceauthorizationses", marketplaceAuthorizationsService.findAllMarketplaceAuthorizationses());
        }
        addDateTimeFormatPatterns(uiModel);
        return "marketplaceauthorizationses/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid MarketplaceAuthorizations marketplaceAuthorizations, BindingResult bindingResult, Model uiModel,
            HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, marketplaceAuthorizations);
            return "marketplaceauthorizationses/update";
        }
        uiModel.asMap().clear();
        marketplaceAuthorizationsService.updateMarketplaceAuthorizations(marketplaceAuthorizations);
        return "redirect:/marketplaceauthorizationses/"
                + encodeUrlPathSegment(conversionService.convert(marketplaceAuthorizations.getKey(), String.class), httpServletRequest);
    }

    @RequestMapping(value = "/{key}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("key") MarketplaceAuthorizationsCompositeKey key, Model uiModel) {
        populateEditForm(uiModel, marketplaceAuthorizationsService.findMarketplaceAuthorizations(key));
        return "marketplaceauthorizationses/update";
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("key") MarketplaceAuthorizationsCompositeKey key,
            @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        MarketplaceAuthorizations marketplaceAuthorizations = marketplaceAuthorizationsService.findMarketplaceAuthorizations(key);
        marketplaceAuthorizationsService.deleteMarketplaceAuthorizations(marketplaceAuthorizations);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/marketplaceauthorizationses";
    }

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("marketplaceAuthorizations_expirationtime_date_format",
                DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }

    void populateEditForm(Model uiModel, MarketplaceAuthorizations marketplaceAuthorizations) {
        uiModel.addAttribute("marketplaceAuthorizations", marketplaceAuthorizations);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("liberecousers", liberecoUserService.findAllLiberecoUsers());
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
