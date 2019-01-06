package com.samsung.nmt.cmenrichment.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samsung.nmt.cmenrichment.constants.AppProperties;
import com.samsung.nmt.cmenrichment.dto.MergedData;
import com.samsung.nmt.cmenrichment.dto.NwEMS;
import com.samsung.nmt.cmenrichment.dto.NwElement;
import com.samsung.nmt.cmenrichment.exceptions.LocationNotFound;
import com.samsung.nmt.cmenrichment.qualifiers.NwStatusQ;
import com.samsung.nmt.cmenrichment.qualifiers.NwElementTypeQ;
import com.samsung.nmt.cmenrichment.repo.MetadataRepo;
import com.samsung.nmt.cmenrichment.repo.StaticMetadataRepo;
import com.samsung.nmt.cmenrichment.utils.DateTimeUtil;
import com.samsung.nmt.cmenrichment.repo.LocationRepo;
import com.samsung.platform.domain.kafka.Event;
import com.samsung.platform.domain.kafka.Identifier;

@Component
public class NwElementServiceImpl implements NwElementService {

    @Autowired
    @NwElementTypeQ
    private MetadataRepo<String> nwElementTypeRepo;

    @Autowired
    @NwStatusQ
    private StaticMetadataRepo<String> nwStatusRepo;

    @Autowired
    private LocationRepo locationRepo;

    @Autowired
    private AppProperties appProperties;

    @Override
    public NwElement createNwElementFromCollectorAddData(Event event, ObjectNode objectNode, NwEMS nwEMS) {

        NwElement nwElement = new NwElement();

        //get enodeb name
        Identifier identifier = event.getIdentifier();
        String neName = identifier.getValue();
        nwElement.setName(neName);

        //get neid
        Integer neId = objectNode.get(appProperties.getNwElementSysIdAttrKey()).asInt();
        nwElement.setNeId(neId);

        //get ems id
        nwElement.setEmsId(nwEMS.getEmsId());

        //get type id
        Integer neTypeId = nwElementTypeRepo.addIfAbsentAndGetId(appProperties.getNwElementTypeName());
        nwElement.setTypeId(neTypeId);

        //get location Id
        Integer locationId = locationRepo.getLocationId(neName);
        if (locationId == null) {
            throw new LocationNotFound(
                    "Location id not found for element : " + neName);
        }
        nwElement.setLocationId(locationId);

        nwElement.setProperties(objectNode);

        nwElement.setCreatedTimeStamp(DateTimeUtil.getInstance().currTime());

        Integer statusId = nwStatusRepo.getId(appProperties.getStatusActiveValue());
        nwElement.setStatusId(statusId);

        String vendor = objectNode.get(appProperties.getNwElementVendorNameAttrKey()).asText();
        nwElement.setVendor(vendor);

        String version = objectNode.get(appProperties.getNwElementSoftwareVersionAttrKey()).asText();
        nwElement.setVersion(version);

        Calendar currTime = DateTimeUtil.getInstance().currTime();
        nwElement.setLastModifiedTimeStamp(currTime);
        nwElement.setLastModifiedTimeStamp(currTime);

        return nwElement;
    }

    @Override
    public NwElement createNwElementFromCollectorUpdateData(MergedData mergedData, NwElement dbNwElement) {

        NwElement updatedNwElement = new NwElement();
        //check if only properties are updated set pojo values accordingly
        updatedNwElement.setElementId(dbNwElement.getElementId());
        updatedNwElement.setProperties(mergedData.getMergedJson());
        updatedNwElement.setLastModifiedTimeStamp(DateTimeUtil.getInstance().currTime());
        if (mergedData.isOnlyPropUpdated() == false) {
            //get vendor
            if (mergedData.getMergedJson().has(appProperties.getNwElementVendorNameAttrKey())) {
                String vendor = mergedData.getMergedJson().get(appProperties.getNwElementVendorNameAttrKey()).asText();
                updatedNwElement.setVendor(vendor);
            } else {
                updatedNwElement.setVendor(dbNwElement.getVendor());
            }

            //get version
            if (mergedData.getMergedJson().has(appProperties.getNwElementSoftwareVersionAttrKey())) {
                String version = mergedData.getMergedJson().get(appProperties.getNwElementSoftwareVersionAttrKey())
                        .asText();
                updatedNwElement.setVersion(version);
            } else {
                updatedNwElement.setVersion(dbNwElement.getVersion());
            }

            Integer statusId = nwStatusRepo.getId(appProperties.getStatusActiveValue());
            updatedNwElement.setStatusId(statusId);
        }

        return updatedNwElement;

    }

}
