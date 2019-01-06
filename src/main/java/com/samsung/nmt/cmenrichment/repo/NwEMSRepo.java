package com.samsung.nmt.cmenrichment.repo;

public interface NwEMSRepo<D> extends StaticMetadataRepo<D> {

    int updateStatusToActive(Integer emsId);
}
