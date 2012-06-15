package com.libereco.core.domain;

import org.apache.commons.lang3.StringUtils;

public enum ShippingService {

    InternationalPriorityShipping, ShippingMethodStandard, USPSGlobalExpress, ePacketChina, ePacketSingapore, ePacketHongKong, ePacketThailand, UPSGround, USPSGlobalPriority, USPSGround, ShippingMethodExpress, USPSEconomyLetter, USPSPriority, USPSEconomyParcel, USPSAirmailLetter, USPSPriorityFlatRateEnvelope, USPSAirmailParcel, USPSFirstClassMailInternational, USPSPriorityMailSmallFlatRateBox, USPSPriorityMailInternational, USPSPriorityFlatRateBox, USPSPriorityMailLargeFlatRateBox, USPSPriorityMailInternationalFlatRateEnvelope, USPSPriorityMailPaddedFlatRateEnvelope, USPSPriorityMailLegalFlatRateEnvelope, USPSPriorityMailInternationalSmallFlatRateBox, USPSPriorityMailRegionalBoxA, USPSPriorityMailInternationalFlatRateBox, USPSPriorityMailRegionalBoxB, USPSPriorityMailRegionalBoxC, USPSPriorityMailInternationalLargeFlatRateBox, USPSPriorityMailInternationalPaddedFlatRateEnvelope, USPSFirstClass, USPSPriorityMailInternationalLegalFlatRateEnvelope, UPS3rdDay, USPSExpressMailInternational, UPS2ndDay, USPSExpressMailInternationalFlatRateEnvelope, USPSExpressMailInternationalLegalFlatRateEnvelope, UPSWorldWideExpressPlus, Other, UPSWorldWideExpress, ExpressInternationalShipping_2, ExpressInternationalShipping, USPSParcel, USPSMedia, UPSWorldWideExpedited, UPSWorldwideSaver, UPSStandardToCanada, LocalDelivery, ShippingMethodOvernight, FedExInternationalEconomy, FedExInternationalPriority, FedExGroundInternationalToCanada, StandardInternational, USPSExpressMail, ExpeditedInternational, USPSExpressFlatRateEnvelope, USPSExpressMailLegalFlatRateEnvelope, OtherInternational, UPSNextDay, UPSNextDayAir, FedExHomeDelivery, FedExExpressSaver, FedEx2Day, FedExPriorityOvernight, FedExStandardOvernight, USPSGlobalPrioritySmallEnvelope, USPSGlobalPriorityLargeEnvelope, UPSWorldWideExpressBox10kg, UPSWorldWideExpressBox25kg, UPSWorldWideExpressPlusBox10kg, UPSWorldWideExpressPlusBox25kg, USPSFirstClassLetter, USPSFirstClassLargeEnvelop, USPSFirstClassParcel, EconomyShippingFromOutsideUS, ExpressShippingFromOutsideUS, USPSExpressMailFlatRateBox, StandardShippingFromOutsideUS, ExpeditedShippingFromOutsideUS, UPS2DayAirAM, FreightShippingInternational, PromotionalShippingMethod, USPSExpressMailInternationalFlatRateBox, USPSGlobalExpressGuaranteed, FreightShipping, Freight, Delivery, Pickup, USPSFirstClassMailInternationalLetter, USPSFirstClassMailInternationalLargeEnvelope, USPSFirstClassMailInternationalParcel;
    
    public static ShippingService fromString(String shippingService){
        ShippingService[] values = ShippingService.values();
        for (ShippingService value : values) {
            if(StringUtils.equals(shippingService, value.name())){
                return value;
            }
        }
        return null;
    }
}
