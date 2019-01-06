package com.samsung.nmt.cmenrichment.repo;

public interface MetadataRepo<D> {
    Integer addIfAbsentAndGetId(D d);

}
