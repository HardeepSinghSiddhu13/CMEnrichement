package com.samsung.nmt.cmenrichment.repo;

public interface NwSubElementTypeRepo {
    Integer addIfAbsentAndGetId(String typeName, Integer nwElementTypeId);
}
